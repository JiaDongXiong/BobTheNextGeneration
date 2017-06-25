package com.bob.game;

import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.bob.game.inputs.*;
import com.bob.game.levels.Level;
import com.bob.game.world.WorldController;

import java.util.*;

public class GameController {

    private final LayerGroup layerGroup;
    private Level currentLevel;
    private final InputsManager inputsManager;
    private final MacroManager macroManager;
    private final WorldController worldController;
    private int currentHint = 0;
    private EditorController editorController;
    
    //Layers
    private final LPSLayer lps;
    private final CausalLayer causal;
    private final MacroRulesLayer macro;
    private final ControlsLayer controls;
    private final ExampleLayer examples;
    private final TextLayer textual;

    public GameController(Skin skin, OrthographicCamera camera) {

        layerGroup = new LayerGroup();

        layerGroup.add("background", new BackgroundLayer(skin));
        layerGroup.add("controls", new ControlsLayer(skin, this));
        layerGroup.add("inputs", new InputsLayer(skin));
        layerGroup.add("macro", new MacroLayer(skin));
        layerGroup.add("modal_inputs", new ModalLayer(skin));
        layerGroup.add("winning", new WinningLayer(skin, this));
        layerGroup.add("message", new MessageLayer(skin, this));
        layerGroup.add("help screen", new HelpScreen(skin));
        layerGroup.add("lps", new LPSLayer(skin));
        layerGroup.add("causal", new CausalLayer(skin));
        layerGroup.add("macroRules", new MacroRulesLayer(skin));
        layerGroup.add("examples", new ExampleLayer());
        layerGroup.add("textual", new TextLayer(skin, this));
        
        lps = (LPSLayer)layerGroup.get("lps");
        causal = (CausalLayer)layerGroup.get("causal");
        macro = (MacroRulesLayer)layerGroup.get("macroRules");
        controls = (ControlsLayer)layerGroup.get("controls");
        examples = (ExampleLayer)layerGroup.get("examples");
        textual = (TextLayer)layerGroup.get("textual");
        
        inputsManager = new InputsManager();
        macroManager = new MacroManager();
        worldController = new WorldController();

        worldController.setCamera(camera);

        inputsManager.setLayer((InputsLayer)layerGroup.get("inputs"));
        inputsManager.setController(this);
        inputsManager.initRuleView(skin, 1475, 1080 - 495);

        macroManager.setLayers((MacroLayer)layerGroup.get("macro"), (ModalLayer)layerGroup.get("modal_inputs"));
        macroManager.initView(skin);
        macroManager.addButtons(skin);
        
        layerGroup.hide();
    }

    public void reset() {
        if (currentLevel.allowRuleReset()) {
            inputsManager.resetRules();
            macroManager.resetMacros();
        }
        resetWorld();
    }

    public void startNewLevel() {
    	controls.reset();
    	layerGroup.show();
        currentHint = 0;
        inputsManager.resetRules();
        ((ControlsLayer)layerGroup.get("controls")).setSpeedFactor(2);

        if (currentLevel.hasTutorial()) {
            ((HelpScreen)layerGroup.get("help screen")).setImages(currentLevel.getTutorialImages());
            layerGroup.setVisibility("help screen", true);
        }

        if (currentLevel.allowMacro()) {
        	controls.displayExample(false);
            layerGroup.setVisibility("macro", true);
            layerGroup.setVisibility("inputs", false);

            macroManager.resetMacros();
            macroManager.resetMacroInputs();
        } else {
        	controls.displayExample(true);
            layerGroup.setVisibility("macro", false);
            layerGroup.setVisibility("inputs", true);
            if (getCurrGameMode().equals("READ")) {
            	inputsManager.disableClearRuleButtons(true);
            	inputsManager.setUpRulesForRead(currentLevel.getRules(), false);
            } else {
            	int noOfRules = currentLevel.getNoRules();
            	inputsManager.disableClearRuleButtons(false);
            	inputsManager.setupRules(noOfRules, currentLevel.getRules(), false);
            	inputsManager.disableRuleUIs(noOfRules);
            }
            inputsManager.setupInputs(currentLevel.getInputs(), 1415, 1080-165);
        }

        ((ControlsLayer)layerGroup.get("controls")).disableReset(currentLevel.allowRuleReset());
        ((ControlsLayer)layerGroup.get("controls")).disableHints(currentLevel.hasHints());

        worldController.setupWorld(currentLevel);
        worldController.initRender();
        ((BackgroundLayer)layerGroup.get("background")).changeText(currentLevel.getText());
        ((BackgroundLayer)layerGroup.get("background")).setMaxLights(worldController.getMaxObjects());
    }

    public void render(float deltaTime) {

        inputsManager.toggleLights();
        inputsManager.lightOffRules();
        inputsManager.lightOnRule(worldController.getCurrentRuleIndexes());

        macroManager.toggleLights();

        worldController.render(deltaTime);
        ((BackgroundLayer)layerGroup.get("background")).setNoLights(worldController.getMaxObjects() - worldController.noOfLightBulbs());

        if (worldController.isLevelWon()) {
        	if(!editorController.isTrying) {
        		currentLevel.save();
        		layerGroup.setVisibility("winning", true);
        	}
        }

        if (worldController.isBobConfused()) {
            ((MessageLayer) layerGroup.get("message")).changeText("Uh oh, Bob does not know which rule to follow...");
            layerGroup.setVisibility("message", true);
        }
        
        if (worldController.outOfMap()) {
        	this.resetWorld();
        }
        
    }
    
    public boolean isRulesValid() {
    	return inputsManager.checkRules();
    }
    
    public void setLevel(Level level) {
        this.currentLevel = level;
    }

    public void loadNextLevel() {
        if (currentLevel.getNext() == null) { // Mode completed, back to menu
            hide();
        } else {
            currentLevel = currentLevel.getNext();
            startNewLevel();
        }

        layerGroup.setVisibility("winning", false);
    }

    public void submit() {
        resetWorld();

        if (currentLevel.allowMacro()) {
        	worldController.setAllowMacro(true);
            startLPSAnim(macroManager.getRulesString());
        } else {
        	worldController.setAllowMacro(false);
            if (inputsManager.mixedParadigmUsed()) {
                ((HelpScreen)layerGroup.get("help screen")).setImage("screens/both_paradigm.png");
                layerGroup.setVisibility("help screen", true);
            } else if (inputsManager.onlyConsequentUsed()) {
                startMockAnim(inputsManager.getBlockStack());
            } else {
                startLPSAnim(inputsManager.getRulesString());
            }
        }
    }

    public void resetWorld() {
        worldController.resetStage(currentLevel.getX(), currentLevel.getY());
        ((BackgroundLayer)layerGroup.get("background")).setNoLights(0);
    }

    private void startMockAnim(LinkedList<Block> blockStack) {
        worldController.startMockAnimation(blockStack);
    }

    private void startLPSAnim(String LPS) {
        worldController.startLPSAnimation(currentLevel, LPS);
    }

    public void updateSpeed(float value) {
        worldController.updateSpeed(value);
    }

    public void linkStage(Stage stage) {
        layerGroup.setStage(stage);
        worldController.setStage(stage);
    }

    public void show() {
        layerGroup.show();
    }

    public void hide() {
    	inputsManager.showRuleUIs();
        layerGroup.hide();
    }

    public boolean isVisible() {
        return layerGroup.isVisible();
    }
    
    // ------------------ My Code ---------------- \\
    /////////////////////////////////////////////////
    
    //display LPS reactive rules and causal theory
    public void drawLPSRules(SpriteBatch b) {
    	if (lps.isVisible()) {
        	lps.DisplayLPSRules(b, inputsManager.getRulesStringForDisplay());
        }
    }
    
    public void drawCausalTheory(SpriteBatch b) {
    	if (causal.isVisible()) {
    		causal.DisplayCausalTheory(b);
    	}
    }
    
    public void drawMacros(SpriteBatch b) {
    	if (macro.isVisible()) {
    		macro.DisplayMacro(b, inputsManager.getRulesStringForDisplay());
    	}
    }
    
    // set the visibility of the lps rules layer to true
    public void displayLPS() {
    	layerGroup.setVisibility("lps", true);
    	lps.setVisibe(true);
    }
    
    // set the visibility of the causal layer to true
	public void displayCausal() {
		layerGroup.setVisibility("causal", true);
		causal.setVisibe(true);
	}
    
	public void displayMacroRules(int i) {
		macro.setUpRule(i);
		layerGroup.setVisibility("macroRules", true);
		macro.setVisibe(true);
	}
	
	// set the visibility of the examples layer to true
	public void displayExamples() {
		examples.setVisibility(true);
	}
	
	public void displayTextual(int index) {
		if (index > -1) {
			textual.initialize(index, getLPSRule(index));
		}
		textual.setVisibility(true);
	}
	
	// get displaying rule string for a given rule index
    protected String getLPSRule(int index) {
    	return inputsManager.getRuleStringForDisplay(index);
    }
	
    public int noOfRules() {
    	return inputsManager.noOfRules();
    }
    //////////////////////////////////////////////////
    
    // return the current rule page index
    public boolean isOnwhichPage(int index) {
    	return inputsManager.isOnwhichPage(index);
    }
    
    // clear a rule with a given index
    public void clearRule(int index) {
    	inputsManager.clearRule(index);
    }
    
    // turn to the next rule page
    public void nextRulePage() {
    	inputsManager.nextPage(true);
    }
    
    // turn to the previous rule page
    public void prevRulePage() {
    	inputsManager.prevPage(true);
    }
    
    // does the current map has a golden tile?
    public boolean hasGoldTile() {
    	return worldController.hasGoldTile();
    }
    
    // return the game mode type of the current level
    public String getCurrGameMode() {
    	return currentLevel != null ? currentLevel.getGameMode() : "";
    }
    
    // return number of available rules
    public int getNoOfAvaliRules() {
    	return currentLevel != null ? currentLevel.getNoRules() : 8;
    }
    
    public void loadEditor(EditorController editorController) {
		this.editorController = editorController;
	}
    
    public void setRuleBlocks(Block[] blocks, int ruleIndex) {
    	if (ruleIndex < 0) {
    		return;
    	}
    	inputsManager.setRule(ruleIndex, blocks);
    }
    
    // ----------------------------------------- \\
    ///////////////////////////////////////////////

    // display provided hints
    public void displayHints() {
        if (currentLevel.hasHints()) {
            ((MessageLayer) layerGroup.get("message")).changeText(currentLevel.getHints()[currentHint]);
            layerGroup.setVisibility("message", true);
            currentHint = (currentHint + 1) % currentLevel.getHints().length;
        }
    }

    // display user manuals
    public void displayHelp() {
        String[] res = new String[2];
        res[0] = "screens/userManualOne.png";
        res[1] = "screens/userManualTwo.png";

        ((HelpScreen)layerGroup.get("help screen")).setImages(res);
        layerGroup.setVisibility("help screen", true);
    }

}

package com.bob.mapEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.bob.game.inputs.Block;
import com.bob.game.inputs.InputsManager;

public class idManager {
	
	private ReadLayer readLayer; 
	private LoadingLayer loadingLayer;
	//private GameController gameController;
	
	private MapEditor mapEditor;
	private InputsManager inputsManager;
	private List<ArrayList<Block>> rules;
	private Block[][] rulesArray = new Block[8][14];
	private boolean showRead = false;
	private boolean isLoadingShown = false;
	private Group UIGroup;
	private TextField textField;
	private Label label;
	
	//map control
	private Integer id;
	//private TiledMapTileLayer floorLayer;
	private ObjectList objectList = new ObjectList(); // for drawing objectList in editor mode
	private MyPair<Integer, Integer> bobPos;     // Bob cord in mapEditor
	private int[][] floor = new int[23][23];
    private int[][] objects = new int[23][23];
    private String modeType = "WRITE";	//default game mode

	
	public idManager(Integer id) {
		inputsManager = new InputsManager();
		this.id = id;
		clearDrawing();
	}
	
	public void initView(Skin skin) {
        inputsManager.initRuleView(skin, 1464, 1080 - 545);
    }
	
	public void setMap(int[][] floor, int[][] objects) {
		mapEditor.setMap(floor, objects);
		this.floor = rotate(floor);
		this.objects = rotate(objects);
	}
	
	private int[][] rotate(int[][] m) {
		int [][] result = new int[23][23];
		for(int j=0; j<m.length; j++) {
			for (int k=0; k<m[j].length; k++) {
				result[22-k][j] = m[j][k]; 
			}
		}
		return result;
	}
	
	public List<ArrayList<Block>> getRuleBlocks() {
		return rules;
	}
	
	public void loadRuleBlocks() {
		inputsManager.upDateAllRules();
		rules = inputsManager.getRLRules();
		saveRulesInArray();
	}
	
	private void saveRulesInArray() {
		rulesArray = inputsManager.getRules();
	}
	
	public void setRulesArray(Block[][] newRules) {
		rulesArray = newRules;
		inputsManager.resetRules(rulesArray);
	}
	
	public boolean checkAllRules() {
		return inputsManager.checkRules();
	}
	
	public void resetRules() {
		inputsManager.resetRules();
		rules = null;
		rulesArray = new Block[8][14];
		modeType = "WRITE";
	}
	
	public void toggleLights() {
        inputsManager.toggleLights();
    }
	
	public void setLayers(ReadLayer readLayer, LoadingLayer loadingLayer) {
		this.readLayer = readLayer;
		this.loadingLayer = loadingLayer;
	    inputsManager.setLayer(readLayer);
	}
	 
	public void addHiddenLButtons(Skin skin) {
		readLayer.addButtons(skin, this);
	}
	 
	public void displayReadL() {
		showRead = true;
		hideUIAndMap();
		displayMacroModal();
	}
	
	public void quitReadLayer() {
		showRead = false;
    	showUIAndMap();
	}
	
	public boolean getShowRead() {
		return showRead;
	}
	
	public void displayLoadingL(Skin skin) {
		loadingLayer.addLvlButtons(skin, this);
		loadingLayer.setVisibility(true);
		hideUIAndMap();
		isLoadingShown = true;
	}
	
	public void quiteLoadingL(String mode) {
		isLoadingShown = false;
		loadingLayer.clear();
		showUIAndMap();
		setModeType(mode);
		labelCM();
	}
	
	public boolean isLoadingShown() {
		return isLoadingShown;
	}
	 
	private void displayMacroModal() {
		//inputsManager.setupRules(8, rules, true);
		inputsManager.setUpRulesForRead(rulesArray, true);
		inputsManager.setupInputs(Block.values(), 1404, 1080 - 215);
		readLayer.setVisibility(true);
	}
	
	public void setFloor(int x, int y) {
		floor[22-y][x] = id;
	}
	
	public int getFloorId(int x, int y) {
		return floor[x][y];
	}
	
	/*public void setObjects(int x, int y) {
		switch(id) {
		
		case 11:	//place a bulb
			objects[22-y][x] = 25;
			break;
		case 12:	//remove a bulb
			objects[22-y][x] = 0;
			break;
		case 13:	//place a boat
			objects[22-y][x] = 26;
			break;
		case 14:	//remove a boat
			objects[22-y][x] = 0;
			break;
		default:
			
		}
	}*/
	
	public void draw(SpriteBatch batch, OrthographicCamera camera) {
		batch.setProjectionMatrix(camera.combined);
        batch.begin();
        
        Texture object = new Texture(Gdx.files.internal("ui/small_bulb.png"));
        Texture bob = new Texture(Gdx.files.internal("ui/bob.png"));
        Texture boat = new Texture(Gdx.files.internal("ui/boat.png"));
        
        //draw Objects
        if (!objectList.isEmpty()) {
        	for (MyPair<MyPair<Integer, Integer>, String> b : objectList.getElemts()) {
        		MyPair<Integer, Integer> objectPos = b.key();
        		if(objectPos.equals(bobPos)) { continue; }
        		
        		String objectType = b.value();
        		Vector2 cord = calTilePos(objectPos.key(), objectPos.value());
        		switch (objectType) {
        		case "Boat":
        			batch.draw(boat, cord.x-37, cord.y+14);
        			break;
        		case "Bulb":
            		batch.draw(object, cord.x-26, cord.y+13);
        			break;
        		default:
        		}
        	}
        } 
        
        //draw Bob
        if (bobPos != null) {
        	Vector2 cord = calTilePos(bobPos.key(), bobPos.value());
        	batch.draw(bob, cord.x-26, cord.y+8);
        }
        batch.end();
	}
	
	public Vector2 calTilePos(int x, int y) {
		Vector2 result = new Vector2(0,0);
		//int width = (int)floorLayer.getWidth();
    	//int height = (int)floorLayer.getHeight();
		
		if (x-y > 0) {
			result.add(x*32+(y-1)*32, (x-y)*16);
		} else if (x-y < 0) {
			result.add(x*32+(y-1)*32, (y-x)*(-16));
		} else {
			result.add(x*64-32, 0);
		}
		
		return result;
	}

	public void addObject(MyPair<MyPair<Integer, Integer>, String> object, int x, int y) {
		objectList.add(object);
		switch(id) {
		case 11:	//place a bulb
			objects[22-y][x] = 25;
			break;
		case 13:	//place a boat
			objects[22-y][x] = 26;
			break;
		default:
		}
	}
	
	public void delObject(MyPair<MyPair<Integer, Integer>, String> object, int x, int y) {
		if (objectList.del(object)) {
			objects[22-y][x] = 0;
		}
	}
	
	public int[][] getFloor() {
		return floor;
	}
	
	public int[][] getObjects() {
		return objects;
	}
	
	//use to create XML file in menu
	public String getModeType() {
		return modeType;
	}
	
	public Integer getID() {
		return id;
	}
	
	public void setID(Integer newID) {
		id = newID;
	}
	
	public int getBobPosY() {
		if (bobPos != null) {
			return (int)bobPos.key()-1;
		}
		return 0;
	}
	
	public int getBobPosX() {
		if (bobPos != null) {
			return (int)bobPos.value()-1;
		}
		return 0;
	}
	
	public void setBobPos(int x, int y) {
		if(x==1 && y==1) {
			bobPos = null;
			return;
		}
		
		if (bobPos == null) {
			bobPos = new MyPair<Integer, Integer>(x, y);
		} else {
			bobPos.setKey(x);
			bobPos.setValue(y);
		}
	}

	public void setModeType(String mode) {
		if (!mode.equals("")) {
			modeType = mode;
		}
	}
	
	private void hideUIAndMap() {
		UIGroup.setVisible(false);
		mapEditor.setVisible(false);
	}
	
	public void showUIAndMap() {
		if(UIGroup!=null) {UIGroup.setVisible(true);}; 
    	if(mapEditor!=null) {mapEditor.setVisible(true);};
	}

	public void clearDrawing() {
		for (int[] row: floor) { Arrays.fill(row, 9);}
		setUpLayer(floor);
        for (int[] row: objects){ Arrays.fill(row, 0);}
        objectList.clear();
        bobPos = null;
	}
	
	public void setUpFloor() {
		setUpLayer(floor);
	}
	
	public void setUpLayer(int[][] layer) {
		int upB = 12;
		int lowB = 11;
		for(int j=0; j<layer.length; j++) {
			if (j!=0 && j!=22) {
				if (j==11) {
					for( int k=0; k<layer.length; k++) {
						if (k>21 || k<1) {
							layer[j][k]=0;
						}
					}
				}
					
				if (j<11) {
					for (int k=0; k<layer.length; k++) {
						if (k>=upB+j || k<=lowB-j) {
							layer[j][k]=0;
						}
					}
				}
				
				if (j>11) {
					for (int k=0; k<layer.length; k++) {
						if (k>=upB+j-(j-upB+1)*2-1 || k<Math.abs(lowB-j)) {
							layer[j][k]=0;
						}
					}
				}
			} else {
				for (int k=0; k<layer.length; k++) {
					layer[j][k]=0;
				}
			}
		}
	}
	
	public void loadMapEditor(MapEditor mapEditor) {
		this.mapEditor = mapEditor;
	}

	public void prepSave() {
		for(int j=0; j<floor.length; j++) {
			for (int k=0; k<floor.length; k++) {
				if (floor[j][k] == 0) {
					floor[j][k] = 9;
				}
			}
		}
		loadRuleBlocks();
	}

	public void setUpUI(Group editorGroup, TextField textField, Label label) {
		UIGroup = editorGroup;
		this.textField = textField;
		this.label = label;
	}

	public void removeAllBubls() {
		objectList.clear();
	}

	public void setFieldText(String name) {
		if (name != null) {
			textField.setText(name);
		} 
	}

	public void clearRulesInArray() {
		rulesArray = new Block[8][14];
		inputsManager.resetRules();
	}

	public void labelCM() {
		switch(modeType) {
    	case "READ":
    		label.setBounds(1480, 590, 50, 50);
    		break;
    	case "WRITE":
    		label.setBounds(1480, 650, 50, 50);
    		break;
    	case "MACRO":
    		label.setBounds(1480, 530, 50, 50);
    		break;
    	default:
    		label.setVisible(false);	
    	}
    	label.setVisible(true);
	}
	
	////////////////  -----------Rule Page-------------- //////////////////
	//////////////////////////////////////////////////////////////
	
	/*public void setUpGameController(GameController gameController) {
		this.gameController = gameController;
	}*/

	public boolean isOnwhichPage(int i) {
		return inputsManager.isOnwhichPage(i);
	}

	public void nextRulePage() {
		inputsManager.nextPage(false);
	}

	public void prevRulePage() {
		inputsManager.prevPage(false);
	}
	
}

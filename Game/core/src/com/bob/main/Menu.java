package com.bob.main;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.XmlWriter;
import com.bob.game.EditorController;
import com.bob.game.GameController;
import com.bob.game.inputs.Block;
import com.bob.game.inputs.Type;
import com.bob.game.levels.*;
import com.bob.mapEditor.idManager;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.util.*;
import java.util.List;

public class Menu {

    private final Group menuGroup = new Group();
    private final Group modeGroup = new Group();
    private final Group settingsGroup = new Group();
    private final Group levelsGroup = new Group();
    //private final Group storeGroup = new Group();
    private final Group editorGroup = new Group();
    private boolean isVisible = true;
    private Level levelSelected;
    private boolean isEditting = false;
    private final EditorController editorController;
    private final GameController gameController;
    //private idManager manager;
    
    private Label label;
    private String defaultFileName = "DEF";
    private String customFileName = defaultFileName;
    private TextField textField; //editor
    
    public Menu(Skin skin, idManager manager, EditorController editorController, GameController gameController) {
        initMenu(skin);
        initLevels(skin);
        initMode(skin);
        initSettings(skin);
        initEditor(skin, manager);
        //initStore(skin);
        
        //this.manager = manager;
        this.editorController = editorController;
        this.gameController = gameController;
    }

    private void initMenu(final Skin skin) {
        // Bkg
        Image menuBkg = new Image(TextureFactory.createTexture("screens/menu.png"));
        menuBkg.setBounds(0,0, 1920, 1080);
        menuGroup.addActor(menuBkg);

        // Menu button
        String[] menu = {"PLAY", "LEVELS", "SETTINGS", "EDITOR", "QUIT"};

        Map<String, Button> buttons = addButtons(menuGroup, skin, menu);

        buttons.get("PLAY").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
            	initAllLevelButtons(skin);
                modeGroup.setVisible(true);
            }
        });

        buttons.get("QUIT").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                Gdx.app.exit();
            }
        });

        buttons.get("LEVELS").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                levelsGroup.clear();
                initLevels(skin);
                levelsGroup.setVisible(true);
            }
        });

        buttons.get("SETTINGS").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                settingsGroup.setVisible(true);
            }
        });
        
        buttons.get("EDITOR").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
            	hide();
            	editorGroup.setVisible(true);
            	label.setVisible(false);
            	addTextField();
            	editorController.show();
            	isEditting = true;
            }
        });
    }

    private Map<String,Button> addButtons(Group group, Skin skin, String[] names) {
        Map<String, Button> buttons = new HashMap<>();
        int menuButtonY = 460;

        for (String buttonName : names) {

            TextButton button = new TextButton(buttonName, skin, "big_grey_button");
            button.setBounds(760, menuButtonY, 400, 100);

            buttons.put(buttonName, button);
            group.addActor(button);

            menuButtonY -= 105;
        }

        return buttons;
    }
    
    /*private void initStore(Skin skin) {
    	Image foreground = new Image(TextureFactory.createTexture("screens/store.jpg"));
        foreground.setBounds(0, 0, 800, 800);
        storeGroup.addActor(foreground);
        
        addBackButton(skin, storeGroup);
        storeGroup.setVisible(false);
    }*/
    
    private void initEditor(final Skin skin, final idManager manager) {
    	iniUIStuff(skin);
    	addEditorButtons(skin, manager);
    	editorGroup.setVisible(false);
    	
    	manager.setUpUI(editorGroup, textField, label);
    }
    
    private void saveMap(idManager manager) {
    	try {
    		FileHandle file = Gdx.files.local("levels/custom/" + customFileName + ".xml");
    		Writer writer = file.writer(false);
    		XmlWriter xml = new XmlWriter(writer);
    		String mode = manager.getModeType();
    		
    		xml.element("root")
    		.attribute("type", mode)		//Game Modes
    			.element("bob")				//bob's starting position
    				.attribute("x", manager.getBobPosX())  
    				.attribute("y", manager.getBobPosY())
    		.pop();
    		
    		xml.element("text", "This is a custom map"); //Level Instruction: TODO
    		xml.element("hints")			//Level Hints
    			.element("hint", "Testing")
    		.pop();
    		
    		//only write mode has inputs
    		if (mode.equals("WRITE")) {
    			xml.element("inputs");
    			for (Block b:Block.values()) {
    				if (b.getType() == Type.OTHER) continue;
    				
    				String imageN = b.getImageName();
    				xml.element("block")
    					.attribute("name", imageN)
    					.pop();
    			}
    			xml.pop();
    		}
    		
    		//Write&&Macro mode:
    		if (mode.equals("WRITE") || mode.equals("MACRO")) {
    			xml.element("rules")
    				.attribute("available", "8")
    				.pop();
    		} else {   //Else Read mode
    			List<ArrayList<Block>> ruleBlocks = manager.getRuleBlocks();
    			XmlWriter rules = xml.element("rules");
    				if (ruleBlocks != null) {
    					for (ArrayList<Block> l : ruleBlocks) {
    						XmlWriter rule = rules.element("rule");
    						for(Block b : l) {
    							if (b != null) {
    								String attrValue = b.getImageName();
    								rule.element("block")
    									.attribute("name", attrValue)
    									.pop();
    							}    
    						}
    						rule.pop();
    					}
    				} else {
    					for (int j=0; j<8; j++) {
    						XmlWriter rule = rules.element("rule");
    						rule.element("block")
								.attribute("name", "")
								.pop();
    						rule.pop();
    					}
    				}
    			rules.pop();
    		}
    		
    		int[][] floor = manager.getFloor();
    	    int[][] objects = manager.getObjects();
            StringWriter f = new StringWriter();
            StringWriter o = new StringWriter();
            //generate floor&&objects string
            for (int i = 0; i < floor.length; i++) {
                for (int j = 0; j < floor[i].length; j++) {
                	f.append(String.valueOf(floor[i][j])+",");
                	int obID = objects[i][j];
                	// If not in macro mode, bulbs are not allowed!!
                	if (obID == 25 && !manager.getModeType().equals("MACRO")) {
                		obID = 0;
                	}
                	o.append(String.valueOf(obID)+",");
                }
                f.append("\n");
                o.append("\n");
            }
    		xml.element("floor", f.toString());
    		xml.element("object", o.toString());
    		o.close();
    		f.close();
    			
    		xml.close();
    		writer.close();
    	} catch (IOException e) {
    	}
    }
    
    private boolean equalThenReplace (List<Level> list, String s) {
    	for(Level l : list) {
    		if (l.getFileName().equals(s)) {
    			int index = list.indexOf(l);
    			list.remove(index);
    			Level newLevel = LevelFactory.loadInternalLevel("levels/custom/"+s+".xml");
    			newLevel.setFileName(s);
    			list.add(index, newLevel);
    			return true;
    		}
    	}
    	return false;
    }
    
    private void iniUIStuff(Skin skin) {
    	TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("impact_small");
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.cursor = new Image(TextureFactory.createTexture("buttons/cursor.png")).getDrawable();

        textField = new TextField("Map Name", textFieldStyle);
        textField.setBounds(1540, 470, 300, 50);
        editorGroup.addActor(textField);
        
        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = skin.getFont("impact");
        label = new Label("=>", titleStyle);
        label.setAlignment(Align.right);
        //label.setBounds(x-50, y, 40, 40);
        label.setVisible(false);
        editorGroup.addActor(label);
    }
  
    private void addEditorButtons(final Skin skin, final idManager manager) {
    	
    	// ************************ controls button *********************** \\
    	int controlY = 50;
    	TextButton button = new TextButton("SAVE", skin, "green_button");
    	button.setBounds(1700, controlY, 200, 70);
    	button.addListener(new ClickListener() {
        	
        	@Override
            public void clicked(InputEvent ie, float x, float y) {
        		customFileName = textField.getText(); //TODO: Check Valid Name
        		if (customFileName.equals("") || customFileName.length()>4) {
        			customFileName = defaultFileName;
        		}
        		manager.prepSave();
            	saveMap(manager);
            	//if same name applied REPLACE it, otherwise add a new level
            	if (!equalThenReplace(LevelFactory.CUSTOM, customFileName)) {
            		LevelFactory.addNewCustomLevel(customFileName);
            	}
            }
        	
        });
    	editorGroup.addActor(button);
        
        button = new TextButton("BACK", skin, "blue_button");
        button.setBounds(50, controlY, 200, 70);
        button.addListener(new ClickListener() {
        	
        	@Override
            public void clicked(InputEvent ie, float x, float y) {
                manager.resetRules();
                editorGroup.setVisible(false);
                editorController.resetMap();
                editorController.hide();
                isEditting = false;
                show();
            }
        	
        });
        editorGroup.addActor(button);
        
        button = new TextButton("TRY", skin, "orange_button");
        button.setBounds(1470, controlY, 200, 70);
        button.addListener(new ClickListener() {
        	
        	@Override
            public void clicked(InputEvent ie, float x, float y) {
        		customFileName = textField.getText(); //TODO: Check Valid Name
        		if (customFileName.equals("") || customFileName.length()>4) {
        			customFileName = defaultFileName;
        		}
        		manager.prepSave();
            	saveMap(manager);
            	//if same name applied REPLACE it, otherwise add a new level
            	if (!equalThenReplace(LevelFactory.CUSTOM, customFileName)) {
            		Level l = LevelFactory.addNewCustomLevel(customFileName);
            		levelSelected = l;
            	} else {
            		for(Level l : LevelFactory.CUSTOM) {
                		if (l.getFileName().equals(customFileName)) {
                			levelSelected = l;
                			break;
                		}
            		}
            	}
            	modeGroup.setVisible(false);
            	editorController.hide();
            	editorController.setTrying(true);
            	editorGroup.setVisible(false);
            }
        	
        });
        editorGroup.addActor(button);
        // ***************************************************************** \\
        
        button = new TextButton("Load Existing Map", skin, "grey_button");
        button.setBounds(1470, 135, 430, 80);
        button.addListener(new ClickListener() {
        	
        	@Override
            public void clicked(InputEvent ie, float x, float y) {
        		manager.displayLoadingL(skin);
            }
        	
        });
        editorGroup.addActor(button);
        
        int modeW = 200;
        int modeH = 50;
        button = new TextButton("Write", skin, "blue_button");
        button.setBounds(1540, 650, modeW, modeH);
        button.addListener(new ClickListener() {
        	
        	@Override
            public void clicked(InputEvent ie, float x, float y) {
            	manager.setModeType("WRITE");
            	labelSelectedMode(manager);
            }
        	
        });
        editorGroup.addActor(button);
        
        button = new TextButton("Read", skin, "green_button");
        button.setBounds(1540, 590, modeW, modeH);
        button.addListener(new ClickListener() {
        	
        	@Override
            public void clicked(InputEvent ie, float x, float y) {
            	manager.setModeType("READ");
            	labelSelectedMode(manager);
            }
        	
        });
        editorGroup.addActor(button);
        
        final TextButton Rules = new TextButton("Rules", skin, "grey_button") {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                this.setDisabled(!manager.getModeType().equals("READ"));
                super.draw(batch, parentAlpha);
            }
        };
        Rules.setBounds(1540+205, 590, 120, modeH);
        Rules.addListener(new ClickListener() {
        	
        	@Override
            public void clicked(InputEvent ie, float x, float y) {
        		if (!Rules.isDisabled()) {
        			manager.displayReadL();
        		}
            }
        	
        });
        editorGroup.addActor(Rules);
        
        button = new TextButton("Macro", skin, "orange_button");
        button.setBounds(1540, 530, modeW, modeH);
        button.addListener(new ClickListener() {
        	
        	@Override
            public void clicked(InputEvent ie, float x, float y) {
            	manager.setModeType("MACRO");
            	labelSelectedMode(manager);
            }
        	
        });
        editorGroup.addActor(button);

    }
    
    private void addTextField() {
    	textField.setText("Map Name");
        textField.setVisible(true);
    }
    
    private void labelSelectedMode(idManager manager) {
    	manager.labelCM();
    }

    private void initMode(Skin skin) {

        Image levelsBkg = new Image(TextureFactory.createTexture("screens/menu.png"));
        levelsBkg.setBounds(0,0, 1920, 1080);

        modeGroup.addActor(levelsBkg);

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = skin.getFont("impact");
        Label startLabel = new Label("< Start here!", titleStyle);
        startLabel.setBounds(1185, 455, 400, 100);
        modeGroup.addActor(startLabel);

        String[] menu = {"WRITER", "READER", "MACRO"};
        Map<String, Button> buttons = addButtons(modeGroup, skin, menu);

        buttons.get("WRITER").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                int lvlIndex = 0;

                if (Config.levelsAreLocked) {
                    Preferences prefs = Gdx.app.getPreferences("Progress");
                    lvlIndex = prefs.getInteger("writeProgress", -1) + 1;
                }

                launchLevel(lvlIndex < LevelFactory.WRITE.size() ? LevelFactory.WRITE.get(lvlIndex) : LevelFactory.WRITE.get(0));
            }
        });

        buttons.get("READER").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                int lvlIndex = 0;

                if (Config.levelsAreLocked) {
                    Preferences prefs = Gdx.app.getPreferences("Progress");
                    lvlIndex = prefs.getInteger("readProgress", -1) + 1;
                }

                launchLevel(lvlIndex < LevelFactory.READ.size() ? LevelFactory.READ.get(lvlIndex) : LevelFactory.READ.get(0));
            }
        });

        buttons.get("MACRO").addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                int lvlIndex = 0;

                if (Config.levelsAreLocked) {
                    Preferences prefs = Gdx.app.getPreferences("Progress");
                    lvlIndex = prefs.getInteger("macroProgress", -1) + 1;
                }

                launchLevel(lvlIndex < LevelFactory.MACRO.size() ? LevelFactory.MACRO.get(lvlIndex) : LevelFactory.MACRO.get(0));
            }
        });

        addBackButton(skin, modeGroup);

        modeGroup.setVisible(false);
    }

    private void initSettings(Skin skin) {
        // Bkg
        Image menuBkg = new Image(TextureFactory.createTexture("screens/menu.png"));
        menuBkg.setBounds(0,0, 1920, 1080);
        settingsGroup.addActor(menuBkg);

        TextButton button = new TextButton("RESET LEVELS", skin, "grey_button");
        button.setBounds(810, 430, 300, 100);

        button.addListener(
                new ClickListener() {
                   public void clicked(InputEvent ie, float x, float y) {
                       Preferences prefs = Gdx.app.getPreferences("Progress");
                       prefs.putInteger("writeProgress", -1);
                       prefs.putInteger("readProgress", -1);
                       prefs.putInteger("macroProgress", -1);
                   }
               }
        );
        settingsGroup.addActor(button);

        addBackButton(skin, settingsGroup);

        settingsGroup.setVisible(false);
    }

    private void launchLevel(Level level) {
    	gameController.show();
        levelSelected = level;
        hide();
    }

    private void initLevels(Skin skin) {
        // Levels Menu
        Image levelsBkg = new Image(TextureFactory.createTexture("screens/menu.png"));
        levelsBkg.setBounds(0,0, 1920, 1080);
        levelsGroup.addActor(levelsBkg);

        /*addLevelButtons(skin, LevelFactory.WRITE, "writeProgress", "Write", 430);
        addLevelButtons(skin, LevelFactory.READ, "readProgress", "Read", 300);
        addLevelButtons(skin, LevelFactory.MACRO, "macroProgress", "Macro", 170);
        addLevelButtons(skin, LevelFactory.CUSTOM, "customCollection", "Custom", 40);*/
        
        initAllLevelButtons(skin);

        addBackButton(skin, levelsGroup);

        levelsGroup.setVisible(false);
    }
    
    private void initAllLevelButtons(Skin skin) {
    	addLevelButtons(skin, LevelFactory.WRITE, "writeProgress", "Write", 430);
        addLevelButtons(skin, LevelFactory.READ, "readProgress", "Read", 300);
        addLevelButtons(skin, LevelFactory.MACRO, "macroProgress", "Macro", 170);
        addLevelButtons(skin, LevelFactory.CUSTOM, "customCollection", "Custom", 40);
    }

    private void addLevelButtons(Skin skin, final List<Level> levels, String prefString, String title, int startY) {
        int noLevels = levels.size();
        int levelsButtonX = 660;
        int levelsButtonY = startY;

        //Texture lockTexture = TextureFactory.createTexture("buttons/lock.png");

        Label.LabelStyle titleStyle = new Label.LabelStyle();
        titleStyle.font = skin.getFont("impact");
        Label titleLabel = new Label(title, titleStyle);
        titleLabel.setAlignment(Align.right);
        titleLabel.setBounds(0, startY, 640, 100);

        levelsGroup.addActor(titleLabel);

        //Preferences prefs = Gdx.app.getPreferences("Progress");
        //int unlocked = prefs.getInteger(prefString, -1);

        for (int i = 0; i < noLevels; i++) {
            final int j = i;
            Level level = levels.get(i);
            TextButton button;
            switch (level.getGameMode()) {
            case "WRITE": //blue
            	button = new TextButton(level.getFileName(), skin, "blue_button");
            	break;
            case "READ":  //green
            	button = new TextButton(level.getFileName(), skin, "green_button");
            	break;
            case "MACRO": //orange
            	button = new TextButton(level.getFileName(), skin, "orange_button");
            	break;
            default:
            	button = new TextButton(level.getFileName(), skin, "grey_square_button");
            	break;
            }
            
            button.setBounds(levelsButtonX, levelsButtonY, 100, 100);

            button.addListener(new ClickListener() {
                public void clicked(InputEvent ie, float x, float y) {
                    launchLevel(levels.get(j));
                }
            });

            levelsGroup.addActor(button);

            // Disable if not unlocked
            /*if (i > unlocked + 1 && Config.levelsAreLocked) {
                button.setDisabled(true);
                Image lock = new Image(lockTexture);
                lock.setBounds(levelsButtonX - 14, levelsButtonY - 14, 128, 128);
                levelsGroup.addActor(lock);
            }*/

            levelsButtonX += 125;

            if (levelsButtonX >= 1610) {
                levelsButtonY -= 125;
                levelsButtonX = 660;
            }
        }

    }

    private void addBackButton(Skin skin, final Group group) {
        TextButton backButton = new TextButton("BACK", skin, "grey_button");
        backButton.setBounds(10, 15, 200, 60);
        backButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                group.setVisible(false);
            }
        });

        group.addActor(backButton);
    }
    
    public void showEditor() {
    	editorGroup.setVisible(true);
    }
    
    public void showLevels() {
    	levelsGroup.setVisible(true);
    }

    public void show() {
        menuGroup.setVisible(true);
        isVisible = true;
    }

    public void hide() {
        menuGroup.setVisible(false);
        modeGroup.setVisible(false);
        levelsGroup.setVisible(false);
        settingsGroup.setVisible(false);
        editorGroup.setVisible(false);
        //storeGroup.setVisible(false);
        isVisible = false;
    }

    public boolean isVisible() {
        return isVisible;
    }
    
    public boolean isEditting() {
    	return isEditting;
    }

    public Level getLevelSelected() {
        return levelSelected;
    }

    public void setStage(Stage stage) {
        stage.addActor(menuGroup);
        stage.addActor(levelsGroup);
        stage.addActor(modeGroup);
        stage.addActor(settingsGroup);
        stage.addActor(editorGroup);
        //stage.addActor(storeGroup);
    }
}

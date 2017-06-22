package com.bob.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.main.TextureFactory;

class ControlsLayer extends Layer {

    private final Button submitButton;
    private final Button resetButton;
    private final Button hintButton;
    
    private final TextButton lpsButton;
    private final TextButton causalButton;
    private final TextButton macroButton;
    
    private final Slider slider;
    private boolean pressed = false;
    private Group buttonGroup = new Group();

    public ControlsLayer(final Skin skin, final GameController controller) {
        TextButton quitButton = new TextButton("BACK", skin, "blue_button");
        quitButton.setBounds(10, 10, 200, 60);
        quitButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                controller.hide();
                group.removeActor(buttonGroup);
                slider.setValue(2);
            }
        });
        group.addActor(quitButton);

        // ----------
        // SUBMIT BUTTON
        submitButton = new TextButton("SUBMIT", skin, "green_button") {
            @Override
            public void draw(Batch batch, float parentAlpha) {
            	if(!(controller.isRulesValid() && controller.hasGoldTile())) {
            		this.setDisabled(true);
            	} else {
            		this.setDisabled(false);
            	}
                super.draw(batch, parentAlpha);
            }
        };
        submitButton.setBounds(1700, 10, 200, 60);
        submitButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                if (!submitButton.isDisabled()) {
                    controller.submit();
                }
            }
        });
        group.addActor(submitButton);

        // ----------
        // HINTS BUTTON
        hintButton = new TextButton("HINTS", skin, "grey_button");
        hintButton.setBounds(1110, 930, 200, 60);
        hintButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                if (!hintButton.isDisabled()) {
                    controller.displayHints();
                }
            }
        });
        group.addActor(hintButton);
        
        // ----------
        // example BUTTON
        TextButton exampleButton = new TextButton("EXAMPLES", skin, "rock_button");
        exampleButton.setBounds(880, 930, 220, 60);
        exampleButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                if (!exampleButton.isDisabled()) {
                    controller.displayExamples();
                }
            }
        });
        group.addActor(exampleButton);

        // ----------
        // HELPS BUTTON
        final TextButton helpButton = new TextButton("?", skin, "grey_button");
        helpButton.setBounds(1318, 931, 50, 58);
        helpButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                if (!helpButton.isDisabled()) {
                    controller.displayHelp();
                }
            }
        });
        group.addActor(helpButton);

        // ----------
        // LPS BUTTON
        lpsButton = new TextButton("LPS Rules", skin, "blue_button") {
            @Override
            public void draw(Batch batch, float parentAlpha) {
            	this.setDisabled(!controller.isRulesValid());
                super.draw(batch, parentAlpha);
            }
        };
        lpsButton.setBounds(515, 10, 240, 60);
        lpsButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                if (!lpsButton.isDisabled()) {
                	controller.displayLPS();
                }
            }
        });
        group.addActor(lpsButton);
        
        causalButton = new TextButton("Causal Theory", skin, "blue_button");
        causalButton.setBounds(765, 10, 300, 60);
        causalButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                if (!causalButton.isDisabled()) {
                	controller.displayCausal();
                }
            }
        });
        group.addActor(causalButton);
        
        
        macroButton = new TextButton("Macro", skin, "blue_button") {
            @Override
            public void draw(Batch batch, float parentAlpha) {
            	this.setDisabled(!(controller.isRulesValid() && controller.noOfRules()>0));
                super.draw(batch, parentAlpha);
            }
        };
        macroButton.setBounds(1075, 10, 170, 60);
        macroButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                if (!macroButton.isDisabled()) {
                	addMacroButtons(skin, controller);
                } 
            }
        });
        group.addActor(macroButton);
        // ----------
        

        // RESET BUTTON
        resetButton = new TextButton("RESET ALL", skin, "orange_button");
        resetButton.setBounds(1260, 10, 210, 60);
        resetButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
            	resetMacros();
                controller.reset();
            }
        });

        group.addActor(resetButton);
        // ----------

        // RESET BOB
        TextButton resetBobButton = new TextButton("RESET BOB", skin, "yellow_button");
        resetBobButton.setBounds(1480, 10, 210, 60);
        resetBobButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                controller.resetWorld();
            }
        });

        group.addActor(resetBobButton);
        // ----------

        // SLIDER
        int xSlider = 300;
        BitmapFont font = new BitmapFont(Gdx.files.internal("font/impact.fnt"));
        font.getData().scale(-0.6f);
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = font;
        Label speedLabel = new Label("Speed", labelStyle);
        speedLabel.setBounds(xSlider, 50, 100, 25);
        group.addActor(speedLabel);

        Image turtle = new Image(TextureFactory.createTexture("buttons/turtle.png"));
        turtle.setBounds(xSlider -10, 10, 35, 25);
        group.addActor(turtle);

        Image rabbit = new Image(TextureFactory.createTexture("buttons/rabbit.png"));
        rabbit.setBounds(xSlider + 175, 10, 35, 25);
        group.addActor(rabbit);

        skin.add("slider_bkg", TextureFactory.createTexture("buttons/slider_bkg.png"));
        skin.add("slider_knob", TextureFactory.createTexture("buttons/slider_knob.png"));
        Slider.SliderStyle sliderStyle = new Slider.SliderStyle();
        sliderStyle.knob = skin.getDrawable("slider_knob");
        sliderStyle.background = skin.getDrawable("slider_bkg");
        slider = new Slider(0, 4, 0.01f, false, sliderStyle);
        slider.setBounds(xSlider, 30, 200, 25);
        slider.setValue(2);

        slider.addListener(new ChangeListener() {
            public void changed (ChangeEvent event, Actor actor) {
                controller.updateSpeed(slider.getValue());
            }
        });

        group.addActor(slider);

        // Pause
        TextButton pauseButton = new TextButton("||", skin, "grey_button");
        pauseButton.setBounds(220, 20, 33, 47);
        pauseButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                slider.setValue(0);
            }
        });

        group.addActor(pauseButton);

        // Play
        TextButton playButton = new TextButton(">", skin, "grey_button");
        playButton.setBounds(260, 20, 33, 47);
        playButton.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
                slider.setValue(2);
            }
        });
        group.addActor(playButton);
        
        // --------------------- rule page controls ------------------------ \\
        ///////////////////////////////////////////////////////////////////////
        
        final TextButton nextRulePage = new TextButton(">", skin, "green_square_button") {
            @Override
            public void draw(Batch batch, float parentAlpha) {
            	this.setDisabled(controller.isOnwhichPage(1));
                super.draw(batch, parentAlpha);
            }
        };
        nextRulePage.setBounds(1755, 645, 38, 42);
        nextRulePage.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
            	if(!nextRulePage.isDisabled()) {
            		controller.nextRulePage();
            	}
            }
        });
        group.addActor(nextRulePage);
        
        final TextButton prevRulePage = new TextButton("<", skin, "green_square_button") {
            @Override
            public void draw(Batch batch, float parentAlpha) {
            	this.setDisabled(controller.isOnwhichPage(0));
                super.draw(batch, parentAlpha);
            }
        };
        prevRulePage.setBounds(1512, 645, 38, 42);
        prevRulePage.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
            	if(!prevRulePage.isDisabled()) {
            		controller.prevRulePage();
            	}
            }
        });
        group.addActor(prevRulePage);
        
        // --------------------------------------------------------------------//
        /////////////////////////////////////////////////////////////////////////
    }
    
    // add macro buttons
    private void addMacroButtons(Skin skin, final GameController controller) {
    	group.removeActor(buttonGroup);
    	if (pressed) { 
    		pressed = !pressed;
    		return; 
    	}
    	
    	int x = 1075;
    	int y = 80;
    	int height = 60;
    	int width = 170;
    	int noRules = controller.noOfRules();
    	for (int i=0; i<noRules; i++) {
    		final int index = i;
    		TextButton button = new TextButton("Rule"+(i+1), skin, "grey_button");
            button.setBounds(x, y, width, height);
            button.addListener(new ClickListener() {
            	@Override
                public void clicked(InputEvent ie, float x, float y) {
            		controller.displayMacroRules(index);
                }
            });
            buttonGroup.addActor(button);
            y+=(height+5);
    	}
    	
    	group.addActor(buttonGroup);
    	pressed = !pressed;
    }

    public void setSpeedFactor(float factor) {
    	slider.setValue(factor);
    }
    
    public void disableSubmit(boolean disabled) {
        this.submitButton.setDisabled(disabled);
    }

    public void disableReset(boolean disabled) {
        this.resetButton.setVisible(disabled);
    }

    public void disableHints(boolean disabled) {
        this.hintButton.setVisible(disabled);
    }
    
    public void resetMacros() {
    	//buttonGroup.setVisible(false);
    	group.removeActor(buttonGroup);
        pressed = false;
    }
    
    public void disableLPS(boolean disabled) {
        this.lpsButton.setVisible(disabled);
        this.causalButton.setVisible(disabled);
        group.removeActor(buttonGroup);
        pressed = false;
    }
    
}

package com.bob.mapEditor;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.game.inputs.InputsLayer;
import com.bob.main.TextureFactory;

public class ReadLayer extends InputsLayer {

    public ReadLayer(Skin skin) {
        super();
        this.skin = skin;
        //initialVisibility = false;

        addActor(new Image(TextureFactory.createTexture("screens/macro_modal_2.png")));
    }
    
    public void addButtons(final Skin skin, final idManager manager) {
    	//final ReadLayer layer = this;
    	
        // Submit modal button
        final TextButton submit = new TextButton("Submit", skin, "green_button") {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                this.setDisabled(!manager.checkAllRules());
                super.draw(batch, parentAlpha);
            }
        };
        submit.setBounds(1160, 860, 200, 80);
        submit.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	if (!submit.isDisabled()) {
            		manager.loadRuleBlocks();
            		manager.quitReadLayer();
            		setVisibility(false);
            	}
            };
        });
        addActor(submit);

        // Cancel modal button
        TextButton button = new TextButton("Cancel", skin, "blue_button");
        button.setBounds(1160, 950, 200, 80);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
            	manager.clearRulesInArray();
            	manager.quitReadLayer();
            	setVisibility(false);
            };
        });
        addActor(button);

        /////////////--------------------------------------------------////////////
        //////////////////////////////////////////////////////////////////////////
        
        final TextButton nextRulePage = new TextButton(">", skin, "green_button") {
            @Override
            public void draw(Batch batch, float parentAlpha) {
            	this.setDisabled(manager.isOnwhichPage(1));
                super.draw(batch, parentAlpha);
            }
        };
        nextRulePage.setBounds(1740, 600, 30, 35);
        nextRulePage.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
            	if(!nextRulePage.isDisabled()) {
            		manager.nextRulePage();
            	}
            }
        });
        group.addActor(nextRulePage);
        
        final TextButton prevRulePage = new TextButton("<", skin, "green_button") {
            @Override
            public void draw(Batch batch, float parentAlpha) {
            	this.setDisabled(manager.isOnwhichPage(0));
                super.draw(batch, parentAlpha);
            }
        };
        prevRulePage.setBounds(1500, 600, 30, 35);
        prevRulePage.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
            	if(!prevRulePage.isDisabled()) {
            		manager.prevRulePage();
            	}
            }
        });
        group.addActor(prevRulePage);
    }
    
}
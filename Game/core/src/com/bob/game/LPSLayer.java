package com.bob.game;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.main.TextureFactory;

public class LPSLayer extends Layer {
	
	protected Skin skin;
	private BitmapFont font;
	private boolean visible = false;
	
	public LPSLayer(Skin skin) {
		this.skin = skin;
		initialVisibility = false;
		font = skin.getFont("val");
		
		addActor(new Image(TextureFactory.createTexture("screens/loading.png")));
		
		Actor actor = new Actor();
		actor.setBounds(0, 0, 1920, 1080);
		actor.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
            	group.setVisible(false);
            	visible = false;
            }
        });
		group.addActor(actor);
		
		/*TextButton button = new TextButton("BACK", skin, "blue_button");
		button.addListener(new ClickListener() {
	           @Override
	           public void clicked(InputEvent event, float x, float y) {
	        	   group.setVisible(false);
	        	   visible = false;
	           };
	    });
		button.setBounds(10, 10, 200, 60);
		addActor(button);*/
	}
	
	public void DisplayLPSRules(SpriteBatch batch, ArrayList<String> rules) {
		int length = rules.size();
		int colHeight = 216;
		
		if(length > 0) {
			batch.begin();
			for (int i=0; i<length; i++) {
				String rule = rules.get(i);
				rule = "IsIn(X,Y) & " + rule;
				font.draw(batch, rule, 216, 940-i*colHeight, 1425, 100, true);
			}
			batch.end();
		}
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisibe(boolean b) {
		visible = b;
	}

}

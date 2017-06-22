package com.bob.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.main.TextureFactory;

public class ExampleLayer extends Layer {
	
	public ExampleLayer() {
		initialVisibility = false;
		Image foreground = new Image(TextureFactory.createTexture("screens/examples.png"));
		group.addActor(foreground);
		
		Actor actor = new Actor();
		actor.setBounds(0, 0, 1920, 1080);
		actor.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
            	group.setVisible(false);
            }
        });
		group.addActor(actor);
		
	}
	
}

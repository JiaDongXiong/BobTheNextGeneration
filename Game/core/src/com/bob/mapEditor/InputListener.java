package com.bob.mapEditor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class InputListener extends ClickListener {
	
	private Actor actor;
    
    public InputListener(Actor actor) {
        this.actor = actor;
    }
    
    @Override
    public void clicked(InputEvent event, float x, float y) {
    	System.out.println("Clicked");
    }

}

package com.bob.mapEditor;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class UIListener extends ClickListener {

	private UIActor actor;
	public idManager m;
	
	public UIListener(UIActor actor, idManager m) {
		this.actor = actor;
		this.m = m;
	}
	
	@Override
    public void clicked(InputEvent event, float x, float y) {
		m.setID(actor.colorID);
		//System.out.println(actor.colorID);
		
		//TODO: change image(to clicked when clicked)
	}
}

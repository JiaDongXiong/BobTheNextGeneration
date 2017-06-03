package com.bob.game;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.main.TextureFactory;

public class MacroRulesLayer extends Layer {	
	protected Skin skin;
	private BitmapFont font;
	private boolean visible = false;
	private int ruleIndex = 100; //default int:no rules have been selected
	
	public MacroRulesLayer(Skin skin) {
		this.skin = skin;
		initialVisibility = false;
		font = skin.getFont("val");
		
		addActor(new Image(TextureFactory.createTexture("screens/loading.png")));
		
		TextButton button = new TextButton("BACK", skin, "blue_button");
		button.addListener(new ClickListener() {
	           @Override
	           public void clicked(InputEvent event, float x, float y) {
	        	   group.setVisible(false);
	        	   visible = false;
	           };
	    });
		button.setBounds(10, 10, 200, 60);
		addActor(button);
	}

	public boolean isVisible() {
		return visible;
	}
	
	public void setVisibe(boolean b) {
		visible = b;
	}

	public void setUpRule(int i) {
		ruleIndex = i;
	}

	public void DisplayMacro(SpriteBatch batch, ArrayList<String> rules) {
		if (rules.size()==0) {
			return;
		}
		String ruleString = rules.get(ruleIndex);
		ruleString = macroHelper(ruleString);
		
		batch.begin();
		
		font.draw(batch, ruleString, 250, 600);
		
		batch.end();
	}
	
	private String macroHelper(String str) {
		String[] strArray;
		String direc;
		strArray = str.split(" -> ");
		direc = strArray[1];
		String res = "";
		
		if (direc.contains("moveNorth")) {
			res = "moveNorth(N) :-  isIn(X,Y) & isNext(Y,Z) & moveFromTo(X,Y,X,Z).";
		} else if (direc.contains("moveSouth")) {
			res = "moveSouth(N) :-  isIn(X,Y) & isPrevious(Y,Z) & moveFromTo(X,Y,X,Z).";
		} else if (direc.contains("moveEast")) {
			res = "moveEast(N) :-  isIn(X,Y) & isNext(X,Z) & moveFromTo(X,Y,Z,Y).";
		} else if (direc.contains("moveWest")) {
			res = "moveWest(N) :-  isIn(X,Y) & isPrevious(X,Z) & moveFromTo(X,Y,Z,Y).";
		}
		
		return res;
	}

}

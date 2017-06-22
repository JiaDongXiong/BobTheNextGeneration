package com.bob.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.game.inputs.Block;
import com.bob.main.TextureFactory;

public class TextLayer extends Layer {
	
	//textual rule
	private final TextField textField;
	
	public TextLayer(Skin skin, GameController gameController) {
		
		//initialVisibility = false;
		Image foreground = new Image(TextureFactory.createTexture("screens/textual.png"));
		group.addActor(foreground);
		
		// cancel button
		TextButton button = new TextButton("CANCEL", skin, "blue_button");
		button.addListener(new ClickListener() {
	           @Override
	           public void clicked(InputEvent event, float x, float y) {
	        	   group.setVisible(false);
	        	   clearText();
	           };
	    });
		button.setBounds(500, 110, 250, 70);
		addActor(button);
		
		// submit button
		button = new TextButton("SUBMIT", skin, "green_button");
		button.addListener(new ClickListener() {
	           @Override
	           public void clicked(InputEvent event, float x, float y) {
	        	   submit(gameController);
	        	   group.setVisible(false);
	        	   clearText();
	           };
	    });
		button.setBounds(1200, 110, 250, 70);
		addActor(button);
		
		// textual rule textfield
		TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("val");
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.cursor = new Image(TextureFactory.createTexture("buttons/cursor.png")).getDrawable();

        textField = new TextField("Enter Your Textual Rule Here", textFieldStyle);
        textField.setBounds(370, 220, 1210, 80);

        addActor(textField);
		
	}
	
	protected void submit(GameController gameController) {
		
		String rule = getText();
		char[] cArray = rule.toCharArray();
		String curString = "";
		// 14 blocks per rule slot
		Block[] blocks = new Block[14];
		int blockIndex = 0;
		
		for (char c:cArray) {
			// ignore spaces
			if (c == ' ') {
				continue;
			}
			curString += c;
			
			// block check
			Block block = blockCheck(curString);
			if (block != null) {
				blocks[blockIndex] = block;
				blockIndex++;
				curString = "";
			}
			
		}
		//TODO
		gameController.setRuleBlocks(blocks, 0);
		
	}
	
	private Block blockCheck(String s) {
		
		for (Block block:Block.values()) {
			//TODO lower case
			if (s.equals(block.getLPSString())) {
				System.out.println(block.getLPSString());
				return block;
			}
		}
		return null;
		
	}
	
	// load existing rule strings for that index
	public void initialize() {
		
	}
	
	public void clearText() {
		textField.setText("Enter Your Textual Rule Here");
	}

	public String getText() {
		return textField.getText();
	}
	
	public void setText(String text) {
        textField.setText(text);
    }

}

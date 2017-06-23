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
	private final TextField indexField;
	private String iniRule = "isIn(X,Y) & ";
	private String[] acceptedIndex = new String[]{"1","2","3","4"};  // valid rule slot index
	
	public TextLayer(Skin skin, GameController gameController) {
		
		initialVisibility = false;
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
		button.setBounds(370, 110, 250, 70);
		addActor(button);
		////////////////////////
		
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
		button.setBounds(1060, 110, 250, 70);
		addActor(button);
		////////////////////
		
		TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("val");
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.cursor = new Image(TextureFactory.createTexture("buttons/cursor.png")).getDrawable();

        // textual rule textfield
        textField = new TextField(iniRule, textFieldStyle);
        textField.setBounds(300, 218, 1075, 80);
        addActor(textField);
        
        // choose the index of the rule
        indexField = new TextField("RuleNo", textFieldStyle);
        indexField.setBounds(1500, 218, 160, 80);
        addActor(indexField);
		
	}
	
	protected void submit(GameController gameController) {
		
		String rule = getRuleText();
		char[] cArray = rule.toCharArray();
		String curString = "";
		// 14 blocks per rule slot
		Block[] blocks = new Block[14];
		int blockIndex = 0;
		
		for (char c:cArray) {
			// the first clause will be isIn(X,Y)& otherwise not valid
			if (curString.equals("isIn(X,Y)&")) {
				curString = "";
			}
			
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
		Integer ruleIndex = indexCheck();
		gameController.setRuleBlocks(blocks, ruleIndex);
		
	}
	
	private Integer indexCheck() {
		for (String index:acceptedIndex) {
			if (index.equals(indexField.getText())) {
				return Integer.valueOf(index)-1;
			}
		}
		return -1; // no rules get changed
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
	public void initialize(int i, String rule) {
		String completeRule = "isIn(X,Y) & " + rule; //for completeness of the rule
		setRuleText(completeRule);
		indexField.setText(Integer.toString(i+1));
	}
	
	public void clearText() {
		textField.setText(iniRule);
		indexField.setText("RuleNo");
	}

	public String getRuleText() {
		return textField.getText();
	}
	
	public void setRuleText(String text) {
        textField.setText(text);
    }

}

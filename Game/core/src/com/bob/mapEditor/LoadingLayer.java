package com.bob.mapEditor;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.XmlReader;
import com.badlogic.gdx.utils.XmlReader.Element;
import com.bob.game.Layer;
import com.bob.game.inputs.Block;
import com.bob.game.levels.Level;
import com.bob.game.levels.LevelFactory;
import com.bob.main.TextureFactory;

public class LoadingLayer extends Layer {
	protected Skin skin;
	
	public LoadingLayer(Skin skin) {
		this.skin = skin;
		initialVisibility = false;
	}
	
	public void addLvlButtons(final Skin skin, final idManager manager) {
		//bkg
		addActor(new Image(TextureFactory.createTexture("screens/loading.png")));
		
		TextButton button = new TextButton("Cancel", skin, "grey_button");
		button.addListener(new ClickListener() {
	           @Override
	           public void clicked(InputEvent event, float x, float y) {
	        	   manager.quiteLoadingL("");
	           };
	    });
		button.setBounds(1700, 25, 200, 50);
		addActor(button);
		
		List<Level> levels = new ArrayList<>();
		LevelFactory.loadLevelFiles("levels/custom", levels);
		
		int i = 0;
		int initX = 150;
		int initY = 840;
		for(final Level l:levels) {
			String name = l.getFileName();
			switch (l.getGameMode()) {
            case "WRITE": //blue
            	button = new TextButton(name, skin, "blue_button");
            	break;
            case "READ":  //green
            	button = new TextButton(name, skin, "green_button");
            	break;
            case "MACRO": //orange
            	button = new TextButton(name, skin, "orange_button");
            	break;
            default:
            	button = new TextButton(name, skin, "grey_square_button");
            	break;
            }
			
			button.addListener(new ClickListener() {
		           @Override
		           public void clicked(InputEvent event, float x, float y) {
		        	   manager.resetRules();
		        	   int[][] floorL = l.getFloor();
		        	   int[][] objects = l.getObjects();
		        	   String mode = l.getGameMode();
		        	   
		        	   manager.setUpLayer(floorL);
		        	   manager.setMap(floorL, objects);
		        	   manager.setBobPos(l.getY()+1, l.getX()+1);
		        	   manager.setFieldText(l.getFileName());
		        	   extractObjects(objects, manager);
		        	   if (mode.equals("READ")) {
		        		   manager.setRulesArray(extractRules(l.getXmlRoot()));
		        	   }
		        	   manager.quiteLoadingL(mode);
		           };
		    });
			int newX = initX+i*290;
			if (newX+250 > 1920) {
				initY -= 150;
				i = 0;
				button.setBounds(initX, initY, 250, 75);
			} else {
				button.setBounds(newX, initY, 250, 75);
			}
			addActor(button);
			i++;
		}
		
	}
	
	private Block[][] extractRules(Element root) {
		Block[][] results = new Block[8][7];
		Element rules = root.getChildByName("rules");
		
		for (int i=0; i<8; i++) {
			results[i] = extractBlocks(rules.getChild(i));
		}
		return results;
	}
	
	 private Block[] extractBlocks(XmlReader.Element element) {
		 if (element == null) return new Block[0];
		 Block[] res = new Block[element.getChildCount()];
		 for (int i = 0; i < res.length; i++) {
			 res[i] = Block.getBlock(element.getChild(i).getAttribute("name"));
		 }
		 return res;
	}
	
	private void extractObjects(int[][] objects, idManager manager) {
		manager.removeAllBubls();
		for (int k = 0; k < objects.length; k++) {
	           for (int j = 0; j < objects.length; j++) {
	        	   MyPair<Integer, Integer> tilePos = new MyPair<Integer, Integer>(j+1, k+1);
        		   MyPair<MyPair<Integer, Integer>, String> object = 
        				   new MyPair<MyPair<Integer, Integer>, String>(tilePos, "Bulb");
        		   
	        	   if (objects[k][j] == 25) {
	        		   manager.addObject(object,k,j);
	        	   }
	        	   if (objects[k][j] == 26) {
	        		   object.setValue("Boat");
	        		   manager.addObject(object,k,j);
	        	   }
	           }
		}
	}
	
	public void clear() {
		group.clear();
	}

}

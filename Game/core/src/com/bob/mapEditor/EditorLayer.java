package com.bob.mapEditor;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bob.game.Layer;
import com.bob.game.inputs.Block;
import com.bob.game.inputs.BlockCoordinatesGenerator;
import com.bob.game.inputs.Type;
import com.bob.main.TextureFactory;

public class EditorLayer extends Layer{
	protected final List<Image> inputs;
	protected Skin skin;
	private Block[] bList = {Block.WHITE, Block.RED, Block.YELLOW, Block.GREEN,
					Block.PURPLE, Block.ORANGE, Block.GOLD, Block.QUESTION, 
					Block.OCEAN,  Block.BOB, Block.BULB, Block.NOBULB, Block.BOAT, Block.NOBOAT};

	public EditorLayer() {
		inputs = new ArrayList<>();
	}
	
    public EditorLayer(Skin skin) {
    	this();
        this.skin = skin;
        
        BitmapFont font = new BitmapFont();
        font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        skin.add("default", new Label.LabelStyle(font, Color.WHITE));
        
        createUIInputs();
    }	
	
	private void createUIInputs() {
		 clearInputs();
		 BlockCoordinatesGenerator bcg = new BlockCoordinatesGenerator(1540, 915);

		 for (Block b: bList) {
			 int[] coord = bcg.getCoordinates(Type.FLUENT);
			 generateTexture(b.getImageName(), coord, 50, 50);
		 }
    }
	
	private void generateTexture(String textureName, int[] coord, int xb, int yb) {
		Image image = new Image(TextureFactory.createTexture("blocks/"+ textureName +".png"));
		image.setBounds(coord[0], coord[1], xb, yb);
		group.addActor(image);
		inputs.add(image);
	}

    public void clearInputs() {
        for (Image i: inputs) {
            i.clear();
        }
        inputs.clear();
    }

	public int getUINum() {
		return bList.length;
	}
}

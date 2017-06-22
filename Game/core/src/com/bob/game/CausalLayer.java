package com.bob.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.main.TextureFactory;

public class CausalLayer extends Layer {
	
	protected Skin skin;
	private BitmapFont font;
	private boolean visible = false;
	
	public CausalLayer(Skin skin) {
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
	
	public void DisplayCausalTheory(SpriteBatch batch) {
		int colHeight = 50;
		int rowCount = 1;
		
		batch.begin();
		font.draw(batch, "moveFromTo(X, Y, A, B) = (", 216, 1080-140-rowCount*colHeight); //first row
		rowCount++;
		font.setColor(Color.FOREST);
		font.draw(batch, "       Preconditions(", 216, 1080-140-rowCount*colHeight);
		rowCount++;
		font.draw(batch, "             conditions: isIn(X, Y)", 216, 1080-140-rowCount*colHeight); 
		rowCount++;
		font.draw(batch, "                                 reachable(A, B).", 216, 1080-140-rowCount*colHeight); 
		rowCount++;
		font.draw(batch, "             conflicts: moveFromTo(X, Y, U, V)", 
				216, 1080-140-rowCount*colHeight);
		rowCount++;
		font.draw(batch, "                                       if (X,Y) != (U,V)", 
				216, 1080-140-rowCount*colHeight);
		rowCount++;
		font.draw(batch, "                                       where U != A OR V != B.", 
				216, 1080-140-rowCount*colHeight);
		rowCount++;
		font.draw(batch, "       )", 216, 1080-140-rowCount*colHeight);
		rowCount++;
		font.setColor(Color.ORANGE);
		font.draw(batch, "       Postconditions(", 216, 1080-140-rowCount*colHeight); 
		rowCount++;
		font.draw(batch, "             initiates(moveFromTo(X, Y, A, B), isIn(A, B)).", 216, 1080-140-rowCount*colHeight);
		rowCount++;
		font.draw(batch, "             terminates(moveFromTo(X, Y, A, B), isIn(X, Y)).", 216, 1080-140-rowCount*colHeight);
		rowCount++;
		font.draw(batch, "       )", 216, 1080-140-rowCount*colHeight);
		rowCount++;
		font.setColor(Color.WHITE);
		font.draw(batch, ")", 216, 1080-140-rowCount*colHeight); //last row
		
		batch.end();
	}
	
	public boolean isVisible() {
		return visible;
	}
	
	public void setVisibe(boolean b) {
		visible = b;
	}

}

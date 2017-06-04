package com.bob.mapEditor;

import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bob.game.Layer;
import com.bob.main.TextureFactory;

public class BackGroundLayer extends Layer {
	
	public BackGroundLayer(Skin skin) {
		Image foreground = new Image(TextureFactory.createTexture("screens/03.png"));
        group.addActor(foreground);
	}
	
}

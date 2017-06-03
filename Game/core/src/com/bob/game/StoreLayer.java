package com.bob.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bob.main.TextureFactory;

public class StoreLayer extends Layer {
	private final Label text;
	
    public StoreLayer(Skin skin) {
        // Bkg
        Image foreground = new Image(TextureFactory.createTexture("screens/store.png"));
        foreground.setBounds(0, 0, 1920, 1080);
        group.addActor(foreground);

        // Text
        Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = new BitmapFont();
        labelStyle.font.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        text = new Label("", labelStyle);
        text.setBounds(240, 945, 575, 125);
        group.addActor(text);
    }
  
}

package com.bob.mapEditor;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.game.EditorController;
import com.bob.game.Layer;
import com.bob.main.TextureFactory;

public class EditorManualLayer extends Layer {
	
	public EditorManualLayer(Skin skin, EditorController editorController) {
		initialVisibility = false;
		Image foreground = new Image(TextureFactory.createTexture("screens/editorguide.png"));
        group.addActor(foreground);
        
        Actor actor = new Actor();
		actor.setBounds(0, 0, 1920, 1080);
		actor.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
            	group.setVisible(false);
            	editorController.setGuideShown(false);
            }
        });
		group.addActor(actor);
	}

}

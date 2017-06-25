package com.bob.game;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.InputListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.main.TextureFactory;

public class HelpScreen extends Layer {
    private Image[] images;
    private Group imageGroup;
    private int current = 0;


    public HelpScreen(Skin skin) {
        initialVisibility = false;

        imageGroup = new Group();
        group.addActor(imageGroup);
        
        Actor actor = new Actor();
		actor.setBounds(0, 0, 1920, 1080);
		actor.addListener(new ClickListener() {
            public void clicked(InputEvent ie, float x, float y) {
            	next();
            }
        });
		group.addActor(actor);

        group.setVisible(false);
    }


    public void setImage(String path) {
        setImages(new String[]{path});
    }

    // load up all tutorial images
    public void setImages(String[] imagePaths) {
        imageGroup.clear();
        images = new Image[imagePaths.length];

        for (int i = 0; i < images.length; i++) {
            images[i] = new Image(TextureFactory.createTexture(imagePaths[i]));
            imageGroup.addActor(images[i]);
            images[i].setVisible(false);
        }

        images[0].setVisible(true);
    }

    // go to next tutorial screen if any
    private void next() {
        if (current + 1 >= images.length) {
            hide();
        } else {
            images[current].setVisible(false);
            current++;
            images[current].setVisible(true);
        }
    }

    public void setStage(Stage stage) {
        stage.addActor(group);

        stage.addListener(new InputListener(){
            public boolean keyDown(InputEvent ie, int keycode) {
                if (group.isVisible() && (keycode == com.badlogic.gdx.Input.Keys.ENTER || keycode == com.badlogic.gdx.Input.Keys.SPACE)) {
                    next();
                } else if (keycode == Input.Keys.ESCAPE) {
                    hide();
                }
                return false;
            }
        });
    }

    // hide all images
    private void hide() {
        current = 0;
        images[images.length - 1].setVisible(false);
        images[0].setVisible(true);
        setVisibility(false);
    }
}

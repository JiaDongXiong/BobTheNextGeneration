package com.bob.game.inputs;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.bob.main.TextureFactory;

public class ModalLayer extends InputsLayer {

    private TextField textField;
    private int index;

    public ModalLayer(Skin skin) {
        super();
        this.skin = skin;
        this.index = -1;

        addActor(new Image(TextureFactory.createTexture("screens/macro_modal_2.png")));

        TextField.TextFieldStyle textFieldStyle = new TextField.TextFieldStyle();
        textFieldStyle.font = skin.getFont("impact_small");
        textFieldStyle.fontColor = Color.WHITE;
        textFieldStyle.cursor = new Image(TextureFactory.createTexture("buttons/cursor.png")).getDrawable();

        textField = new TextField("Macro Title", textFieldStyle);
        textField.setBounds(1525, 1005, 415, 50);

        addActor(textField);
    }

    public void addButtons(final Skin skin, final MacroManager macroManager) {
        // Submit modal button
        final TextButton submitbutton = new TextButton("Submit", skin, "green_button") {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                this.setDisabled(!macroManager.checkRules());
                super.draw(batch, parentAlpha);
            }
        };
        submitbutton.setBounds(1160, 500, 200, 65);
        submitbutton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (!submitbutton.isDisabled()) macroManager.submitModal(skin);
            };
        });
        addActor(submitbutton);

        // Cancel modal button
        TextButton button = new TextButton("Cancel", skin, "blue_button");
        button.setBounds(1160, 575, 200, 65);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                setVisibility(false);
            };
        });
        addActor(button);

        // Delete modal button
        button = new TextButton("Delete", skin, "orange_button");
        button.setBounds(1160, 650, 200, 65);
        button.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                macroManager.deleteButtonModal();
            };
        });
        addActor(button);

    }

    public String getText() {
        return textField.getText();
    }

    public void setText(String text) {
        textField.setText(text);
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}

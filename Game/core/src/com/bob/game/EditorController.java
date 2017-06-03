package com.bob.game;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.bob.game.inputs.Block;
import com.bob.mapEditor.BackGroundLayer;
import com.bob.mapEditor.EditorLayer;
import com.bob.mapEditor.LoadingLayer;
import com.bob.mapEditor.MapEditor;
import com.bob.mapEditor.ReadLayer;
import com.bob.mapEditor.idManager;

public class EditorController {
	private final LayerGroup layerGroup;
	private final MapEditor mapEditor;
	Block[] bList = {Block.WHITE, Block.RED};
	boolean isVisible = false;
	boolean isTrying = false;
	private idManager manager;
	
	public EditorController(Skin skin, OrthographicCamera camera, idManager manager) {
		layerGroup = new LayerGroup();
		layerGroup.add("bkg", new BackGroundLayer(skin));
		layerGroup.add("editor", new EditorLayer(skin));
		layerGroup.add("read", new ReadLayer(skin));
		layerGroup.add("loading", new LoadingLayer(skin));
		
		int noOfUIs = ((EditorLayer)layerGroup.get("editor")).getUINum();
		
		this.manager = manager;
		mapEditor = new MapEditor(manager, noOfUIs);
		manager.loadMapEditor(mapEditor);
		mapEditor.setVisible(false);
		
		manager.setLayers((ReadLayer)layerGroup.get("read"), (LoadingLayer)layerGroup.get("loading"));
		manager.addHiddenLButtons(skin);
		manager.initView(skin);
		
		layerGroup.hide();
	}
	
	public void initRender() {
		isTrying = false;
		mapEditor.initRender();
	}
	
	public void resetMap() {
		manager.clearDrawing();
		mapEditor.resetMap();
	}
	
	public void render() {
		if (manager.getShowRead()) {
			manager.toggleLights();
		}
		if (!manager.isLoadingShown()) {
			mapEditor.render();
		}
	}
	
	public void linkStage(Stage stage) {
		layerGroup.setStage(stage);
        MapEditor.setStage(stage);
    }
	
	public void hide() {
		layerGroup.hide();
		mapEditor.setVisible(false);
		isVisible = false;
	}
	
	public void show() {
		isVisible = true;
		layerGroup.show();
		mapEditor.setVisible(true);
	}

	public boolean isVisible() {
		return isVisible;
	}
	
	public boolean isTrying() {
		return isTrying;
	}
	
	public void setTrying(boolean b) {
		isTrying = b;
	}
	
}

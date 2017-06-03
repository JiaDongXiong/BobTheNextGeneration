package com.bob.mapEditor;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class TiledMapActor extends Actor {
    private TiledMap tiledMap;
    private TiledMapTileLayer tiledLayer;

    public TiledMapActor(TiledMap tiledMap, TiledMapTileLayer tiledLayer) {
        this.tiledMap = tiledMap;
        this.tiledLayer = tiledLayer;
    }
    
    public TiledMap getMap() {
    	return tiledMap;
    }
    
    public TiledMapTileLayer getLayer() {
    	return tiledLayer;
    }
}

package com.bob.mapEditor;

import java.util.Iterator;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTile;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.maps.tiled.TiledMapTileSet;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.IsometricTiledMapRenderer;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.bob.game.inputs.BlockCoordinatesGenerator;
import com.bob.game.inputs.Type;

public class MapEditor {
	private final TiledMap map;
	private static Group group = new Group();
	private IsometricTiledMapRenderer renderer;
    //private OrthogonalTiledMapRenderer renderer;
    private OrthographicCamera camera;
    private final TiledMapTileLayer floorLayer;
    private final TiledMapTileLayer objectsLayer;
    private idManager manager;
    
    private SpriteBatch batch;
    
    public MapEditor(idManager manager, int noOfUIs) {
    	map = new TmxMapLoader().load("maps/editorMap.tmx");
    	Iterator<TiledMapTileSet> tileSets = map.getTileSets().iterator();
        while(tileSets.hasNext())
        {
            Iterator<TiledMapTile> tiles = tileSets.next().iterator();
            
            while(tiles.hasNext())
            {
                tiles.next().getTextureRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
            }
        }
        
        floorLayer = (TiledMapTileLayer)map.getLayers().get("Floor");
        objectsLayer = (TiledMapTileLayer)map.getLayers().get("Objects");
        this.manager = manager;
        
        resetMap();
        createActorsForFloorLayer(floorLayer);
        createActorsForUI(noOfUIs);
    }
    
    //TiledMap Actors
    private void createActorsForFloorLayer(TiledMapTileLayer tiledLayer) {
    	TiledMapActor actor = new TiledMapActor(map, tiledLayer);
        actor.setBounds(0, 0, 1500, 1080);
        EventListener eventListener = new TiledMapClickListener(actor, manager);
        actor.addListener(eventListener);
        group.addActor(actor);
    }
    
    //UI Actors
    private void createActorsForUI(int noOfUIs) {
    	BlockCoordinatesGenerator bcg = new BlockCoordinatesGenerator(1540, 915);
    	
    	for(int i=0; i<noOfUIs; i++) {
    		int[] coord = bcg.getCoordinates(Type.FLUENT);
    		UIActor imageA = new UIActor(i+1);
			imageA.setBounds(coord[0], coord[1], 50, 50);
			//Add Listener
			EventListener listener = new UIListener(imageA, manager);
			imageA.addListener(listener);
			group.addActor(imageA);
    	}
    }
    
    public void resetMap() {
        setMap(manager.getFloor(), manager.getObjects());
	}
    
    public void setMap(int[][] floor, int[][] objects) {
        for (int i = 0; i < floor.length; i++) {
            for (int j = 0; j < floor[i].length; j++) {
            	floorLayer.getCell(i,j).setTile(map.getTileSets().getTile(floor[i][j]));
            	if(objects!=null) {
            		if (objects[i][j] == 0) {
            			objectsLayer.getCell(i, j).getTile().setId(0);
            		} else {
            			objectsLayer.getCell(i, j).setTile(map.getTileSets().getTile(objects[i][j]));
            		}
            	}
            }
        }
    }

    public void initRender() {
        camera = new OrthographicCamera(1920, 1080);
        camera.position.set(960,180,0);
        //camera.position.set(Config.tileWidth * Config.noHorizontalTile - 50, 67, 0);
        renderer = new IsometricTiledMapRenderer(map);
        batch = new SpriteBatch();
        //resetMap();
        group.setVisible(true);
        //renderer = new OrthogonalTiledMapRenderer(map);
    }
    
    public void render() {
    	camera.update();
    	renderer.setView(camera);
    	renderer.render();
    	manager.draw(batch, camera);
	}
   
    public Vector3 getMousePos() {
    	return camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
    }

	public static void setStage(Stage stage) {
		stage.addActor(group);
	}

	public void setVisible(boolean b) {
		group.setVisible(b);
	}
}

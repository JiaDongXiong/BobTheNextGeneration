package com.bob.mapEditor;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class TiledMapClickListener extends ClickListener {

    private TiledMapActor actor;
    private OrthographicCamera camera;
    private idManager m;
    
    public TiledMapClickListener(TiledMapActor actor, idManager m) {
        this.actor = actor;
        this.m = m;
        
        camera = new OrthographicCamera(1920, 1080);
        camera.position.set(960,0,0);
    }
    
    @Override
    public void clicked(InputEvent event, float x, float y) {
    	int worldX = Gdx.input.getX();
    	int worldY = Gdx.input.getY();
    	Vector3 worldPos = worldToIso(worldX, worldY);
    	//System.out.println("(" + worldPos.x + ", " + worldPos.y + " has been clicked.");
    	
    	int x_tilePos = ((int)Math.round(worldPos.x))+9;
    	int y_tilePos = ((int)Math.round(worldPos.y))+20;
    	//System.out.println("(" + x_tilePos + ", " + y_tilePos + " has been clicked.");
    	
    	float width = actor.getLayer().getWidth();
    	float height = actor.getLayer().getHeight();
    	if (0<=x_tilePos && x_tilePos<width && 0<=y_tilePos && y_tilePos<height) {
    		
    		//System.out.println(m.getFloorId(x_tilePos, y_tilePos));
    		if (m.getFloorId(x_tilePos, y_tilePos) == 0) {
    			return;
    		}
    		Integer id = m.getID();
    		
    		//Deal with clicking on floor tiles
    		if (id < 10 && id > 0) {			//change a tile's color
    			((TiledMapTileLayer)actor.getMap().getLayers().get("Floor")).getCell(
    					x_tilePos, y_tilePos).setTile(actor.getMap().getTileSets().getTile(id));
    			m.setFloor(x_tilePos, y_tilePos);
    		} else if (id == 10) { 				//place bob
    			m.setBobPos(y_tilePos+1, x_tilePos+1);
    		} else { 				
    			MyPair<Integer, Integer> tilePos = new MyPair<Integer, Integer>(y_tilePos+1, x_tilePos+1);
    			MyPair<MyPair<Integer, Integer>, String> object = 
    					new MyPair<MyPair<Integer, Integer>, String>(tilePos, "Bulb");
    			
    			switch(id) {
    			
    			case 11:	//place a bulb
    				m.addObject(object, x_tilePos, y_tilePos);
    				break;
    			case 12:	//remove a bulb
    				m.delObject(object, x_tilePos, y_tilePos);
    				break;
    			case 13:	//place a boat
    				object.setValue("Boat");
    				m.addObject(object, x_tilePos, y_tilePos);
    				break;
    			case 14:	//remove a boat
    				object.setValue("Boat");
    				m.delObject(object, x_tilePos, y_tilePos);
    				break;
    			default:
    				
    			}
    			//m.setObjects(x_tilePos, y_tilePos, deleted);
    		} 
    		
    	}
    }
    
    private Vector3 worldToIso(int x, int y) {
    	Vector3 iso = new Vector3(x, y, 0);
    	float width = actor.getLayer().getTileWidth();
    	float height = actor.getLayer().getTileHeight();
    	camera.unproject(iso);
    	iso.x /= width;
    	iso.y = (iso.y-height/2)/height + iso.x;
    	iso.x -= (iso.y-iso.x);
    	
    	return iso;
    }
    
}

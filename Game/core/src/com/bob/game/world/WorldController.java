package com.bob.game.world;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.bob.game.inputs.Block;
import com.bob.game.levels.Level;

import java.util.*;

public class WorldController {

    //view
    private boolean isAnimPlaying;
    private float speedFactor;
    private int nbWon;

    // Bob
    private OrthographicCamera camera;
    private SpriteBatch batch;
    private final Entity bob;
    
    //boat
    private boolean onBoat = false;
    private Entity theBoat;
    private boolean allowMacro = false;
    private Vector2 boatOriginPos = null;
    private List<WorldCoordinates> originalBoats = new ArrayList<WorldCoordinates>();
    private Vector2 boatPosition = null;

    // Objects
    private List<Entity> objects;

    // Map and LPS
    private MapManager mapManager;
    private InstructionStrategy instructionRetriever;
    private List<Integer> currentRuleIndexes;

    // Golden cells
    private Stage stage;
    private final List<ClickListener> goldListener;

    public WorldController() {
        goldListener = new LinkedList<>();
        isAnimPlaying = false;
        speedFactor = 2f;
        nbWon = 0;
        currentRuleIndexes = new ArrayList<Integer>();
        bob = new Entity(0, 0, "bob");
        objects = new ArrayList<Entity>();
    }

    public void setCamera(OrthographicCamera camera) {
        this.camera = camera;
    }
    
    public boolean hasGoldTile() {
    	return mapManager.hasGoldTile();
    }

    public void setupWorld(Level level) {
        mapManager = new MapManager(level.getFloor(), level.getObjects());
        originalBoats = mapManager.getCoordinatesList("Objects", "boat");
        resetStage(level.getX(), level.getY());
    }

    public void initRender() {
        mapManager.initRender();
        setGoldListeners();
        batch = new SpriteBatch();
    }

    private void setGoldListeners() {
        clearListener();
        List<WorldCoordinates> questions = mapManager.getCoordinatesList("Floor", "question");

        for (WorldCoordinates coord: questions) {
            addQuestionListener(coord);
        }

    }

    private void clearListener() {
        for (ClickListener listener : goldListener) {
            stage.removeListener(listener);
        }
    }

    public void resetObjects() {
        List<WorldCoordinates> lights = mapManager.getCoordinatesList("Objects", "light_bulb");
        List<WorldCoordinates> boats = originalBoats;
        objects = new ArrayList<>();

        for (WorldCoordinates l: lights) {
            Entity light = new Entity(l.getWorldX(), l.getWorldY(), "light_bulb");
            light.updateState(EntityState.LIGHT);

            objects.add(light);
        }
        
        for (WorldCoordinates l: boats) {
            Entity boat = new Entity(l.getWorldX(), l.getWorldY(), "boat");
            boat.updateState(EntityState.BOAT);

            objects.add(boat);
        }

    }

    public void resetBob(float x, float y) {
        nbWon = 0;
        bob.setPosition(x, y);
        isAnimPlaying = false;
    }

    public void resetStage(float x, float y) {
        resetBob(x, y);
        resetObjects();
        currentRuleIndexes.clear();
    }

    public void startLPSAnimation(Level level, String rules) {
        instructionRetriever = new LPSHandler(mapManager.getLPSDescription(), 
        		mapManager.getLightsString(), rules, level.getX(), level.getY());
        isAnimPlaying = true;
    }
    
    /*public void startMacroLPSAnimation(Level level, String rules) {
    	 instructionRetriever = new LPSHandler(mapManager.getLPSDescription(), 
         		mapManager.getLightsString(), rules, level.getX(), level.getY());
         isAnimPlaying = true;
	}*/

    public void startMockAnimation(LinkedList<Block> blockStack) {
        instructionRetriever = new MockLPSHandler(blockStack);
        isAnimPlaying = true;
    }

    public void render(float deltaTime) {
        float deltaTimeAdjusted = isAnimPlaying ? deltaTime * speedFactor: 0;

        // UPDATE OF ENTITY
        updateWorld(deltaTimeAdjusted);

        //Map
        if (mapManager != null) mapManager.draw(deltaTimeAdjusted);

        // Batch
        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        for (Entity object: objects) {
            object.increaseTime(deltaTimeAdjusted);
            object.draw(batch);
        }

        if (bob != null && !onBoat) bob.draw(batch);
        
        batch.end();
    }

    ////////////////////////////////////////////////////////////////////
    /////--------------------Private Helpers-----------------------/////
    ////////////////////////////////////////////////////////////////////
    
    private void updateWorld(float deltaTimeAdjusted) {
        updateBob(deltaTimeAdjusted);
        cleanObjects(); 
    }

    private void cleanObjects() {
        List<Entity> toDelete = new LinkedList<>();
        for (Entity object: objects) {
        	//Only remove it if it is a bulb
        	if (object.getEntityType().equals("boat")) {
        		// stepping on boat
            	// if Bob collides with a boat, also update the boat position
        		if (object.getCoord().collide(bob.getCoord())) {
            		onBoat = true;
            		theBoat = object;
            	} 
        		
        		boolean isOnWater = mapManager.checkIfWet(bob.getCoord());
        		// stepping off boat
                if (!isOnWater && onBoat) {
                	onBoat = false;
                	if (boatPosition != null) {
                		theBoat.updateObjectPosition(boatPosition.x, boatPosition.y);
                	}
                	theBoat = null;
                }
        		continue; 
        	}
        	
        	if (object.getCoord().collide(bob.getCoord())) {  
                toDelete.add(object); // todo anim
            }
        }

        for (Entity o: toDelete) {
            objects.remove(o);
        }
    }

    private void updateBob(float deltaTime) {
        bob.increaseTime(deltaTime);
        if (bob.needInstructions() && isAnimPlaying) {
        	retrieveInstructions();
        	updateGameState();
        }
        
        // if bob is on a boat, update the boat position as well
        if (onBoat) {
        	if (theBoat != null) {
        		// TODO
        		if (allowMacro) {
        			updateBoatPos(theBoat.getCoord().getWorldX(), theBoat.getCoord().getWorldY(), 
        					bob.getCoord().getWorldX(), bob.getCoord().getWorldY());
        		}
        		//for animation
        		theBoat.updateObjectPosition(bob.getCoord().getWorldX(), bob.getCoord().getWorldY());
        	}
        } 
    }
    
    //TODO
    private void updateBoatPos(float x, float y, float nx, float ny) {
    	
    	// initialize boatPosition and boatOriginPos
    	if (boatPosition == null) {
    		boatOriginPos = new Vector2();
    		boatPosition = new Vector2();
    		boatOriginPos.add(x, y);
    		boatPosition.add(x, y);
    	}
    	
    	TiledMapTileLayer objectsLayer = mapManager.getObjectLayer();
    	TiledMap map = mapManager.getMap();
    	
    	float movedDis = distanceHelper(boatPosition, new Vector2(nx, ny));
    	
    	if (movedDis>1) {
    		// temp boat position set to water
    		objectsLayer.getCell((int)boatPosition.x, (int)boatPosition.y).setTile(map.getTileSets().getTile(9));
    		boatPosition.x = boatPosition.x + updateHelper(boatPosition.x, nx);
    		boatPosition.y = boatPosition.y + updateHelper(boatPosition.y, ny);
    	}
    	
    	//System.out.println("( " + boatPosition.x + ", " + boatPosition.y + " ) ");
    	// new boat position set to boat
    	objectsLayer.getCell((int)boatPosition.x, (int)boatPosition.y).setTile(map.getTileSets().getTile(26));
    	
    }
    
    private int updateHelper(float x, float nx) {
    	int fx = (int) Math.round(x);
    	int fnx = (int) Math.round(nx);
    	
    	// update if not equal
    	if (fx!=fnx) {
    		return fnx-fx;
    	}
    	return 0;
    }
    
    private float distanceHelper(Vector2 v1, Vector2 v2) {
    	return v1.dst2(v2);
    }

    private void retrieveInstructions() {
        instructionRetriever.update();
        bob.updateState(instructionRetriever.getState());
    }

    private void updateGameState() {
        WorldCoordinates coord = bob.getCoord();

        boolean isOnWater = mapManager.checkIfWet(coord);
        if (isOnWater && !onBoat) {
            bob.updateState(EntityState.WET);
            isAnimPlaying = false;
        }

        // Adds delay to show winning screen
        if (mapManager.chekIfWon(coord, noOfLightBulbs())) {
            bob.updateState(EntityState.WON);
            nbWon++;
        }

        // Update current rule
        currentRuleIndexes.clear();
        currentRuleIndexes.addAll(instructionRetriever.getAppliedRuleIndexes());
    }
        
    private void addQuestionListener(final WorldCoordinates coord) {

        ClickListener listener = new ClickListener(Input.Buttons.LEFT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Math.abs(x - coord.getScreenX()) < 50 && Math.abs(y - coord.getScreenY()) < 30) {
                    if (!isAnimPlaying) {
                        mapManager.setGold((int) coord.getWorldX(), (int) coord.getWorldY());
                    }
                }
            }
        };

        goldListener.add(listener);
        stage.addListener(listener);
    }

    ///////////////////////////////////////////////////////////////////
    //\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\\
    
    public void setAllowMacro(boolean b) {
    	allowMacro = b;
    }
    
    // reset bob if bob is out of the map
    public boolean outOfMap() {
    	if (bob.getCoord().getWorldX() < 1 || bob.getCoord().getWorldX() > 21 
    			|| bob.getCoord().getWorldY() < 1 || bob.getCoord().getWorldY() > 21) {
    		return true;
    	}
    	return false;
    }
    
    public boolean isLevelWon() {
        return nbWon > 0;
    }

    public void updateSpeed(float newValue) {
        speedFactor = newValue;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public List<Integer> getCurrentRuleIndexes() {
        return currentRuleIndexes;
    }
    
    // count number of light bulbs left on map
    public int noOfLightBulbs() {
    	int num = 0;
    	for (Entity object: objects) {
        	if (object.getEntityType().equals("light_bulb")) { num++; }
    	}
    	return num;
    }

    public int getNoObjects() {
        return objects.size();
    }

    public int getMaxObjects() {
        return mapManager.getCoordinatesList("Objects", "light_bulb").size();
    }

    public boolean isBobConfused() {
        return bob.isConfused() && isAnimPlaying;
    }

    public boolean isOnQuestionMark() {
        return mapManager.isQuestionMark(bob.getCoord());
    }
	
}

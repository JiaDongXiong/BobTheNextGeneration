package com.bob.main;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.badlogic.gdx.utils.viewport.*;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.bob.game.EditorController;
import com.bob.game.GameController;
import com.bob.game.levels.Level;
import com.bob.game.levels.LevelFactory;
import com.bob.game.world.Textures;
import com.bob.mapEditor.idManager;

public class Main extends ApplicationAdapter {

	// Game State
	protected GameState gameState;
	private float gameStateTime;

	private Skin skin;
	private Stage stage;

	protected Menu menu;
	private GameController gameController;
	private EditorController editorController;
	private idManager manager;
	private SpriteBatch batch;
	
	@Override
	public void create() {

        skin = new Skin();
		stage = new Stage(new FitViewport(1920, 1080));
		
		batch = new SpriteBatch();

		skin.add("font", new BitmapFont());

		addButtonStyle();

		final OrthographicCamera camera = new OrthographicCamera();
		Viewport viewport = new FitViewport(1920,1080,camera);
		camera.position.set(960,540,0);
		viewport.apply();

		manager = new idManager(9);
		gameController = new GameController(skin, camera);
		editorController = new EditorController(skin, camera, manager);
		menu = new Menu(skin, manager, editorController, gameController);
		
		//manager.setUpGameController(gameController);

		editorController.linkStage(stage);
		gameController.linkStage(stage);
		menu.setStage(stage);

		gameController.loadEditor(editorController);
		
		gameStateTime = 0;
		gameState = GameState.MENU;

		LevelFactory.initialiseLevels();

		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void dispose() {
		//batch.dispose();
		stage.dispose();
		skin.dispose();

		for (Textures t: Textures.values()) {
			t.dispose();
		}

		TextureFactory.dispose();
	}

	@Override
	public void resize (int width, int height) {
		stage.getViewport().update(width, height, true);
	}

	@Override
	public void render() {
		float deltaTime = Gdx.graphics.getDeltaTime();
        gameStateTime += deltaTime;

       
        
		Gdx.gl.glClearColor(39, 156, 255, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		if (gameState == GameState.MENU) {
			if (!menu.isVisible()) {
				if(!menu.isEditting()) {
					editorController.hide();
					startLevel(menu.getLevelSelected());
				} else {
					gameController.hide();
					startEditor();
				}
			}
		}
		
		if (gameState == GameState.PLAYING) {
			gameController.render(deltaTime);
			
			if (!gameController.isVisible()) {
				if(!editorController.isTrying()) {
					menu.quitFromPlaying();
					gameState = GameState.MENU;
				} else {
					gameState = GameState.EDITTING;
					editorController.setTrying(false);
					manager.setUpFloor();
					editorController.show();
					menu.showEditor();
				}
			}
		}
		 
		// Stage
     	stage.act(deltaTime);
     	stage.draw();
     	//stage.setDebugAll(true);
     	
     	if (gameState == GameState.EDITTING) {
			editorController.render();
			
			if (!editorController.isVisible()) {
				if(!editorController.isTrying()) {
					menu.show();
					gameState = GameState.MENU;
				} else {
					startLevel(menu.getLevelSelected());
				}
			}
		}
     	if (gameState == GameState.PLAYING) {
     		gameController.drawLPSRules(batch);
     		gameController.drawCausalTheory(batch);
     		gameController.drawMacros(batch);
     	}
	}

	protected void startLevel(Level lvl) {
		gameController.setLevel(lvl);
		gameController.show();
		gameController.startNewLevel();
		gameState = GameState.PLAYING;
	}
	
	protected void startEditor() {
		gameState = GameState.EDITTING;
		editorController.initRender();
	}

	private void addButtonStyle() {
		
		BitmapFont font;
		FreeTypeFontGenerator generator = new FreeTypeFontGenerator(Gdx.files.internal("font/VALENTIN.ttf"));
	    FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
	    parameter.size = 48;
	    parameter.borderWidth = 1;
	    parameter.color = Color.WHITE;
	    parameter.shadowOffsetX = 3;
	    parameter.shadowOffsetY = 3;
	    parameter.shadowColor = Color.GRAY;
	    font = generator.generateFont(parameter); 
	    generator.dispose();
	    skin.add("val", font);
	    
	    /*generator = new FreeTypeFontGenerator(Gdx.files.internal("font/Chunkfive.otf"));
	    parameter.size = 48;
	    parameter.borderWidth = 1;
	    parameter.color = Color.DARK_GRAY;
	    parameter.shadowOffsetX = 0;
	    parameter.shadowOffsetY = 0;
	    parameter.shadowColor = Color.GRAY;
	    font = generator.generateFont(parameter);
	    generator.dispose();
	    skin.add("chunk", font);*/
		
	    
	    BitmapFont whiteFont = new BitmapFont(Gdx.files.internal("font/white.fnt"));
		whiteFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		whiteFont.getData().scale(-0.2f);
		skin.add("white", whiteFont);

		BitmapFont impactFont = new BitmapFont(Gdx.files.internal("font/impact.fnt"));
		impactFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		skin.add("impact", impactFont);

		BitmapFont smallFont = new BitmapFont(Gdx.files.internal("font/impact.fnt"));
		smallFont.getData().scale(-0.3f);
		smallFont.getRegion().getTexture().setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);
		skin.add("impact_small", smallFont);

		String[] buttonColor = {"grey", "grey_square", "big_grey", "orange", "red", "green", "blue", "yellow", 
				"orange_square", "green_square", "blue_square", "yellow_square", "rock"};
		//TODO
		String[] buttonFont = {"impact_small", "impact_small", "impact", 
				"impact_small", "impact_small", "impact_small", "impact_small", "impact_small", 
				"impact_small", "impact_small", "impact_small", "impact_small", "impact_small"};
		skin.add("disabled_button", TextureFactory.createTexture("buttons/disabled.png"));

		for (int i = 0; i < buttonColor.length; i++) {
			String color = buttonColor[i];
			skin.add(color + "_button", TextureFactory.createTexture("buttons/" + color + ".png"));
			skin.add(color + "_clicked", TextureFactory.createTexture("buttons/" + color + "_clicked.png"));
			TextButton.TextButtonStyle colorButtonStyle = new TextButton.TextButtonStyle();
			colorButtonStyle.up = skin.getDrawable(color + "_button");
			colorButtonStyle.down = skin.getDrawable(color + "_clicked");
			colorButtonStyle.disabled = skin.getDrawable("disabled_button");
			colorButtonStyle.font = skin.getFont(buttonFont[i]);
			skin.add(color + "_button", colorButtonStyle);
		}
	}
}

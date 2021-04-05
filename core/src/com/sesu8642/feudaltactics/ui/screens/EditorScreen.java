package com.sesu8642.feudaltactics.ui.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.EditorController;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.MapRenderer;
import com.sesu8642.feudaltactics.input.CombinedInputProcessor;
import com.sesu8642.feudaltactics.input.EditorInputHandler;

public class EditorScreen implements Screen {

	private OrthographicCamera camera;
	private MapRenderer mapRenderer;
	private InputMultiplexer multiplexer;
	private EditorInputHandler inputValidator;
	
	private Stage stage;
	private Table rootTable;
	private Viewport viewport;

	public EditorScreen() {
		EditorController editorController = new EditorController();
		inputValidator = new EditorInputHandler(editorController);
		//editorController.setHud(this);
		camera = new OrthographicCamera();
		mapRenderer = new MapRenderer(camera);
		editorController.setMapRenderer(mapRenderer);
		initUI();
		CombinedInputProcessor inputProcessor = new CombinedInputProcessor(inputValidator, camera);
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(stage);
		multiplexer.addProcessor(new GestureDetector(inputProcessor));
		multiplexer.addProcessor(inputProcessor);
		editorController.generateEmptyMap();
	}

	private void initUI() {
		Camera camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);
		stage = new Stage(viewport);
		rootTable = new Table();
		rootTable.setDebug(true);
		rootTable.setFillParent(true);
		stage.addActor(rootTable);
	}
	
	@Override
	public void show() {
		Gdx.input.setInputProcessor(multiplexer);
		camera.position.set(camera.viewportWidth, camera.viewportHeight, 0);
		camera.update();
		camera.zoom = 0.2F;
	}

	@Override
	public void render(float delta) {
		camera.update();
		mapRenderer.render();
		viewport.apply();
		stage.draw();
		stage.act();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		viewport.apply();
		rootTable.pack(); // VERY IMPORTANT!!! makes everything scale correctly on startup and going fullscreen etc.; took me hours to find out
		camera.viewportHeight = height;
		camera.viewportWidth = width;
		camera.update();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		mapRenderer.dispose();
		stage.dispose();
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

}

package com.sesu8642.feudaltactics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.engine.CombinedInputProcessor;
import com.sesu8642.feudaltactics.engine.EditorController;
import com.sesu8642.feudaltactics.engine.EditorInputHandler;
import com.sesu8642.feudaltactics.engine.MapRenderer;
import com.sesu8642.feudaltactics.scenes.EditorUIOverlay;

public class EditorScreen implements Screen {

	private OrthographicCamera camera;
	private MapRenderer mapRenderer;
	private EditorUIOverlay editorUIOverlay;
	private InputMultiplexer multiplexer;

	public EditorScreen(FeudalTactics game) {
		EditorController editorController = new EditorController();
		EditorInputHandler editorInputHandler = new EditorInputHandler(editorController);
		editorUIOverlay = new EditorUIOverlay(editorInputHandler);
		camera = new OrthographicCamera();
		//camera.rotate(90);
		mapRenderer = new MapRenderer(camera);
		editorController.setMapRenderer(mapRenderer);
		CombinedInputProcessor inputProcessor = new CombinedInputProcessor(editorInputHandler, camera);
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(editorUIOverlay.getStage());
		multiplexer.addProcessor(new GestureDetector(inputProcessor));
		multiplexer.addProcessor(inputProcessor);
		editorController.generateEmptyMap();
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(multiplexer);
		camera.position.set(camera.viewportWidth, camera.viewportHeight, 0);
		camera.update();
		camera.zoom = 10;
	}

	@Override
	public void render(float delta) {
		camera.update();
		Gdx.gl.glClearColor(0, 0.2f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mapRenderer.render();
		editorUIOverlay.render();
	}

	@Override
	public void resize(int width, int height) {
		editorUIOverlay.resize(width, height);
		camera.viewportWidth = 30f;
		camera.viewportHeight = 30f * height / width;
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
		editorUIOverlay.dispose();
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

}

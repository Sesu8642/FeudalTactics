package com.sesu8642.feudaltactics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.engine.CombinedInputProcessor;
import com.sesu8642.feudaltactics.engine.GameController;
import com.sesu8642.feudaltactics.engine.HexMap;
import com.sesu8642.feudaltactics.engine.InputValidator;
import com.sesu8642.feudaltactics.engine.MapRenderer;
import com.sesu8642.feudaltactics.scenes.Hud;

public class IngameScreen implements Screen {

	private OrthographicCamera camera;
	private MapRenderer mapRenderer;
	private Hud hud;
	private InputMultiplexer multiplexer;

	public IngameScreen(FeudalTactics game) {
		HexMap map = new HexMap();
		mapRenderer = new MapRenderer(map);
		GameController gameController = new GameController(map, mapRenderer);
		hud = new Hud(gameController);
		gameController.setHud(hud);
		camera = new OrthographicCamera();
		CombinedInputProcessor inputProcessor = new CombinedInputProcessor(new InputValidator(gameController), camera);
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(hud.getStage());
		multiplexer.addProcessor(new GestureDetector(inputProcessor));
		multiplexer.addProcessor(inputProcessor);
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
		mapRenderer.render(camera);
		hud.render();
	}

	@Override
	public void resize(int width, int height) {
		hud.resize(width, height);
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
		hud.dispose();
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

}

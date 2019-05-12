package com.sesu8642.feudaltactics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.engine.GameController;
import com.sesu8642.feudaltactics.engine.HexMap;
import com.sesu8642.feudaltactics.engine.MapRenderer;
import com.sesu8642.feudaltactics.scenes.Hud;

public class IngameScreen implements Screen, GestureListener, InputProcessor {

	private OrthographicCamera camera;
	private MapRenderer mapRenderer;
	private HexMap map;
	private GameController gameController;

	private Hud hud;
	private InputMultiplexer multiplexer;

	public IngameScreen(FeudalTactics game) {
		map = new HexMap();
		mapRenderer = new MapRenderer(map);
		gameController = new GameController(map, mapRenderer);
		hud = new Hud(gameController);
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(hud.getStage());
		multiplexer.addProcessor(new GestureDetector((GestureListener) this));
		multiplexer.addProcessor(this);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(multiplexer);

		camera = new OrthographicCamera();
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

	@Override
	public boolean scrolled(int amount) {
		if (camera.zoom + amount > 0 && camera.zoom + amount < 50) {
			camera.zoom += amount;
		}
		return true;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// not very good and dependent on resolution; refactor later
		final int DRAG_MULTIPLIER = 50; // 50 seems to work pretty well; no idea why
		camera.translate(-deltaX * camera.zoom / DRAG_MULTIPLIER, deltaY * camera.zoom / DRAG_MULTIPLIER);
		return true;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// kind of works... good enough for now
		float amount = (initialDistance-distance)/2500;
		if (camera.zoom + amount > 0 && camera.zoom + amount < 50) {
			camera.zoom += amount;
		}
		return true;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}
	
	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		Vector3 fullWorldCoords = camera.unproject(new Vector3(x, y, 0));
		Vector2 worldCoords = new Vector2(fullWorldCoords.x, fullWorldCoords.y);
		gameController.printTileInfo(worldCoords);
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}
	
	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	@Override
	public void pinchStop() {
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}

}

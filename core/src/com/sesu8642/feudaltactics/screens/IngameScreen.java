package com.sesu8642.feudaltactics.screens;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.GameState;
import com.sesu8642.feudaltactics.HexMap;
import com.sesu8642.feudaltactics.MapRenderer;
import com.sesu8642.feudaltactics.Player;
import com.sesu8642.feudaltactics.scenes.Hud;

public class IngameScreen implements Screen, InputProcessor {

	private OrthographicCamera camera;
	private MapRenderer mapRenderer;
	private HexMap map;
	private Hud hud;
	private InputMultiplexer multiplexer;

	public IngameScreen(FeudalTactics game) {
		hud = new Hud();

		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(hud.getStage());
		multiplexer.addProcessor(this);

		map = new HexMap();
		ArrayList<Player> players = new ArrayList<Player>();
		Player p1 = new Player(new Color(0, 0.5f, 0.8f, 1));
		Player p2 = new Player(new Color(1F, 0F, 0F, 1));
		Player p3 = new Player(new Color(0F, 1F, 0F, 1));
		Player p4 = new Player(new Color(1F, 1F, 0F, 1));
		Player p5 = new Player(new Color(1F, 1F, 1F, 1));
		Player p6 = new Player(new Color(0F, 1F, 1F, 1));
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);
		players.add(p5);
		players.add(p6);
		GameState gameState = new GameState(players, 0, map);
		map.generate(players, 500, 0, null);
		mapRenderer = new MapRenderer(map);

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
		// TODO Auto-generated method stub

	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub

	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub

	}

	@Override
	public void dispose() {
		mapRenderer.dispose();
		hud.dispose();
	}

	@Override
	public boolean keyDown(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		// not very good and dependent on resolution; refactor later
		final int DRAG_MULTIPLIER = 50; // 50 seems to work pretty well; no idea why
		float dx = Gdx.input.getDeltaX();
		float dy = Gdx.input.getDeltaY();
		camera.translate(-dx * camera.zoom / DRAG_MULTIPLIER, dy * camera.zoom / DRAG_MULTIPLIER);
		return true;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean scrolled(int amount) {
		if (camera.zoom + amount > 0 && camera.zoom + amount < 50) {
			camera.zoom += amount;
		}
		return true;
	}

}

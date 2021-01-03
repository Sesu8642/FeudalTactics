package com.sesu8642.feudaltactics.screens;

import java.util.LinkedHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.stages.SplashImageStage;

public class SplashScreen implements Screen {
	private SplashImageStage stage;
	private Viewport viewport;
	private long startTime;

	public SplashScreen() {
		initUI();
	}

	private void initUI() {
		Camera camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);

		LinkedHashMap<String, Runnable> buttonData = new LinkedHashMap<String, Runnable>();
		buttonData.put("Play", () -> FeudalTactics.game.setScreen(new IngameScreen()));
		buttonData.put("Tutorial", () -> FeudalTactics.game.setScreen(new EditorScreen()));
		buttonData.put("About", () -> {
		});
		stage = new SplashImageStage(viewport);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0.2f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		viewport.apply();
		stage.draw();
		stage.act();
		if (TimeUtils.timeSinceMillis(startTime) > 1000) {
			FeudalTactics.game.setScreen(new MainMenuScreen());
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		viewport.apply();
		((Table) stage.getActors().get(0)).pack();
	}

	@Override
	public void show() {
		startTime = TimeUtils.millis();
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}

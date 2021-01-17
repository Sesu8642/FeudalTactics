package com.sesu8642.feudaltactics.ui.screens;

import java.util.LinkedHashMap;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.ui.stages.GenericMenuStage;

public class SplashScreen implements Screen {
	private GenericMenuStage stage;
	private Viewport viewport;
	private long startTime;

	public SplashScreen() {
		initUI();
	}

	private void initUI() {
		Camera camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);
		stage = new GenericMenuStage(viewport, new LinkedHashMap<String, Runnable>());
		stage.setBottomLabelText("By Sesu8642");
	}

	@Override
	public void render(float delta) {
		viewport.apply();
		stage.draw();
		stage.act();
		if (TimeUtils.timeSinceMillis(startTime) > 1000) {
			FeudalTactics.game.setScreen(new MainMenuScreen());
			this.hide();
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		viewport.apply();
		((Table) stage.getActors().get(0)).pack();
		stage.updateOnResize(width, height);
	}

	@Override
	public void show() {
		startTime = TimeUtils.millis();
	}

	@Override
	public void hide() {
		// TODO: causes error "buffer not allocated with newUnsafeByteBuffer or already disposed"; maybe because the call is caused by the render method
		//dispose();
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

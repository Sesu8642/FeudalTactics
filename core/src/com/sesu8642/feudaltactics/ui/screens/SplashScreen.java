package com.sesu8642.feudaltactics.ui.screens;

import java.util.LinkedHashMap;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.dagger.MenuCamera;
import com.sesu8642.feudaltactics.ui.stages.MenuStage;
import com.sesu8642.feudaltactics.ui.stages.StageFactory;

@Singleton
public class SplashScreen implements Screen {
	
	private OrthographicCamera camera;
	private MenuStage menuStage;
	private Viewport viewport;
	private long startTime;
	
	private Screen mainMenuScreen;
	private StageFactory stageFactory;

	@Inject
	public SplashScreen(@MenuCamera OrthographicCamera camera, MainMenuScreen mainMenuScreen, StageFactory stageFactory) {
		this.camera = camera;
		this.mainMenuScreen = mainMenuScreen;
		this.stageFactory = stageFactory;
		initUI();
	}

	private void initUI() {
		viewport = new ScreenViewport(camera);
		// using a menu stage without buttons here
		menuStage = stageFactory.createMenuStage(viewport, new LinkedHashMap<String, Runnable>());
		menuStage.setBottomLabelText("By Sesu8642");
	}

	@Override
	public void render(float delta) {
		viewport.apply();
		menuStage.draw();
		menuStage.act();
		if (TimeUtils.timeSinceMillis(startTime) > 1000) {
			FeudalTactics.game.setScreen(mainMenuScreen);
			this.hide();
		}
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		viewport.apply();
		menuStage.updateOnResize(width, height);
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
		menuStage.dispose();
	}

}

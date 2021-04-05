package com.sesu8642.feudaltactics.ui.screens;

import java.util.LinkedHashMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.ui.stages.GenericMenuStage;

public class MainMenuScreen implements Screen {

	private Viewport viewport;
	private GenericMenuStage menuStage;

	public MainMenuScreen() {
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
		menuStage = new GenericMenuStage(viewport, buttonData);
		menuStage.setBottomLabelText("Version 1.0");
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(menuStage);
	}

	@Override
	public void render(float delta) {
		viewport.apply();
		menuStage.draw();
		menuStage.act();
	}

	@Override
	public void resize(int width, int height) {
		menuStage.updateOnResize(width, height);
		viewport.update(width, height, true);
		viewport.apply();
		((Table) menuStage.getActors().get(0)).pack();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		menuStage.dispose();
	}

}

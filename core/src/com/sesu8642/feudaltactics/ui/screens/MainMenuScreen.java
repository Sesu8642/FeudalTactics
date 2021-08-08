package com.sesu8642.feudaltactics.ui.screens;

import java.util.LinkedHashMap;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.dagger.MenuCamera;
import com.sesu8642.feudaltactics.dagger.VersionProperty;
import com.sesu8642.feudaltactics.ui.stages.MenuStage;
import com.sesu8642.feudaltactics.ui.stages.StageFactory;

@Singleton
public class MainMenuScreen implements Screen {

	private OrthographicCamera camera;
	private Viewport viewport;
	private MenuStage menuStage;

	// using providers here to avoid depdendency cycle problems
	private Provider<IngameScreen> ingameScreenProvider;
	// editor is unfinished and hidden
	@SuppressWarnings("unused")
	private Provider<EditorScreen> editorScreenProvider;
	private Provider<TutorialScreen> tutorialScreenProvider;
	private StageFactory stageFactory;
	private String gameVersion;

	@Inject
	public MainMenuScreen(@MenuCamera OrthographicCamera camera, Provider<IngameScreen> ingameScreenProvider, Provider<EditorScreen> editorScreenProvider, Provider<TutorialScreen> tutorialScreenProvider,
			StageFactory stageFactory, @VersionProperty String gameVersion) {
		this.camera = camera;
		this.ingameScreenProvider = ingameScreenProvider;
		this.editorScreenProvider = editorScreenProvider;
		this.tutorialScreenProvider = tutorialScreenProvider;
		this.stageFactory = stageFactory;
		this.gameVersion = gameVersion;
		initUI();
	}

	private void initUI() {
		viewport = new ScreenViewport(camera);

		LinkedHashMap<String, Runnable> buttonData = new LinkedHashMap<String, Runnable>();
		buttonData.put("Play", () -> FeudalTactics.game.setScreen(ingameScreenProvider.get()));
		buttonData.put("Tutorial", () -> FeudalTactics.game.setScreen(tutorialScreenProvider.get()));
		buttonData.put("About", () -> {
		});
		menuStage = stageFactory.createMenuStage(viewport, buttonData);
		menuStage.setBottomLabelText(String.format("Version %s", gameVersion));
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
		viewport.update(width, height, true);
		viewport.apply();
		menuStage.updateOnResize(width, height);
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
//		dispose();
	}

	@Override
	public void dispose() {
		menuStage.dispose();
	}

}

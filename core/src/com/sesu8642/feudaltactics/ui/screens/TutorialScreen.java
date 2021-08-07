package com.sesu8642.feudaltactics.ui.screens;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.dagger.MenuCamera;
import com.sesu8642.feudaltactics.dagger.TutorialSlides;
import com.sesu8642.feudaltactics.ui.stages.GenericSlideStage;
import com.sesu8642.feudaltactics.ui.stages.Slide;
import com.sesu8642.feudaltactics.ui.stages.StageFactory;

@Singleton
public class TutorialScreen implements Screen {

	private OrthographicCamera camera;
	private Viewport viewport;
	private GenericSlideStage slideStage;
	private List<Slide> tutorialSlides;
	private MainMenuScreen menuScreen;
	private StageFactory stageFactory;

	@Inject
	public TutorialScreen(@MenuCamera OrthographicCamera camera, MainMenuScreen menuScreen, StageFactory stageFactory,
			Skin skin, @TutorialSlides List<Slide> tutorialSlides) {
		this.camera = camera;
		this.menuScreen = menuScreen;
		this.stageFactory = stageFactory;
		this.tutorialSlides = tutorialSlides;
		initUI(skin);
	}

	private void initUI(Skin skin) {
		viewport = new ScreenViewport(camera);

		slideStage = stageFactory.createSlideStage(viewport, tutorialSlides, () -> {
			FeudalTactics.game.setScreen(menuScreen);
		});
	}

	@Override
	public void show() {
		slideStage.reset();
		Gdx.input.setInputProcessor(slideStage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		viewport.apply();
		slideStage.draw();
		slideStage.act();
	}

	@Override
	public void resize(int width, int height) {
		slideStage.updateOnResize(width, height);
		viewport.update(width, height, true);
		viewport.apply();
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
//		dispose();
	}

	@Override
	public void dispose() {
		// note: slides can contain other stuff that should be disposed as well
		slideStage.dispose();
	}

}

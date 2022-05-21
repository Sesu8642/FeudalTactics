package com.sesu8642.feudaltactics.ui.screens;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.MainMenuScreen;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.MenuCamera;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.MenuViewport;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.SplashScreenStage;
import com.sesu8642.feudaltactics.ui.stages.ResizableResettableStage;

/** {@link Screen} for displaying a splash image. */
@Singleton
public class SplashScreen extends GameScreen {

	private long startTime;
	private GameScreen nextScreen;

	@Inject
	public SplashScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
			@SplashScreenStage ResizableResettableStage stage, @MainMenuScreen GameScreen nextScreen) {
		super(camera, viewport, stage);
		this.nextScreen = nextScreen;
	}

	@Override
	public void render(float delta) {
		super.render(delta);
		if (TimeUtils.timeSinceMillis(startTime) > 1000) {
			FeudalTactics.game.setScreen(nextScreen);
			this.hide();
		}
	}

	@Override
	public void show() {
		startTime = TimeUtils.millis();
		super.show();
	}

	@Override
	public void hide() {
		super.hide();
		// TODO: disposing here causes error "buffer not allocated with
		// newUnsafeByteBuffer or already
		// disposed"; maybe because the call is caused by the render method
	}

}

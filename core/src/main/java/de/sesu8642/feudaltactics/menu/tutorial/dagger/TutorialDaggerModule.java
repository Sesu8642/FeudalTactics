// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.tutorial.dagger;

import java.util.List;

import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;
import de.sesu8642.feudaltactics.menu.tutorial.ui.TutorialSlideFactory;

/** Dagger module for the tutorial. */
@Module
public class TutorialDaggerModule {

	private TutorialDaggerModule() {
		// prevent instantiation
		throw new AssertionError();
	}

	@Provides
	@TutorialSlides
	static List<Slide> provideTutorialSlides(TutorialSlideFactory slideFactory) {
		return slideFactory.createAllSlides();
	}

	@Provides
	@Singleton
	@TutorialSlideStage
	static SlideStage provideTutorialSlideStage(EventBus eventBus, @MenuViewport Viewport viewport,
			@TutorialSlides List<Slide> tutorialSlides, @MenuBackgroundCamera OrthographicCamera camera, Skin skin) {
		return new SlideStage(viewport, tutorialSlides,
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.MAIN_MENU_SCREEN)), camera,
				skin);
	}

	@Provides
	@Singleton
	@TutorialScreen
	static GameScreen provideTutorialScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
			@TutorialSlideStage SlideStage slideStage) {
		return new GameScreen(camera, viewport, slideStage);
	}

}

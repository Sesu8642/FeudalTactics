// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences.ui;

import java.util.Arrays;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.events.MainPreferencesChangeEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;
import de.sesu8642.feudaltactics.menu.preferences.MainGamePreferences;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;

/**
 * {@link Stage} that displays the global preferences menu.
 */
@Singleton
public class PreferencesStage extends SlideStage {

	private EventBus eventBus;
	private PreferencesSlide preferencesSlide;
	private MainPreferencesDao mainPrefsDao;

	/**
	 * Constructor.
	 * 
	 * @param eventBus     event bus
	 * @param mainPrefsDao main preferences dao
	 * @param viewport     viewport for the stage
	 * @param camera       camera to use
	 * @param skin         game skin
	 */
	@Inject
	public PreferencesStage(EventBus eventBus, PreferencesSlide preferencesSlide, MainPreferencesDao mainPrefsDao,
			@MenuViewport Viewport viewport, @MenuCamera OrthographicCamera camera, Skin skin) {
		super(viewport, Arrays.asList(preferencesSlide),
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.MAIN_MENU_SCREEN)), camera,
				skin);
		this.eventBus = eventBus;
		this.preferencesSlide = preferencesSlide;
		this.mainPrefsDao = mainPrefsDao;
		initUi();
	}

	private void initUi() {
		Stream.of(preferencesSlide.getForgottenKingdomSelectBox(), preferencesSlide.getShowEnemyTurnsSelectBox())
				.forEach((actor) -> actor
						.addListener(new ExceptionLoggingChangeListener(() -> sendPreferencesChangedEvent())));
	}

	private void sendPreferencesChangedEvent() {
		eventBus.post(new MainPreferencesChangeEvent(
				new MainGamePreferences(preferencesSlide.getForgottenKingdomSelectBox().getSelected(),
						preferencesSlide.getShowEnemyTurnsSelectBox().getSelected())));
	}

	@Override
	public void reset() {
		super.reset();
		// sync the UI with the current preferences
		MainGamePreferences currentPreferences = mainPrefsDao.getMainPreferences();
		preferencesSlide.getForgottenKingdomSelectBox().setSelected(currentPreferences.isWarnAboutForgottenKingdoms());
		preferencesSlide.getShowEnemyTurnsSelectBox().setSelected(currentPreferences.isShowEnemyTurns());
	}

}

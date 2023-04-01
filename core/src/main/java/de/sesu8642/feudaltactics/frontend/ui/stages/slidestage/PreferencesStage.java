// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.ui.stages.slidestage;

import java.util.Arrays;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MenuCamera;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MenuViewport;
import de.sesu8642.feudaltactics.frontend.events.MainPreferencesChangeEvent;
import de.sesu8642.feudaltactics.frontend.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.frontend.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.frontend.persistence.MainGamePreferences;
import de.sesu8642.feudaltactics.frontend.persistence.MainPreferencesDao;

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
				.forEach((actor) -> actor.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						sendPreferencesChangedEvent();
					}
				}));
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

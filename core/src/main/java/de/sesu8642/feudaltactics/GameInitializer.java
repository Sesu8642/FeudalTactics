// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.backend.gamelogic.persistence.AutoSaveRepository;
import de.sesu8642.feudaltactics.events.GameResumedEvent;
import de.sesu8642.feudaltactics.frontend.ScreenNavigationController;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.VersionProperty;
import de.sesu8642.feudaltactics.frontend.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.frontend.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.frontend.persistence.GameVersionDao;

/**
 * Class for initializing the game in a non-static context that can get injected
 * dependencies.
 */
@Singleton
public class GameInitializer {

	private EventBus eventBus;
	private GameVersionDao gameVersionDao;
	private AutoSaveRepository autoSaveRepository;
	private ScreenNavigationController screenNavigationController;
	private String gameVersion;

	/** Constructor. */
	@Inject
	public GameInitializer(EventBus eventBus, GameVersionDao gameVersionDao, AutoSaveRepository autoSaveRepository,
			ScreenNavigationController screenNavigationController, @VersionProperty String gameVersion) {
		this.eventBus = eventBus;
		this.gameVersionDao = gameVersionDao;
		this.autoSaveRepository = autoSaveRepository;
		this.screenNavigationController = screenNavigationController;
		this.gameVersion = gameVersion;

	}

	void initializeGame() {

		// enable debug logging to console
		Gdx.app.setLogLevel(Application.LOG_DEBUG);

		// do not close on android back key
		Gdx.input.setCatchKey(Keys.BACK, true);

		eventBus.register(screenNavigationController);

		// show appropriate screen
		if (autoSaveRepository.getNoOfAutoSaves() > 0) {
			// resume running game
			eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INGAME_SCREEN));
			eventBus.post(new GameResumedEvent());
		} else {
			// fresh start
			eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.SPLASH_SCREEN));
		}

		// save current game version
		gameVersionDao.saveGameVersion(gameVersion);

	}

}

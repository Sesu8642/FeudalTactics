// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.dagger.DaggerFeudalTacticsComponent;
import de.sesu8642.feudaltactics.dagger.FeudalTacticsComponent;
import de.sesu8642.feudaltactics.events.GameResumedEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.preferences.PreferencesHelper;

/** The game's entry point. */
public class FeudalTactics extends Game {

	// this needs to be accessed somehow by the other classes and cannot be provided
	// by DI because it is created by the libGDX framework
	static FeudalTactics game;

	// TODO: put those in a custom skin
	public static final Color buttonIconColor = new Color(1, 0.7F, 0.15F, 1);
	public static final Color disabledButtonIconColor = new Color(0.75F, 0.75F, 0.75F, 1);
	public static final Color backgroundColor = new Color(0, 0.2f, 0.8f, 1);

	private FeudalTacticsComponent component;

	@Override
	public void create() {
		game = this;
		// if Eclipse cannot resolve this: https://stackoverflow.com/a/31669111 (note:
		// too lazy to try it)
		component = DaggerFeudalTacticsComponent.create();

		EventBus eventBus = component.getEventBus();
		eventBus.register(component.getScreenTransitionController());

		String gameVersion = component.getGameVersion();
		PreferencesHelper.saveGameVersion(gameVersion);

		if (PreferencesHelper.getNoOfAutoSaves() > 0) {
			eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INGAME_SCREEN));
			eventBus.post(new GameResumedEvent());
		} else {
			eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.SPLASH_SCREEN));
		}
		// do not close on android back key
		Gdx.input.setCatchKey(Keys.BACK, true);
	}

	@Override
	public void dispose() {
		// shutdown executor services to kill all background threads
		component.getBotAiExecutor().shutdownNow();
		super.dispose();
	}

}
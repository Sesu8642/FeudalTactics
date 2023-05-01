// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Preferences;

import de.sesu8642.feudaltactics.menu.preferences.dagger.GamePrefsPrefStore;

/** Data access object for the main preferences. */
@Singleton
public class MainPreferencesDao {

	public static final String MAIN_PREFERENCES_NAME = "gamePreferences";

	private static final String WARN_ABOUT_FORGOTTEN_KINGDOMS_NAME = "warnAboutForgottenKingdoms";
	private static final String SHOW_ENEMY_TURNS_NAME = "showEnemyTurns";

	private final Preferences prefStore;

	@Inject
	public MainPreferencesDao(@GamePrefsPrefStore Preferences gamePrefs) {
		this.prefStore = gamePrefs;
	}

	/**
	 * Saves the preferences the users configured in the main preferences menu.
	 * 
	 * @param prefs preferences to save
	 */
	public void saveMainPreferences(MainGamePreferences prefs) {
		prefStore.putBoolean(WARN_ABOUT_FORGOTTEN_KINGDOMS_NAME, prefs.isWarnAboutForgottenKingdoms());
		prefStore.putBoolean(SHOW_ENEMY_TURNS_NAME, prefs.isShowEnemyTurns());
		prefStore.flush();
	}

	/**
	 * Loads the preferences the users configured in the main preferences menu.
	 * 
	 * @return preferences to load
	 */
	public MainGamePreferences getMainPreferences() {
		boolean warnAboutForgottenKingdoms = prefStore.getBoolean(WARN_ABOUT_FORGOTTEN_KINGDOMS_NAME, true);
		boolean showEnemyTurns = prefStore.getBoolean(SHOW_ENEMY_TURNS_NAME, true);
		return new MainGamePreferences(warnAboutForgottenKingdoms, showEnemyTurns);
	}

}
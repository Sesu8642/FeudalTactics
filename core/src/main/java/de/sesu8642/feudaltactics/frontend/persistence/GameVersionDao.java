// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.persistence;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

/** Data access object for the (previous) game version. */
@Singleton
public class GameVersionDao {

	private static final String VERSION_PREFERENCES_NAME = "versionPreferences";
	private static final String VERSION_GAME_VERSION_NAME = "gameVersion";

	@Inject
	public GameVersionDao() {
		// empty constructor for DI
	}

	/**
	 * Saves the given game version.
	 * 
	 * @param version version to save
	 */
	public void saveGameVersion(String version) {
		Preferences versionPrefs = Gdx.app.getPreferences(VERSION_PREFERENCES_NAME);
		versionPrefs.putString(VERSION_GAME_VERSION_NAME, version);
		versionPrefs.flush();
	}

}
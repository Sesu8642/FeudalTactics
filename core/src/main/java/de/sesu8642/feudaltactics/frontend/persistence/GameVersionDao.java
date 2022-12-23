// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.persistence;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Preferences;

import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.GameVersionPrefStore;

/** Data access object for the (previous) game version. */
@Singleton
public class GameVersionDao {

	public static final String VERSION_PREFERENCES_NAME = "versionPreferences";
	private static final String VERSION_GAME_VERSION_NAME = "gameVersion";

	private Preferences versionPrefs;

	@Inject
	public GameVersionDao(@GameVersionPrefStore Preferences versionPrefs) {
		this.versionPrefs = versionPrefs;
	}

	/**
	 * Saves the given game version.
	 * 
	 * @param version version to save
	 */
	public void saveGameVersion(String version) {
		versionPrefs.putString(VERSION_GAME_VERSION_NAME, version);
		versionPrefs.flush();
	}

}
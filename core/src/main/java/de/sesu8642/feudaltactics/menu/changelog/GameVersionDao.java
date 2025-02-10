// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.changelog;

import com.badlogic.gdx.Preferences;
import de.sesu8642.feudaltactics.menu.changelog.dagger.GameVersionPrefStore;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Data access object for the (previous) game version.
 */
@Singleton
public class GameVersionDao {

    public static final String VERSION_PREFERENCES_NAME = "versionPreferences";
    private static final String VERSION_GAME_VERSION_NAME = "gameVersion";

    private final Preferences prefStore;

    @Inject
    public GameVersionDao(@GameVersionPrefStore Preferences prefStore) {
        this.prefStore = prefStore;
    }

    /**
     * Loads the game version.
     */
    public String getGameVersion() {
        return prefStore.getString(VERSION_GAME_VERSION_NAME);
    }

    /**
     * Saves the given game version.
     */
    public void saveGameVersion(String version) {
        prefStore.putString(VERSION_GAME_VERSION_NAME, version);
        prefStore.flush();
    }

}
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
    private static final String CHANGELOG_STATE_NAME = "showChangeLogNextTime";

    private final Preferences versionPrefs;

    @Inject
    public GameVersionDao(@GameVersionPrefStore Preferences versionPrefs) {
        this.versionPrefs = versionPrefs;
    }

    /**
     * Loads the game version.
     */
    public String getGameVersion() {
        return versionPrefs.getString(VERSION_GAME_VERSION_NAME);
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

    /**
     * Loads changelog state (whether to show the changelog next time).
     */
    public boolean getChangelogState() {
        return versionPrefs.getBoolean(CHANGELOG_STATE_NAME, false);
    }

    /**
     * Saves the given changelog state.
     *
     * @param state whether to show the changelog next time
     */
    public void saveChangelogState(boolean state) {
        versionPrefs.putBoolean(CHANGELOG_STATE_NAME, state);
        versionPrefs.flush();
    }

}
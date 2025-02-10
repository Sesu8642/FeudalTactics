// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences;

import com.badlogic.gdx.Preferences;
import de.sesu8642.feudaltactics.menu.preferences.dagger.NagPrefsPrefStore;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Data access object for the main preferences.
 */
@Singleton
public class NagPreferencesDao {

    public static final String NAG_PREFERENCES_NAME = "nagPreferences";
    private static final String SHOW_TUTORIAL_NAG_NAME = "showTutorialNag";
    private static final String CHANGELOG_STATE_NAME = "showChangeLog";

    private final Preferences prefStore;

    @Inject
    public NagPreferencesDao(@NagPrefsPrefStore Preferences gamePrefs) {
        this.prefStore = gamePrefs;
    }

    /**
     * Loads whether to show the tutorial nag or not.
     */
    public boolean getShowTutorialNag() {
        return prefStore.getBoolean(SHOW_TUTORIAL_NAG_NAME, true);
    }

    /**
     * Saves whether to show the tutorial nag or not.
     */
    public void setShowTutorialNag(boolean showTutorialNag) {
        prefStore.putBoolean(SHOW_TUTORIAL_NAG_NAME, showTutorialNag);
        prefStore.flush();
    }

    /**
     * Loads changelog state (whether to show the changelog next time).
     */
    public boolean getShowChangelog() {
        return prefStore.getBoolean(CHANGELOG_STATE_NAME, false);
    }

    /**
     * Saves the given changelog state (whether to show the changelog next time).
     */
    public void setShowChangelog(boolean showChangelog) {
        prefStore.putBoolean(CHANGELOG_STATE_NAME, showChangelog);
        prefStore.flush();
    }

}
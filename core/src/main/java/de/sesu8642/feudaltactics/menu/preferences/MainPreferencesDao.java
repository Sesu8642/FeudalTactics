// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences;

import com.badlogic.gdx.Preferences;
import de.sesu8642.feudaltactics.menu.preferences.dagger.GamePrefsPrefStore;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Data access object for the main preferences.
 */
@Singleton
public class MainPreferencesDao {

    public static final String MAIN_PREFERENCES_NAME = "gamePreferences";
    private static final String WARN_ABOUT_FORGOTTEN_KINGDOMS_NAME = "warnAboutForgottenKingdoms";
    private static final String SHOW_ENEMY_TURNS_NAME = "showEnemyTurns";
    private static final String LANGUAGE = "language";

    private final Preferences prefStore;

    @Inject
    public MainPreferencesDao(@GamePrefsPrefStore Preferences gamePrefs) {
        prefStore = gamePrefs;
    }

    /**
     * Saves the preferences the users configured in the main preferences menu.
     *
     * @param prefs preferences to save
     */
    public void saveMainPreferences(MainGamePreferences prefs) {
        prefStore.putBoolean(WARN_ABOUT_FORGOTTEN_KINGDOMS_NAME, prefs.isWarnAboutForgottenKingdoms());
        prefStore.putBoolean(SHOW_ENEMY_TURNS_NAME, prefs.isShowEnemyTurns());
        prefStore.putString(LANGUAGE, prefs.getLanguage());
        prefStore.flush();
    }

    /**
     * Loads the preferences the users configured in the main preferences menu.
     *
     * @return preferences to load
     */
    public MainGamePreferences getMainPreferences() {
        final boolean warnAboutForgottenKingdoms = prefStore.getBoolean(WARN_ABOUT_FORGOTTEN_KINGDOMS_NAME, true);
        final boolean showEnemyTurns = prefStore.getBoolean(SHOW_ENEMY_TURNS_NAME, true);
        final String language = prefStore.getString(LANGUAGE, "English");
        return new MainGamePreferences(warnAboutForgottenKingdoms, showEnemyTurns, language);
    }
}

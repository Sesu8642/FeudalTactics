// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences;

import com.badlogic.gdx.Preferences;
import de.sesu8642.feudaltactics.lib.ingame.botai.Speed;
import de.sesu8642.feudaltactics.localization.SupportedLanguage;
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
    private static final String ENEMY_TURN_SPEED_NAME = "enemyTurnSpeed";
    private static final String LANGUAGE_NAME = "language";

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
        prefStore.putString(ENEMY_TURN_SPEED_NAME, prefs.getEnemyTurnSpeed().name());
        if (prefs.getLanguage() == SupportedLanguage.AUTO) {
            prefStore.remove(LANGUAGE_NAME);
        } else {
            prefStore.putString(LANGUAGE_NAME, prefs.getLanguage().getLanguageTag());
        }
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
        final Speed enemyTurnSpeed = Speed.valueOf(prefStore.getString(ENEMY_TURN_SPEED_NAME, Speed.NORMAL.name()));
        final String languageTag = prefStore.getString(LANGUAGE_NAME);
        final SupportedLanguage language = !languageTag.isEmpty() ? SupportedLanguage.fromLanguageTag(languageTag) :
            SupportedLanguage.AUTO;
        return new MainGamePreferences(warnAboutForgottenKingdoms, showEnemyTurns, enemyTurnSpeed, language);
    }
}

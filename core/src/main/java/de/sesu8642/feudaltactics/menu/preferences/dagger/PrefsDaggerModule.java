// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences.dagger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.dagger.PreferencesPrefixProperty;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;
import de.sesu8642.feudaltactics.menu.preferences.NagPreferencesDao;

import javax.inject.Singleton;

/**
 * Dagger module for the game preferences.
 */
@Module
public class PrefsDaggerModule {

    private PrefsDaggerModule() {
        // prevent instantiation
        throw new AssertionError();
    }

    @Provides
    @Singleton
    @GamePrefsPrefStore
    static Preferences provideGamePrefsPrefStore(@PreferencesPrefixProperty String prefix) {
        return Gdx.app.getPreferences(prefix + MainPreferencesDao.MAIN_PREFERENCES_NAME);
    }

    @Provides
    @Singleton
    @NagPrefsPrefStore
    static Preferences provideNagPrefsPrefStore(@PreferencesPrefixProperty String prefix) {
        return Gdx.app.getPreferences(prefix + NagPreferencesDao.NAG_PREFERENCES_NAME);
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences.dagger;

import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.dagger.PreferencesPrefixProperty;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;

/** Dagger module for the main game preferences. */
@Module
public class GamePrefsDaggerModule {

	private GamePrefsDaggerModule() {
		// prevent instantiation
		throw new AssertionError();
	}

	@Provides
	@Singleton
	@GamePrefsPrefStore
	static Preferences provideGamePrefsPrefStore(@PreferencesPrefixProperty String prefix) {
		return Gdx.app.getPreferences(prefix + MainPreferencesDao.MAIN_PREFERENCES_NAME);
	}

}

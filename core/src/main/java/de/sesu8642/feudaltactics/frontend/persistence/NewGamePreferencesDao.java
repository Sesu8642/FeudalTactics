// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.persistence;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;

import de.sesu8642.feudaltactics.backend.ingame.BotAi.Intelligence;
import de.sesu8642.feudaltactics.frontend.persistence.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.frontend.persistence.NewGamePreferences.MapSizes;

/** Data access object for the new game preferences. */
@Singleton
public class NewGamePreferencesDao {

	private static final String NEW_GAME_PREFERENCES_NAME = "newGamePreferences";
	private static final String NEW_GAME_PREFERENCES_DENSITY_NAME = "density";
	private static final String NEW_GAME_PREFERENCES_MAP_SIZE_NAME = "mapSize";
	private static final String NEW_GAME_PREFERENCES_BOT_INTELLIGENCE_NAME = "botIntelligence";

	private final Preferences prefStore = Gdx.app.getPreferences(NEW_GAME_PREFERENCES_NAME);

	@Inject
	public NewGamePreferencesDao() {
		// empty constructor for DI
	}

	/**
	 * Saves the preferences the users configured last when starting a new game.
	 * 
	 * @param prefs preferences to save
	 */
	public void saveNewGamePreferences(NewGamePreferences prefs) {
		prefStore.putInteger(NEW_GAME_PREFERENCES_BOT_INTELLIGENCE_NAME, prefs.getBotIntelligence().ordinal());
		prefStore.putInteger(NEW_GAME_PREFERENCES_MAP_SIZE_NAME, prefs.getMapSize().ordinal());
		prefStore.putInteger(NEW_GAME_PREFERENCES_DENSITY_NAME, prefs.getDensity().ordinal());
		prefStore.flush();
	}

	/**
	 * Loads the preferences the users configured last when starting a new game.
	 * 
	 * @return preferences to load
	 */
	public NewGamePreferences getNewGamePreferences() {
		Intelligence botIntelligence = Intelligence.values()[prefStore
				.getInteger(NEW_GAME_PREFERENCES_BOT_INTELLIGENCE_NAME, 0)];
		MapSizes mapSize = MapSizes.values()[prefStore.getInteger(NEW_GAME_PREFERENCES_MAP_SIZE_NAME, 0)];
		Densities density = Densities.values()[prefStore.getInteger(NEW_GAME_PREFERENCES_DENSITY_NAME, 0)];
		return new NewGamePreferences(botIntelligence, mapSize, density);
	}

}
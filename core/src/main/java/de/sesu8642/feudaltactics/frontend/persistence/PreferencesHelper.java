// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.persistence;

import java.util.Map;
import java.util.Optional;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import de.sesu8642.feudaltactics.backend.exceptions.SaveLoadingException;
import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.GameState;
import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.GameStateSerializer;
import de.sesu8642.feudaltactics.backend.gamelogic.ingame.BotAi.Intelligence;
import de.sesu8642.feudaltactics.frontend.persistence.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.frontend.persistence.NewGamePreferences.MapSizes;

/** Helper class for saving and loading preferences. */
public class PreferencesHelper {
	private static final String VERSION_PREFERENCES_NAME = "versionPreferences";
	private static final String VERSION_GAME_VERSION_NAME = "gameVersion";
	private static final String AUTO_SAVE_PREFERENCES_NAME = "autoSavePreferences";
	private static final String NEW_GAME_PREFERENCES_DENSITY_NAME = "density";
	private static final String NEW_GAME_PREFERENCES_MAP_SIZE_NAME = "mapSize";
	private static final String NEW_GAME_PREFERENCES_BOT_INTELLIGENCE_NAME = "botIntelligence";
	private static final String NEW_GAME_PREFERENCES_NAME = "newGamePreferences";
	private static final int MAX_AUTOSAVES = 50;
	private static final String MAIN_PREFERENCES_NAME = "gamePreferences";
	private static final String WARN_ABOUT_FORGOTTEN_KINGDOMS_NAME = "warnAboutForgottenKingdoms";
	private static final String SHOW_ENEMY_TURNS_NAME = "showEnemyTurns";

	private static final String TAG = PreferencesHelper.class.getName();

	private PreferencesHelper() {
		// utility class -> prevent instantiation
		throw new AssertionError();
	}

	/**
	 * Saves the given game version.
	 * 
	 * @param version version to save
	 */
	public static void saveGameVersion(String version) {
		Preferences versionPrefs = Gdx.app.getPreferences(VERSION_PREFERENCES_NAME);
		versionPrefs.putString(VERSION_GAME_VERSION_NAME, version);
		versionPrefs.flush();
	}

	/**
	 * Saves the preferences the users configured last when starting a new game.
	 * 
	 * @param prefs preferences to save
	 */
	public static void saveNewGamePreferences(NewGamePreferences prefs) {
		Preferences newGamePrefs = Gdx.app.getPreferences(NEW_GAME_PREFERENCES_NAME);
		newGamePrefs.putInteger(NEW_GAME_PREFERENCES_BOT_INTELLIGENCE_NAME, prefs.getBotIntelligence().ordinal());
		newGamePrefs.putInteger(NEW_GAME_PREFERENCES_MAP_SIZE_NAME, prefs.getMapSize().ordinal());
		newGamePrefs.putInteger(NEW_GAME_PREFERENCES_DENSITY_NAME, prefs.getDensity().ordinal());
		newGamePrefs.flush();
	}

	/**
	 * Loads the preferences the users configured last when starting a new game.
	 * 
	 * @return preferences to load
	 */
	public static NewGamePreferences getNewGamePreferences() {
		Preferences prefStore = Gdx.app.getPreferences(NEW_GAME_PREFERENCES_NAME);
		Intelligence botIntelligence = Intelligence.values()[prefStore
				.getInteger(NEW_GAME_PREFERENCES_BOT_INTELLIGENCE_NAME, 0)];
		MapSizes mapSize = MapSizes.values()[prefStore.getInteger(NEW_GAME_PREFERENCES_MAP_SIZE_NAME, 0)];
		Densities density = Densities.values()[prefStore.getInteger(NEW_GAME_PREFERENCES_DENSITY_NAME, 0)];
		return new NewGamePreferences(botIntelligence, mapSize, density);
	}

	/**
	 * Saves the preferences the users configured in the main preferences menu.
	 * 
	 * @param prefs preferences to save
	 */
	public static void saveMainPreferences(MainGamePreferences prefs) {
		Preferences prefStore = Gdx.app.getPreferences(MAIN_PREFERENCES_NAME);
		prefStore.putBoolean(WARN_ABOUT_FORGOTTEN_KINGDOMS_NAME, prefs.isWarnAboutForgottenKingdoms());
		prefStore.putBoolean(SHOW_ENEMY_TURNS_NAME, prefs.isShowEnemyTurns());
		prefStore.flush();
	}

	/**
	 * Loads the preferences the users configured in the main preferences menu.
	 * 
	 * @return preferences to load
	 */
	public static MainGamePreferences getMainPreferences() {
		Preferences prefStore = Gdx.app.getPreferences(MAIN_PREFERENCES_NAME);
		boolean warnAboutForgottenKingdoms = prefStore.getBoolean(WARN_ABOUT_FORGOTTEN_KINGDOMS_NAME);
		boolean showEnemyTurns = prefStore.getBoolean(SHOW_ENEMY_TURNS_NAME);
		return new MainGamePreferences(warnAboutForgottenKingdoms, showEnemyTurns);
	}

	/**
	 * Saves a game state (autosave).
	 * 
	 * @param gameState game state to save
	 */
	public static void autoSaveGameState(GameState gameState) {
		Gdx.app.debug(TAG, "autosaving");
		Preferences prefStore = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
		String saveString = null;
		Json json = new Json(OutputType.json);
		json.setSerializer(GameState.class, new GameStateSerializer());
		saveString = json.toJson(gameState, GameState.class);
		// using current time as key
		prefStore.putString(String.valueOf(System.currentTimeMillis()), saveString);
		prefStore.flush();
		deleteAllAutoSaveExceptLatestN(MAX_AUTOSAVES);
	}

	/**
	 * Loads the last autosave.
	 * 
	 * @return loaded game state
	 */
	public static GameState getLatestAutoSave() {
		Preferences prefStore = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
		if (prefStore.get().isEmpty()) {
			throw new SaveLoadingException("No autosave available");
		}
		// cannot be empty if there is a save
		String latestSaveName = getLatestAutoSaveName().get();
		String loadedString = prefStore.getString(latestSaveName);
		JsonValue loadedStateJsonValue = new JsonReader().parse(loadedString);
		Json json = new Json();
		json.setSerializer(GameState.class, new GameStateSerializer());
		return json.readValue(GameState.class, loadedStateJsonValue);
	}

	/**
	 * Deletes the newest autosave.
	 */
	public static void deleteLatestAutoSave() {
		Optional<String> latestSaveNameOptional = getLatestAutoSaveName();
		latestSaveNameOptional.ifPresent(latestSaveName -> {
			Preferences prefStore = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
			prefStore.remove(latestSaveName);
			prefStore.flush();
		});
	}

	/**
	 * Determines the name (key) of the newest autosave.
	 */
	private static Optional<String> getLatestAutoSaveName() {
		Preferences prefStore = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
		Map<String, ?> prefsMap = prefStore.get();
		if (prefsMap.isEmpty()) {
			return Optional.empty();
		}
		return prefsMap.keySet().stream().max((a, b) -> Long.parseLong(a) > Long.parseLong(b) ? 1 : -1);
	}

	/**
	 * Deletes all autosaves except for the newest n.
	 * 
	 * @param n number of autosaves to keep.
	 */
	public static void deleteAllAutoSaveExceptLatestN(int n) {
		Preferences prefStore = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
		Map<String, ?> prefsMap = prefStore.get();
		int noOfAutoSaves = prefsMap.size();
		if (n > noOfAutoSaves) {
			return;
		}
		// sort by age (oldest first) and remove the oldest ones
		prefsMap.keySet().stream().sorted((a, b) -> Long.parseLong(a) > Long.parseLong(b) ? 1 : -1)
				.limit(noOfAutoSaves - n).forEach(prefStore::remove);
		prefStore.flush();
	}

	/**
	 * Determines how many autosaves exist.
	 */
	public static int getNoOfAutoSaves() {
		return Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME).get().size();
	}

}

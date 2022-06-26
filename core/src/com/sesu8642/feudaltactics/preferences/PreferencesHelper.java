package com.sesu8642.feudaltactics.preferences;

import java.util.Map;
import java.util.Optional;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.sesu8642.feudaltactics.exceptions.SaveLoadingException;
import com.sesu8642.feudaltactics.gamelogic.BotAi.Intelligence;
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameState;
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameStateSerializer;
import com.sesu8642.feudaltactics.preferences.NewGamePreferences.Densities;
import com.sesu8642.feudaltactics.preferences.NewGamePreferences.MapSizes;

/** Helper class for saving and loading preferences. */
public class PreferencesHelper {
	private static final String AUTO_SAVE_PREFERENCES_NAME = "autoSavePreferences";
	private static final String NEW_GAME_PREFERENCES_DENSITY_NAME = "density";
	private static final String NEW_GAME_PREFERENCES_MAP_SIZE_NAME = "mapSize";
	private static final String NEW_GAME_PREFERENCES_BOT_INTELLIGENCE_NAME = "botIntelligence";
	private static final String NEW_GAME_PREFERENCES_NAME = "newGamePreferences";
	private static final int MAX_AUTOSAVES = 50;

	private PreferencesHelper() {
		// utility class -> prevent instantiation
		throw new AssertionError();
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
		Preferences newGamePrefs = Gdx.app.getPreferences(NEW_GAME_PREFERENCES_NAME);
		Intelligence botIntelligence = Intelligence.values()[newGamePrefs
				.getInteger(NEW_GAME_PREFERENCES_BOT_INTELLIGENCE_NAME, 0)];
		MapSizes mapSize = MapSizes.values()[newGamePrefs.getInteger(NEW_GAME_PREFERENCES_MAP_SIZE_NAME, 0)];
		Densities density = Densities.values()[newGamePrefs.getInteger(NEW_GAME_PREFERENCES_DENSITY_NAME, 0)];
		return new NewGamePreferences(botIntelligence, mapSize, density);
	}

	/**
	 * Saves a game state (autosave).
	 * 
	 * @param gameState game state to save
	 */
	public static void autoSaveGameState(GameState gameState) {
		Preferences autoSavePrefs = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
		String saveString = null;
		Json json = new Json(OutputType.json);
		json.setSerializer(GameState.class, new GameStateSerializer());
		saveString = json.toJson(gameState, GameState.class);
		// using current time as key
		autoSavePrefs.putString(String.valueOf(System.currentTimeMillis()), saveString);
		autoSavePrefs.flush();
		deleteAllAutoSaveExceptLatestN(MAX_AUTOSAVES);
	}

	/**
	 * Loads the last autosave.
	 * 
	 * @return loaded game state
	 */
	public static GameState getLatestAutoSave() {
		Preferences autoSavePrefs = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
		if (autoSavePrefs.get().isEmpty()) {
			throw new SaveLoadingException("No autosave available");
		}
		// cannot be empty if there is a save
		String latestSaveName = getLatestAutoSaveName().get();
		String loadedString = autoSavePrefs.getString(latestSaveName);
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
			Preferences autoSavePrefs = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
			autoSavePrefs.remove(latestSaveName);
			autoSavePrefs.flush();
		});
	}

	/**
	 * Determines the name (key) of the newest autosave.
	 */
	private static Optional<String> getLatestAutoSaveName() {
		Preferences autoSavePrefs = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
		Map<String, ?> prefsMap = autoSavePrefs.get();
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
		Preferences autoSavePrefs = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
		Map<String, ?> prefsMap = autoSavePrefs.get();
		int noOfAutoSaves = prefsMap.size();
		if (n > noOfAutoSaves) {
			return;
		}
		// sort by age (oldest first) and remove the oldest ones
		prefsMap.keySet().stream().sorted((a, b) -> Long.parseLong(a) > Long.parseLong(b) ? 1 : -1)
				.limit(noOfAutoSaves - n).forEach(autoSavePrefs::remove);
		autoSavePrefs.flush();
	}

	/**
	 * Determines how many autosaves exist.
	 */
	public static int getNoOfAutoSaves() {
		return Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME).get().size();
	}

}

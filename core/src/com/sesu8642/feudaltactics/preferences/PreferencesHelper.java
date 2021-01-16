package com.sesu8642.feudaltactics.preferences;

import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.sesu8642.feudaltactics.BotAI.Intelligence;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.GameStateSerializer;
import com.sesu8642.feudaltactics.preferences.NewGamePreferences.Densities;
import com.sesu8642.feudaltactics.preferences.NewGamePreferences.MapSizes;

public class PreferencesHelper {
	private static final String AUTO_SAVE_PREFERENCES_NAME = "autoSavePreferences";
	private static final String NEW_GAME_PREFERENCES_DENSITY_NAME = "density";
	private static final String NEW_GAME_PREFERENCES_MAP_SIZE_NAME = "mapSize";
	private static final String NEW_GAME_PREFERENCES_BOT_INTELLIGENCE_NAME = "botIntelligence";
	private static final String NEW_GAME_PREFERENCES_NAME = "newGamePreferences";
	private static final int MAX_AUTOSAVES = 50;

	public static void saveNewGamePreferences(NewGamePreferences prefs) {
		Preferences newGamePrefs = Gdx.app.getPreferences(NEW_GAME_PREFERENCES_NAME);
		newGamePrefs.putInteger(NEW_GAME_PREFERENCES_BOT_INTELLIGENCE_NAME, prefs.getBotIntelligence().ordinal());
		newGamePrefs.putInteger(NEW_GAME_PREFERENCES_MAP_SIZE_NAME, prefs.getMapSize().ordinal());
		newGamePrefs.putInteger(NEW_GAME_PREFERENCES_DENSITY_NAME, prefs.getDensity().ordinal());
		newGamePrefs.flush();
	}

	public static NewGamePreferences getNewGamePreferences() {
		Preferences newGamePrefs = Gdx.app.getPreferences(NEW_GAME_PREFERENCES_NAME);
		Intelligence botIntelligence = Intelligence.values()[newGamePrefs.getInteger(NEW_GAME_PREFERENCES_BOT_INTELLIGENCE_NAME, 0)];
		MapSizes mapSize = MapSizes.values()[newGamePrefs.getInteger(NEW_GAME_PREFERENCES_MAP_SIZE_NAME, 0)];
		Densities density = Densities.values()[newGamePrefs.getInteger(NEW_GAME_PREFERENCES_DENSITY_NAME, 0)];
		return new NewGamePreferences(botIntelligence, mapSize, density);
	}

	public static void autoSaveGameState(GameState gameState) {
		Preferences autoSavePrefs = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
		String saveString = null;
		Json json = new Json(OutputType.json);
		json.setSerializer(GameState.class, new GameStateSerializer());
		saveString = json.toJson(gameState, GameState.class);
		autoSavePrefs.putString(String.valueOf(System.currentTimeMillis()), saveString);
		autoSavePrefs.flush();
		deleteAllAutoSaveExceptLatestN(MAX_AUTOSAVES);
	}

	public static GameState getLatestAutoSave() {
		Preferences autoSavePrefs = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
		if (autoSavePrefs.get().isEmpty()) {
			return null;
		}
		String latestSaveName = getLatestAutoSaveName();
		String loadedString = autoSavePrefs.getString(latestSaveName);
		JsonValue loadedStateJsonValue = new JsonReader().parse(loadedString);
		Json json = new Json();
		json.setSerializer(GameState.class, new GameStateSerializer());
		return json.readValue(GameState.class, loadedStateJsonValue);
	}

	public static void deleteLatestAutoSave() {
		String latestSaveName = getLatestAutoSaveName();
		if (latestSaveName != null) {
			Preferences autoSavePrefs = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
			autoSavePrefs.remove(latestSaveName);
			autoSavePrefs.flush();
		}
	}

	private static String getLatestAutoSaveName() {
		Preferences autoSavePrefs = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
		Map<String, ?> prefsMap = autoSavePrefs.get();
		if (prefsMap.isEmpty()) {
			return null;
		}
		return prefsMap.keySet().stream().max((a, b) -> Long.parseLong(a) > Long.parseLong(b) ? 1 : -1).get();
	}

	public static void deleteAllAutoSaveExceptLatestN(int n) {
		Preferences autoSavePrefs = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);
		Map<String, ?> prefsMap = autoSavePrefs.get();
		int noOfAutoSaves = prefsMap.size();
		if (n > noOfAutoSaves) {
			return;
		}
		// sort by age (oldest first) and remove the oldest ones
		prefsMap.keySet().stream().sorted((a, b) -> Long.parseLong(a) > Long.parseLong(b) ? 1 : -1)
				.limit(noOfAutoSaves - n).forEach(key -> {
					autoSavePrefs.remove(key);
				});
		autoSavePrefs.flush();
	}

	public static int getNoOfAutoSaves() {
		return Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME).get().size();
	}

}

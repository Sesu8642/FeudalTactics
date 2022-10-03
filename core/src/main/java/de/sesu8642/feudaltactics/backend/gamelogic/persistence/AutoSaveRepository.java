// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.backend.gamelogic.persistence;

import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import de.sesu8642.feudaltactics.backend.exceptions.SaveLoadingException;
import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.GameState;
import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.GameStateSerializer;

/** Repository for autosaves. */
@Singleton
public class AutoSaveRepository {

	private static final String TAG = AutoSaveRepository.class.getName();
	private static final String AUTO_SAVE_PREFERENCES_NAME = "autoSavePreferences";
	private static final int MAX_AUTOSAVES = 50;

	Preferences prefStore = Gdx.app.getPreferences(AUTO_SAVE_PREFERENCES_NAME);

	@Inject
	public AutoSaveRepository() {
		// empty constructor for DI
	}

	/**
	 * Saves a game state (autosave).
	 * 
	 * @param gameState game state to save
	 */
	public void autoSaveGameState(GameState gameState) {
		Gdx.app.debug(TAG, "autosaving");
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
	public GameState getLatestAutoSave() {
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
	public void deleteLatestAutoSave() {
		Optional<String> latestSaveNameOptional = getLatestAutoSaveName();
		latestSaveNameOptional.ifPresent(latestSaveName -> {
			prefStore.remove(latestSaveName);
			prefStore.flush();
		});
	}

	/**
	 * Determines the name (key) of the newest autosave.
	 */
	private Optional<String> getLatestAutoSaveName() {
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
	public void deleteAllAutoSaveExceptLatestN(int n) {
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
	public int getNoOfAutoSaves() {
		return prefStore.get().size();
	}

}

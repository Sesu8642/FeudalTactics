// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import de.sesu8642.feudaltactics.exceptions.SaveLoadingException;
import de.sesu8642.feudaltactics.ingame.dagger.FullAutoSavePrefStore;
import de.sesu8642.feudaltactics.ingame.dagger.IncrementalAutoSavePrefStore;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.lib.gamestate.GameStateSerializer;
import de.sesu8642.feudaltactics.lib.ingame.PlayerMove;

/** Repository for autosaves. */
@Singleton
public class AutoSaveRepository {

	public static final String FULL_AUTO_SAVE_PREFERENCES_NAME = "fullAutoSavePreferences";

	public static final String INCREMENTAL_AUTO_SAVE_PREFERENCES_NAME = "incrementalAutoSavePreferences";

	private static final String FULL_GAME_SAVE_KEY_NAME = "fullGameSave";

	private static final String CURRENT_UNDO_DEPTH_NAME = "currentUndoDepth";

	private static final int MAX_UNDOS = 50;

	/**
	 * Number of incremental saves that can be saved before some will be merged into
	 * the last full save, leaving only the required amount to enable undoing enough
	 * moves.
	 */
	private static final int MAX_INCREMENTAL_SAVES = 100;

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private final Preferences fullAutoSavePrefStore;

	private final Preferences incrementalAutoSavePrefStore;

	private final Json fullSaveJson = new Json(OutputType.json);

	private final Json incrementalSaveJson = new Json(OutputType.json);

	private final JsonReader jsonReader = new JsonReader();

	private int currentUndoDepth;

	private long idCounter;

	@Inject
	public AutoSaveRepository(@FullAutoSavePrefStore Preferences fullAutoSavePrefStore,
			@IncrementalAutoSavePrefStore Preferences incrementalAutoSavePrefStore) {
		this.fullAutoSavePrefStore = fullAutoSavePrefStore;
		this.incrementalAutoSavePrefStore = incrementalAutoSavePrefStore;

		fullSaveJson.setSerializer(GameState.class, new GameStateSerializer());
		currentUndoDepth = incrementalAutoSavePrefStore.getInteger(CURRENT_UNDO_DEPTH_NAME, 0);
		idCounter = getLatestIncrementalSaveKey().map(Long::parseLong).orElse(0L);
	}

	/**
	 * Saves a game state (autosave).
	 */
	public void autoSaveFullGameState(GameState gameState) {
		logger.debug("autosaving full gamestate");

		String saveString = fullSaveJson.toJson(gameState, GameState.class);
		fullAutoSavePrefStore.putString(FULL_GAME_SAVE_KEY_NAME, saveString);
		fullAutoSavePrefStore.flush();
		incrementalAutoSavePrefStore.clear();
		incrementalAutoSavePrefStore.putInteger(CURRENT_UNDO_DEPTH_NAME, currentUndoDepth);
		incrementalAutoSavePrefStore.flush();
	}

	/**
	 * Saves a player move as an increment on top of an already existing full game
	 * save.
	 */
	public void autoSaveIncrementalPlayerMove(PlayerMove playerMove) {
		// after undoing sth and doing a new move, the player can undo this new move
		// again
		if (currentUndoDepth > 0) {
			incrementalAutoSavePrefStore.putInteger(CURRENT_UNDO_DEPTH_NAME, --currentUndoDepth);
			logger.info("undo depth decreased to " + currentUndoDepth);
		}

		// if there are too many incremental saves, merge them into the last full one
		if (incrementalAutoSavePrefStore.get().size() >= MAX_INCREMENTAL_SAVES) {
			logger.info("merging incremental saves into full save");
			GameState fullSave = getFullSave();
			SortedMap<Long, PlayerMove> incrementalSaves = getIncrementalSavesInOrder();
			// keep enough incremental saves to enable undoing moves
			SortedMap<Long, PlayerMove> incrementalSavesToMerge = incrementalSaves.subMap(incrementalSaves.firstKey(),
					new ArrayList<Long>(incrementalSaves.keySet()).get(MAX_UNDOS + 1));
			mergeIncrementalSavesIntoFull(fullSave, incrementalSavesToMerge.values());
			String saveStringFullSave = fullSaveJson.toJson(fullSave, GameState.class);
			fullAutoSavePrefStore.putString(FULL_GAME_SAVE_KEY_NAME, saveStringFullSave);
			fullAutoSavePrefStore.flush();
			for (Long mergedIncrementKey : incrementalSavesToMerge.keySet()) {
				logger.info("deleting incremental save with key {}", mergedIncrementKey);
				incrementalAutoSavePrefStore.remove(mergedIncrementKey.toString());
			}
		}

		// using current time as key
		String saveStringIncrement = incrementalSaveJson.toJson(playerMove);
		incrementalAutoSavePrefStore.putString(String.valueOf(++idCounter), saveStringIncrement);
		incrementalAutoSavePrefStore.flush();
	}

	private void mergeIncrementalSavesIntoFull(GameState fullSave, Collection<PlayerMove> incrementalSaves) {
		for (PlayerMove increment : incrementalSaves) {
			logger.debug("merging player move {} into full gamestate", increment.getPlayerActionType());
			GameStateHelper.applyPlayerMove(fullSave, increment);
		}
	}

	/**
	 * Loads the last autosave (with incremental saves merged).
	 * 
	 * @return loaded game state
	 */
	public GameState getCombinedAutoSave() {
		GameState lastFullSave = getFullSave();
		// now apply all the incremental saves on top (oldest first)
		Collection<PlayerMove> incrementalSaves = getIncrementalSavesInOrder().values();
		mergeIncrementalSavesIntoFull(lastFullSave, incrementalSaves);
		return lastFullSave;
	}

	private GameState getFullSave() {
		if (fullAutoSavePrefStore.get().isEmpty()) {
			throw new SaveLoadingException("No full save available");
		}
		// cannot be empty if there is a save
		String loadedString = fullAutoSavePrefStore.getString(FULL_GAME_SAVE_KEY_NAME);
		JsonValue loadedStateJsonValue = jsonReader.parse(loadedString);
		return fullSaveJson.readValue(GameState.class, loadedStateJsonValue);
	}

	private SortedMap<Long, PlayerMove> getIncrementalSavesInOrder() {
		return incrementalAutoSavePrefStore.get().entrySet().stream()
				.filter(entry -> !entry.getKey().equals(CURRENT_UNDO_DEPTH_NAME))
				.collect(Collectors.toMap(entry -> Long.parseLong(entry.getKey()),
						entry -> incrementalSaveJson.fromJson(PlayerMove.class, (String) entry.getValue()),
						(entry1, entry2) -> entry1, TreeMap::new));
	}

	/**
	 * Returns the last autosave as JSON string.
	 * 
	 * @return full save as JSON string
	 */
	public String getFullSaveAsString() {
		if (fullAutoSavePrefStore.get().isEmpty()) {
			throw new SaveLoadingException("No full save available");
		}
		// cannot be empty if there is a save
		return fullAutoSavePrefStore.getString(FULL_GAME_SAVE_KEY_NAME);
	}

	/**
	 * Returns the available incremental saves as string.
	 * 
	 * @return string representation of the incremental saves
	 */
	public String getIncrementalSavesAsString() {
		return incrementalAutoSavePrefStore.get().toString();
	}

	/**
	 * Deletes the newest autosave.
	 */
	public void deleteLatestIncrementalSave() {
		Optional<String> latestSaveKeyOptional = getLatestIncrementalSaveKey();
		latestSaveKeyOptional.ifPresent(latestSaveKey -> {
			incrementalAutoSavePrefStore.remove(latestSaveKey);
			incrementalAutoSavePrefStore.putInteger(CURRENT_UNDO_DEPTH_NAME, ++currentUndoDepth);
			incrementalAutoSavePrefStore.flush();
			logger.info("undo depth increased to " + currentUndoDepth);
		});
	}

	/**
	 * Determines the name (key) of the newest incremental autosave.
	 */
	private Optional<String> getLatestIncrementalSaveKey() {
		Map<String, ?> prefsMap = incrementalAutoSavePrefStore.get();
		if (prefsMap.isEmpty()) {
			return Optional.empty();
		}
		return prefsMap.keySet().stream().filter(key -> !key.equals(CURRENT_UNDO_DEPTH_NAME))
				.max((a, b) -> Long.compare(Long.parseLong(a), Long.parseLong(b)));
	}

	/**
	 * Deletes all autosaves: both full ones and increments.
	 */
	public void deleteAllAutoSaves() {
		fullAutoSavePrefStore.clear();
		fullAutoSavePrefStore.flush();
		incrementalAutoSavePrefStore.clear();
		incrementalAutoSavePrefStore.flush();
	}

	/**
	 * Returns if there is a saved game.
	 */
	public boolean hasFullAutosave() {
		return !fullAutoSavePrefStore.get().isEmpty();
	}

	/**
	 * Returns if undoing is possible.
	 */
	public boolean isUndoPossible() {
		// one entry is the undo depth
		return currentUndoDepth < MAX_UNDOS && incrementalAutoSavePrefStore.get().size() > 1;
	}

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonValue;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
import de.sesu8642.feudaltactics.menu.statistics.HistoricGame.GameResult;
import de.sesu8642.feudaltactics.menu.statistics.dagger.HistoryPrefsPrefStore;
import jakarta.inject.Inject;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data access object for the statistics. Stores Game History as local files.
 */
@Singleton
public class HistoryDao {

    public static final String HISTORY_NAME = "history";
    public static final String GAME_HISTORY_NAME = "HistoricGames";

    public static final int MAX_STORED_GAMES = 500;

    private final Preferences prefStore;

    private final Json json = new Json();

    @Inject
    public HistoryDao(@HistoryPrefsPrefStore Preferences historyPrefs) {
        this.prefStore = historyPrefs;
        json.setOutputType(JsonWriter.OutputType.json);
        registerSerializers();
    }

    /**
     * Teach the LibGDX Json mapper how to handle NewGamePreferences that lacks a zero-arg constructor.
     * We serialize NewGamePreferences as its existing sharable string format to avoid
     * adding a dummy constructor just for deserialization.
     */
    private void registerSerializers() {
        json.setSerializer(NewGamePreferences.class, new Json.Serializer<NewGamePreferences>() {
            @Override
            @SuppressWarnings("rawtypes")
            public void write(Json json, NewGamePreferences prefs, Class knownType) {
                json.writeValue(prefs.toSharableString());
            }

            @Override
            @SuppressWarnings("rawtypes")
            public NewGamePreferences read(Json json, JsonValue jsonData, Class type) {
                String sharedString = jsonData == null ? "" : jsonData.asString();
                return NewGamePreferences.fromSharableString(sharedString);
            }
        });
    }

    public void registerPlayedGame(GameState gameState, NewGamePreferences gamePreferences, GameResult gameResult) {
        if (gameState == null || gameState.getScenarioMap() != ScenarioMap.NONE) {
            return; // only record generated maps for now. We must treat ScenarioMaps differently.
        }

        Integer roundsPlayed = gameState.getWinningRound();
        if (null == roundsPlayed)
            // game was aborted, so return the current round. 
            // In other cases, winningRound is one lower than round, because round is incremented at the start of a round after evaluating victory conditions,
            // but before invoking this method.
            roundsPlayed = gameState.getRound();

        HistoricGame historicGame = new HistoricGame(gamePreferences, gameResult, roundsPlayed, System.currentTimeMillis());

        List<HistoricGame> gameHistoryList = new ArrayList<>(Arrays.asList(getGameHistory()));
        gameHistoryList.add(historicGame);

        if (gameHistoryList.size() > MAX_STORED_GAMES) {
            gameHistoryList = gameHistoryList.subList(gameHistoryList.size() - MAX_STORED_GAMES, gameHistoryList.size());
        }

        persistHistory(gameHistoryList);
    }

    private void persistHistory(List<HistoricGame> gameHistoryList) {
        String jsonData = json.toJson(gameHistoryList);
        prefStore.putString(GAME_HISTORY_NAME, jsonData);
        prefStore.flush();
    }

    /**
     * Loads the GameHistory data.
     *
     * @return the loaded GameHistory
     */
    public HistoricGame[] getGameHistory() {
        String jsonData = prefStore.getString(GAME_HISTORY_NAME, "");
        if (jsonData.isEmpty()) {
            return new HistoricGame[0];
        }

        return json.fromJson(HistoricGame[].class, jsonData);
    }
}

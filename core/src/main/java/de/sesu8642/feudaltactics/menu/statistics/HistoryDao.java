// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
import de.sesu8642.feudaltactics.menu.statistics.HistoricGame.GameResult;
import de.sesu8642.feudaltactics.menu.statistics.dagger.HistoryPrefStore;
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

    public static final int MAX_STORED_GAMES = 1000;

    private final Preferences prefStore;

    private final Json json = new Json();

    @Inject
    public HistoryDao(@HistoryPrefStore Preferences historyPrefs) {
        prefStore = historyPrefs;
        json.setOutputType(JsonWriter.OutputType.json);
    }

    public void registerPlayedGame(GameState gameState, NewGamePreferences gamePreferences, GameResult gameResult) {
        if (gameState == null || gameState.getScenarioMap() != ScenarioMap.NONE) {
            return; // only record generated maps for now. We must treat ScenarioMaps differently.
        }

        Integer roundsPlayed = gameState.getWinningRound();
        if (roundsPlayed == null)
        // game was aborted, so return the current round.
        // In other cases, winningRound is one lower than round, because round is incremented at the start of a
        // round after evaluating victory conditions,
        // but before invoking this method.
        {
            roundsPlayed = gameState.getRound();
        }

        final HistoricGame historicGame = new HistoricGame(gamePreferences, gameResult, roundsPlayed,
            System.currentTimeMillis());

        List<HistoricGame> gameHistoryList = new ArrayList<>(Arrays.asList(getGameHistory()));
        gameHistoryList.add(historicGame);

        if (gameHistoryList.size() > MAX_STORED_GAMES) {
            gameHistoryList = gameHistoryList.subList(gameHistoryList.size() - MAX_STORED_GAMES,
                gameHistoryList.size());
        }

        persistHistory(gameHistoryList);
    }

    private void persistHistory(List<HistoricGame> gameHistoryList) {
        final String jsonData = json.toJson(gameHistoryList);
        prefStore.putString(GAME_HISTORY_NAME, jsonData);
        prefStore.flush();
    }

    /**
     * Loads the GameHistory data.
     *
     * @return the loaded GameHistory, with the oldest game at index 0 and the most recent game at the last index.
     */
    public HistoricGame[] getGameHistory() {
        final String jsonData = prefStore.getString(GAME_HISTORY_NAME, "");
        if (jsonData.isEmpty()) {
            return new HistoricGame[0];
        }

        return json.fromJson(HistoricGame[].class, jsonData);
    }
}

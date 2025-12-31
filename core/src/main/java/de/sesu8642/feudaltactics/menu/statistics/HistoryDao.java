// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.menu.statistics.HistoricGame.GameResult;
import jakarta.inject.Inject;
import de.sesu8642.feudaltactics.lib.gamestate.Player;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Data access object for the statistics. Stores Game History as local files.
 */
@Singleton
public class HistoryDao {

    public static final String STATISTICS_FILE_NAME = "gamehistory.json";

    private final FileHandle historyFileHandle = Gdx.files.local(STATISTICS_FILE_NAME);

    private List<HistoricGame> gameHistoryList = new ArrayList<HistoricGame>();
    
    private final Json json = new Json();

    @Inject
    public HistoryDao() {
        json.setOutputType(JsonWriter.OutputType.json);
        loadHistory();
    }

    public void registerPlayedGame(GameState gameState, GameResult gameResult) {
        if (gameState == null || gameState.getScenarioMap() != null) {
            return; // only record generated maps for now. We must treat ScenarioMaps differently.
        }

        int humanPlayerIndex = gameState.getPlayers().stream()
            .filter(p -> p.getType() == Player.Type.LOCAL_PLAYER)
            .findFirst()
            .map(p -> gameState.getPlayers().indexOf(p))
            .orElse(-1);

        NewGamePreferences prefs = new NewGamePreferences(
            gameState.getSeed(),
            gameState.getBotIntelligence(),
            MapSizes.LARGE, // TODO: figure out map size from game state
            Densities.MEDIUM, // TODO: figure out density from game state
            humanPlayerIndex,
            gameState.getPlayers().size() - 1 // exclude human player
        );

        HistoricGame historicGame = new HistoricGame(prefs, gameResult);

        gameHistoryList.add(historicGame);

        persistHistory();
    }

    private void persistHistory() {
        try {
            String jsonData = json.toJson(gameHistoryList);
            historyFileHandle.writeString(jsonData, false);
        } catch (Exception e) {
            Gdx.app.error("HistoryDao", "Failed to persist game history", e);
        }
    }

    private void loadHistory() {
        if (!historyFileHandle.exists()) {
            return;
        }
        
        try {
            String jsonData = historyFileHandle.readString();
            HistoricGame[] loadedGames = json.fromJson(HistoricGame[].class, jsonData);
            if (loadedGames != null) {
                gameHistoryList = List.of(loadedGames);
            }
        } catch (Exception e) {
            Gdx.app.error("HistoryDao", "Failed to load game history", e);
        }
    }

    /**
     * Loads the GameHistory data.
     *
     * @return the loaded GameHistory
     */
    public HistoricGame[] getGameHistory() {
        return gameHistoryList.toArray(new HistoricGame[0]);
    }
}

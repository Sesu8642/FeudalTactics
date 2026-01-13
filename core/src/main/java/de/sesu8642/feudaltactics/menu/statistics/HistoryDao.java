// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.JsonValue;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
import de.sesu8642.feudaltactics.menu.statistics.HistoricGame.GameResult;
import jakarta.inject.Inject;
import de.sesu8642.feudaltactics.lib.gamestate.Player;

import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Data access object for the statistics. Stores Game History as local files.
 */
@Singleton
public class HistoryDao {

    public static final String STATISTICS_FILE_NAME = "gamehistory.json";

    private final FileHandle historyFileHandle;

    private List<HistoricGame> gameHistoryList = new ArrayList<HistoricGame>();

    private final Json json = new Json();

    @Inject
    public HistoryDao() {
        this(Gdx.files.local(STATISTICS_FILE_NAME));
    }

    // Constructor for testing
    public HistoryDao(FileHandle fileHandle) {
        this.historyFileHandle = fileHandle;
        json.setOutputType(JsonWriter.OutputType.json);
        registerSerializers();
        loadHistory();
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

    public void registerPlayedGame(GameState gameState, GameResult gameResult) {
        if (gameState == null || gameState.getScenarioMap() != ScenarioMap.NONE) {
            return; // only record generated maps for now. We must treat ScenarioMaps differently.
        }

        int humanPlayerIndex = gameState.getPlayers().stream()
            .filter(p -> p.getType() == Player.Type.LOCAL_PLAYER)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No local player found"))
            .getPlayerIndex();

        int tileCount = gameState.getMap().values().size();
        MapSizes mapSize = Arrays.stream(MapSizes.values())
            .filter(ms -> ms.getAmountOfTiles() == tileCount)
            .findFirst()
            .orElseThrow(() -> new IllegalStateException("No matching map size found for tile count " + tileCount));

        NewGamePreferences prefs = new NewGamePreferences(
            gameState.getSeed(),
            gameState.getBotIntelligence(),
            mapSize,
            Densities.MEDIUM, // TODO: figure out density from game state
            humanPlayerIndex,
            gameState.getPlayers().size() - 1 // exclude human player
        );

        int roundsPlayed = gameState.getRound();

        HistoricGame historicGame = new HistoricGame(prefs, gameResult, roundsPlayed);

        gameHistoryList.add(historicGame);

        persistHistory();
    }

    private void persistHistory() {
        String jsonData = json.toJson(gameHistoryList);
        historyFileHandle.writeString(jsonData, false);
    }

    private void loadHistory() {
        if (!historyFileHandle.exists()) {
            return;
        }

        String jsonData = historyFileHandle.readString();
        HistoricGame[] loadedGames = json.fromJson(HistoricGame[].class, jsonData);
        if (loadedGames != null) {
            gameHistoryList = new ArrayList<HistoricGame>(Arrays.asList(loadedGames));
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

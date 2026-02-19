package de.ui.statistics;

import com.badlogic.gdx.math.Vector2;
import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.HexTile;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.statistics.HistoricGame.GameResult;
import de.sesu8642.feudaltactics.menu.statistics.HistoryDao;
import de.sesu8642.feudaltactics.menu.statistics.StatisticsDao;
import de.sesu8642.feudaltactics.menu.statistics.ui.StatisticsEventHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;

import java.util.LinkedHashMap;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;

/**
 * Tests for {@link de.sesu8642.feudaltactics.menu.statistics.ui.StatisticsEventHandler}.
 */
public class StatisticsEventHandlerTest {

    private StatisticsDao statisticsDao;
    private HistoryDao historyDao;
    private StatisticsEventHandler handler;
    private GameState gameState;
    private NewGamePreferences newGamePreferences;

    @BeforeEach
    void setUp() {
        statisticsDao = Mockito.mock(StatisticsDao.class);
        historyDao = Mockito.mock(HistoryDao.class);

        handler = new StatisticsEventHandler(statisticsDao, historyDao);

        gameState = generateGameState();
        newGamePreferences = new NewGamePreferences(12345L, Intelligence.LEVEL_2, MapSizes.SMALL, Densities.LOOSE, 3);
    }

    @Test
    void handleGameExited_handlesAllPlayerTypes() {

        int cycle = 0;
        for (Player.Type playerType : Player.Type.values()) {
            final Player winnerPlayer = new Player(0, playerType);
            gameState.setWinner(winnerPlayer);

            final GameExitedEvent event = new GameExitedEvent(gameState, newGamePreferences);
            handler.handleGameExited(event);

            // we are happy that no exception was thrown

            // Verify historyDao was called to store the game history
            verify(historyDao, Mockito.times(++cycle)).registerPlayedGame(Mockito.any(), Mockito.any(), Mockito.any());

            // Verify statisticsDao.registerPlayedGame was called with the correct AI difficulty
            verify(statisticsDao, Mockito.times(cycle)).registerPlayedGame(Mockito.eq(Intelligence.LEVEL_2),
                Mockito.any());
        }
    }

    @ParameterizedTest
    @CsvSource({
        "LOCAL_PLAYER, WIN",
        "LOCAL_BOT, LOSS",
        "REMOTE, LOSS",
    })
    void handleGameExited_mapsPlayerTypeToGameResult(Player.Type playerType, GameResult expectedResult) {
        final Player winnerPlayer = new Player(0, playerType);
        gameState.setWinner(winnerPlayer);

        final GameExitedEvent event = new GameExitedEvent(gameState, newGamePreferences);
        handler.handleGameExited(event);

        // Verify historyDao was called to store the game history
        verify(historyDao).registerPlayedGame(Mockito.eq(gameState), Mockito.eq(newGamePreferences),
            Mockito.eq(expectedResult));

        // Verify statisticsDao.registerPlayedGame was called with the correct AI difficulty and result
        verify(statisticsDao).registerPlayedGame(Mockito.eq(Intelligence.LEVEL_2), Mockito.eq(expectedResult));
    }

    @Test
    void handleGameExited_detectsDefeatOnAbort() {
        // Simulate aborting the game (no winner set)
        gameState.setWinner(null);
        // Mark the local player as defeated
        final Player localPlayer = gameState.getPlayers().get(0);
        assertSame(localPlayer.getType(), Player.Type.LOCAL_PLAYER);  // This is actually a test for the test,
        // should be done in generateGameState
        localPlayer.setRoundOfDefeat(12);

        final GameExitedEvent event = new GameExitedEvent(gameState, newGamePreferences);
        handler.handleGameExited(event);

        // Verify historyDao was called to store the game history
        verify(historyDao).registerPlayedGame(Mockito.eq(gameState), Mockito.eq(newGamePreferences),
            Mockito.eq(GameResult.LOSS));

        // Verify statisticsDao.registerPlayedGame was called with the correct AI difficulty
        verify(statisticsDao).registerPlayedGame(Mockito.eq(Intelligence.LEVEL_2), Mockito.eq(GameResult.LOSS));
    }

    @Test
    void handleGameExited_detectsRealAbort() {
        // Simulate aborting the game (no winner set)
        gameState.setWinner(null);
        // Ensure the local player is not marked as defeated
        final Player localPlayer = gameState.getPlayers().get(0);
        assertSame(localPlayer.getType(), Player.Type.LOCAL_PLAYER);  // This is actually a test for the test,
        // should be done in generateGameState
        localPlayer.setRoundOfDefeat(null);

        final GameExitedEvent event = new GameExitedEvent(gameState, newGamePreferences);
        handler.handleGameExited(event);

        // Verify historyDao was called to store the game history
        verify(historyDao).registerPlayedGame(Mockito.eq(gameState), Mockito.eq(newGamePreferences),
            Mockito.eq(GameResult.ABORTED));

        // Verify statisticsDao.registerPlayedGame was called with the correct AI difficulty
        verify(statisticsDao).registerPlayedGame(Mockito.eq(Intelligence.LEVEL_2), Mockito.eq(GameResult.ABORTED));
    }

    private GameState generateGameState() {
        // Prepare a dummy game state
        final GameState gameState = new GameState();
        final Player localPlayer = new Player(0, Player.Type.LOCAL_PLAYER);
        gameState.setPlayers(
            java.util.List.of(
                localPlayer,
                new Player(1, Player.Type.LOCAL_BOT),
                new Player(2, Player.Type.LOCAL_BOT),
                new Player(3, Player.Type.LOCAL_BOT)
            )
        );

        // SMALL dummy map
        final LinkedHashMap<Vector2, HexTile> mapTiles = new LinkedHashMap<Vector2, HexTile>();
        for (int n = 0; n < MapSizes.SMALL.getAmountOfTiles(); n++) {
            final Vector2 position = new Vector2(0, n);
            mapTiles.put(position, new HexTile(localPlayer, position));
        }
        gameState.setMap(mapTiles);

        gameState.setSeed(12345L);
        gameState.setBotIntelligence(Intelligence.LEVEL_2);
        return gameState;
    }
}

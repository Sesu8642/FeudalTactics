package de.ui.statistics;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.math.Vector2;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.HexTile;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.statistics.HistoryDao;
import de.sesu8642.feudaltactics.menu.statistics.StatisticsDao;
import de.sesu8642.feudaltactics.menu.statistics.ui.StatisticsEventHandler;

import java.util.LinkedHashMap;

import org.junit.jupiter.api.Test;

/**
 * Tests for {@link de.sesu8642.feudaltactics.menu.statistics.ui.StatisticsEventHandler}.
 */
public class StatisticsEventHandlerTest {

    @Test
    void handleGameExited_handlesAllPlayerTypes() {
        final Preferences mockPrefs = new MockPreferences();
        final StatisticsDao statisticsDao = new StatisticsDao(mockPrefs);
        
        // Create a mock FileHandle that doesn't require real file I/O
        FileHandle mockFileHandle = new FileHandle("test.json") {
            @Override
            public boolean exists() {
                return false;
            }
            
            @Override
            public void writeString(String string, boolean append) {
                // Do nothing in tests
            }
        };
        
        HistoryDao historyDao = new HistoryDao(mockPrefs);

        final StatisticsEventHandler handler = new StatisticsEventHandler(statisticsDao, historyDao);

        final GameState gameState = generateGameState();
        final NewGamePreferences newGamePreferences = new NewGamePreferences(12345L, Intelligence.LEVEL_2, MapSizes.SMALL, Densities.LOOSE, 3);

        for (Player.Type playerType : Player.Type.values()) {
            final Player winnerPlayer = new Player(0, playerType);
            gameState.setWinner(winnerPlayer);

            final GameExitedEvent event = new GameExitedEvent(gameState, newGamePreferences);
            handler.handleGameExited(event);

            // No assertions ... we are happy that no exception was thrown
        }
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
      LinkedHashMap<Vector2, HexTile> mapTiles = new LinkedHashMap<Vector2, HexTile>();
      for (int n = 0; n < MapSizes.SMALL.getAmountOfTiles(); n++) {
        Vector2 position = new Vector2(0, n);
        mapTiles.put(position, new HexTile(localPlayer, position));
      }
      gameState.setMap(mapTiles);

      gameState.setSeed(12345L);
      gameState.setBotIntelligence(Intelligence.LEVEL_2);
        return gameState;
    }
}

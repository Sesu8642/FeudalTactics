package de.ui.statistics;

import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.files.FileHandle;
import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.menu.statistics.HistoryDao;
import de.sesu8642.feudaltactics.menu.statistics.StatisticsDao;
import de.sesu8642.feudaltactics.menu.statistics.ui.StatisticsEventHandler;
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
        
        HistoryDao historyDao = new HistoryDao(mockFileHandle);

        final StatisticsEventHandler handler = new StatisticsEventHandler(statisticsDao, historyDao);

        final GameState gameState = new GameState();
        for (Player.Type playerType : Player.Type.values()) {
            final Player winnerPlayer = new Player(0, playerType);
            gameState.setWinner(winnerPlayer);

            final GameExitedEvent event = new GameExitedEvent(gameState);
            handler.handleGameExited(event);

            // No assertions ... we are happy that no exception was thrown
        }
    }
}

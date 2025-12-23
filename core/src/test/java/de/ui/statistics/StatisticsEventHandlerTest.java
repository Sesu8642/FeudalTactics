package de.ui.statistics;
import com.badlogic.gdx.Preferences;

import org.junit.jupiter.api.Test;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.menu.statistics.StatisticsDao;
import de.sesu8642.feudaltactics.menu.statistics.ui.StatisticsEventHandler;

/**
 * Tests for {@link de.sesu8642.feudaltactics.menu.statistics.ui.StatisticsEventHandler}.
 */
public class StatisticsEventHandlerTest {
 
    @Test
    void handleGameExited_handlesAllPlayerTypes() {
        Preferences mockPrefs = new MockPreferences();
        StatisticsDao mockDao = new StatisticsDao(mockPrefs);

        StatisticsEventHandler handler = new StatisticsEventHandler(mockDao, null);

        GameState gameState = new GameState();
        for (Player.Type playerType : Player.Type.values()) {
            Player winnerPlayer = new Player(0, playerType);
            gameState.setWinner(winnerPlayer);

            GameExitedEvent event = new GameExitedEvent(gameState);
            handler.handleGameExited(event);

            // No assertions ... we are happy that no exception was thrown
        }
    }
}

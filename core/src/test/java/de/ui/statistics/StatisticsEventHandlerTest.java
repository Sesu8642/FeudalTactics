package de.ui.statistics;

import com.badlogic.gdx.Preferences;
import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
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
        final StatisticsDao mockDao = new StatisticsDao(mockPrefs);

        final StatisticsEventHandler handler = new StatisticsEventHandler(mockDao);

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

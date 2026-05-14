package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;

/**
 * Achievement: Abort a game in first round.
 */
public class AbortGameAchievement extends AbstractAchievement {

    public AbortGameAchievement() {
        super(1, "Abort a game in first round");
    }

    @Override
    public boolean isSecret() {
        return true;
    }

    @Override
    public String getId() {
        return "abort_game";
    }

    @Override
    public String getBaseDescription() {
        return "Abort a game in the first round.";
    }
    
    @Override
    public boolean onGameExited(GameExitedEvent event) {
        final GameState gameState = event.getGameState();
        if (gameState == null) {
            return false;     // Ignore exits from editor or similar
        }

        if (gameState.getRound() > 1) {
            return false;     // Not an abort in the first round, ignore
        }
    
        final Player winnerOfTheGame = gameState.getWinner();
        if (winnerOfTheGame == null) {
            storeProgress(1);
            return true;
        }
        return false;
    }
}

package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;

/**
 * Achievement: Abort a game in first round.
 */
public class AbortGameAchievement extends AbstractAchievement {

    public AbortGameAchievement() {
        super(1, TranslationKeys.ACHIEVEMENT_ABORT_GAME_NAME, TranslationKeys.ACHIEVEMENT_ABORT_GAME_DESCRIPTION);
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

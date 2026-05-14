package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

/**
 * Achievement: Lose a game against the weakest AI. Aborting does not count!
 */
public class LoseAgainstWeakestAiAchievement extends AbstractAchievement {

    public LoseAgainstWeakestAiAchievement() {
        super(1, "Lose against the weak AI");
    }

    @Override
    public boolean isSecret() {
        return true;
    }

    @Override
    public String getId() {
        return "lose_against_weak_ai";
    }

    @Override
    public String getBaseDescription() {
        return "Lose a game against the weakest AI. Aborting does not count!";
    }
    
    @Override
    public boolean onGameExited(GameExitedEvent event) {
        final GameState gameState = event.getGameState();
        if (gameState == null) {
            return false;     // Ignore exits from editor or similar
        }   
    
        final Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame == null || winnerOfTheGame.getType() == Player.Type.LOCAL_PLAYER) {
            return false;     // Player didn't lose, so ignore
        }

        Intelligence aiLevel = gameState.getBotIntelligence();
        if (aiLevel != Intelligence.LEVEL_1) {
            return false;    // Not the weakest AI, ignore
        }

        storeProgress(1);
        return true;
    }
}

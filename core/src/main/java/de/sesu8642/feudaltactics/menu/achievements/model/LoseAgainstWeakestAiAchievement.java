package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

/**
 * Achievement: Lose a game against the weakest AI. Aborting does not count!
 */
public class LoseAgainstWeakestAiAchievement extends AbstractAchievement {

    public LoseAgainstWeakestAiAchievement(AchievementRepository repository) {
        super(repository, 1, "Lose against the weak AI");
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
    public void onGameExited(GameExitedEvent event) {
        final GameState gameState = event.getGameState();
        if (gameState == null) {
            return;     // Ignore exits from editor or similar
        }   
    
        final Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame == null || winnerOfTheGame.getType() == Player.Type.LOCAL_PLAYER) {
            return;     // Player didn't lose, so ignore
        }

        Intelligence aiLevel = gameState.getBotIntelligence();
        if (aiLevel != Intelligence.LEVEL_1) {
            return;    // Not the weakest AI, ignore
        }

        storeProgress(1);
    }
}

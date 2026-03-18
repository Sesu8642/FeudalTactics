package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

public class AbortGameAchievement extends AbstractAchievement {

    public AbortGameAchievement(AchievementRepository repository) {
        super(repository, 1, "Abort a game");
    }

    @Override
    public String getId() {
        return "abort_game";
    }

    @Override
    public String getBaseDescription() {
        return "Abort a game.";
    }
    
    @Override
    public void onGameExited(GameExitedEvent event) {
        final GameState gameState = event.getGameState();
        if (gameState == null) {
            return;     // Ignore exits from editor or similar
        }   
    
        final Player winnerOfTheGame = gameState.getWinner();
        if (winnerOfTheGame == null) {
            storeProgress(1);
        }
    }
}

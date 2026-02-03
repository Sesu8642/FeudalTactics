package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

public class WinNGamesAchievement extends AbstractAchievement {

    public WinNGamesAchievement(AchievementRepository achievementRepository, int gamesToWin) {
        super(achievementRepository, gamesToWin);
    }

    @Override
    public String getId() {
        return "win-" + getGoal() + "-games";
    }

    @Override
    public String getName() {
        return "Win " + getGoal() + " Games";
    }

    @Override
    public String getDescription() {
        return "Win " + getGoal() + " games, either by defeating your enemies or them giving up. Any difficulty and map size is allowed.";
    }
    
    @Override
    public void onGameExited(GameExitedEvent event) {
        final GameState gameState = event.getGameState();
        if (gameState == null) {
            return;     // Ignore exits from editor or similar
        }   
    
        final Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame != null && winnerOfTheGame.getType() == Player.Type.LOCAL_PLAYER) {
            storeProgress(getProgress() + 1);
        }
    }
}

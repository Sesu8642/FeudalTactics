package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;

/**
 * Achievement: Win a specified number of games, either by defeating your enemies or them giving up. Any difficulty and map size is allowed.
 */
public class WinNGamesAchievement extends AbstractAchievement {

    public WinNGamesAchievement(int gamesToWin) {
        super(gamesToWin, "Win " + gamesToWin + " Games");
    }

    @Override
    public String getId() {
        return "win-" + getGoal() + "-games";
    }

    @Override
    public String getBaseDescription() {
        return "Win " + getGoal() + " games, either by defeating your enemies or them giving up. Any difficulty and map size is allowed.";
    }
    
    @Override
    public boolean onGameExited(GameExitedEvent event) {
        final GameState gameState = event.getGameState();
        if (gameState == null) {
            return false;     // Ignore exits from editor or similar
        }   
    
        final Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame != null && winnerOfTheGame.getType() == Player.Type.LOCAL_PLAYER) {
            storeProgress(getProgress() + 1);
            return true;
        }
        return false;
    }
}

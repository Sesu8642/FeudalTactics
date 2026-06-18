package de.sesu8642.feudaltactics.menu.achievements.model;

import com.google.common.collect.ImmutableList;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;

/**
 * Achievement: Win a specified number of games, either by defeating your enemies or them giving up. Any difficulty
 * and map size is allowed.
 */
public class WinNGamesAchievement extends AbstractAchievement {

    public WinNGamesAchievement(int gamesToWin) {
        super(gamesToWin, TranslationKeys.ACHIEVEMENT_WIN_N_GAMES_NAME, ImmutableList.of(String.valueOf(gamesToWin)),
            TranslationKeys.ACHIEVEMENT_WIN_N_GAMES_DESCRIPTION, ImmutableList.of(String.valueOf(gamesToWin)));
    }

    @Override
    public String getId() {
        return "win-" + getGoal() + "-games";
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

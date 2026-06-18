package de.sesu8642.feudaltactics.menu.achievements.model;

import com.google.common.collect.ImmutableList;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;

/**
 * Achievement: Win a game where your enemies surrender -- and there are still a specified number of or more
 * different enemies with surviving kingdoms.
 */
public class WinAgainstManyEnemiesAchievement extends AbstractAchievement {

    private final int enemyCount;

    public WinAgainstManyEnemiesAchievement(int enemyCount) {
        super(1, TranslationKeys.ACHIEVEMENT_WIN_AGAINST_MANY_ENEMIES_NAME,
            ImmutableList.of(String.valueOf(enemyCount)),
            TranslationKeys.ACHIEVEMENT_WIN_AGAINST_MANY_ENEMIES_DESCRIPTION,
            ImmutableList.of(String.valueOf(enemyCount)));
        this.enemyCount = enemyCount;
    }

    private static boolean isSurvivingPlayer(GameState gameState, Player player) {
        return gameState.getKingdoms().stream()
            .anyMatch(kingdom -> kingdom.getPlayer() == player);
    }

    @Override
    public String getId() {
        return "win_against_" + enemyCount + "_enemies";
    }

    @Override
    public boolean onGameExited(GameExitedEvent event) {
        final de.sesu8642.feudaltactics.lib.gamestate.GameState gameState = event.getGameState();
        if (gameState == null) {
            return false;     // Ignore exits from editor or similar
        }

        final de.sesu8642.feudaltactics.lib.gamestate.Player winnerOfTheGame = gameState.getWinner();
        if (winnerOfTheGame == null || winnerOfTheGame.getType() != de.sesu8642.feudaltactics.lib.gamestate.Player.Type.LOCAL_PLAYER) {
            return false;     // Player didn't win, so ignore
        }

        final long survivingEnemies = gameState.getPlayers().stream()
            .filter(player -> player.getType() != de.sesu8642.feudaltactics.lib.gamestate.Player.Type.LOCAL_PLAYER)
            .filter(player -> isSurvivingPlayer(gameState, player))
            .count();

        if (survivingEnemies >= enemyCount) {
            storeProgress(1); // unlock
            return true;
        }
        return false;
    }
}

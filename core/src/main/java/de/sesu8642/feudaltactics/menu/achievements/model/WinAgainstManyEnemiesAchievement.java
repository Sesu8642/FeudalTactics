package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

public class WinAgainstManyEnemiesAchievement extends AbstractAchievement {

    private final int enemyCount;

    public WinAgainstManyEnemiesAchievement(AchievementRepository repository, int enemyCount) {
        super(repository, 1, "Win with " + enemyCount + " surviving enemies");
        this.enemyCount = enemyCount;
    }

    @Override
    public String getDescription() {
        return "Win a game where your enemies surrender -- and there are still " + enemyCount + " or more different enemies with surviving kingdoms.";
    }

    @Override
    public String getId() {
        return "win_against_" + enemyCount + "_enemies";
    }

    @Override
    public void onGameExited(de.sesu8642.feudaltactics.events.GameExitedEvent event) {
        final de.sesu8642.feudaltactics.lib.gamestate.GameState gameState = event.getGameState();
        if (gameState == null) {
            return;     // Ignore exits from editor or similar
        }

        final de.sesu8642.feudaltactics.lib.gamestate.Player winnerOfTheGame = gameState.getWinner();
        if (winnerOfTheGame == null || winnerOfTheGame.getType() != de.sesu8642.feudaltactics.lib.gamestate.Player.Type.LOCAL_PLAYER) {
            return;     // Player didn't win, so ignore
        }

        long survivingEnemies = gameState.getPlayers().stream()
            .filter(player -> player.getType() != de.sesu8642.feudaltactics.lib.gamestate.Player.Type.LOCAL_PLAYER)
            .filter(player -> isSurvivingPlayer(gameState, player))
            .count();

        if (survivingEnemies >= enemyCount) {
            storeProgress(1); // unlock
        }
    }

    private static boolean isSurvivingPlayer(GameState gameState, Player player) {
        return gameState.getKingdoms().stream()
            .anyMatch(kingdom -> kingdom.getPlayer() == player);
    }
}

package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Kingdom;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.mockito.Mockito.*;

class WinAgainstManyEnemiesAchievementTest extends AbstractAchievementTest<WinAgainstManyEnemiesAchievement> {

    private static final int ENEMY_COUNT = 3;

    @Override
    protected WinAgainstManyEnemiesAchievement createAchievement(AchievementRepository repo) {
        return new WinAgainstManyEnemiesAchievement(repo, ENEMY_COUNT);
    }

    @Override
    protected WinAgainstManyEnemiesAchievement createAchievementWithDifferentParams(AchievementRepository repo) {
        return new WinAgainstManyEnemiesAchievement(repo, 5);
    }

    @Test
    void noWinner_doesNotUnlock() {
        achievement.onGameExited(noWinnerEvent());
        verifyNoProgress();
    }

    @Test
    void botWins_doesNotUnlock() {
        achievement.onGameExited(winEvent(Player.Type.LOCAL_BOT, null));
        verifyNoProgress();
    }

    @Test
    void enoughSurvivingEnemies_unlocks() {
        achievement.onGameExited(enemyWinEvent(ENEMY_COUNT));
        verifyProgress(1);
    }

    @Test
    void tooFewSurvivingEnemies_doesNotUnlock() {
        achievement.onGameExited(enemyWinEvent(ENEMY_COUNT - 1));
        verifyNoProgress();
    }

    @Test
    void moreThanEnoughSurvivingEnemies_unlocks() {
        achievement.onGameExited(enemyWinEvent(ENEMY_COUNT + 1));
        verifyProgress(1);
    }

    /** Creates an event where local player wins and {@code survivingBots} bots have kingdoms. */
    private GameExitedEvent enemyWinEvent(int survivingBots) {
        Player localPlayer = new Player(0, Player.Type.LOCAL_PLAYER);
        Player[] allPlayers = new Player[TOTAL_PLAYERS];
        Kingdom[] kingdoms = new Kingdom[survivingBots];

        allPlayers[0] = localPlayer;
        for (int i = 1; i <= survivingBots; i++) {
            allPlayers[i] = new Player(i, Player.Type.LOCAL_BOT);
            kingdoms[i] = new Kingdom(allPlayers[i]);
        }
        for (int j = survivingBots +1; j < TOTAL_PLAYERS; j++) {
            allPlayers[j] = new Player(j, Player.Type.LOCAL_BOT);
        }

        GameState gs = new GameState();
        gs.setWinner(localPlayer);
        gs.setPlayers(Arrays.asList(allPlayers));
        gs.setKingdoms(Arrays.asList(kingdoms));

        return new GameExitedEvent(gs, null);
    }
}

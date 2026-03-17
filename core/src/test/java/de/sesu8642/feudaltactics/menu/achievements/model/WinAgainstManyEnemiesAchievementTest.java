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
        Player[] bots = new Player[survivingBots + 1]; // +1 extra bot with no kingdom
        Kingdom[] kingdoms = new Kingdom[survivingBots];

        for (int i = 0; i < survivingBots + 1; i++) {
            bots[i] = new Player(i + 1, Player.Type.LOCAL_BOT);
            if (i < survivingBots) {
                kingdoms[i] = new Kingdom(bots[i]);
            }
        }

        GameState gs = mock(GameState.class);
        when(gs.getWinner()).thenReturn(localPlayer);

        Player[] allPlayers = new Player[survivingBots + 2];
        allPlayers[0] = localPlayer;
        System.arraycopy(bots, 0, allPlayers, 1, bots.length);
        when(gs.getPlayers()).thenReturn(Arrays.asList(allPlayers));
        when(gs.getKingdoms()).thenReturn(Arrays.asList(kingdoms));

        GameExitedEvent event = mock(GameExitedEvent.class);
        when(event.getGameState()).thenReturn(gs);
        return event;
    }
}

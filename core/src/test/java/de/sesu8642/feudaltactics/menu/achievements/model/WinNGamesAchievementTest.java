package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WinNGamesAchievementTest extends AbstractAchievementTest<WinNGamesAchievement> {

    @Override
    protected WinNGamesAchievement createAchievement() {
        return new WinNGamesAchievement(5);
    }

    @Override
    protected WinNGamesAchievement createAchievementWithDifferentParams() {
        return new WinNGamesAchievement(50);
    }

    @Test
    void noWinner_doesNothing() {
        achievement.onGameExited(noWinnerEvent());
        verifyNoProgress();
    }

    @Test
    void botWins_doesNothing() {
        achievement.onGameExited(winEvent(Player.Type.LOCAL_BOT, null));
        verifyNoProgress();
    }

    @Test
    void localPlayerWins_incrementsProgress() {
        achievement.onGameExited(winEvent(Player.Type.LOCAL_PLAYER, Intelligence.LEVEL_2));
        verifyProgress(1);
    }

    @Test
    void multipleWins_progressIncrementsEachTime() {
        GameExitedEvent event = winEvent(Player.Type.LOCAL_PLAYER, Intelligence.LEVEL_2);
        boolean result1 = achievement.onGameExited(event);
        boolean result2 = achievement.onGameExited(event);
        boolean result3 = achievement.onGameExited(event);

        assertEquals(3, achievement.getProgress());
        assertTrue(result1);
        assertTrue(result2);
        assertTrue(result3);
    }
}

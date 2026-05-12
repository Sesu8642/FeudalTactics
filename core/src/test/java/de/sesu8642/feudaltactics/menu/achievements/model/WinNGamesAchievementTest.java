package de.sesu8642.feudaltactics.menu.achievements.model;

import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.events.achievements.AchievementProgressEvent;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WinNGamesAchievementTest extends AbstractAchievementTest<WinNGamesAchievement> {

    @Override
    protected WinNGamesAchievement createAchievement(EventBus eventBus) {
        return new WinNGamesAchievement(eventBus, 5);
    }

    @Override
    protected WinNGamesAchievement createAchievementWithDifferentParams(EventBus eventBus) {
        return new WinNGamesAchievement(eventBus, 50);
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
        achievement.onGameExited(event);
        achievement.onGameExited(event);
        achievement.onGameExited(event);

        assertEquals(3, achievement.getProgress());
        verify(eventBus, times(3)).post(any(AchievementProgressEvent.class));
    }
}

package de.sesu8642.feudaltactics.menu.achievements.model;

import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import org.junit.jupiter.api.Test;

class WinAgainstAiLevelAchievementTest extends AbstractAchievementTest<WinAgainstAiLevelAchievement> {

    @Override
    protected WinAgainstAiLevelAchievement createAchievement(EventBus eventBus) {
        return new WinAgainstAiLevelAchievement(eventBus, Intelligence.LEVEL_2);
    }

    @Override
    protected WinAgainstAiLevelAchievement createAchievementWithDifferentParams(EventBus eventBus) {
        return new WinAgainstAiLevelAchievement(eventBus, Intelligence.LEVEL_4);
    }

    @Test
    void localPlayerWins_matchingAiLevel_unlocks() {
        achievement.onGameExited(winEvent(Player.Type.LOCAL_PLAYER, Intelligence.LEVEL_2));
        verifyProgress(1);
    }

    @Test
    void localPlayerWins_differentAiLevel_doesNotUnlock() {
        achievement.onGameExited(winEvent(Player.Type.LOCAL_PLAYER, Intelligence.LEVEL_3));
        verifyNoProgress();
    }

    @Test
    void botWins_matchingAiLevel_doesNotUnlock() {
        achievement.onGameExited(winEvent(Player.Type.LOCAL_BOT, Intelligence.LEVEL_2));
        verifyNoProgress();
    }

    @Test
    void noWinner_doesNotUnlock() {
        achievement.onGameExited(noWinnerEvent());
        verifyNoProgress();
    }
}

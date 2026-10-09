package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import org.junit.jupiter.api.Test;

class WinWhenStartingLastAchievementTest extends AbstractAchievementTest<WinWhenStartingLastAchievement> {

    @Override
    protected WinWhenStartingLastAchievement createAchievement() {
        return new WinWhenStartingLastAchievement();
    }

    @Test
    void allConditionsMet_unlocks() {
        // starts last (index 5 out of 6 players), very hard AI
        achievement.onGameExited(localPlayerWinEventFull(Intelligence.LEVEL_4, 5));
        verifyProgress(1);
    }

    @Test
    void notStartingLast_doesNotUnlock() {
        achievement.onGameExited(localPlayerWinEventFull(Intelligence.LEVEL_4, 0));
        verifyNoProgress();
    }

    @Test
    void wrongAiLevel_doesNotUnlock() {
        achievement.onGameExited(localPlayerWinEventFull(Intelligence.LEVEL_3, 5));
        verifyNoProgress();
    }

    @Test
    void noWinner_doesNotUnlock() {
        achievement.onGameExited(noWinnerEvent());
        verifyNoProgress();
    }
}

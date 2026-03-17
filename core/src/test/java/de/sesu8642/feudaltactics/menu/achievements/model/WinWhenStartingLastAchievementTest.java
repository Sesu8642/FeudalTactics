package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import org.junit.jupiter.api.Test;

class WinWhenStartingLastAchievementTest extends AbstractAchievementTest<WinWhenStartingLastAchievement> {

    @Override
    protected WinWhenStartingLastAchievement createAchievement(AchievementRepository repo) {
        return new WinWhenStartingLastAchievement(repo);
    }

    @Test
    void allConditionsMet_unlocks() {
        // starts last (index 5 out of 6 players), very hard AI
        achievement.onGameExited(localPlayerWinEventFull(Intelligence.LEVEL_4, 5, 6));
        verifyProgress(1);
    }

    @Test
    void notStartingLast_doesNotUnlock() {
        achievement.onGameExited(localPlayerWinEventFull(Intelligence.LEVEL_4, 0, 6));
        verifyNoProgress();
    }

    @Test
    void wrongAiLevel_doesNotUnlock() {
        achievement.onGameExited(localPlayerWinEventFull(Intelligence.LEVEL_3, 5, 6));
        verifyNoProgress();
    }

    @Test
    void noWinner_doesNotUnlock() {
        achievement.onGameExited(noWinnerEvent());
        verifyNoProgress();
    }
}

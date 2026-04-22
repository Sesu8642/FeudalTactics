package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import org.junit.jupiter.api.Test;

class WinAgainstAiLevelAchievementTest extends AbstractAchievementTest<WinAgainstAiLevelAchievement> {

    @Override
    protected WinAgainstAiLevelAchievement createAchievement(AchievementRepository repo) {
        return new WinAgainstAiLevelAchievement(repo, Intelligence.LEVEL_2);
    }

    @Override
    protected WinAgainstAiLevelAchievement createAchievementWithDifferentParams(AchievementRepository repo) {
        return new WinAgainstAiLevelAchievement(repo, Intelligence.LEVEL_4);
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

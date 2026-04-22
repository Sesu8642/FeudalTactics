package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import org.junit.jupiter.api.Test;

class WinInNRoundsAchievementTest extends AbstractAchievementTest<WinInNRoundsAchievement> {

    private static final int ROUND_LIMIT = 16;

    @Override
    protected WinInNRoundsAchievement createAchievement(AchievementRepository repo) {
        return new WinInNRoundsAchievement(repo, ROUND_LIMIT);
    }

    @Override
    protected WinInNRoundsAchievement createAchievementWithDifferentParams(AchievementRepository repo) {
        return new WinInNRoundsAchievement(repo, 14);
    }

    @Test
    void noWinner_doesNotUnlock() {
        achievement.onGameExited(noWinnerEvent());
        verifyNoProgress();
    }

    @Test
    void botWins_doesNotUnlock() {
        achievement.onGameExited(winEvent(Player.Type.LOCAL_BOT, Intelligence.LEVEL_4));
        verifyNoProgress();
    }

    @Test
    void wrongAiLevel_doesNotUnlock() {
        achievement.onGameExited(localPlayerWinEventWithPrefs(Intelligence.LEVEL_3, MapSizes.LARGE, 10));
        verifyNoProgress();
    }

    @Test
    void mapTooSmall_doesNotUnlock() {
        achievement.onGameExited(localPlayerWinEventWithPrefs(Intelligence.LEVEL_4, MapSizes.SMALL, 10));
        verifyNoProgress();
    }

    @Test
    void tooManyRounds_doesNotUnlock() {
        achievement.onGameExited(localPlayerWinEventWithPrefs(Intelligence.LEVEL_4, MapSizes.MEDIUM, ROUND_LIMIT + 1));
        verifyNoProgress();
    }

    @Test
    void exactlyAtRoundLimit_unlocks() {
        achievement.onGameExited(localPlayerWinEventWithPrefs(Intelligence.LEVEL_4, MapSizes.MEDIUM, ROUND_LIMIT));
        verifyProgress(1);
    }

    @Test
    void belowRoundLimit_unlocks() {
        achievement.onGameExited(localPlayerWinEventWithPrefs(Intelligence.LEVEL_4, MapSizes.LARGE, ROUND_LIMIT - 3));
        verifyProgress(1);
    }
}

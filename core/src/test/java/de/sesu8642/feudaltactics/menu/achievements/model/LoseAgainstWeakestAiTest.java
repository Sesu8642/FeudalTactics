package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import org.junit.jupiter.api.Test;

class LoseAgainstWeakestAiTest extends AbstractAchievementTest<LoseAgainstWeakestAi> {

    @Override
    protected LoseAgainstWeakestAi createAchievement(AchievementRepository repo) {
        return new LoseAgainstWeakestAi(repo);
    }

    @Test
    void noWinner_doesNotUnlock() {
        achievement.onGameExited(noWinnerEvent());
        verifyNoProgress();
    }

    @Test
    void localPlayerWins_doesNotUnlock() {
        achievement.onGameExited(winEvent(Player.Type.LOCAL_PLAYER, Intelligence.LEVEL_1));
        verifyNoProgress();
    }

    @Test
    void botWinsOnWeakestAi_unlocks() {
        achievement.onGameExited(winEvent(Player.Type.LOCAL_BOT, Intelligence.LEVEL_1));
        verifyProgress(1);
    }

    @Test
    void botWinsOnStrongerAi_doesNotUnlock() {
        achievement.onGameExited(winEvent(Player.Type.LOCAL_BOT, Intelligence.LEVEL_2));
        verifyNoProgress();
    }
}

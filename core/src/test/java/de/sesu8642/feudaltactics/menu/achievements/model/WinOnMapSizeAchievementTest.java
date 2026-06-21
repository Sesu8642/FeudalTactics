package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import org.junit.jupiter.api.Test;


class WinOnMapSizeAchievementTest extends AbstractAchievementTest<WinOnMapSizeAchievement> {

    @Override
    protected WinOnMapSizeAchievement createAchievement() {
        return new WinOnMapSizeAchievement(MapSizes.LARGE);
    }

    @Override
    protected WinOnMapSizeAchievement createAchievementWithDifferentParams() {
        return new WinOnMapSizeAchievement(MapSizes.SMALL);
    }

    @Test
    void noWinner_doesNotUnlock() {
        achievement.onGameExited(noWinnerEvent());
        verifyNoProgress();
    }

    @Test
    void botWins_doesNotUnlock() {
        achievement.onGameExited(winEvent(Player.Type.LOCAL_BOT, Intelligence.LEVEL_3));
        verifyNoProgress();
    }

    @Test
    void matchingMapSize_unlocks() {
        achievement.onGameExited(localPlayerWinEventWithPrefs(Intelligence.LEVEL_2, MapSizes.LARGE, 7));
        verifyProgress(1);
    }

    @Test
    void differentMapSize_doesNotUnlock() {
        achievement.onGameExited(localPlayerWinEventWithPrefs(Intelligence.LEVEL_2, MapSizes.SMALL, 7));
        verifyNoProgress();
    }
}

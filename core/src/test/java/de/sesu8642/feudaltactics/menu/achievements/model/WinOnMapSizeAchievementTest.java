package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class WinOnMapSizeAchievementTest extends AbstractAchievementTest<WinOnMapSizeAchievement> {

    @Override
    protected WinOnMapSizeAchievement createAchievement(AchievementRepository repo) {
        return new WinOnMapSizeAchievement(repo, MapSizes.LARGE);
    }

    @Override
    protected WinOnMapSizeAchievement createAchievementWithDifferentParams(AchievementRepository repo) {
        return new WinOnMapSizeAchievement(repo, MapSizes.SMALL);
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
        achievement.onGameExited(localPlayerWinEventWithPrefs(Intelligence.LEVEL_2, MapSizes.LARGE, 7));
        verifyNoProgress();
    }
}

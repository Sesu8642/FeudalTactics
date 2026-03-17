package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
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
        achievement.onGameExited(winEvent(Player.Type.LOCAL_BOT, null));
        verifyNoProgress();
    }

    @Test
    void matchingMapSize_unlocks() {
        achievement.onGameExited(mapSizeWinEvent(MapSizes.LARGE));
        verifyProgress(1);
    }

    @Test
    void differentMapSize_doesNotUnlock() {
        achievement.onGameExited(mapSizeWinEvent(MapSizes.MEDIUM));
        verifyNoProgress();
    }

    private GameExitedEvent mapSizeWinEvent(MapSizes mapSize) {
        Player winner = new Player(0, Player.Type.LOCAL_PLAYER);
        GameState gs = mock(GameState.class);
        when(gs.getWinner()).thenReturn(winner);

        NewGamePreferences prefs = mock(NewGamePreferences.class);
        when(prefs.getMapSize()).thenReturn(mapSize);

        GameExitedEvent event = mock(GameExitedEvent.class);
        when(event.getGameState()).thenReturn(gs);
        when(event.getGamePreferences()).thenReturn(prefs);
        return event;
    }
}

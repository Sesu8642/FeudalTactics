package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class WinNGamesAchievementTest extends AbstractAchievementTest<WinNGamesAchievement> {

    @Override
    protected WinNGamesAchievement createAchievement(AchievementRepository repo) {
        return new WinNGamesAchievement(repo, 5);
    }

    @Override
    protected WinNGamesAchievement createAchievementWithDifferentParams(AchievementRepository repo) {
        return new WinNGamesAchievement(repo, 50);
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
        achievement.onGameExited(localPlayerWinEvent());
        verifyProgress(1);
    }

    @Test
    void multipleWins_progressIncrementsEachTime() {
        GameExitedEvent event = localPlayerWinEvent();
        achievement.onGameExited(event);
        achievement.onGameExited(event);
        achievement.onGameExited(event);

        verify(repository).storeProgress(achievement.getId(), 1);
        verify(repository).storeProgress(achievement.getId(), 2);
        verify(repository).storeProgress(achievement.getId(), 3);
    }

    private GameExitedEvent localPlayerWinEvent() {
        Player winner = new Player(0, Player.Type.LOCAL_PLAYER);
        GameState gs = mock(GameState.class);
        when(gs.getWinner()).thenReturn(winner);
        GameExitedEvent event = mock(GameExitedEvent.class);
        when(event.getGameState()).thenReturn(gs);
        return event;
    }
}

package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class PlayMoreThanNRoundsAchievementTest extends AbstractAchievementTest<PlayMoreThanNRoundsAchievement> {

    private static final int ROUND_COUNT = 50;

    @Override
    protected PlayMoreThanNRoundsAchievement createAchievement(AchievementRepository repo) {
        return new PlayMoreThanNRoundsAchievement(repo, ROUND_COUNT);
    }

    @Override
    protected PlayMoreThanNRoundsAchievement createAchievementWithDifferentParams(AchievementRepository repo) {
        return new PlayMoreThanNRoundsAchievement(repo, 100);
    }

    @Test
    void belowThreshold_doesNotUnlock() {
        achievement.onGameExited(eventWithRounds(ROUND_COUNT - 1));
        verifyNoProgress();
    }

    @Test
    void atThreshold_unlocks() {
        achievement.onGameExited(eventWithRounds(ROUND_COUNT));
        verifyProgress(1);
    }

    @Test
    void aboveThreshold_unlocks() {
        achievement.onGameExited(eventWithRounds(ROUND_COUNT + 10));
        verifyProgress(1);
    }

    private GameExitedEvent eventWithRounds(int rounds) {
        GameState gs = mock(GameState.class);
        when(gs.getRound()).thenReturn(rounds);
        GameExitedEvent event = mock(GameExitedEvent.class);
        when(event.getGameState()).thenReturn(gs);
        return event;
    }
}

package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Common base for achievement tests. Provides shared helpers and tests that apply to all achievements.
 */
abstract class AbstractAchievementTest<T extends AbstractAchievement> {

    protected AchievementRepository repository;
    protected T achievement;

    /** Create the achievement under test. Called in {@link #setUp()}. */
    protected abstract T createAchievement(AchievementRepository repository);

    /**
     * Create the same type of achievement but with different constructor parameters,
     * so that we can verify the id changes. Return {@code null} if no parameterised variant exists.
     */
    protected T createAchievementWithDifferentParams(AchievementRepository repository) {
        return null; // override when applicable
    }

    @BeforeEach
    void setUp() {
        repository = mock(AchievementRepository.class);
        achievement = createAchievement(repository);
    }

    // ---- common id / description tests ----

    @Test
    void id_isConstant() {
        String first = achievement.getId();
        String second = achievement.getId();
        assertEquals(first, second, "getId() must return a stable value");
    }

    @Test
    void id_changesWithDifferentParams() {
        T other = createAchievementWithDifferentParams(repository);
        if (other != null) {
            assertNotEquals(achievement.getId(), other.getId(),
                    "Achievements with different params should produce different ids");
        }
    }

    @Test
    void description_hasDecentLength() {
        String desc = achievement.getBaseDescription();
        assertNotNull(desc);
        assertTrue(desc.length() >= 30, "Description should be meaningful (>=10 chars), was: " + desc);
    }

    @Test
    void name_hasDecentLength() {
        String name = achievement.getName();
        assertNotNull(name);
        assertTrue(name.length() >= 5, "Name should be meaningful (>=5 chars), was: " + name);
    }

    // ---- common onGameExited null-safety test ----

    @Test
    void onGameExited_nullGameState_doesNothing() {
        GameExitedEvent event = mock(GameExitedEvent.class);
        when(event.getGameState()).thenReturn(null);

        achievement.onGameExited(event);

        verifyNoInteractions(repository);
    }

    // ---- event builder helpers ----

    protected static GameExitedEvent nullGameStateEvent() {
        GameExitedEvent event = mock(GameExitedEvent.class);
        when(event.getGameState()).thenReturn(null);
        return event;
    }

    protected static GameExitedEvent noWinnerEvent() {
        GameState gs = mock(GameState.class);
        when(gs.getWinner()).thenReturn(null);
        GameExitedEvent event = mock(GameExitedEvent.class);
        when(event.getGameState()).thenReturn(gs);
        return event;
    }

    /** Event where a player of the given type wins, with specific AI level. */
    protected static GameExitedEvent winEvent(Player.Type winnerType, Intelligence aiLevel) {
        Player winner = new Player(0, winnerType);
        GameState gs = mock(GameState.class);
        when(gs.getWinner()).thenReturn(winner);
        when(gs.getBotIntelligence()).thenReturn(aiLevel);
        GameExitedEvent event = mock(GameExitedEvent.class);
        when(event.getGameState()).thenReturn(gs);
        return event;
    }

    /** Event where local player wins, with configurable GameState properties. */
    protected static GameExitedEvent localPlayerWinEvent(Intelligence aiLevel) {
        return winEvent(Player.Type.LOCAL_PLAYER, aiLevel);
    }

    /** Event where local player wins with preferences attached. */
    protected static GameExitedEvent localPlayerWinEventWithPrefs(
            Intelligence aiLevel, NewGamePreferences.MapSizes mapSize, int winningRound) {
        Player winner = new Player(0, Player.Type.LOCAL_PLAYER);
        GameState gs = mock(GameState.class);
        when(gs.getWinner()).thenReturn(winner);
        when(gs.getBotIntelligence()).thenReturn(aiLevel);
        when(gs.getWinningRound()).thenReturn(winningRound);

        NewGamePreferences prefs = mock(NewGamePreferences.class);
        when(prefs.getMapSize()).thenReturn(mapSize);

        GameExitedEvent event = mock(GameExitedEvent.class);
        when(event.getGameState()).thenReturn(gs);
        when(event.getGamePreferences()).thenReturn(prefs);
        return event;
    }

    /** Event where local player wins with preferences (including starting position). */
    protected static GameExitedEvent localPlayerWinEventFull(
            Intelligence aiLevel, int startingPosition, int totalPlayers) {
        Player winner = new Player(0, Player.Type.LOCAL_PLAYER);

        Player[] players = new Player[totalPlayers];
        for (int i = 0; i < totalPlayers; i++) {
            players[i] = new Player(i, i == 0 ? Player.Type.LOCAL_PLAYER : Player.Type.LOCAL_BOT);
        }

        GameState gs = mock(GameState.class);
        when(gs.getWinner()).thenReturn(winner);
        when(gs.getBotIntelligence()).thenReturn(aiLevel);
        when(gs.getPlayers()).thenReturn(Arrays.asList(players));

        NewGamePreferences prefs = mock(NewGamePreferences.class);
        when(prefs.getStartingPosition()).thenReturn(startingPosition);

        GameExitedEvent event = mock(GameExitedEvent.class);
        when(event.getGameState()).thenReturn(gs);
        when(event.getGamePreferences()).thenReturn(prefs);
        return event;
    }

    /** Verify that storeProgress was called for the achievement. */
    protected void verifyProgress(int expectedProgress) {
        verify(repository).storeProgress(achievement.getId(), expectedProgress);
    }

    /** Verify no repository interaction happened. */
    protected void verifyNoProgress() {
        verifyNoInteractions(repository);
    }
}

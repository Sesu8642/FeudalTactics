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
        GameExitedEvent event = new GameExitedEvent(null, null);

        achievement.onGameExited(event);

        verifyNoInteractions(repository);
    }

    // ---- event builder helpers ----
    protected static final int TOTAL_PLAYERS = 6;

    protected static GameExitedEvent noWinnerEvent() {
        GameState gs = new GameState();
        gs.setWinner(null);
        return new GameExitedEvent(gs, null);
    }

    /** Event where a player of the given type wins, with specific AI level. */
    protected static GameExitedEvent winEvent(Player.Type winnerType, Intelligence aiLevel) {
        Player winner = new Player(0, winnerType);
        GameState gs = new GameState();
        gs.setWinner(winner);
        gs.setBotIntelligence(aiLevel);
        return new GameExitedEvent(gs, null);
    }

    /** Event where local player wins with preferences attached. */
    protected static GameExitedEvent localPlayerWinEventWithPrefs(
            Intelligence aiLevel, NewGamePreferences.MapSizes mapSize, int winningRound) {
        Player winner = new Player(0, Player.Type.LOCAL_PLAYER);
        GameState gs = new GameState();
        gs.setWinner(winner);
        gs.setBotIntelligence(aiLevel);
        gs.setWinningRound(winningRound);

        NewGamePreferences prefs = new NewGamePreferences(
            142,        // seed, just some number
            aiLevel,
            mapSize,
            NewGamePreferences.Densities.MEDIUM,
            0,  // startingPosition
            TOTAL_PLAYERS
        );

        return new GameExitedEvent(gs, prefs);
    }

    /** Event where local player wins with preferences (including starting position). */
    protected static GameExitedEvent localPlayerWinEventFull(
            Intelligence aiLevel, int startingPosition) {
        Player winner = new Player(0, Player.Type.LOCAL_PLAYER);
        Player[] players = new Player[TOTAL_PLAYERS];
        for (int i = 0; i < TOTAL_PLAYERS; i++) {
            players[i] = i == startingPosition ? new Player(i, Player.Type.LOCAL_PLAYER) : new Player(i, Player.Type.LOCAL_BOT);
        }

        GameState gs = new GameState();
        gs.setWinner(winner);
        gs.setBotIntelligence(aiLevel);
        gs.setPlayers(Arrays.asList(players));

        NewGamePreferences prefs = new NewGamePreferences(
            142,        // seed, just some number
            aiLevel,
            NewGamePreferences.MapSizes.MEDIUM,
            NewGamePreferences.Densities.MEDIUM,
            startingPosition,  // startingPosition
            TOTAL_PLAYERS
        );

        return new GameExitedEvent(gs, prefs);
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

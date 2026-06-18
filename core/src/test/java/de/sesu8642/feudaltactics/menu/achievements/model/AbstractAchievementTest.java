package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Common base for achievement tests. Provides shared helpers and tests that apply to all achievements.
 */
abstract class AbstractAchievementTest<T extends AbstractAchievement> {

    // ---- event builder helpers ----
    protected static final int TOTAL_PLAYERS = 6;
    protected T achievement;

    protected static GameExitedEvent noWinnerEvent() {
        final GameState gs = new GameState();
        gs.setWinner(null);
        return new GameExitedEvent(gs, null);
    }

    /**
     * Event where a player of the given type wins, with specific AI level.
     */
    protected static GameExitedEvent winEvent(Player.Type winnerType, Intelligence aiLevel) {
        final Player winner = new Player(0, winnerType);
        final GameState gs = new GameState();
        gs.setWinner(winner);
        gs.setBotIntelligence(aiLevel);
        return new GameExitedEvent(gs, null);
    }

    // ---- common id / description tests ----

    /**
     * Event where local player wins with preferences attached.
     */
    protected static GameExitedEvent localPlayerWinEventWithPrefs(
        Intelligence aiLevel, NewGamePreferences.MapSizes mapSize, int winningRound) {
        final Player winner = new Player(0, Player.Type.LOCAL_PLAYER);
        final GameState gs = new GameState();
        gs.setWinner(winner);
        gs.setBotIntelligence(aiLevel);
        gs.setWinningRound(winningRound);

        final NewGamePreferences prefs = new NewGamePreferences(
            142,        // seed, just some number
            aiLevel,
            mapSize,
            NewGamePreferences.Densities.MEDIUM,
            0,  // startingPosition
            TOTAL_PLAYERS
        );

        return new GameExitedEvent(gs, prefs);
    }

    /**
     * Event where local player wins with preferences (including starting position).
     */
    protected static GameExitedEvent localPlayerWinEventFull(
        Intelligence aiLevel, int startingPosition) {
        final Player winner = new Player(0, Player.Type.LOCAL_PLAYER);
        final Player[] players = new Player[TOTAL_PLAYERS];
        for (int i = 0; i < TOTAL_PLAYERS; i++) {
            players[i] = i == startingPosition ? new Player(i, Player.Type.LOCAL_PLAYER) : new Player(i,
                Player.Type.LOCAL_BOT);
        }

        final GameState gs = new GameState();
        gs.setWinner(winner);
        gs.setBotIntelligence(aiLevel);
        gs.setPlayers(Arrays.asList(players));

        final NewGamePreferences prefs = new NewGamePreferences(
            142,        // seed, just some number
            aiLevel,
            NewGamePreferences.MapSizes.MEDIUM,
            NewGamePreferences.Densities.MEDIUM,
            startingPosition,  // startingPosition
            TOTAL_PLAYERS
        );

        return new GameExitedEvent(gs, prefs);
    }

    /**
     * Create the achievement under test. Called in {@link #setUp()}.
     */
    protected abstract T createAchievement();

    /**
     * Create the same type of achievement but with different constructor parameters,
     * so that we can verify the id changes. Return {@code null} if no parameterised variant exists.
     */
    protected T createAchievementWithDifferentParams() {
        return null; // override when applicable
    }

    // ---- common onGameExited null-safety test ----

    @BeforeEach
    void setUp() {
        achievement = createAchievement();
    }

    @Test
    void id_isConstant() {
        final String first = achievement.getId();
        final String second = achievement.getId();
        assertEquals(first, second, "getId() must return a stable value");
    }

    @Test
    void id_changesWithDifferentParams() {
        final T other = createAchievementWithDifferentParams();
        if (other != null) {
            assertNotEquals(achievement.getId(), other.getId(),
                "Achievements with different params should produce different ids");
        }
    }

    @Test
    void descriptionKey_isNotNull() {
        final String desc = achievement.getBaseDescriptionTranslationKey();
        assertNotNull(desc);
    }

    @Test
    void nameKey_isNotNull() {
        final String name = achievement.getNameTranslationKey();
        assertNotNull(name);
    }

    @Test
    void onGameExited_nullGameState_doesNothing() {
        final GameExitedEvent event = new GameExitedEvent(null, null);

        final boolean result = achievement.onGameExited(event);
        assertFalse(result);
    }

    /**
     * Verify that storeProgress was called for the achievement.
     */
    protected void verifyProgress(int expectedProgress) {
        assertEquals(expectedProgress, achievement.getProgress());
    }

    /**
     * Verify no event bus interaction happened.
     */
    protected void verifyNoProgress() {
        assertEquals(0, achievement.getProgress());
    }
}

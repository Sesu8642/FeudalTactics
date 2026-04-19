package de.sesu8642.feudaltactics.menu.achievements.model;

import com.badlogic.gdx.Preferences;
import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.ingame.GameParameters;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class WinVeryHardGamesInARowAchievementTest extends AbstractAchievementTest<WinVeryHardGamesInARowAchievement> {

    private static final int GOAL = 3;
    private Map<String, Object> prefData;
    private Preferences prefs;

    @Override
    protected WinVeryHardGamesInARowAchievement createAchievement(AchievementRepository repo) {
        return new WinVeryHardGamesInARowAchievement(repo, prefs, GOAL);
    }

    @Override
    protected WinVeryHardGamesInARowAchievement createAchievementWithDifferentParams(AchievementRepository repo) {
        return new WinVeryHardGamesInARowAchievement(repo, prefs, 10);
    }

    @Override
    @BeforeEach
    void setUp() {
        prefData = new HashMap<>();
        prefs = createInMemoryPrefs();
        super.setUp();
    }

    @Test
    void noWinner_resetsProgress() {
        achievement.onGameExited(noWinnerEvent());
        verifyProgress(0);
    }

    @Test
    void botWins_resetsProgress() {
        achievement.onGameExited(winEvent(Player.Type.LOCAL_PLAYER, Intelligence.LEVEL_4));
        achievement.onGameExited(winEvent(Player.Type.LOCAL_BOT, Intelligence.LEVEL_4));
        verifyProgress(0);
    }

    @Test
    void wrongDifficulty_resetsProgress() {
        achievement.onGameExited(winEvent(Player.Type.LOCAL_PLAYER, Intelligence.LEVEL_4));
        achievement.onGameExited(winEvent(Player.Type.LOCAL_PLAYER, Intelligence.LEVEL_3));
        verifyProgress(0);
    }

    @Test
    void veryHardWin_incrementsProgress() {
        achievement.onGameExited(winEvent(Player.Type.LOCAL_PLAYER, Intelligence.LEVEL_4));
        verifyProgress(1);
    }

    @Test
    void firstMapGeneration_doesNotResetProgress() {
        achievement.onMapRegeneration(mapRegenEvent(System.currentTimeMillis()));
        verifyNoProgress();
    }

    @Test
    void secondMapGeneration_resetsProgress() {
        achievement.onMapRegeneration(mapRegenEvent(System.currentTimeMillis()));
        achievement.onMapRegeneration(mapRegenEvent(System.currentTimeMillis()));
        verifyProgress(0);
    }

    @Test
    void copiedMapSeed_resetsProgress() {
        long oldSeed = System.currentTimeMillis() - 100_000;
        achievement.onMapRegeneration(mapRegenEvent(oldSeed));
        verifyProgress(0);
    }

    @Test
    void gameExitResetsMapGeneratedFlag() {
        achievement.onMapRegeneration(mapRegenEvent(System.currentTimeMillis()));

        // Win resets the flag
        achievement.onGameExited(winEvent(Player.Type.LOCAL_PLAYER, Intelligence.LEVEL_4));
        reset(repository);

        // Next generation should be treated as first again
        achievement.onMapRegeneration(mapRegenEvent(System.currentTimeMillis()));
        verifyNoProgress();
    }

    private RegenerateMapEvent mapRegenEvent(long seed) {
        Player local = new Player(0, Player.Type.LOCAL_PLAYER);
        Player bot = new Player(1, Player.Type.LOCAL_BOT);

        GameParameters gp = new GameParameters(
            0, // humanPlayerIndex, 0 is okay for the test
            444,              // seed, just some number
            NewGamePreferences.MapSizes.MEDIUM.getAmountOfTiles(), // landMass
            NewGamePreferences.Densities.MEDIUM.getDensityFloat(),    // density
            Intelligence.LEVEL_2,   // AI level, not important for the test
            1                 // number of bot player
        );

        return new RegenerateMapEvent(gp);
    }

    private Preferences createInMemoryPrefs() {
        Preferences mockPrefs = mock(Preferences.class);    // TODO: Check whether there is an implementation for this in the lib already
        when(mockPrefs.getInteger(anyString(), anyInt())).thenAnswer(inv -> {
            Object val = prefData.get((String) inv.getArgument(0));
            return val != null ? (int) val : (int) inv.getArgument(1);
        });
        when(mockPrefs.getBoolean(anyString(), anyBoolean())).thenAnswer(inv -> {
            Object val = prefData.get((String) inv.getArgument(0));
            return val != null ? (boolean) val : (boolean) inv.getArgument(1);
        });
        doAnswer(inv -> { prefData.put(inv.getArgument(0), inv.getArgument(1)); return null; })
                .when(mockPrefs).putInteger(anyString(), anyInt());
        doAnswer(inv -> { prefData.put(inv.getArgument(0), inv.getArgument(1)); return null; })
                .when(mockPrefs).putBoolean(anyString(), anyBoolean());
        doNothing().when(mockPrefs).flush();
        return mockPrefs;
    }
}

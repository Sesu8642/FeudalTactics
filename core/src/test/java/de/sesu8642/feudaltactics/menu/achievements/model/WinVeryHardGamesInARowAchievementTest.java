package de.sesu8642.feudaltactics.menu.achievements.model;

import com.badlogic.gdx.Preferences;
import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.ingame.GameParameters;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import de.ui.statistics.MockPreferences;

import static org.mockito.Mockito.reset;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WinVeryHardGamesInARowAchievementTest extends AbstractAchievementTest<WinVeryHardGamesInARowAchievement> {

    private static final int GOAL = 3;
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
        prefs = new MockPreferences();
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
        verifyNoProgress();
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
        GameParameters gp = new GameParameters(
            0, // humanPlayerIndex, 0 is okay for the test
            seed,              // seed, just some number
            NewGamePreferences.MapSizes.MEDIUM.getAmountOfTiles(), // landMass
            NewGamePreferences.Densities.MEDIUM.getDensityFloat(),    // density
            Intelligence.LEVEL_2,   // AI level, not important for the test
            1                 // number of bot player
        );

        return new RegenerateMapEvent(gp);
    }
}

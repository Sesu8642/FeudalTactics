package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.ingame.GameParameters;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.shared.events.RegenerateMapEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class WinVeryHardGamesInARowAchievementTest extends AbstractAchievementTest<WinVeryHardGamesInARowAchievement> {

    private static final int GOAL = 3;

    @Override
    protected WinVeryHardGamesInARowAchievement createAchievement() {
        return new WinVeryHardGamesInARowAchievement(GOAL);
    }

    @Override
    protected WinVeryHardGamesInARowAchievement createAchievementWithDifferentParams() {
        return new WinVeryHardGamesInARowAchievement(10);
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
        assertEquals(0, achievement.getProgress());
        achievement.onMapRegeneration(mapRegenEvent(System.currentTimeMillis()));
        assertEquals(0, achievement.getProgress());
    }

    @Test
    void secondMapGeneration_resetsProgress() {
        achievement.onMapRegeneration(mapRegenEvent(System.currentTimeMillis()));
        achievement.onMapRegeneration(mapRegenEvent(System.currentTimeMillis()));
        verifyProgress(0);
    }

    @Test
    void copiedMapSeed_resetsProgress() {
        final long oldSeed = System.currentTimeMillis() - 100_000;
        achievement.onMapRegeneration(mapRegenEvent(oldSeed));
        verifyProgress(0);
    }

    @Test
    void gameExitResetsMapGeneratedFlag() {
        achievement.onMapRegeneration(mapRegenEvent(System.currentTimeMillis()));

        // Win resets the flag
        final boolean resExitedEvent = achievement.onGameExited(winEvent(Player.Type.LOCAL_PLAYER, Intelligence.LEVEL_4));
        assertTrue(resExitedEvent);
        final int progress = achievement.getProgress();
        assertTrue(progress > 0);

        // Next generation should be treated as first again (progress preserved, not reset)
        final boolean resMapRegen2 = achievement.onMapRegeneration(mapRegenEvent(System.currentTimeMillis()));
        assertTrue(resMapRegen2);
        verifyProgress(progress);
    }

    private RegenerateMapEvent mapRegenEvent(long seed) {
        final GameParameters gp = new GameParameters(
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

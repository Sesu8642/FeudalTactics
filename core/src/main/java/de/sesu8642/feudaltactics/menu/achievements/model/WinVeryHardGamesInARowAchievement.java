package de.sesu8642.feudaltactics.menu.achievements.model;

import com.badlogic.gdx.Preferences;

import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

public class WinVeryHardGamesInARowAchievement extends AbstractAchievement {

    private Preferences prefStore;

    private boolean nextMapHasBeenGenerated;

    private void setNextMapHasBeenGenerated(boolean value) {
        this.nextMapHasBeenGenerated = value;
        prefStore.putBoolean(getId() + "-mapHasBeenGenerated", value);
        prefStore.flush();
    }

    public WinVeryHardGamesInARowAchievement(
        AchievementRepository achievementRepository,
        Preferences achievementsPrefs,
        int numberOfGamesInARowToWin) {
        super(achievementRepository, numberOfGamesInARowToWin);

        this.prefStore = achievementsPrefs;

        // TODO: Load which color the player used in the current streak

        nextMapHasBeenGenerated = prefStore.getBoolean(getId() + "-mapHasBeenGenerated", false);
    }

    @Override
    public String getId() {
        return "win_" + getGoal() + "_very_hard_games_in_a_row";
    }

    @Override
    public String getName() {
        return "Win " + getGoal() + " Very Hard Games in a Row";
    }

    @Override
    public String getDescription() {
        return "Win " + getGoal() + " games in a row on Very Hard AI difficulty. Any map size is allowed, "
        + "but you must win consecutively without losing or aborting, and you must not re-generate the map (always use the first generated map). "
        + "Furthermore, you must always play with the same color and you must not play a game on lower difficulty in between.";
    }

    @Override
    public void onGameExited(de.sesu8642.feudaltactics.events.GameExitedEvent event) {
        GameState gameState = event.getGameState();

        if (gameState == null) {
            return;     // Ignore exits from editor or similar
        }

        setNextMapHasBeenGenerated(false);

        final Player winnerOfTheGame = gameState.getWinner();
        if (winnerOfTheGame == null || winnerOfTheGame.getType() != Player.Type.LOCAL_PLAYER) {
            storeProgress(0);   // Lost or aborted a game, reset progress
            return;
        }

        Intelligence aiLevel = gameState.getBotIntelligence();
        if (aiLevel != Intelligence.LEVEL_4) {
            storeProgress(0);   // Not a Very Hard game, reset progress
            return;
        }

        storeProgress(getProgress() + 1);
    }

    @Override
    public void onMapRegeneration(de.sesu8642.feudaltactics.events.RegenerateMapEvent event) {
        // Find whether this is the next game in the current streak or whether the player re-generated the map in between, and reset progress if it's the latter

        if (nextMapHasBeenGenerated) {
            storeProgress(0);   // Map was re-generated, reset progress
        } else {
            setNextMapHasBeenGenerated(true);
        }
    }
}

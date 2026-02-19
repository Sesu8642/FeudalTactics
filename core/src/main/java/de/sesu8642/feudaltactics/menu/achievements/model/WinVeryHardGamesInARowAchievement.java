package de.sesu8642.feudaltactics.menu.achievements.model;

import com.badlogic.gdx.Preferences;

import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.ingame.GameParameters;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

public class WinVeryHardGamesInARowAchievement extends AbstractAchievement {

    private static final String CURRENT_STREAK_PLAYER_INDEX_NAME = "current-streak-player-index";

    private Preferences prefStore;

    private boolean nextMapHasBeenGenerated;

    private void setNextMapHasBeenGenerated(boolean value) {
        this.nextMapHasBeenGenerated = value;
        prefStore.putBoolean(getId() + "-mapHasBeenGenerated", value);
        prefStore.flush();
    }

    private int currentStreakPlayerIndex;

    private void setCurrentStreakPlayerIndex(int playerIndex) {
        this.currentStreakPlayerIndex = playerIndex;
        prefStore.putInteger(CURRENT_STREAK_PLAYER_INDEX_NAME, playerIndex);
        prefStore.flush();
    }

    public WinVeryHardGamesInARowAchievement(
        AchievementRepository achievementRepository,
        Preferences achievementsPrefs,
        int numberOfGamesInARowToWin) {
        super(achievementRepository, numberOfGamesInARowToWin);

        this.prefStore = achievementsPrefs;

        currentStreakPlayerIndex = prefStore.getInteger(CURRENT_STREAK_PLAYER_INDEX_NAME, -1);
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

        int thisTimePlayerIndex = winnerOfTheGame.getPlayerIndex();
        if (thisTimePlayerIndex != currentStreakPlayerIndex) {
            setCurrentStreakPlayerIndex(thisTimePlayerIndex);    // Start new streak with this color
            // Progress must have been 0 already and was now increased to 1,
            // as this streak color change was detected when starting the game
        }
    }

    @Override
    public void onMapRegeneration(RegenerateMapEvent event) {
        // First check whether this is a random map generation or whether it is a copied map, which is "illegal" for this achievement
        // The seed for a random map is based on the current time, so if the seed is for now, it is random.
        GameParameters gameParams = event.getGameParams();
        long seed = gameParams.getSeed();
        long expectedSeed = System.currentTimeMillis();
        if (Math.abs(seed - expectedSeed) > 1000) {   // If the seed is more than 10 seconds in the past or future, it is not random
            storeProgress(0);
            return;
        }

        // Check that the player did not change the color
        gameParams.getPlayers().stream()
            .filter(player -> player.getType() == Player.Type.LOCAL_PLAYER)
            .findFirst()
            .ifPresent(player -> {
                int playerIndex = player.getPlayerIndex();
                if (currentStreakPlayerIndex != -1 && playerIndex != currentStreakPlayerIndex) {
                    storeProgress(0);   // Player changed color, reset progress
                        // We don't need to store the streak color here, as it will be store when exiting this game 
                        // if the player wins and actually starts a streak
                }
            });
        
        // Find whether this is the next game in the current streak or whether the player re-generated the map in between, and reset progress if it's the latter
        if (nextMapHasBeenGenerated) {
            storeProgress(0);   // Map was re-generated, reset progress
        } else {
            setNextMapHasBeenGenerated(true);
        }
    }
}

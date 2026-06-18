package de.sesu8642.feudaltactics.menu.achievements.model;

import com.badlogic.gdx.utils.Json;
import com.google.common.collect.ImmutableList;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.ingame.GameParameters;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementNeedsFullStorage;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;
import de.sesu8642.feudaltactics.shared.events.RegenerateMapEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Achievement: Win a specified number of Very Hard games in a row without losing or aborting.
 * <p>
 * It implements AchievementNeedsFullStorage, as the two values nextMapHasBeenGenerated and currentStreakPlayerIndex
 * must be persisted
 * in order to properly track the progress of this achievement.
 */
@EqualsAndHashCode(callSuper = true, onlyExplicitlyIncluded = true)
public class WinVeryHardGamesInARowAchievement extends AbstractAchievement implements AchievementNeedsFullStorage {

    private final Json json = new Json();
    @Getter
    @EqualsAndHashCode.Include
    private boolean nextMapHasBeenGenerated;
    @Getter
    @EqualsAndHashCode.Include
    private int currentStreakPlayerIndex;

    public WinVeryHardGamesInARowAchievement(int numberOfGamesInARowToWin) {
        super(numberOfGamesInARowToWin, TranslationKeys.ACHIEVEMENT_WIN_VERY_HARD_GAMES_IN_A_ROW_NAME,
            ImmutableList.of(String.valueOf(numberOfGamesInARowToWin)),
            TranslationKeys.ACHIEVEMENT_WIN_VERY_HARD_GAMES_IN_A_ROW_DESCRIPTION,
            ImmutableList.of(String.valueOf(numberOfGamesInARowToWin)));
    }

    @Override
    public String getId() {
        return "win_" + getGoal() + "_very_hard_games_in_a_row";
    }

    @Override
    public boolean onGameExited(GameExitedEvent event) {
        final GameState gameState = event.getGameState();

        if (gameState == null) {
            return false;     // Ignore exits from editor or similar
        }

        nextMapHasBeenGenerated = false;

        final Player winnerOfTheGame = gameState.getWinner();
        if (winnerOfTheGame == null || winnerOfTheGame.getType() != Player.Type.LOCAL_PLAYER) {
            storeProgress(0);   // Lost or aborted a game, reset progress
            return true;
        }

        final Intelligence aiLevel = gameState.getBotIntelligence();
        if (aiLevel != Intelligence.LEVEL_4) {
            storeProgress(0);   // Not a Very Hard game, reset progress
            return true;
        }

        final int thisTimePlayerIndex = winnerOfTheGame.getPlayerIndex();
        if (thisTimePlayerIndex != currentStreakPlayerIndex) {
            currentStreakPlayerIndex = thisTimePlayerIndex;    // Start new streak with this color
            // Progress must have been 0 already and will now increase to 1,
            // as this streak color change was detected when starting the game
        }

        storeProgress(getProgress() + 1);
        return true;
    }

    @Override
    public boolean onMapRegeneration(RegenerateMapEvent event) {
        // First check whether this is a random map generation or whether it is a copied map, which is "illegal" for
        // this achievement
        // The seed for a random map is based on the current time, so if the seed is for now, it is random.
        final GameParameters gameParams = event.getGameParams();
        final long seed = gameParams.getSeed();
        final long expectedSeed = System.currentTimeMillis();
        if (Math.abs(seed - expectedSeed) > 1000) {   // If the seed is more than 10 seconds in the past or future,
            // it is not random
            storeProgress(0);
            return true;
        }

        // Check that the player did not change the color
        gameParams.getPlayers().stream()
            .filter(player -> player.getType() == Player.Type.LOCAL_PLAYER)
            .findFirst()
            .ifPresent(player -> {
                final int playerIndex = player.getPlayerIndex();
                if (currentStreakPlayerIndex != -1 && playerIndex != currentStreakPlayerIndex) {
                    storeProgress(0);   // Player changed color, reset progress
                    // We don't need to store the streak color here, as it will be store when exiting this game
                    // if the player wins and actually starts a streak
                }
            });

        // Find whether this is the next game in the current streak or whether the player re-generated the map in
        // between, and reset progress if it's the latter
        if (nextMapHasBeenGenerated) {
            storeProgress(0);   // Map was re-generated, reset progress
        } else {
            nextMapHasBeenGenerated = true;
            storeProgress(getProgress());   // This is just for triggering the storage of the nextMapHasBeenGenerated
            // value
        }
        return true;
    }

    @Override
    public String serializeToJson() {
        final SerializedData data = new SerializedData();
        data.currentStreakPlayerIndex = currentStreakPlayerIndex;
        data.nextMapHasBeenGenerated = nextMapHasBeenGenerated;
        return json.toJson(data);
    }

    @Override
    public void deserializeFromJson(String serializedData) {
        final SerializedData data = json.fromJson(SerializedData.class, serializedData);
        // Progress is already loaded otherwise
        currentStreakPlayerIndex = data.currentStreakPlayerIndex;
        nextMapHasBeenGenerated = data.nextMapHasBeenGenerated;
    }

    private static class SerializedData {
        public int currentStreakPlayerIndex;
        public boolean nextMapHasBeenGenerated;
    }
}

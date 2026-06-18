package de.sesu8642.feudaltactics.menu.achievements.model;

import com.google.common.collect.ImmutableList;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;

/**
 * Achievement: Win a game that lasts at least a specified number of rounds.
 */
public class PlayMoreThanNRoundsAchievement extends AbstractAchievement {

    private final int roundCount;

    // TODO: how to insert the rowCount later
    public PlayMoreThanNRoundsAchievement(int roundCount) {
        super(1, TranslationKeys.ACHIEVEMENT_PLAY_MORE_THAN_N_ROUNDS_NAME,
            ImmutableList.of(String.valueOf(roundCount)),
            TranslationKeys.ACHIEVEMENT_PLAY_MORE_THAN_N_ROUNDS_DESCRIPTION,
            ImmutableList.of(String.valueOf(roundCount)));
        this.roundCount = roundCount;
    }

    @Override
    public boolean isSecret() {
        return true;
    }

    @Override
    public String getId() {
        return "win_in_more_than_" + roundCount + "_rounds";
    }

    @Override
    public boolean onGameExited(GameExitedEvent event) {
        final de.sesu8642.feudaltactics.lib.gamestate.GameState gameState = event.getGameState();
        if (gameState == null) {
            return false;     // Ignore exits from editor or similar
        }

        if (gameState.getRound() >= roundCount) {
            storeProgress(1); // unlock
            return true;
        }
        return false;
    }
}

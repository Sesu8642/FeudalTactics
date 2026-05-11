package de.sesu8642.feudaltactics.menu.achievements.model;

import com.google.common.eventbus.EventBus;

/**
 * Achievement: Win a game that lasts at least a specified number of rounds.
 */
public class PlayMoreThanNRoundsAchievement extends AbstractAchievement {

    private final int roundCount;

    public PlayMoreThanNRoundsAchievement(EventBus eventBus, int roundCount) {
        super(eventBus, 1, "Win using more than " + roundCount + " rounds");
        this.roundCount = roundCount;
    }

    @Override
    public boolean isSecret() {
        return true;
    }

    @Override
    public String getBaseDescription() {
        return "Win a game that lasts at least " + roundCount + " rounds.";
    }

    @Override
    public String getId() {
        return "win_in_more_than_" + roundCount + "_rounds";
    }

    @Override
    public void onGameExited(de.sesu8642.feudaltactics.events.GameExitedEvent event) {
        final de.sesu8642.feudaltactics.lib.gamestate.GameState gameState = event.getGameState();
        if (gameState == null) {
            return;     // Ignore exits from editor or similar
        }

        if ( gameState.getRound() >= roundCount) {
            storeProgress(1); // unlock
        }
    }
}

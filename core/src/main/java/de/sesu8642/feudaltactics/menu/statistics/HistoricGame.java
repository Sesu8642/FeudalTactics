// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Value object: game history.
 */
@EqualsAndHashCode
@NoArgsConstructor
public class HistoricGame {

    @Getter
    private NewGamePreferences gameSettings;

    @Getter
    private GameResult gameResult;

    @Getter
    private int turnsPlayed;

    @Getter
    private long timestamp;

    /**
     * Constructor.
     *
     * @param gameSettings settings used for the game
     * @param gameResult   result of the game
     * @param turnsPlayed number of turns played in the game
     * @param timestamp   timestamp when the game was played
     */
    public HistoricGame(NewGamePreferences gameSettings, GameResult gameResult, int turnsPlayed, long timestamp) {
        this.gameSettings = gameSettings;
        this.gameResult = gameResult;
        this.turnsPlayed = turnsPlayed;
        this.timestamp = timestamp;
    }

    public enum GameResult {
        WIN,
        LOSS,
        ABORTED,
        /**
         * The game was started but has not yet been completed. It will be modified later to WIN, LOSS or ABORTED
         * in a new instance of HistoricGame.
         */
        STARTED
    }
}

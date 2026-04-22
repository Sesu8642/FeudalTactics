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
    private int roundsPlayed;

    @Getter
    private long timestamp;

    /**
     * Constructor.
     *
     * @param gameSettings settings used for the game
     * @param gameResult   result of the game
     * @param roundsPlayed number of rounds played in the game
     * @param timestamp    timestamp when the game was played
     */
    public HistoricGame(NewGamePreferences gameSettings, GameResult gameResult, int roundsPlayed, long timestamp) {
        this.gameSettings = gameSettings;
        this.gameResult = gameResult;
        this.roundsPlayed = roundsPlayed;
        this.timestamp = timestamp;
    }

    public enum GameResult {
        WIN,
        LOSS,
        ABORTED
    }
}

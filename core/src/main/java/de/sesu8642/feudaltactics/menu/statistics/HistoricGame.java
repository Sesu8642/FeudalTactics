// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Value object: game history.
 */
@EqualsAndHashCode
public class HistoricGame {

    @Getter
    private final NewGamePreferences gameSettings;

    @Getter
    private final GameResult gameResult;

    /**
     * Constructor.
     *
     * @param gameSettings settings used for the game
     * @param gameResult   result of the game
     */
    public HistoricGame(NewGamePreferences gameSettings, GameResult gameResult) {
        this.gameSettings = gameSettings;
        this.gameResult = gameResult;
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

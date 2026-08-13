// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Contains multiple historic games. This class is used as a wrapper for persisting the game history, because json
 * .setElementType does not support List<GameHistory> as the first parameter directly.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameHistory {

    /**
     * This field was previously a list but that caused crashes on some devices when trying to parse from JSON: "com
     * .badlogic.gdx.utils.SerializationException: Class cannot be created (missing no-arg constructor): java.util
     * .ArrayList$SubList"
     */
    private HistoricGame[] historicGames = {};

    /**
     * Returning a list for better usability.
     */
    public List<HistoricGame> getHistoricGames() {
        return Arrays.stream(historicGames).collect(Collectors.toList());
    }

}

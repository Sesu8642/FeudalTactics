// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import java.util.Map;

import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import lombok.Getter;

/**
 * Value object: count of events categorized by AI difficulty level.
 */
public class CountByAiLevel {
    @Getter
    private final int totalCount;

    @Getter
    private final Map<Intelligence, Integer> countByAiLevel;

    public CountByAiLevel(int totalCount, Map<Intelligence, Integer> countByAiLevel) {
        this.totalCount = totalCount;
        this.countByAiLevel = countByAiLevel;
    }
}
// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Value object: game statistics.
 */
@EqualsAndHashCode
public class Statistics {

    @Getter
    private final int gamesPlayed;
    @Getter
    private final int mapsGenerated;
    @Getter
    private final CountByAiLevel gamesWon;
    @Getter
    private final CountByAiLevel gamesLost;
    @Getter
    private final CountByAiLevel gamesAborted;

    /**
     * Constructor.
     *
     * @param gamesPlayed   number of games played
     * @param mapsGenerated number of maps generated
     * @param gamesWon      number of games won
     * @param gamesLost     number of games lost
     * @param gamesAborted  number of games aborted
     */
    public Statistics(int mapsGenerated, CountByAiLevel gamesWon, CountByAiLevel gamesLost,
                      CountByAiLevel gamesAborted) {
        this.mapsGenerated = mapsGenerated;
        this.gamesWon = gamesWon;
        this.gamesLost = gamesLost;
        this.gamesAborted = gamesAborted;
        gamesPlayed = gamesWon.getTotalCount() + gamesLost.getTotalCount() + gamesAborted.getTotalCount();
    }
}

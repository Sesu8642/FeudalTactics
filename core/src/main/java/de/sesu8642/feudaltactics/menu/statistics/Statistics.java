// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import lombok.EqualsAndHashCode;
import lombok.Getter;

import de.sesu8642.feudaltactics.menu.statistics.CountByAiLevel;

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
    @Getter
    private final long recordSeed;
    @Getter
    private final int recordSeedCount;

    /**
     * Constructor.
     *
     * @param gamesPlayed    number of games played
     * @param mapsGenerated  number of maps generated
     * @param gamesWon       number of games won
     * @param gamesLost      number of games lost
     * @param gamesAborted   number of games aborted
     * @param recordSeedCount 
     * @param recordSeed 
     */
    public Statistics(int gamesPlayed, int mapsGenerated, CountByAiLevel gamesWon, CountByAiLevel gamesLost, CountByAiLevel gamesAborted, long recordSeed, int recordSeedCount) {
        this.gamesPlayed = gamesPlayed;
        this.mapsGenerated = mapsGenerated;
        this.gamesWon = gamesWon;
        this.gamesLost = gamesLost;
        this.gamesAborted = gamesAborted;
        this.recordSeed = recordSeed;
        this.recordSeedCount = recordSeedCount;
    }
}
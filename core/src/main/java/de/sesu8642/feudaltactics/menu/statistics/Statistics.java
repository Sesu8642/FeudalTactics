// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import lombok.Getter;

import java.util.Map;
import java.util.Objects;

import de.sesu8642.feudaltactics.menu.statistics.CountByAiLevel;

/**
 * Value object: game statistics.
 */
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
     * @param gamesPlayed    number of games played
     * @param mapsGenerated  number of maps generated
     * @param gamesWon       number of games won
     * @param gamesLost      number of games lost
     * @param gamesAborted   number of games aborted
     */
    public Statistics(int gamesPlayed, int mapsGenerated, CountByAiLevel gamesWon, CountByAiLevel gamesLost, CountByAiLevel gamesAborted) {
        this.gamesPlayed = gamesPlayed;
        this.mapsGenerated = mapsGenerated;
        this.gamesWon = gamesWon;
        this.gamesLost = gamesLost;
        this.gamesAborted = gamesAborted;
    }

    @Override
    public int hashCode() {
        return Objects.hash(gamesPlayed, mapsGenerated, gamesWon, gamesLost, gamesAborted);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Statistics other = (Statistics) obj;
        return gamesPlayed == other.gamesPlayed && mapsGenerated == other.mapsGenerated
                && gamesWon == other.gamesWon && gamesLost == other.gamesLost && gamesAborted == other.gamesAborted;
    }

}
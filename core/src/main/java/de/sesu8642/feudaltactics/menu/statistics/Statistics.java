// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import lombok.Getter;

import java.util.Objects;

/**
 * Value object: game statistics.
 */
public class Statistics {

    @Getter
    private final int gamesPlayed;
    @Getter
    private final int mapsGenerated;
    @Getter
    private final int gamesWon;
    @Getter
    private final int gamesLost;
    @Getter
    private final int gamesAborted;

    /**
     * Constructor.
     *
     * @param gamesPlayed    number of games played
     * @param mapsGenerated  number of maps generated
     * @param gamesWon       number of games won
     * @param gamesLost      number of games lost
     * @param gamesAborted   number of games aborted
     */
    public Statistics(int gamesPlayed, int mapsGenerated, int gamesWon, int gamesLost, int gamesAborted) {
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
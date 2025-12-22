// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import com.badlogic.gdx.Preferences;
import de.sesu8642.feudaltactics.menu.statistics.dagger.StatisticsPrefsPrefStore;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Data access object for the statistics.
 */
@Singleton
public class StatisticsDao {

    public static final String STATISTICS_NAME = "statistics";
    private static final String GAMES_PLAYED_NAME = "GamesPlayed";
    private static final String GAMES_WON_NAME = "GamesWon";
    private static final String GAMES_LOST_NAME = "GamesLost";
    private static final String MAPS_GENERATED_NAME = "MapsGenerated";

    private final Preferences prefStore;

    @Inject
    public StatisticsDao(@StatisticsPrefsPrefStore Preferences statisticsPrefs) {
        prefStore = statisticsPrefs;
    }

    /**
     * Saves the statistics.
     *
     * @param statistics statistics to save
     */
    public void saveStatistics(Statistics statistics) {
        prefStore.putInteger(GAMES_PLAYED_NAME, statistics.getGamesPlayed());
        prefStore.putInteger(MAPS_GENERATED_NAME, statistics.getMapsGenerated());
        prefStore.putInteger(GAMES_WON_NAME, statistics.getGamesWon());
        prefStore.putInteger(GAMES_LOST_NAME, statistics.getGamesLost());
        prefStore.flush();
    }

    public void incrementGamesStarted() {
        int gamesPlayed = prefStore.getInteger(GAMES_PLAYED_NAME, 0);
        prefStore.putInteger(GAMES_PLAYED_NAME, gamesPlayed + 1);
        prefStore.flush();
    }

    /**
     * Loads the statistics data.
     *
     * @return the loaded statistics
     */
    public Statistics getStatistics() {
        final int gamesPlayed = prefStore.getInteger(GAMES_PLAYED_NAME, 0);
        final int mapsGenerated = prefStore.getInteger(MAPS_GENERATED_NAME, 0);
        final int gamesWon = prefStore.getInteger(GAMES_WON_NAME, 0);
        final int gamesLost = prefStore.getInteger(GAMES_LOST_NAME, 0);
        return new Statistics(gamesPlayed, mapsGenerated, gamesWon, gamesLost);
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics;

import com.badlogic.gdx.Preferences;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.statistics.dagger.StatisticsPrefsPrefStore;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.Map;

/**
 * Data access object for the statistics.
 */
@Singleton
public class StatisticsDao {

    public static final String STATISTICS_NAME = "statistics";
    private static final String GAMES_WON_NAME = "GamesWon";
    private static final String GAMES_LOST_NAME = "GamesLost";
    private static final String GAMES_ABORTED_NAME = "GamesAborted";
    private static final String MAPS_GENERATED_NAME = "MapsGenerated";
    // This is the seed that has been played the most times in a row

    private final Preferences prefStore;

    @Inject
    public StatisticsDao(@StatisticsPrefsPrefStore Preferences statisticsPrefs) {
        prefStore = statisticsPrefs;
    }

    public void incrementGamesWon(Intelligence aiDifficulty) {
        incrementCountingStat(GAMES_WON_NAME + "-AI" + aiDifficulty.name());
    }

    public void incrementGamesLost(Intelligence aiDifficulty) {
        incrementCountingStat(GAMES_LOST_NAME + "-AI" + aiDifficulty.name());
    }

    public void incrementGamesAborted(Intelligence aiDifficulty) {
        incrementCountingStat(GAMES_ABORTED_NAME + "-AI" + aiDifficulty.name());
    }

    private void incrementCountingStat(String statName) {
        final int currentValue = prefStore.getInteger(statName, 0);
        prefStore.putInteger(statName, currentValue + 1);
        prefStore.flush();
    }

    private CountByAiLevel loadCountByAiLevel(String baseStatName) {
        int totalCount = 0;
        final Map<Intelligence, Integer> countByAiLevel = new HashMap<>();
        for (Intelligence level : Intelligence.values()) {
            final int count = prefStore.getInteger(baseStatName + "-AI" + level.name(), 0);
            totalCount += count;
            countByAiLevel.put(level, count);
        }
        return new CountByAiLevel(totalCount, countByAiLevel);
    }

    /**
     * Loads the statistics data.
     *
     * @return the loaded statistics
     */
    public Statistics getStatistics() {
        final int mapsGenerated = prefStore.getInteger(MAPS_GENERATED_NAME, 0);
        final CountByAiLevel gamesWon = loadCountByAiLevel(GAMES_WON_NAME);
        final CountByAiLevel gamesLost = loadCountByAiLevel(GAMES_LOST_NAME);
        final CountByAiLevel gamesAborted = loadCountByAiLevel(GAMES_ABORTED_NAME);
        return new Statistics(mapsGenerated, gamesWon, gamesLost, gamesAborted);
    }

    public void incrementSeedsGenerated() {
        incrementCountingStat(MAPS_GENERATED_NAME);
    }
}

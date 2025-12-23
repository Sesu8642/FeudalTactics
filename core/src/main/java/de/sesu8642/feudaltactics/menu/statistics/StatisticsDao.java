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
    private static final String GAMES_PLAYED_NAME = "GamesPlayed";
    private static final String GAMES_WON_NAME = "GamesWon";
    private static final String GAMES_LOST_NAME = "GamesLost";
    private static final String GAMES_ABORTED_NAME = "GamesAborted";
    private static final String MAPS_GENERATED_NAME = "MapsGenerated";
    private static final String RECORD_SEED_NAME = "RecordSeed";
    private static final String RECORD_SEED_COUNT_NAME = "RecordSeedCount";
    private static final String LAST_SEED_NAME = "LastSeed";
    private static final String LAST_SEED_COUNT_NAME = "LastSeedCount";

    private final Preferences prefStore;

    @Inject
    public StatisticsDao(@StatisticsPrefsPrefStore Preferences statisticsPrefs) {
        prefStore = statisticsPrefs;
    }

    public void incrementGamesPlayed() {
        incrementCountingStat(GAMES_PLAYED_NAME);
    }

    public void incrementGamesWon(Intelligence aiDifficulty) {
        incrementCountingStat(GAMES_WON_NAME);
        incrementCountingStat(GAMES_WON_NAME + "-AI" + aiDifficulty.name());
    }

    public void incrementGamesLost(Intelligence aiDifficulty) {
        incrementCountingStat(GAMES_LOST_NAME);
        incrementCountingStat(GAMES_LOST_NAME + "-AI" + aiDifficulty.name());
    }

    public void incrementGamesAborted(Intelligence aiDifficulty) {
        incrementCountingStat(GAMES_ABORTED_NAME);
        incrementCountingStat(GAMES_ABORTED_NAME + "-AI" + aiDifficulty.name());
    }

    private void incrementCountingStat(String statName) {
        int currentValue = prefStore.getInteger(statName, 0);
        prefStore.putInteger(statName, currentValue + 1);
        prefStore.flush();
    }

    /**
     * Registers that a seed has been played, updating the record if necessary.
     * Only consecutive plays of the same seed count towards the record.
     *
     * @param seed the seed that was played
     */
    public void registerSeedPlayed(long seed) {
        final long lastSeed = prefStore.getLong(LAST_SEED_NAME, Long.MAX_VALUE);
        int lastSeedCount;
        if (seed != lastSeed) {
            prefStore.putLong(LAST_SEED_NAME, seed);
            lastSeedCount = 0;
        } else {
            lastSeedCount = prefStore.getInteger(LAST_SEED_COUNT_NAME, 0);
        }
        prefStore.putInteger(LAST_SEED_COUNT_NAME, ++lastSeedCount);

        long recordSeedValue = prefStore.getLong(RECORD_SEED_COUNT_NAME, 0);
        if (lastSeedCount > recordSeedValue) {
            prefStore.putLong(RECORD_SEED_NAME, seed);
            prefStore.putInteger(RECORD_SEED_COUNT_NAME, lastSeedCount);
        }

        prefStore.flush();
    }

    private CountByAiLevel loadCountByAiLevel(String baseStatName) {
        int totalCount = prefStore.getInteger(baseStatName, 0);
        Map<Intelligence, Integer> countByAiLevel = new HashMap<>();
        for (Intelligence level : Intelligence.values()) {
            int count = prefStore.getInteger(baseStatName + "-AI" + level.name(), 0);
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
        final int gamesPlayed = prefStore.getInteger(GAMES_PLAYED_NAME, 0);
        final int mapsGenerated = prefStore.getInteger(MAPS_GENERATED_NAME, 0);
        final CountByAiLevel gamesWon = loadCountByAiLevel(GAMES_WON_NAME);
        final CountByAiLevel gamesLost = loadCountByAiLevel(GAMES_LOST_NAME);
        final CountByAiLevel gamesAborted = loadCountByAiLevel(GAMES_ABORTED_NAME);
        return new Statistics(gamesPlayed, mapsGenerated, gamesWon, gamesLost, gamesAborted);
    }
}

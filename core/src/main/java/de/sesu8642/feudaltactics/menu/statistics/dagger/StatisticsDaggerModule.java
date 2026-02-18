// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.dagger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.dagger.PreferencesPrefixProperty;
import de.sesu8642.feudaltactics.menu.statistics.HistoryDao;
import de.sesu8642.feudaltactics.menu.statistics.StatisticsDao;

import javax.inject.Singleton;

/**
 * Dagger module for the statistics preferences.
 */
@Module
public class StatisticsDaggerModule {

    private StatisticsDaggerModule() {
        // prevent instantiation
        throw new AssertionError();
    }

    @Provides
    @Singleton
    @StatisticsPrefStore
    static Preferences provideStatisticsPrefsPrefStore(@PreferencesPrefixProperty String prefix) {
        return Gdx.app.getPreferences(prefix + StatisticsDao.STATISTICS_NAME);
    }

    @Provides
    @Singleton
    @HistoryPrefStore
    static Preferences provideHistoryPrefsPrefStore(@PreferencesPrefixProperty String prefix) {
        return Gdx.app.getPreferences(prefix + HistoryDao.HISTORY_NAME);
    }
}
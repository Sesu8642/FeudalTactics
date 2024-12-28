// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.crashreporting.dagger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.dagger.PreferencesPrefixProperty;
import de.sesu8642.feudaltactics.menu.crashreporting.CrashReportDao;

import javax.inject.Singleton;

/**
 * Dagger module for crash reporting.
 */
@Module
public class CrashReportingDaggerModule {

    private CrashReportingDaggerModule() {
        // prevent instantiation
        throw new AssertionError();
    }

    @Provides
    @Singleton
    @CrashReportPrefStore
    static Preferences provideCrashReportPrefStore(@PreferencesPrefixProperty String prefix) {
        return Gdx.app.getPreferences(prefix + CrashReportDao.CRASH_REPORT_PREFERENCES_NAME);
    }

}

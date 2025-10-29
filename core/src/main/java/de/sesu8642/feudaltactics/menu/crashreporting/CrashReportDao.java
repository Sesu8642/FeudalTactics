// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.crashreporting;

import com.badlogic.gdx.Preferences;
import de.sesu8642.feudaltactics.menu.crashreporting.dagger.CrashReportPrefStore;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Data access object for crash reports.
 */
@Singleton
public class CrashReportDao {

    public static final String CRASH_REPORT_PREFERENCES_NAME = "crashReportPreferences";

    private static final String CRASH_REPORT_NAME = "crashReport";
    private static final String FRESH_CRASH_MARKER_NAME = "isCrashReportFresh";

    private final Preferences prefStore;

    @Inject
    public CrashReportDao(@CrashReportPrefStore Preferences gamePrefs) {
        prefStore = gamePrefs;
    }

    /**
     * Saves a crash report.
     *
     * @param crashReportText text to save
     */
    public void saveCrashReport(String crashReportText) {
        prefStore.putString(CRASH_REPORT_NAME, crashReportText);
        // mark this new crash report as fresh to prompt the user to report it on the
        // next start
        prefStore.putBoolean(FRESH_CRASH_MARKER_NAME, true);
        prefStore.flush();
    }

    /**
     * Loads a crash report.
     *
     * @return a previously saved crash info or empty string if none was saved
     * before
     */
    public String getLastCrashReport() {
        return prefStore.getString(CRASH_REPORT_NAME);
    }

    /**
     * Returns whether there is a fresh crash report, i.e. this is the first start
     * after the previous crash.
     */
    public boolean hasFreshCrashReport() {
        return prefStore.getBoolean(FRESH_CRASH_MARKER_NAME);
    }

    /**
     * Marks the existing crash report as non-fresh.
     */
    public void markCrashReportAsNonFresh() {
        prefStore.putBoolean(FRESH_CRASH_MARKER_NAME, false);
        prefStore.flush();
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import de.sesu8642.feudaltactics.menu.preferences.NagPreferencesDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class for migration data / settings from older versions.
 */
@Singleton
public class DataMigrator {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final NagPreferencesDao nagPreferencesDao;

    /**
     * Constructor.
     */
    @Inject
    public DataMigrator(NagPreferencesDao nagPreferencesDao) {
        this.nagPreferencesDao = nagPreferencesDao;
    }

    /**
     * Migrates the data from a given version to the current one.
     */
    @SuppressWarnings("checkstyle:FallThrough")
    public void migrateData(String previousVersion) {
        final String[] versionComponents = previousVersion.split("\\.");
        // e.g. "1.2"
        final String majorMinorVersionOnly = versionComponents[0] + "." + versionComponents[1];
        switch (majorMinorVersionOnly) {
            // fall-through intentional
            case "1.1":
            case "1.2":
                clearObsoleteAutoSavePreferences();
            case "1.3":
                disableTutorialNag();
            default:
                // nothing to do
        }
        logger.info("Migrated from version {} to current.", majorMinorVersionOnly);
    }

    /**
     * These preferences were used up until version 1.2. Apparently, they cannot be deleted easily so this method at
     * least clears them.
     */
    private void clearObsoleteAutoSavePreferences() {
        final Preferences oldPrefs = Gdx.app.getPreferences("FeudalTactics_autoSavePreferences");
        if (!oldPrefs.get().isEmpty()) {
            oldPrefs.clear();
            oldPrefs.flush();
        }
    }

    /**
     * The tutorial nag was added in version 1.4. If the user upgraded, they are probably not new players.
     */
    private void disableTutorialNag() {
        nagPreferencesDao.setShowTutorialNag(false);
    }

}

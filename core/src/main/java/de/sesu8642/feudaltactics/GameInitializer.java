// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.dagger.VersionProperty;
import de.sesu8642.feudaltactics.events.CenterMapUIEvent;
import de.sesu8642.feudaltactics.events.GameResumedEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.exceptions.InitializationException;
import de.sesu8642.feudaltactics.ingame.AutoSaveRepository;
import de.sesu8642.feudaltactics.menu.changelog.GameVersionDao;
import de.sesu8642.feudaltactics.menu.crashreporting.CrashReportDao;
import de.sesu8642.feudaltactics.menu.preferences.NagPreferencesDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.LogManager;

/**
 * Class for initializing the game in a non-static context that can get injected
 * dependencies.
 */
@Singleton
public class GameInitializer {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final EventBus eventBus;
    private final GameVersionDao gameVersionDao;
    private final NagPreferencesDao nagPreferencesDao;
    private final CrashReportDao crashReportDao;
    private final AutoSaveRepository autoSaveRepository;
    private final ScreenNavigationController screenNavigationController;
    private final DataMigrator dataMigrator;
    private final String gameVersion;

    /**
     * Constructor.
     */
    @Inject
    public GameInitializer(EventBus eventBus, GameVersionDao gameVersionDao, NagPreferencesDao nagPreferencesDao,
                           CrashReportDao crashReportDao,
                           AutoSaveRepository autoSaveRepository,
                           ScreenNavigationController screenNavigationController, DataMigrator dataMigrator,
                           @VersionProperty String gameVersion) {
        this.eventBus = eventBus;
        this.gameVersionDao = gameVersionDao;
        this.nagPreferencesDao = nagPreferencesDao;
        this.crashReportDao = crashReportDao;
        this.autoSaveRepository = autoSaveRepository;
        this.screenNavigationController = screenNavigationController;
        this.dataMigrator = dataMigrator;
        this.gameVersion = gameVersion;
    }

    void initializeGame() {

        try {
            // configure logging
            LogManager logManager = LogManager.getLogManager();
            InputStream stream = GameInitializer.class.getClassLoader().getResourceAsStream("logging.properties");
            logManager.readConfiguration(stream);
        } catch (IOException e) {
            throw new InitializationException("Unable to configure logging", e);
        }

        try {
            // do not close on android back key
            Gdx.input.setCatchKey(Keys.BACK, true);

            eventBus.register(screenNavigationController);

            // show appropriate screen
            if (crashReportDao.hasFreshCrashReport()) {
                // the game crashed on the previous run --> show the crash report screen
                crashReportDao.markCrashReportAsNonFresh();
                eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.CRASH_REPORT_SCREEN_ON_STARTUP));
            } else {
                if (autoSaveRepository.hasFullAutosave()) {
                    // resume running game
                    eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INGAME_SCREEN));
                    eventBus.post(new GameResumedEvent());
                    eventBus.post(new CenterMapUIEvent());
                } else {
                    // fresh start
                    eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.SPLASH_SCREEN));
                }
            }

            String previousVersion = gameVersionDao.getGameVersion();
            if (!Strings.isNullOrEmpty(previousVersion) && !previousVersion.equals(gameVersion)) {
                // first start after update
                logger.info("game was updated from version {} to {}", previousVersion, gameVersion);
                nagPreferencesDao.setShowChangelog(true);
                dataMigrator.migrateData(previousVersion);
            }

            // save current game version
            gameVersionDao.saveGameVersion(gameVersion);
        } catch (Exception e) {
            logger.error("unexpected exception during application start", e);
        }
    }

}

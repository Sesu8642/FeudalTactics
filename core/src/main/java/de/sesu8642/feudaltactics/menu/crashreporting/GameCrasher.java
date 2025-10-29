// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.crashreporting;

import com.badlogic.gdx.Gdx;
import com.google.common.base.Throwables;
import de.sesu8642.feudaltactics.FeudalTactics;
import de.sesu8642.feudaltactics.dagger.VersionProperty;
import de.sesu8642.feudaltactics.ingame.AutoSaveRepository;
import de.sesu8642.feudaltactics.menu.crashreporting.ui.CrashingScreen;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Date;

/**
 * Soft-crashes the game in case of a critical error after collecting important
 * debug information.
 */
@Singleton
public class GameCrasher {

    private final String gameVersion;
    private final CrashReportDao crashReportDao;
    private final AutoSaveRepository autoSaveRepository;

    /**
     * Constructor.
     */
    @Inject
    public GameCrasher(@VersionProperty String gameVersion, CrashReportDao crashReportDao,
                       AutoSaveRepository autoSaveRepository) {
        this.gameVersion = gameVersion;
        this.crashReportDao = crashReportDao;
        this.autoSaveRepository = autoSaveRepository;
    }

    private String buildCrashreportText(String previousLogs, Throwable throwable) {
        try {
            final String template = "Date: %s\n" + "\n" + "Game version: %s\n" + "\n" + "Platform: %s\n" + "\n"
                + "Platform version: %s\n" + "\n" + "Thread: %s\n" + "\n" + "Thrown:\n" + "%s\n" + "\n"
                + "Last full save:\n" + "%s\n" + "\n" + "Incremental saves:\n" + "%s\n" + "\n" + "Previous logs:\n"
                + "%s";
            String lastAutoSave;
            try {
                lastAutoSave = autoSaveRepository.getFullSaveAsString();
            } catch (Exception e) {
                lastAutoSave = Throwables.getStackTraceAsString(e);
            }
            String incrementalSaves;
            try {
                incrementalSaves = autoSaveRepository.getIncrementalSavesAsString();
            } catch (Exception e) {
                incrementalSaves = Throwables.getStackTraceAsString(e);
            }
            return String.format(template, new Date(), gameVersion, Gdx.app.getType(), Gdx.app.getVersion(),
                Thread.currentThread().getName(), Throwables.getStackTraceAsString(throwable), lastAutoSave,
                incrementalSaves, previousLogs);
        } catch (Exception e) {
            return Throwables.getStackTraceAsString(e);
        }

    }

    /**
     * Saves a crash report and then crashes the game. The game should really crash
     * with the proper trace because Google will provide the trace without anyone
     * needing to report it.
     */
    public void crashAfterGeneratingReport(String previousLogs, Throwable throwable) {
        final String crashReportText = buildCrashreportText(previousLogs, throwable);
        crashReportDao.saveCrashReport(crashReportText);
        // The game state may lead to the same crash over and over again. Better delete
        // it.
        autoSaveRepository.deleteAllAutoSaves();
        FeudalTactics.getDaggerComponent().getGameInstance().setScreen(new CrashingScreen(throwable));
    }

}

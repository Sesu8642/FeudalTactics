// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.crashreporting;

/**
 * Value object: crash report information.
 */
public class CrashReportInfo {

    private final String gameVersion;
    private final String platform;
    private final String platformVersion;
    private final String threadName;
    private final String trace;
    private final String previousLogs;

    /**
     * Constructor.
     */
    public CrashReportInfo(String gameVersion, String platform, String platformVersion, String threadName, String trace,
                           String previousLogs) {
        this.gameVersion = gameVersion;
        this.platform = platform;
        this.platformVersion = platformVersion;
        this.threadName = threadName;
        this.trace = trace;
        this.previousLogs = previousLogs;
    }

    public String getGameVersion() {
        return gameVersion;
    }

    public String getPlatform() {
        return platform;
    }

    public String getPlatformVersion() {
        return platformVersion;
    }

    public String getThreadName() {
        return threadName;
    }

    public String getTrace() {
        return trace;
    }

    public String getPreviousLogs() {
        return previousLogs;
    }

}

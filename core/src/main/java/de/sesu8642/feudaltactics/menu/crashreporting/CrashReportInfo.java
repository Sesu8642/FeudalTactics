// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.crashreporting;

import lombok.Getter;

/**
 * Value object: crash report information.
 */
public class CrashReportInfo {

    @Getter
    private final String gameVersion;
    @Getter
    private final String platform;
    @Getter
    private final String platformVersion;
    @Getter
    private final String threadName;
    @Getter
    private final String trace;
    @Getter
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

}

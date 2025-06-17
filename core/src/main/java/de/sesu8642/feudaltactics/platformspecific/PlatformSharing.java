// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.platformspecific;

/**
 * Interface for sharing (text) with other apps. Platform specific implementations reside in their modules.
 */
public interface PlatformSharing {

    /**
     * Shares a text.
     */
    void shareText(String text);

}

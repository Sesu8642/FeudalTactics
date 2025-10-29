// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import lombok.Getter;
import lombok.Setter;

/**
 * Event: Screen transition triggered.
 */
public class ScreenTransitionTriggerEvent {

    @Getter
    @Setter
    private ScreenTransitionTarget transitionTarget;

    public ScreenTransitionTriggerEvent(ScreenTransitionTarget transitionTarget) {
        this.transitionTarget = transitionTarget;
    }

    /**
     * Possible screens that can be transitioned to.
     */
    public enum ScreenTransitionTarget {
        SPLASH_SCREEN, MAIN_MENU_SCREEN, INGAME_SCREEN, EDITOR_SCREEN, ABOUT_SCREEN,
        PREFERENCES_SCREEN, INFORMATION_MENU_SCREEN, INFORMATION_MENU_SCREEN_2, DEPENDENCY_LICENSES_SCREEN,
        CHANGELOG_SCREEN, CRASH_REPORT_SCREEN_IN_MAIN_MENU, CRASH_REPORT_SCREEN_ON_STARTUP
    }

}

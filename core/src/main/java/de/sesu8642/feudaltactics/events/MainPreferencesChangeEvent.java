// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.menu.preferences.MainGamePreferences;
import lombok.Getter;
import lombok.Setter;

/**
 * Event: Main preferences changed.
 */
public class MainPreferencesChangeEvent {

    @Getter
    @Setter
    private MainGamePreferences newPreferences;

    public MainPreferencesChangeEvent(MainGamePreferences newPreferences) {
        this.newPreferences = newPreferences;
    }

}

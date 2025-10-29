// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import lombok.Getter;
import lombok.Setter;

/**
 * Event: Preferences for new games changed.
 */
public class NewGamePreferencesChangedEvent {

    @Getter
    @Setter
    private NewGamePreferences newGamePreferences;

    /**
     * Constructor.
     */
    public NewGamePreferencesChangedEvent(NewGamePreferences newGamePreferences) {
        super();
        this.newGamePreferences = newGamePreferences;
    }
}

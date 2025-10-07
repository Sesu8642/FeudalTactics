// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences;

/**
 * Event: Preferences for new games changed.
 */
public class NewGamePreferencesChangedEvent {

    private NewGamePreferences newGamePreferences;

    /**
     * Constructor.
     */
    public NewGamePreferencesChangedEvent(NewGamePreferences newGamePreferences) {
        super();
        this.newGamePreferences = newGamePreferences;
    }

    public NewGamePreferences getNewGamePreferences() {
        return newGamePreferences;
    }

    public void setNewGamePreferences(NewGamePreferences newGamePreferences) {
        this.newGamePreferences = newGamePreferences;
    }
}

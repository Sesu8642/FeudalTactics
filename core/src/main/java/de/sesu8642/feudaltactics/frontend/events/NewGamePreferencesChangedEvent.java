// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.events;

import de.sesu8642.feudaltactics.frontend.persistence.NewGamePreferences;

/** Event: Parameters for starting a new game changed. */
public class NewGamePreferencesChangedEvent {

	private NewGamePreferences newPreferences;

	public NewGamePreferencesChangedEvent(NewGamePreferences newPreferences) {
		super();
		this.newPreferences = newPreferences;
	}

	public NewGamePreferences getNewPreferences() {
		return newPreferences;
	}

	public void setNewPreferences(NewGamePreferences newPreferences) {
		this.newPreferences = newPreferences;
	}

}

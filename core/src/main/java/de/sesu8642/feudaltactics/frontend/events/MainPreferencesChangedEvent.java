// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.events;

import de.sesu8642.feudaltactics.frontend.persistence.MainGamePreferences;

/** Event: main preferences changed. */
public class MainPreferencesChangedEvent {

	private MainGamePreferences newPreferences;

	public MainPreferencesChangedEvent(MainGamePreferences newPreferences) {
		this.newPreferences = newPreferences;
	}

	public MainGamePreferences getNewPreferences() {
		return newPreferences;
	}

	public void setNewPreferences(MainGamePreferences newPreferences) {
		this.newPreferences = newPreferences;
	}

}

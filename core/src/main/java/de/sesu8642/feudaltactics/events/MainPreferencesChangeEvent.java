// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.menu.preferences.MainGamePreferences;

/** Event: Main preferences changed. */
public class MainPreferencesChangeEvent {

	private MainGamePreferences newPreferences;

	public MainPreferencesChangeEvent(MainGamePreferences newPreferences) {
		this.newPreferences = newPreferences;
	}

	public MainGamePreferences getNewPreferences() {
		return newPreferences;
	}

	public void setNewPreferences(MainGamePreferences newPreferences) {
		this.newPreferences = newPreferences;
	}

}

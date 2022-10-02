// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.persistence;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.eventbus.Subscribe;

import de.sesu8642.feudaltactics.frontend.events.MainPreferencesChangedEvent;
import de.sesu8642.feudaltactics.frontend.events.NewGamePreferencesChangedEvent;

/** Handles events that affect preferences. */
@Singleton
public class EventHandler {

	@Inject
	public EventHandler() {
		// constructor for inject annotation
	}

	/**
	 * Event handler for map parameter change events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleNewGamePreferencesChange(NewGamePreferencesChangedEvent event) {
		PreferencesHelper.saveNewGamePreferences(event.getNewPreferences());
	}

	/**
	 * Event handler for main preferences change events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleMainPreferencesChange(MainPreferencesChangedEvent event) {
		PreferencesHelper.saveMainPreferences(event.getNewPreferences());
	}
}

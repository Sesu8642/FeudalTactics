// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.preferences;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.eventbus.Subscribe;

import de.sesu8642.feudaltactics.events.RegenerateMapEvent;

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
	public void handleMapParamChange(RegenerateMapEvent event) {
		PreferencesHelper.saveNewGamePreferences(new NewGamePreferences(event.getBotIntelligence(),
				event.getMapParams().getLandMass(), event.getMapParams().getDensity()));
	}

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences.ui;

import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.events.MainPreferencesChangeEvent;

import javax.inject.Inject;

/**
 * Handles events for the preferences screen.
 **/
public class PreferencesScreenEventHandler {

    private final PreferencesScreen preferencesScreen;

    /**
     * Constructor.
     *
     * @param preferencesScreen preferences screen
     */
    @Inject
    public PreferencesScreenEventHandler(PreferencesScreen preferencesScreen) {
        this.preferencesScreen = preferencesScreen;
    }

    /**
     * Event handler for preference change events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handlePreferencesChange(MainPreferencesChangeEvent event) {
        preferencesScreen.saveUpdatedPreferences(event.getNewPreferences());
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.editor;

import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.events.RegenerateMapEvent;

import javax.inject.Inject;

/**
 * Handles events (except key/tap inputs).
 **/
public class EventHandler {

    private final EditorController editorController;

    /**
     * Constructor.
     *
     * @param editorController editor controller
     */
    @Inject
    public EventHandler(EditorController editorController) {
        this.editorController = editorController;
    }

    /**
     * Event handler for map re-generation events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleRegenerateMap(RegenerateMapEvent event) {
        editorController.generateEmptyGameState();
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.editor;

import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.shared.events.GameStatePastedEvent;
import de.sesu8642.feudaltactics.shared.events.RegenerateMapEvent;

import javax.inject.Inject;

/**
 * Handles events (except key/tap inputs).
 **/
public class EditorControllerEventHandler {

    private final EditorController editorController;

    /**
     * Constructor.
     *
     * @param editorController editor controller
     */
    @Inject
    public EditorControllerEventHandler(EditorController editorController) {
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

    /**
     * Event handler for gameState pasted events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleGameStatePasted(GameStatePastedEvent event) {
        editorController.loadGameState(event.getGameState());
    }

}

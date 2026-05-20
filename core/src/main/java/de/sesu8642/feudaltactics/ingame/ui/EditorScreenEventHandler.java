// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.shared.events.GameStateChangeEvent;
import de.sesu8642.feudaltactics.shared.events.input.EscInputEvent;

import javax.inject.Inject;

/**
 * Handles events for the ingame screen.
 **/
public class EditorScreenEventHandler {

    private final EditorScreen editorScreen;

    @Inject
    public EditorScreenEventHandler(EditorScreen editorScreen) {
        this.editorScreen = editorScreen;
    }

    /**
     * Event handler for ESC key events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleEscInput(EscInputEvent event) {
        //editorScreen.togglePause();
    }

    /**
     * Event handler for gameState changes.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleGameStateChange(GameStateChangeEvent event) {
        editorScreen.handleGameStateChange(event.getGameState());
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.editor;

import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.events.EditorHandContentUpdatedEvent;
import de.sesu8642.feudaltactics.events.InitializeScenarioEvent;
import de.sesu8642.feudaltactics.events.TapInputEvent;
import de.sesu8642.feudaltactics.lib.gamestate.HexMapHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Handles inputs in the editor.
 **/
@Singleton
public class EditorInputHandler {

    private final EditorController editorController;

    /**
     * Constructor.
     *
     * @param editorController editor controller
     */
    @Inject
    public EditorInputHandler(EditorController editorController) {
        this.editorController = editorController;
    }

    /**
     * Event handler for tap input events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleTapInput(TapInputEvent event) {
        Vector2 hexCoords = HexMapHelper.worldCoordsToHexCoords(event.getWorldCoords());
        editorController.placeHeldObject(hexCoords);
    }

    @Subscribe
    public void handleInitializeScenario(InitializeScenarioEvent event) {
        editorController.initializeScenario(event.getBotIntelligence(), event.getScenarioMap());
    }

    @Subscribe
    public void handleHandContentUpdated(EditorHandContentUpdatedEvent event) {
        editorController.updateHandContent(event.getHeldTileContent(), event.getHeldTilePlayerIndex());
    }

}

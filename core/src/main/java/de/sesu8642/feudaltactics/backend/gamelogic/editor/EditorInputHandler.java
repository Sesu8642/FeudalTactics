// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.backend.gamelogic.editor;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.Subscribe;

import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.HexMapHelper;
import de.sesu8642.feudaltactics.events.TapInputEvent;

/** Handles inputs in the editor. **/
@Singleton
public class EditorInputHandler {

	private EditorController editorController;

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
		editorController.createTile(hexCoords);
	}

}

// SPDX-License-Identifier: GPL-3.0-or-later

package com.sesu8642.feudaltactics.gamelogic.editor;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.Subscribe;
import com.sesu8642.feudaltactics.events.input.TapInputEvent;
import com.sesu8642.feudaltactics.gamelogic.gamestate.HexMapHelper;

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

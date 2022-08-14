// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.gamelogic.editor;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.events.moves.RegenerateMapUiEvent;

/** Handles events (except key/tap inputs). **/
public class EventHandler {

	private EditorController editorController;

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
	public void handleRegenerateMap(RegenerateMapUiEvent event) {
		editorController.generateEmptyGameState();
	}

}

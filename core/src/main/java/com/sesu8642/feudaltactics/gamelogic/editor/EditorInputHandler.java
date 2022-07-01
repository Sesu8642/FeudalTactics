package com.sesu8642.feudaltactics.gamelogic.editor;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.Subscribe;
import com.sesu8642.feudaltactics.events.input.TapInputEvent;
import com.sesu8642.feudaltactics.gamelogic.gamestate.HexMap;

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
		HexMap map = editorController.getGameState().getMap();
		Vector2 hexCoords = map.worldCoordsToHexCoords(event.getWorldCoords());
		editorController.createTile(hexCoords);
	}

}

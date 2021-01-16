package com.sesu8642.feudaltactics.engine;

import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.gamestate.HexMap;

public class EditorInputHandler implements AcceptCommonInput {

	private EditorController editorController;

	public EditorInputHandler(EditorController editorController) {
		this.editorController = editorController;
	}
	
	public void inputTap(Vector2 worldCoords) {
		HexMap map = editorController.getGameState().getMap();
		Vector2 hexCoords = map.worldCoordsToHexCoords(worldCoords);
		editorController.createTile(hexCoords);
	}

	@Override
	public void inputEsc() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void inputBack() {
		// TODO Auto-generated method stub
		
	}

}

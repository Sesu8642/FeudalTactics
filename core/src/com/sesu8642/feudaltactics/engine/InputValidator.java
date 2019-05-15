package com.sesu8642.feudaltactics.engine;

import com.badlogic.gdx.math.Vector2;

public class InputValidator {

	private GameController gameController;

	public InputValidator(GameController gameController) {
		this.gameController = gameController;
	}

	public void tap(Vector2 worldCoords) {
		gameController.printTileInfo(worldCoords);
		gameController.updateInfoText(worldCoords);
	}
}

package com.sesu8642.feudaltactics.engine;

import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.HexMap;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.Player;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Kingdom;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;

public class InputValidator {

	private GameController gameController;
	private GameState gameState;

	public InputValidator(GameController gameController) {
		this.gameController = gameController;
		this.gameState = gameController.getGameState();
	}

	public void tap(Vector2 worldCoords) {
		// print info
		HexMap map = gameState.getMap();
		Vector2 hexCoords = map.worldCoordsToHexCoords(worldCoords);
		gameController.printTileInfo(hexCoords);
		Player player = gameState.getActivePlayer();
		HexTile tile = map.getTiles().get(hexCoords);
		Kingdom kingdom = tile.getKingdom();
		// determine action
		if (gameState.getHeldObject() == null && kingdom != null && gameState.getActiveKingdom() != kingdom) {
			// activate kingdom
			gameController.activateKingdom(kingdom);
		} else if (gameState.getHeldObject() == null && tile.getContent() != null) {
			// pick up object
			if (checkPickupObject(player, tile)) {
				gameController.pickupObject(tile);
			}
		}
	}

	public boolean checkActivateKingdom(Player player, HexTile tile) {
		if (player != gameState.getActivePlayer()) {
			return false;
		}
		if (tile == null) {
			return false;
		}
		return true;
	}
	
	public boolean checkPickupObject(Player player, HexTile tile) {
		if (player != gameState.getActivePlayer()) {
			return false;
		}
		if (tile == null) {
			return false;
		}
		if (gameState.getHeldObject() != null) {
			return false;
		}
		if (tile.getContent() == null) {
			return false;
		}
		if (tile.getPlayer() != player) {
			return false;
		}
		if (!tile.getContent().getClass().isAssignableFrom(Unit.class)) {
			return false;
		}
		return true;
	}
}

package com.sesu8642.feudaltactics.engine;

import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.HexMap;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamestate.Player;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Castle;
import com.sesu8642.feudaltactics.gamestate.mapobjects.MapObject;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Tree;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit.UnitTypes;

public class InputValidator {

	// TODO: when validating multiplayer inputs, make sure that it's the player's
	// turn before calling check*

	private GameController gameController;

	public InputValidator(GameController gameController) {
		this.gameController = gameController;
	}

	public void tap(Vector2 worldCoords) {
		// print info
		HexMap map = gameController.getGameState().getMap();
		Vector2 hexCoords = map.worldCoordsToHexCoords(worldCoords);
		gameController.printTileInfo(hexCoords);
		if (!isActivePlayerLocalHuman()) {
			// don't accept inputs if its not the human player's turn
			return;
		}
		Player player = gameController.getGameState().getActivePlayer();
		HexTile tile = map.getTiles().get(hexCoords);
		// determine action
		if (checkChangeActiveKingdom(player, tile)) {
			// activate kingdom
			gameController.activateKingdom(tile.getKingdom());
		}
		if (gameController.getGameState().getHeldObject() == null) {
			// pick up object
			if (checkPickupObject(player, tile)) {
				gameController.pickupObject(tile);
			}
		} else {
			// place object
			if (checkPlaceObject(player, tile, hexCoords)) {
				gameController.placeObject(tile);
			}
		}
	}

	public void inputEndTurn() {
		if (!isActivePlayerLocalHuman()) {
			// don't accept inputs if its not the human player's turn
			return;
		}
		if (checkEndTurn()) {
			gameController.endTurn();
		}
	}

	public void inputBuyPeasant() {
		if (!isActivePlayerLocalHuman()) {
			// don't accept inputs if its not the human player's turn
			return;
		}
		if (checkBuyObject(Unit.COST)) {
			gameController.buyPeasant();
		}
	}

	public void inputBuyCastle() {
		if (!isActivePlayerLocalHuman()) {
			// don't accept inputs if its not the human player's turn
			return;
		}
		if (checkBuyObject(Castle.COST)) {
			gameController.buyCastle();
		}
	}

	public void inputUndo() {
		if (!isActivePlayerLocalHuman()) {
			// don't accept inputs if its not the human player's turn
			return;
		}
		if (checkUndoAction()) {
			gameController.undoLastAction();
		}
	}

	public boolean isWater(HexTile tile) {
		return (tile == null);
	}

	public boolean isActivePlayerLocalHuman() {
		return (gameController.getGameState().getActivePlayer().getType() == Player.Type.LOCAL_PLAYER);
	}

	public boolean checkChangeActiveKingdom(Player player, HexTile tile) {
		if (isWater(tile)) {
			return false;
		}
		if (player != tile.getPlayer()) {
			return false;
		}
		if (gameController.getGameState().getHeldObject() != null) {
			return false;
		}
		if (tile.getKingdom() == null) {
			return false;
		}
		if (gameController.getGameState().getActiveKingdom() == tile.getKingdom()) {
			return false;
		}
		return true;
	}

	public boolean checkPickupObject(Player player, HexTile tile) {
		if (isWater(tile)) {
			return false;
		}
		if (player != tile.getPlayer()) {
			return false;
		}
		if (gameController.getGameState().getHeldObject() != null) {
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
		if (!((Unit) tile.getContent()).isCanAct()) {
			return false;
		}
		return true;
	}

	public boolean checkPlaceObject(Player player, HexTile tile, Vector2 hexCoords) {
		if (isWater(tile)) {
			return false;
		}
		if (gameController.getGameState().getHeldObject() == null) {
			return false;
		}
		if (tile.getPlayer() == player) {
			// own tile
			if (gameController.getGameState().getHeldObject().getKingdom() != tile.getKingdom() || tile.getKingdom() == null) {
				// own tile but not the one the kingdom the unit belongs to
				return false;
			}
			if (tile.getContent() != null
					&& !(tile.getContent().getClass().isAssignableFrom(Tree.class)
							&& gameController.getGameState().getHeldObject().getClass().isAssignableFrom(Unit.class))
					&& !(tile.getContent().getClass().isAssignableFrom(Unit.class)
							&& gameController.getGameState().getHeldObject().getClass().isAssignableFrom(Unit.class)
							&& (((Unit) tile.getContent()).getUnitType() == UnitTypes.PEASANT
									|| ((Unit) gameController.getGameState().getHeldObject()).getUnitType() == UnitTypes.PEASANT))) {
				// object on object except unit on tree and combining units
				return false;
			}
		} else if (tile.getPlayer() != player) {
			// tile owned by another player
			if (!gameController.getGameState().getHeldObject().getClass().isAssignableFrom(Unit.class)) {
				// not a unit
				return false;
			}
			boolean isNextoToOwnKingdom = false;
			boolean isProtected = false;
			for (HexTile neighborTile : gameController.getGameState().getMap().getNeighborTiles(hexCoords)) {
				if (isWater(neighborTile)) {
					// skip water
					continue;
				}
				// check if tile is next to own kingdom
				if (neighborTile.getKingdom() == gameController.getGameState().getHeldObject().getKingdom()) {
					isNextoToOwnKingdom = true;
				}
				MapObject neighborContent = neighborTile.getContent();
				// check if there is no stronger object next to it protecting it
				if (neighborTile.getKingdom() == tile.getKingdom() && neighborContent != null
						&& neighborContent.getStrength() >= gameController.getGameState().getHeldObject().getStrength()) {
					isProtected = true;
				}
			}
			if (!isNextoToOwnKingdom) {
				// not next to the unit's kingdom
				return false;
			}
			if (isProtected) {
				// protected
				return false;
			}
			if (tile.getContent() != null
					&& tile.getContent().getStrength() >= gameController.getGameState().getHeldObject().getStrength()) {
				// stronger object on the tile
				return false;
			}
		}
		return true;
	}

	public boolean checkEndTurn() {
		return (gameController.getGameState().getHeldObject() == null);
	}

	public boolean checkBuyObject(int cost) {
		Kingdom activeKingdom = gameController.getGameState().getActiveKingdom();
		if (activeKingdom == null) {
			return false;
		}
		if (activeKingdom.getSavings() < cost) {
			return false;
		}
		if (gameController.getGameState().getHeldObject() != null) {
			return false;
		}
		return true;
	}

	public boolean checkUndoAction() {
		return (gameController.getUndoStates().size() > 0);
	}

}

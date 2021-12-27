package com.sesu8642.feudaltactics.input;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.sesu8642.feudaltactics.GameController;
import com.sesu8642.feudaltactics.gamestate.HexMap;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.Player;
import com.sesu8642.feudaltactics.gamestate.Player.Type;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Tree;
import com.sesu8642.feudaltactics.ui.screens.IngameScreen;

import dagger.Lazy;

@Singleton
public class LocalInputHandler implements AcceptCommonInput {

	// TODO: when validating multiplayer inputs, make sure that it's the player's
	// turn before calling check*

	public enum TapAction {
		NONE, PICK_UP, PLACE_OWN, COMBINE_UNITS, CONQUER
	}

	private GameController gameController;
	private Lazy<IngameScreen> ingameScreenLazy;

	@Inject
	public LocalInputHandler(GameController gameController, Lazy<IngameScreen> ingameScreenLazy) {
		this.gameController = gameController;
		// using lazy because of dependency cycle
		this.ingameScreenLazy = ingameScreenLazy;
	}

	@Override
	public void inputEsc() {
		ingameScreenLazy.get().togglePause();
	}

	@Override
	public void inputBack() {
		if (InputValidationHelper.checkUndoAction()) {
			gameController.undoLastAction();
		}
	}

	@Override
	public void inputTap(Vector2 worldCoords) {
		if (!isActivePlayerLocalHuman()) {
			// don't accept inputs if its not the human player's turn
			return;
		}
		HexMap map = gameController.getGameState().getMap();
		Vector2 hexCoords = map.worldCoordsToHexCoords(worldCoords);
		Player player = gameController.getGameState().getActivePlayer();
		HexTile tile = map.getTiles().get(hexCoords);
		// print info
		gameController.printTileInfo(hexCoords);
		if (InputValidationHelper.checkChangeActiveKingdom(gameController.getGameState(), player, tile)) {
			// activate kingdom
			gameController.activateKingdom(tile.getKingdom());
		}
		TapAction action = determineTapAction(player, tile);
		switch (action) {
		case PICK_UP:
			if (InputValidationHelper.checkPickupObject(gameController.getGameState(), player, tile)) {
				gameController.pickupObject(tile);
			}
			break;
		case PLACE_OWN:
			if (InputValidationHelper.checkPlaceOwn(gameController.getGameState(), player, tile)) {
				gameController.placeOwn(tile);
			}
			break;
		case COMBINE_UNITS:
			if (InputValidationHelper.checkCombineUnits(gameController.getGameState(), player, tile)) {
				gameController.combineUnits(tile);
			}
			break;
		case CONQUER:
			if (InputValidationHelper.checkConquer(gameController.getGameState(), player, tile)) {
				gameController.conquer(tile);
			}
			break;
		case NONE:
			break;
		}
	}

	public TapAction determineTapAction(Player player, HexTile tile) {
		// determine action
		if (tile == null) {
			return TapAction.NONE;
		}
		if (gameController.getGameState().getHeldObject() == null) {
			// pick up object
			return TapAction.PICK_UP;
		} else {
			// place object
			if (tile.getPlayer() != null && tile.getPlayer() == player) {
				if (tile.getContent() == null
						|| ClassReflection.isAssignableFrom(Tree.class, tile.getContent().getClass())) {
					return TapAction.PLACE_OWN;
				} else {
					return TapAction.COMBINE_UNITS;
				}
			} else {
				return TapAction.CONQUER;
			}
		}
	}

	private boolean isActivePlayerLocalHuman() {
		return (gameController.getGameState().getActivePlayer().getType() == Type.LOCAL_PLAYER);
	}

}

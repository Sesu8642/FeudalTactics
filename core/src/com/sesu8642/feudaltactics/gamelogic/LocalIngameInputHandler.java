package com.sesu8642.feudaltactics.gamelogic;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.google.common.eventbus.Subscribe;
import com.sesu8642.feudaltactics.events.input.BackInputEvent;
import com.sesu8642.feudaltactics.events.input.TapInputEvent;
import com.sesu8642.feudaltactics.events.moves.GameStartEvent;
import com.sesu8642.feudaltactics.gamelogic.gamestate.HexMap;
import com.sesu8642.feudaltactics.gamelogic.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Player;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Player.Type;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Tree;
import com.sesu8642.feudaltactics.input.InputValidationHelper;

/** Handles inputs of a local player in-game. **/
@Singleton
public class LocalIngameInputHandler {

	private enum TapAction {
		NONE, PICK_UP, PLACE_OWN, COMBINE_UNITS, CONQUER
	}

	private GameController gameController;

	/**
	 * Constructor.
	 * 
	 * @param gameController game controller
	 */
	@Inject
	public LocalIngameInputHandler(GameController gameController) {
		this.gameController = gameController;
	}

	/**
	 * Event handler for back button input events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleBackInput(BackInputEvent event) {
		if (InputValidationHelper.checkUndoAction()) {
			gameController.undoLastAction();
		}
	}

	/**
	 * Event handler for tap input events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleTapInput(TapInputEvent event) {
		if (!isActivePlayerLocalHuman()) {
			// don't accept inputs if its not the human player's turn
			return;
		}
		HexMap map = gameController.getGameState().getMap();
		Vector2 hexCoords = map.worldCoordsToHexCoords(event.getWorldCoords());
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
		default:
			throw new IllegalStateException("Unknown action " + action);
		}
	}

	/**
	 * Event handler for game start events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleGameStart(GameStartEvent event) {
		gameController.startGame();
	}

	private TapAction determineTapAction(Player player, HexTile tile) {
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

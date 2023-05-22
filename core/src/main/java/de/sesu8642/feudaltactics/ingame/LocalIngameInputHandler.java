// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import java.util.Map;
import java.util.Optional;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.google.common.eventbus.Subscribe;

import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.events.TapInputEvent;
import de.sesu8642.feudaltactics.events.input.BackInputEvent;
import de.sesu8642.feudaltactics.events.moves.BuyAndPlaceCastleEvent;
import de.sesu8642.feudaltactics.events.moves.BuyCastleEvent;
import de.sesu8642.feudaltactics.events.moves.BuyPeasantEvent;
import de.sesu8642.feudaltactics.events.moves.EndTurnEvent;
import de.sesu8642.feudaltactics.events.moves.GameStartEvent;
import de.sesu8642.feudaltactics.events.moves.UndoMoveEvent;
import de.sesu8642.feudaltactics.lib.gamestate.Blocking;
import de.sesu8642.feudaltactics.lib.gamestate.Castle;
import de.sesu8642.feudaltactics.lib.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.lib.gamestate.HexMapHelper;
import de.sesu8642.feudaltactics.lib.gamestate.HexTile;
import de.sesu8642.feudaltactics.lib.gamestate.InputValidationHelper;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.gamestate.Unit;
import de.sesu8642.feudaltactics.lib.ingame.GameController;

/** Handles inputs of a local player in-game. **/
@Singleton
public class LocalIngameInputHandler {

	private enum TapAction {
		NONE, PICK_UP, PLACE_OWN, COMBINE_UNITS, CONQUER, BUY_AND_PLACE_PEASANT
	}

	private GameController gameController;
	private AutoSaveRepository autoSaveRepo;

	/**
	 * Constructor.
	 * 
	 * @param gameController game controller
	 */
	@Inject
	public LocalIngameInputHandler(GameController gameController, AutoSaveRepository autoSaveRepo) {
		this.gameController = gameController;
		this.autoSaveRepo = autoSaveRepo;
	}

	/**
	 * Event handler for back button input events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleBackInput(BackInputEvent event) {
		Optional<Player> playerOptional = GameStateHelper.determineActingLocalPlayer(gameController.getGameState());
		if (!playerOptional.isPresent()) {
			return;
		}
		if (InputValidationHelper.checkUndoAction(gameController.getGameState(), playerOptional.get(),
				autoSaveRepo.getNoOfAutoSaves())) {
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
		Vector2 hexCoords = HexMapHelper.worldCoordsToHexCoords(event.getWorldCoords());
		Optional<Player> playerOptional = GameStateHelper.determineActingLocalPlayer(gameController.getGameState());
		if (!playerOptional.isPresent()) {
			return;
		}
		Player player = playerOptional.get();
		Map<Vector2, HexTile> map = gameController.getGameState().getMap();
		HexTile tile = map.get(hexCoords);
		// print info
		gameController.printTileInfo(hexCoords);
		if (InputValidationHelper.checkChangeActiveKingdom(gameController.getGameState(), player, tile)) {
			// activate kingdom
			gameController.activateKingdom(tile.getKingdom());
		}
		TapAction action = determineTapAction(player, tile, event.getCount());
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
		case BUY_AND_PLACE_PEASANT:
			if (InputValidationHelper.checkBuyAndPlaceUnitInstantly(gameController.getGameState(), playerOptional.get(),
					tile)) {
				gameController.buyPeasant();
				gameController.placeOwn(tile);
			}
		case NONE:
			break;
		default:
			throw new IllegalStateException("Unknown action " + action);
		}
	}

	/**
	 * Event handler for map re-generation events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleRegenerateMap(RegenerateMapEvent event) {
		gameController.generateGameState(event.getBotIntelligence(), event.getMapParams());
	}

	/**
	 * Event handler for undo move events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleUndoMove(UndoMoveEvent event) {
		Optional<Player> playerOptional = GameStateHelper.determineActingLocalPlayer(gameController.getGameState());
		if (!playerOptional.isPresent()) {
			return;
		}
		if (InputValidationHelper.checkUndoAction(gameController.getGameState(), playerOptional.get(),
				autoSaveRepo.getNoOfAutoSaves())) {
			gameController.undoLastAction();
		}
	}

	/**
	 * Event handler for buy peasant events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleBuyPeasant(BuyPeasantEvent event) {
		Optional<Player> playerOptional = GameStateHelper.determineActingLocalPlayer(gameController.getGameState());
		if (!playerOptional.isPresent()) {
			return;
		}
		if (InputValidationHelper.checkBuyObject(gameController.getGameState(), playerOptional.get(), Unit.class)) {
			gameController.buyPeasant();
		}
	}

	/**
	 * Event handler for buy castle events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleBuyCastle(BuyCastleEvent event) {
		Optional<Player> playerOptional = GameStateHelper.determineActingLocalPlayer(gameController.getGameState());
		if (!playerOptional.isPresent()) {
			return;
		}
		if (InputValidationHelper.checkBuyObject(gameController.getGameState(), playerOptional.get(), Castle.class)) {
			gameController.buyCastle();
		}
	}

	/**
	 * Event handler for buy and place castle events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleBuyAndPlaceCastle(BuyAndPlaceCastleEvent event) {
		Vector2 hexCoords = HexMapHelper.worldCoordsToHexCoords(event.getWorldCoords());
		Optional<Player> playerOptional = GameStateHelper.determineActingLocalPlayer(gameController.getGameState());
		if (!playerOptional.isPresent()) {
			return;
		}
		Map<Vector2, HexTile> map = gameController.getGameState().getMap();
		HexTile tile = map.get(hexCoords);
		if (InputValidationHelper.checkBuyAndPlaceCastleInstantly(gameController.getGameState(), playerOptional.get(),
				tile)) {
			gameController.buyCastle();
			gameController.placeOwn(tile);
		}
	}

	/**
	 * Event handler for confirmed end turn events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleEndTurn(EndTurnEvent event) {
		Optional<Player> playerOptional = GameStateHelper.determineActingLocalPlayer(gameController.getGameState());
		if (!playerOptional.isPresent()) {
			return;
		}
		if (InputValidationHelper.checkEndTurn(gameController.getGameState(), playerOptional.get())) {
			gameController.endTurn();
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

	private TapAction determineTapAction(Player player, HexTile tile, int count) {
		if (tile == null) {
			return TapAction.NONE;
		}
		if (gameController.getGameState().getHeldObject() == null) {
			if (count == 2) {
				return TapAction.BUY_AND_PLACE_PEASANT;
			}
			return TapAction.PICK_UP;
		} else {
			if (tile.getPlayer() != null && tile.getPlayer() == player) {
				if (tile.getContent() == null
						|| ClassReflection.isAssignableFrom(Blocking.class, tile.getContent().getClass())) {
					return TapAction.PLACE_OWN;
				} else {
					return TapAction.COMBINE_UNITS;
				}
			} else {
				return TapAction.CONQUER;
			}
		}
	}

}

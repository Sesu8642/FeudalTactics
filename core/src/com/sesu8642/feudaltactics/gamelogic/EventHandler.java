package com.sesu8642.feudaltactics.gamelogic;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;
import com.sesu8642.feudaltactics.events.GameResumedEvent;
import com.sesu8642.feudaltactics.events.moves.BuyCastleEvent;
import com.sesu8642.feudaltactics.events.moves.BuyPeasantEvent;
import com.sesu8642.feudaltactics.events.moves.EndTurnEvent;
import com.sesu8642.feudaltactics.events.moves.RegenerateMapUiEvent;
import com.sesu8642.feudaltactics.events.moves.UndoMoveEvent;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Castle;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Unit;
import com.sesu8642.feudaltactics.input.InputValidationHelper;

/** Handles events (except key/tap inputs). **/
public class EventHandler {

	private GameController gameController;

	/**
	 * Constructor.
	 * 
	 * @param gameController game controller
	 */
	@Inject
	public EventHandler(GameController gameController) {
		this.gameController = gameController;
	}

	/**
	 * Event handler for map re-generation events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleRegenerateMap(RegenerateMapUiEvent event) {
		gameController.generateGameState(event.getBotIntelligence(), event.getMapParams());
	}

	/**
	 * Event handler for undo move events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleUndoMove(UndoMoveEvent event) {
		if (InputValidationHelper.checkUndoAction()) {
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
		if (InputValidationHelper.checkBuyObject(gameController.getGameState(), Unit.COST)) {
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
		if (InputValidationHelper.checkBuyObject(gameController.getGameState(), Castle.COST)) {
			gameController.buyCastle();
		}
	}

	/**
	 * Event handler for confirmed end turn events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleEndTurn(EndTurnEvent event) {
		if (InputValidationHelper.checkEndTurn(gameController.getGameState())) {
			gameController.endTurn();
		}
	}

	/**
	 * Event handler for game resumed events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleGameResumed(GameResumedEvent event) {
		gameController.loadLatestAutosave();
	}

}

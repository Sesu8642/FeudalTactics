// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.gamelogic.ingame;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.events.GameResumedEvent;
import de.sesu8642.feudaltactics.events.moves.BuyCastleEvent;
import de.sesu8642.feudaltactics.events.moves.BuyPeasantEvent;
import de.sesu8642.feudaltactics.events.moves.EndTurnEvent;
import de.sesu8642.feudaltactics.events.moves.RegenerateMapUiEvent;
import de.sesu8642.feudaltactics.events.moves.UndoMoveEvent;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Castle;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Unit;
import de.sesu8642.feudaltactics.input.InputValidationHelper;

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

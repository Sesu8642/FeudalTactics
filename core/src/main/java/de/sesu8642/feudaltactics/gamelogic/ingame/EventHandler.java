// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.gamelogic.ingame;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import de.sesu8642.feudaltactics.events.BotTurnFinishedEvent;
import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.events.GameResumedEvent;

/** Handles events (except player inputs). **/
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
	 * Event handler for game exited events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleGameExited(GameExitedEvent event) {
		gameController.cancelBotTurn();
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

	/**
	 * Event handler for finished bot turn events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleBotTurnFinished(BotTurnFinishedEvent event) {
		gameController.setGameState(event.getGameState());
		gameController.endTurn();
	}

}

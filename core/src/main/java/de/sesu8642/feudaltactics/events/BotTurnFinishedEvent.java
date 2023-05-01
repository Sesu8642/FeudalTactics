// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.lib.gamestate.GameState;

/** Event: Bot player finished its turn. */
public class BotTurnFinishedEvent {

	private GameState gameState;

	/**
	 * Constructor.
	 * 
	 * @param gameState new game state
	 */
	public BotTurnFinishedEvent(GameState gameState) {
		this.gameState = gameState;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
}

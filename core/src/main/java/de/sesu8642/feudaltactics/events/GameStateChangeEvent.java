// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.gamelogic.gamestate.GameState;

/** Event: Game state changed. */
public class GameStateChangeEvent {

	private GameState gameState;

	private boolean winnerChanged = false;

	private boolean mapDimensionsChanged = false;

	/**
	 * Constructor.
	 * 
	 * @param gameState new game state
	 */
	public GameStateChangeEvent(GameState gameState) {
		this.gameState = gameState;
	}

	/**
	 * Constructor.
	 * 
	 * @param gameState            new game state
	 * @param winnerChanged        whether the game's winner has changed
	 * @param mapDimensionsChanged whether the map's dimensions have changed
	 */
	public GameStateChangeEvent(GameState gameState, boolean winnerChanged, boolean mapDimensionsChanged) {
		this.gameState = gameState;
		this.winnerChanged = winnerChanged;
		this.mapDimensionsChanged = mapDimensionsChanged;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public boolean isWinnerChanged() {
		return winnerChanged;
	}

	public void setWinnerChanged(boolean winnerChanged) {
		this.winnerChanged = winnerChanged;
	}

	public boolean isMapDimensionsChanged() {
		return mapDimensionsChanged;
	}

	public void setMapDimensionsChanged(boolean mapDimensionsChanged) {
		this.mapDimensionsChanged = mapDimensionsChanged;
	}

}

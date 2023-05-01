// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.lib.gamestate.GameState;

/** Event: Game state changed. */
public class GameStateChangeEvent {

	private GameState gameState;

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
	 * @param mapDimensionsChanged whether the map's dimensions have changed
	 */
	public GameStateChangeEvent(GameState gameState, boolean mapDimensionsChanged) {
		this.gameState = gameState;
		this.mapDimensionsChanged = mapDimensionsChanged;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public boolean isMapDimensionsChanged() {
		return mapDimensionsChanged;
	}

	public void setMapDimensionsChanged(boolean mapDimensionsChanged) {
		this.mapDimensionsChanged = mapDimensionsChanged;
	}

}

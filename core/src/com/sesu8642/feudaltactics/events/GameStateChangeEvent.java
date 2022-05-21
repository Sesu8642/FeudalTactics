package com.sesu8642.feudaltactics.events;

import com.sesu8642.feudaltactics.gamelogic.gamestate.GameState;

/** Event: Game state changed. */
public class GameStateChangeEvent {

	private GameState gameState;

	private boolean winnerChanged = false;

	public GameStateChangeEvent(GameState gameState) {
		this.gameState = gameState;
	}

	public GameStateChangeEvent(GameState gameState, boolean winnerChanged) {
		this.gameState = gameState;
		this.winnerChanged = winnerChanged;
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

}

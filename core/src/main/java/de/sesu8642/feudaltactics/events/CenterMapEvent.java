// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.lib.gamestate.GameState;

/**
 * Event: Map needs to be centered in the UI.
 */
public class CenterMapEvent {

	private GameState gameState;
	private long marginLeftPx;
	private long marginBottomPx;
	private long marginRightPx;
	private long marginTopPx;

	/**
	 * Constructor.
	 * 
	 * @param gameState game state to be centered
	 */
	public CenterMapEvent(GameState gameState, long marginBottomPx, long marginLeftPx, long marginTopPx,
			long marginRightPx) {
		this.gameState = gameState;
		this.marginLeftPx = marginLeftPx;
		this.marginBottomPx = marginBottomPx;
		this.marginRightPx = marginRightPx;
		this.marginTopPx = marginTopPx;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public long getMarginLeftPx() {
		return marginLeftPx;
	}

	public void setMarginLeftPx(long marginLeftPx) {
		this.marginLeftPx = marginLeftPx;
	}

	public long getMarginBottomPx() {
		return marginBottomPx;
	}

	public void setMarginBottomPx(long marginBottomPx) {
		this.marginBottomPx = marginBottomPx;
	}

	public long getMarginRightPx() {
		return marginRightPx;
	}

	public void setMarginRightPx(long marginRightPx) {
		this.marginRightPx = marginRightPx;
	}

	public long getMarginTopPx() {
		return marginTopPx;
	}

	public void setMarginTopPx(long marginTopPx) {
		this.marginTopPx = marginTopPx;
	}

}

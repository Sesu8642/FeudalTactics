// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events.moves;

import com.badlogic.gdx.math.Vector2;

/** Event: User buys a castle and places it immediately. */
public class BuyAndPlaceCastleEvent {

	private Vector2 worldCoords;

	public BuyAndPlaceCastleEvent(Vector2 worldCoords) {
		this.worldCoords = worldCoords;
	}

	public Vector2 getWorldCoords() {
		return worldCoords;
	}

	public void setWorldCoords(Vector2 worldCoords) {
		this.worldCoords = worldCoords;
	}

}

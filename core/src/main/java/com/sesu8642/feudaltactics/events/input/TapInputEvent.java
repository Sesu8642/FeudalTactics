// SPDX-License-Identifier: GPL-3.0-or-later

package com.sesu8642.feudaltactics.events.input;

import com.badlogic.gdx.math.Vector2;

/** Event: User tapped or clicked. */
public class TapInputEvent {

	private Vector2 worldCoords;

	public TapInputEvent(Vector2 worldCoords) {
		this.worldCoords = worldCoords;
	}

	public Vector2 getWorldCoords() {
		return worldCoords;
	}

	public void setWorldCoords(Vector2 worldCoords) {
		this.worldCoords = worldCoords;
	}

}

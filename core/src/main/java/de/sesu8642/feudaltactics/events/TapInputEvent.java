// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import com.badlogic.gdx.math.Vector2;

/** Event: User tapped or clicked. */
public class TapInputEvent {

	private Vector2 worldCoords;
	private int count;

	public TapInputEvent(Vector2 worldCoords, int count) {
		this.worldCoords = worldCoords;
		this.count = count;
	}

	public Vector2 getWorldCoords() {
		return worldCoords;
	}

	public void setWorldCoords(Vector2 worldCoords) {
		this.worldCoords = worldCoords;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}

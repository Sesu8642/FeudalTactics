// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.ingame.botai;

/** Possible speeds for the preview. */
public enum Speed {
	HALF(600), NORMAL(300), TIMES_TWO(150);

	/**
	 * Time to wait after activating each kingdom as well as after doing the moves
	 * for each one. For the player to see what is happening.
	 */
	public final int tickDelayMs;

	private Speed(int tickDelayMs) {
		this.tickDelayMs = tickDelayMs;
	}
}
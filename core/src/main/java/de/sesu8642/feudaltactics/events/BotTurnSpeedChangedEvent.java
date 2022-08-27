// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.gamelogic.ingame.BotAi.Speed;

/** Event: Speed of bot turns is changed. */
public class BotTurnSpeedChangedEvent {

	private Speed speed;

	/**
	 * Constructor.
	 * 
	 * @param speed new speed.
	 */
	public BotTurnSpeedChangedEvent(Speed speed) {
		this.speed = speed;
	}

	public Speed getSpeed() {
		return speed;
	}

	public void setSpeed(Speed speed) {
		this.speed = speed;
	}

}

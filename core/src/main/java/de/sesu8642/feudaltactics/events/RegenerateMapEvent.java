// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.ingame.MapParameters;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

/**
 * Event: Map needs to be re-generated because the Parameters of the generated
 * map changed or the player wants to retry or starts a new game.
 */
public class RegenerateMapEvent {

	private Intelligence botIntelligence;
	private MapParameters mapParams;

	/**
	 * Constructor.
	 * 
	 * @param botIntelligence bot intelligence
	 * @param mapParams       map parameters
	 */
	public RegenerateMapEvent(Intelligence botIntelligence, MapParameters mapParams) {
		super();
		this.botIntelligence = botIntelligence;
		this.mapParams = mapParams;
	}

	public Intelligence getBotIntelligence() {
		return botIntelligence;
	}

	public void setBotIntelligence(Intelligence botIntelligence) {
		this.botIntelligence = botIntelligence;
	}

	public MapParameters getMapParams() {
		return mapParams;
	}

	public void setMapParams(MapParameters mapParams) {
		this.mapParams = mapParams;
	}

}

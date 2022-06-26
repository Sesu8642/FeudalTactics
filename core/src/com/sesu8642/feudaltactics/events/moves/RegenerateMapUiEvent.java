package com.sesu8642.feudaltactics.events.moves;

import com.sesu8642.feudaltactics.gamelogic.BotAi;
import com.sesu8642.feudaltactics.gamelogic.BotAi.Intelligence;
import com.sesu8642.feudaltactics.gamelogic.MapParameters;

/**
 * Event: Map needs te be re-generated because the Parameters of the generated
 * map changed or the player wants to retry.
 */
public class RegenerateMapUiEvent {

	private BotAi.Intelligence botIntelligence;
	private MapParameters mapParams;

	/**
	 * Constructor.
	 * 
	 * @param botIntelligence bot intelligence
	 * @param mapParams       map parameters
	 */
	public RegenerateMapUiEvent(Intelligence botIntelligence, MapParameters mapParams) {
		super();
		this.botIntelligence = botIntelligence;
		this.mapParams = mapParams;
	}

	public BotAi.Intelligence getBotIntelligence() {
		return botIntelligence;
	}

	public void setBotIntelligence(BotAi.Intelligence botIntelligence) {
		this.botIntelligence = botIntelligence;
	}

	public MapParameters getMapParams() {
		return mapParams;
	}

	public void setMapParams(MapParameters mapParams) {
		this.mapParams = mapParams;
	}

}

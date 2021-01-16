package com.sesu8642.feudaltactics.preferences;

import com.sesu8642.feudaltactics.BotAI;
import com.sesu8642.feudaltactics.BotAI.Intelligence;

/**
 * Preferences for a new game
 */
public class NewGamePreferences {

	public enum MapSizes {
		SMALL, MEDIUM, LARGE
	}

	public enum Densities {
		LOOSE, MEDIUM, DENSE
	}

	private BotAI.Intelligence botIntelligence;
	private MapSizes mapSize;
	private Densities density;

	public NewGamePreferences(Intelligence botIntelligence, MapSizes mapSize, Densities density) {
		this.botIntelligence = botIntelligence;
		this.mapSize = mapSize;
		this.density = density;
	}

	public BotAI.Intelligence getBotIntelligence() {
		return botIntelligence;
	}

	public void setBotIntelligence(BotAI.Intelligence botIntelligence) {
		this.botIntelligence = botIntelligence;
	}

	public MapSizes getMapSize() {
		return mapSize;
	}

	public void setMapSize(MapSizes mapSize) {
		this.mapSize = mapSize;
	}

	public Densities getDensity() {
		return density;
	}

	public void setDensity(Densities density) {
		this.density = density;
	}

}

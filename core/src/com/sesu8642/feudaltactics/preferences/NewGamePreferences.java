package com.sesu8642.feudaltactics.preferences;

import com.sesu8642.feudaltactics.gamelogic.ingame.BotAi;
import com.sesu8642.feudaltactics.gamelogic.ingame.BotAi.Intelligence;

/** Value object: preferences for a new game. */
public class NewGamePreferences {

	/** Map sizes that can be generated. */
	public enum MapSizes {
		SMALL(50), MEDIUM(150), LARGE(250);

		private int amountOfTiles;

		private MapSizes(int amountOfTiles) {
			this.amountOfTiles = amountOfTiles;
		}

		public int getAmountOfTiles() {
			return this.amountOfTiles;
		}

	}

	/** Map densities that can be generated. */
	public enum Densities {
		LOOSE(-3), MEDIUM(0), DENSE(3);

		private float densityFloat;

		private Densities(float densityFloat) {
			this.densityFloat = densityFloat;
		}

		public float getDensityFloat() {
			return this.densityFloat;
		}
	}

	private BotAi.Intelligence botIntelligence;
	private MapSizes mapSize;
	private Densities density;

	/**
	 * Constructor.
	 * 
	 * @param botIntelligence intelligence of the bot players for the game
	 * @param mapSize         size of the map for this game
	 * @param density         density of the map for this game
	 */
	public NewGamePreferences(Intelligence botIntelligence, MapSizes mapSize, Densities density) {
		this.botIntelligence = botIntelligence;
		this.mapSize = mapSize;
		this.density = density;
	}

	public BotAi.Intelligence getBotIntelligence() {
		return botIntelligence;
	}

	public void setBotIntelligence(BotAi.Intelligence botIntelligence) {
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

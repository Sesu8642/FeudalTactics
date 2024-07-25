// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

/** Value object: preferences for a new game. */
public class NewGamePreferences {

	private Intelligence botIntelligence;
	private MapSizes mapSize;
	private Densities density;
	private int startingPosition;

	/**
	 * Constructor.
	 * 
	 * @param botIntelligence  intelligence of the bot players for the game
	 * @param mapSize          size of the map for this game
	 * @param density          density of the map for this game
	 * @param startingPosition starting position index of the human player
	 */
	public NewGamePreferences(Intelligence botIntelligence, MapSizes mapSize, Densities density, int startingPosition) {
		this.botIntelligence = botIntelligence;
		this.mapSize = mapSize;
		this.density = density;
		this.startingPosition = startingPosition;
	}

	public Intelligence getBotIntelligence() {
		return botIntelligence;
	}

	public void setBotIntelligence(Intelligence botIntelligence) {
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

	public int getStartingPosition() {
		return startingPosition;
	}

	public void setStartingPosition(int startingPosition) {
		this.startingPosition = startingPosition;
	}

	/** Map sizes that can be generated. */
	public enum MapSizes {
		SMALL(50), MEDIUM(150), LARGE(250), XLARGE(500), XXLARGE(1000);

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

}

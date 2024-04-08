// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import com.badlogic.gdx.graphics.Color;

import de.sesu8642.feudaltactics.lib.ingame.GameController;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

/** Value object: preferences for a new game. */
public class NewGamePreferences {

	private Intelligence botIntelligence;
	private MapSizes mapSize;
	private Densities density;
	private UserColors userColor;

	/**
	 * Constructor.
	 * 
	 * @param botIntelligence intelligence of the bot players for the game
	 * @param mapSize         size of the map for this game
	 * @param density         density of the map for this game
	 * @param userColor       color user selects for their kingdom
	 */
	public NewGamePreferences(Intelligence botIntelligence, MapSizes mapSize, Densities density, UserColors userColor) {
		this.botIntelligence = botIntelligence;
		this.mapSize = mapSize;
		this.density = density;
		this.userColor = userColor;
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

	public UserColors getUserColor() {
		return userColor;
	}

	public void setUserColor(UserColors userColor) {
		this.userColor = userColor;
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

	/** User colors that can be generated. */
	public enum UserColors {
		BLUE(GameController.BLUE), ORANGE(GameController.ORANGE), GREEN(GameController.GREEN),
		YELLOW(GameController.YELLOW), PINK(GameController.PINK), WHITE(GameController.WHITE);

		private Color kingdomColor;

		private UserColors(Color kingdomColor) {
			this.kingdomColor = kingdomColor;
		}

		public Color getKingdomColor() {
			return this.kingdomColor;
		}
	}

}

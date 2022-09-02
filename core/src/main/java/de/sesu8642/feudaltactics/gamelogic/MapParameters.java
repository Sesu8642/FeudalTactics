// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.gamelogic;

import de.sesu8642.feudaltactics.preferences.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.preferences.NewGamePreferences.MapSizes;

/** Parameter class for map generation. Immutable class. */
public class MapParameters {
	private int humanPlayerNo;
	private int botPlayerNo;
	private Long seed;
	private MapSizes landMass;
	private Densities density;

	/**
	 * Constructor.
	 * 
	 * @param humanPlayerNo number of human players that play
	 * @param botPlayerNo   number of bot players that play
	 * @param seed          map seed to use for generating the map
	 * @param landMass      number of tiles to generate
	 * @param density       map density to use for generation
	 */
	public MapParameters(int humanPlayerNo, int botPlayerNo, Long seed, MapSizes landMass, Densities density) {
		this.humanPlayerNo = humanPlayerNo;
		this.botPlayerNo = botPlayerNo;
		this.seed = seed;
		this.landMass = landMass;
		this.density = density;
	}

	/**
	 * Constructor. Assumes one human player vs. 5 bots.
	 * 
	 * @param seed     map seed to use for generating the map
	 * @param landMass number of tiles to generate
	 * @param density  map density to use for generation
	 */
	public MapParameters(Long seed, MapSizes landMass, Densities density) {
		this.humanPlayerNo = 1;
		this.botPlayerNo = 5;
		this.seed = seed;
		this.landMass = landMass;
		this.density = density;
	}

	public int getHumanPlayerNo() {
		return humanPlayerNo;
	}

	public int getBotPlayerNo() {
		return botPlayerNo;
	}

	public Long getSeed() {
		return seed;
	}

	public MapSizes getLandMass() {
		return landMass;
	}

	public Densities getDensity() {
		return density;
	}

	@Override
	public String toString() {
		return String.format("MapParameters [humanPlayerNo=%s, botPlayerNo=%s, seed=%s, landMass=%s, density=%s]",
				humanPlayerNo, botPlayerNo, seed, landMass, density);
	}

}

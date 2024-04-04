// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import com.badlogic.gdx.graphics.Color;

/** Parameter class for map generation. Immutable class. */
public class MapParameters {
	private int humanPlayerNo;
	private int botPlayerNo;
	private Long seed;
	private int landMass;
	private float density;
	private Color userColor;

	/**
	 * Constructor.
	 * 
	 * @param humanPlayerNo number of human players that play
	 * @param botPlayerNo   number of bot players that play
	 * @param seed          map seed to use for generating the map
	 * @param landMass      number of tiles to generate
	 * @param density       map density to use for generation
	 * @param userColor     color user selects for their kingdom
	 */
	public MapParameters(int humanPlayerNo, int botPlayerNo, Long seed, int landMass, float density, Color userColor) {
		this.humanPlayerNo = humanPlayerNo;
		this.botPlayerNo = botPlayerNo;
		this.seed = seed;
		this.landMass = landMass;
		this.density = density;
		this.userColor = userColor;
	}

	/**
	 * Constructor. Assumes one human player vs. 5 bots.
	 * 
	 * @param seed      map seed to use for generating the map
	 * @param landMass  number of tiles to generate
	 * @param density   map density to use for generation
	 * @param userColor color user selects for their kingdom
	 */
	public MapParameters(Long seed, int landMass, float density, Color userColor) {
		this.humanPlayerNo = 1;
		this.botPlayerNo = 5;
		this.seed = seed;
		this.landMass = landMass;
		this.density = density;
		this.userColor = userColor;
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

	public int getLandMass() {
		return landMass;
	}

	public float getDensity() {
		return density;
	}

	public Color getUserColor() {
		return userColor;
	}

	@Override
	public String toString() {
		return String.format("MapParameters [humanPlayerNo=%s, botPlayerNo=%s, seed=%s, landMass=%s, density=%s]",
				humanPlayerNo, botPlayerNo, seed, landMass, density);
	}

}

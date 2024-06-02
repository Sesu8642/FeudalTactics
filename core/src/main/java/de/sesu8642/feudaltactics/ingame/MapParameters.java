// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import java.util.ArrayList;
import java.util.List;

import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;

/** Parameter class for map generation. Immutable class. */
public class MapParameters {
	private List<Player> players;
	private Long seed;
	private int landMass;
	private float density;

	/**
	 * Constructor.
	 * 
	 * @param players  player list
	 * @param seed     map seed to use for generating the map
	 * @param landMass number of tiles to generate
	 * @param density  map density to use for generation
	 */
	public MapParameters(List<Player> players, Long seed, int landMass, float density) {
		this.players = players;
		this.seed = seed;
		this.landMass = landMass;
		this.density = density;
	}

	/**
	 * Constructor. Assumes one human player vs. 5 bots.
	 * 
	 * @param humanPlayerIndex index of the human player
	 * @param seed             map seed to use for generating the map
	 * @param landMass         number of tiles to generate
	 * @param density          map density to use for generation
	 */
	public MapParameters(int humanPlayerIndex, Long seed, int landMass, float density) {
		this.players = new ArrayList<>();
		for (int i = 0; i < 6; i++) {
			if (i == humanPlayerIndex) {
				this.players.add(new Player(i, Type.LOCAL_PLAYER));
			} else {
				this.players.add(new Player(i, Type.LOCAL_BOT));
			}
		}
		this.seed = seed;
		this.landMass = landMass;
		this.density = density;
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public Long getSeed() {
		return seed;
	}

	public void setSeed(Long seed) {
		this.seed = seed;
	}

	public int getLandMass() {
		return landMass;
	}

	public void setLandMass(int landMass) {
		this.landMass = landMass;
	}

	public float getDensity() {
		return density;
	}

	public void setDensity(float density) {
		this.density = density;
	}

	@Override
	public String toString() {
		return "MapParameters [players=" + players + ", seed=" + seed + ", landMass=" + landMass + ", density="
				+ density + "]";
	}

}

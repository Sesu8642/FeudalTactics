// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import com.badlogic.gdx.math.Vector2;

import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

/** Contains all information about a running game. **/
public class GameState {

	private List<Player> players = new ArrayList<>();
	private Player winner = null;
	private int playerTurn = 0;
	// need a map with fix iteration order to avoid randomness
	private LinkedHashMap<Vector2, HexTile> map = new LinkedHashMap<>();
	private List<Kingdom> kingdoms;
	private Kingdom activeKingdom = null;
	private MapObject heldObject = null;
	private Intelligence botIntelligence = Intelligence.LEVEL_1;
	private Long seed;

	/** A round consists of one turn per player. */
	private int round = 0;

	public GameState() {
		// no fields must be set on construction
	}

	public List<Player> getPlayers() {
		return players;
	}

	public void setPlayers(List<Player> players) {
		this.players = players;
	}

	public Player getWinner() {
		return winner;
	}

	public void setWinner(Player winner) {
		this.winner = winner;
	}

	public int getPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(int playerTurn) {
		this.playerTurn = playerTurn;
	}

	public LinkedHashMap<Vector2, HexTile> getMap() {
		return map;
	}

	public void setMap(LinkedHashMap<Vector2, HexTile> map) {
		this.map = map;
	}

	public List<Kingdom> getKingdoms() {
		return kingdoms;
	}

	public void setKingdoms(List<Kingdom> kingdoms) {
		this.kingdoms = kingdoms;
	}

	public Kingdom getActiveKingdom() {
		return activeKingdom;
	}

	public void setActiveKingdom(Kingdom activeKingdom) {
		this.activeKingdom = activeKingdom;
	}

	public MapObject getHeldObject() {
		return heldObject;
	}

	public void setHeldObject(MapObject heldObject) {
		this.heldObject = heldObject;
	}

	public Player getActivePlayer() {
		return players.get(playerTurn);
	}

	public Intelligence getBotIntelligence() {
		return botIntelligence;
	}

	public void setBotIntelligence(Intelligence botIntelligence) {
		this.botIntelligence = botIntelligence;
	}

	public Long getSeed() {
		return seed;
	}

	public void setSeed(Long seed) {
		this.seed = seed;
	}

	public int getRound() {
		return round;
	}

	public void setRound(int round) {
		this.round = round;
	}

	@Override
	public int hashCode() {
		// calculating with enum strings because the hashcode must be consistent across
		// runs
		return Objects.hash(activeKingdom, botIntelligence.toString(), heldObject, kingdoms, map, playerTurn, players,
				round, seed, winner);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		GameState other = (GameState) obj;
		return Objects.equals(activeKingdom, other.activeKingdom) && botIntelligence == other.botIntelligence
				&& Objects.equals(heldObject, other.heldObject) && Objects.equals(kingdoms, other.kingdoms)
				&& Objects.equals(map, other.map) && playerTurn == other.playerTurn
				&& Objects.equals(players, other.players) && round == other.round && Objects.equals(seed, other.seed)
				&& Objects.equals(winner, other.winner);
	}

}

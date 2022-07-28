// SPDX-License-Identifier: GPL-3.0-or-later

package com.sesu8642.feudaltactics.gamelogic.gamestate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.gamelogic.ingame.BotAi;
import com.sesu8642.feudaltactics.gamelogic.ingame.BotAi.Intelligence;

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
	private BotAi.Intelligence botIntelligence = Intelligence.DUMB;
	private Long seed;

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

	public BotAi.Intelligence getBotIntelligence() {
		return botIntelligence;
	}

	public void setBotIntelligence(BotAi.Intelligence botIntelligence) {
		this.botIntelligence = botIntelligence;
	}

	public Long getSeed() {
		return seed;
	}

	public void setSeed(Long seed) {
		this.seed = seed;
	}

	@Override
	public int hashCode() {
		return Objects.hash(activeKingdom, botIntelligence, heldObject, kingdoms, map, playerTurn, players, seed,
				winner);
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
				&& Objects.equals(players, other.players) && Objects.equals(seed, other.seed)
				&& Objects.equals(winner, other.winner);
	}

}

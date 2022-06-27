package com.sesu8642.feudaltactics.gamelogic.gamestate;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.sesu8642.feudaltactics.gamelogic.ingame.BotAi;
import com.sesu8642.feudaltactics.gamelogic.ingame.BotAi.Intelligence;

/** Contains all information about a running game. **/
public class GameState {

	private List<Player> players = new ArrayList<>();
	private Player winner = null;
	private int playerTurn = 0;
	private HexMap map = new HexMap();
	private List<Kingdom> kingdoms;
	private Kingdom activeKingdom = null;
	private MapObject heldObject = null;
	private Random random = new Random();
	private BotAi.Intelligence botIntelligence = Intelligence.DUMB;
	private Long seed = 0L;

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

	public HexMap getMap() {
		return map;
	}

	public void setMap(HexMap map) {
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

	public Random getRandom() {
		return random;
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

}

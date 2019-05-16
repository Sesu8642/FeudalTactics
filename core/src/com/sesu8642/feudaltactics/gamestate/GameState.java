package com.sesu8642.feudaltactics.gamestate;

import java.util.ArrayList;

import com.sesu8642.feudaltactics.gamestate.mapobjects.MapObject;

public class GameState {

	private ArrayList<Player> players;
	private int playerTurn = 0;
	private HexMap map;
	private ArrayList<Kingdom> kingdoms;
	private Kingdom activeKingdom;
	private MapObject heldObject;

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
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

	public ArrayList<Kingdom> getKingdoms() {
		return kingdoms;
	}

	public void setKingdoms(ArrayList<Kingdom> kingdoms) {
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

}

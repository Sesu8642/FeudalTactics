package com.sesu8642.feudaltactics.engine;

import java.util.ArrayList;

import com.sesu8642.feudaltactics.gamelogic.Kingdom;

public class GameState {

	private ArrayList<Player> players;
	private int playerTurn = 0;
	private HexMap map;
	private ArrayList<Kingdom> kingdoms;

	ArrayList<Player> getPlayers() {
		return players;
	}

	void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	int getPlayerTurn() {
		return playerTurn;
	}

	void setPlayerTurn(int playerTurn) {
		this.playerTurn = playerTurn;
	}

	HexMap getMap() {
		return map;
	}

	void setMap(HexMap map) {
		this.map = map;
	}

	public ArrayList<Kingdom> getKingdoms() {
		return kingdoms;
	}

	public void setKingdoms(ArrayList<Kingdom> kingdoms) {
		this.kingdoms = kingdoms;
	}
	
}

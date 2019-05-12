package com.sesu8642.feudaltactics.engine;

import java.util.ArrayList;

public class GameState {

	private ArrayList<Player> players;
	private int playerTurn = 0;
	private HexMap map;

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

	
	
}

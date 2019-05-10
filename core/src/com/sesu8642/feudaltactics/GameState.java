package com.sesu8642.feudaltactics;

import java.util.ArrayList;

public class GameState {

	private ArrayList<Player> players;
	private int playerTurn;
	private HexMap map;

	public GameState(ArrayList<Player> players, int playerTurn, HexMap map) {
		this.players = players;
		this.playerTurn = playerTurn;
		this.map = map;
	}

}

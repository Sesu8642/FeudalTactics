package com.sesu8642.feudaltactics.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.HexMap;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamestate.Player;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Capital;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Castle;
import com.sesu8642.feudaltactics.gamestate.mapobjects.MapObject;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Tree;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit.UnitTypes;
import com.sesu8642.feudaltactics.scenes.Hud;

public class GameController {

	private MapRenderer mapRenderer;
	private GameState gameState;
	private Hud hud;
	private LinkedList<GameState> undoStates;

	public GameController() {
		this.gameState = new GameState();
		this.undoStates = new LinkedList<GameState>();
	}

	public void generateDummyMap() {
		ArrayList<Player> players = new ArrayList<Player>();
		Player p1 = new Player(new Color(0, 0.5f, 0.8f, 1), Player.Type.LOCAL_PLAYER);
		Player p2 = new Player(new Color(1F, 0F, 0F, 1), Player.Type.LOCAL_PLAYER);
		Player p3 = new Player(new Color(0F, 1F, 0F, 1), Player.Type.LOCAL_PLAYER);
		Player p4 = new Player(new Color(1F, 1F, 0F, 1), Player.Type.LOCAL_PLAYER);
		Player p5 = new Player(new Color(1F, 1F, 1F, 1), Player.Type.LOCAL_PLAYER);
		Player p6 = new Player(new Color(0F, 1F, 1F, 1), Player.Type.LOCAL_PLAYER);
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);
		players.add(p5);
		players.add(p6);
		GameStateController.initializeMap(gameState, players, 200, 0, 0.1F, null);
		mapRenderer.updateMap();
	}

	public void printTileInfo(Vector2 hexCoords) {
		System.out.println("clicked tile position " + hexCoords);
		System.out.println(gameState.getMap().getTiles().get(hexCoords));
	}

	public void updateInfoText() {
		if (hud == null) {
			return;
		}
		Kingdom kingdom = gameState.getActiveKingdom();
		if (kingdom == null) {
			hud.setInfoText("");
			return;
		}
		int income = kingdom.getIncome();
		int salaries = kingdom.getSalaries();
		int result = income - salaries;
		int savings = kingdom.getSavings();
		String infoText = "";
		infoText += "Income: " + income;
		infoText += "\nSalaries: " + salaries;
		infoText += "\nResult: " + result;
		infoText += "\nSavings: " + savings;
		hud.setInfoText(infoText);
	}

	public void activateKingdom(Kingdom kingdom) {
		GameStateController.activateKingdom(gameState, kingdom);
		updateInfoText();
		mapRenderer.updateMap();
	}

	public void pickupObject(HexTile tile) {
		undoStates.add(new GameState(this.gameState));
		GameStateController.pickupObject(gameState, tile);
		mapRenderer.updateMap();
		hud.updateHandContent(gameState.getHeldObject().getSpriteName());
	}

	public void placeOwn(HexTile tile) {
		undoStates.add(new GameState(this.gameState));
		GameStateController.placeOwn(gameState, tile);
		mapRenderer.updateMap();
		hud.updateHandContent(null);
	}

	public void combineUnits(HexTile tile) {
		undoStates.add(new GameState(this.gameState));
		GameStateController.combineUnits(gameState, tile);
		mapRenderer.updateMap();
		hud.updateHandContent(null);
	}

	public void conquer(HexTile tile) {
		undoStates.add(new GameState(this.gameState));
		GameStateController.conquer(gameState, tile);
		mapRenderer.updateMap();
		hud.updateHandContent(null);
	}

	public void endTurn() {
		GameStateController.endTurn(gameState);
		// clear undo states
		undoStates.clear();
		// reset info text
		updateInfoText();
		mapRenderer.updateMap();
	}

	public void buyPeasant() {
		undoStates.add(new GameState(this.gameState));
		GameStateController.buyPeasant(gameState);
		updateInfoText();
		hud.updateHandContent(gameState.getHeldObject().getSpriteName());
	}

	public void buyCastle() {
		undoStates.add(new GameState(this.gameState));
		GameStateController.buyCastle(gameState);
		updateInfoText();
		hud.updateHandContent(gameState.getHeldObject().getSpriteName());
	}

	public void undoLastAction() {
		this.gameState = undoStates.removeLast();
		mapRenderer.updateMap();
		updateInfoText();
		if (gameState.getHeldObject() != null) {
			hud.updateHandContent(gameState.getHeldObject().getSpriteName());
		}else {
			hud.updateHandContent(null);
		}
	}

	public void setHud(Hud hud) {
		this.hud = hud;
	}

	public MapRenderer getMapRenderer() {
		return mapRenderer;
	}

	public void setMapRenderer(MapRenderer mapRenderer) {
		this.mapRenderer = mapRenderer;
	}

	public GameState getGameState() {
		return gameState;
	}

	public LinkedList<GameState> getUndoStates() {
		return undoStates;
	}

}
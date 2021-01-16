package com.sesu8642.feudaltactics;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.GameStateHelper;
import com.sesu8642.feudaltactics.gamestate.Player;
import com.sesu8642.feudaltactics.gamestate.Player.Type;

public class EditorController {

	private MapRenderer mapRenderer;
	private GameState gameState;
	
	public EditorController() {
		this.gameState = new GameState();
	}

	public void generateEmptyMap() {
		ArrayList<Player> players = new ArrayList<Player>();
		gameState.setBotIntelligence(BotAI.Intelligence.MEDIUM);
		Player p1 = new Player(new Color(0F, 1F, 1F, 1), Type.LOCAL_PLAYER);
		Player p2 = new Player(new Color(0.75F, 0.5F, 0F, 1), Type.LOCAL_BOT);
		Player p3 = new Player(new Color(1F, 0.67F, 0.67F, 1), Type.LOCAL_BOT);
		Player p4 = new Player(new Color(1F, 1F, 0F, 1), Type.LOCAL_BOT);
		Player p5 = new Player(new Color(1F, 1F, 1F, 1), Type.LOCAL_BOT);
		Player p6 = new Player(new Color(0F, 1F, 0F, 1), Type.LOCAL_BOT);
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);
		players.add(p5);
		players.add(p6);
		GameStateHelper.initializeMap(gameState, players, 0, 0, 0F, null);
		mapRenderer.updateMap(gameState);
	}

	public void printTileInfo(Vector2 hexCoords) {
		System.out.println("clicked tile position " + hexCoords);
		System.out.println(gameState.getMap().getTiles().get(hexCoords));
	}

	public void createTile(Vector2 hexCoords) {
		Player randomPlayer = gameState.getPlayers().get(gameState.getRandom().nextInt(gameState.getPlayers().size()));
		GameStateHelper.placeTile(gameState, hexCoords, randomPlayer);
		mapRenderer.updateMap(gameState);
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

}
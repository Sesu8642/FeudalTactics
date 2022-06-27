package com.sesu8642.feudaltactics.gamelogic.editor;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.EventBus;
import com.sesu8642.feudaltactics.events.GameStateChangeEvent;
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameState;
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameStateHelper;
import com.sesu8642.feudaltactics.gamelogic.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Player;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Player.Type;
import com.sesu8642.feudaltactics.gamelogic.ingame.GameController;

/** Controller for the map editor. */
@Singleton
public class EditorController {

	private EventBus eventBus;
	private GameState gameState;

	/**
	 * Constructor.
	 * 
	 * @param eventBus event bus
	 */
	@Inject
	public EditorController(EventBus eventBus) {
		this.eventBus = eventBus;
		gameState = new GameState();
	}

	/** Generates an empty map. */
	public void generateEmptyGameState() {
		gameState = new GameState();
		ArrayList<Player> players = new ArrayList<>();
		players.add(new Player(GameController.PLAYER_COLORS[0], Type.LOCAL_PLAYER));
		for (int i = 1; i < GameController.PLAYER_COLORS.length; i++) {
			players.add(new Player(GameController.PLAYER_COLORS[i], Type.LOCAL_BOT));
		}
		GameStateHelper.initializeMap(gameState, players, 0, 0, 0F, null);
		eventBus.post(new GameStateChangeEvent(gameState, false, false));
	}

	/** Creates a tile. */
	public void createTile(Vector2 hexCoords) {
		HexTile existingTile = gameState.getMap().getTiles().get(hexCoords);
		int newTilePlayerIndex = 0;
		if (existingTile != null) {
			newTilePlayerIndex = gameState.getPlayers().indexOf(existingTile.getPlayer()) + 1;
		}
		if (newTilePlayerIndex > gameState.getPlayers().size() - 1) {
			GameStateHelper.deleteTile(gameState, hexCoords);
		} else {
			Player newPlayer = gameState.getPlayers().get(newTilePlayerIndex);
			GameStateHelper.placeTile(gameState, hexCoords, newPlayer);
		}
		eventBus.post(new GameStateChangeEvent(gameState, false, false));
	}

	public GameState getGameState() {
		return gameState;
	}

}
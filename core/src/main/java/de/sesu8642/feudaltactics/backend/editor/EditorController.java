// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.backend.editor;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.backend.gamestate.GameState;
import de.sesu8642.feudaltactics.backend.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.backend.gamestate.HexTile;
import de.sesu8642.feudaltactics.backend.gamestate.Player;
import de.sesu8642.feudaltactics.backend.gamestate.Player.Type;
import de.sesu8642.feudaltactics.backend.ingame.GameController;
import de.sesu8642.feudaltactics.events.GameStateChangeEvent;

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
		eventBus.post(new GameStateChangeEvent(gameState, false));
	}

	/** Creates a tile. */
	public void createTile(Vector2 hexCoords) {
		HexTile existingTile = gameState.getMap().get(hexCoords);
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
		eventBus.post(new GameStateChangeEvent(gameState, false));
	}

	public GameState getGameState() {
		return gameState;
	}

}
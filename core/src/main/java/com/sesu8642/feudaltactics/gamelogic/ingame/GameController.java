// SPDX-License-Identifier: GPL-3.0-or-later

package com.sesu8642.feudaltactics.gamelogic.ingame;

import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.EventBus;
import com.sesu8642.feudaltactics.events.GameStateChangeEvent;
import com.sesu8642.feudaltactics.gamelogic.MapParameters;
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameState;
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameStateHelper;
import com.sesu8642.feudaltactics.gamelogic.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Player;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Player.Type;
import com.sesu8642.feudaltactics.preferences.PreferencesHelper;

/** Controller for playing the game. */
@Singleton
public class GameController {

	private static final String TAG = GameController.class.getName();

	public static final Color[] PLAYER_COLORS = { new Color(0.2F, 0.45F, 0.8F, 1), new Color(0.75F, 0.5F, 0F, 1),
			new Color(1F, 0.67F, 0.67F, 1), new Color(1F, 1F, 0F, 1), new Color(1F, 1F, 1F, 1),
			new Color(0F, 1F, 0F, 1) };

	private EventBus eventBus;
	private BotAi botAi;
	private GameState gameState;

	/**
	 * Constructor.
	 * 
	 * @param eventBus event bus
	 * @param botAi    bot AI
	 */
	@Inject
	public GameController(EventBus eventBus, BotAi botAi) {
		this.eventBus = eventBus;
		this.botAi = botAi;
		gameState = new GameState();
	}

	/** Starts the game. Bots will do their turns if they are first. */
	public void startGame() {
		// if a bot begins, make it act
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			gameState = botAi.doTurn(gameState, gameState.getBotIntelligence());
			endTurn();
		}
		PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
		autosave();
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	private void autosave() {
		PreferencesHelper.autoSaveGameState(gameState);
	}

	/** Loads the latest autosave. */
	public void loadLatestAutosave() {
		gameState = PreferencesHelper.getLatestAutoSave();
		eventBus.post(new GameStateChangeEvent(gameState, false, true));
	}

	/**
	 * Generates a map.
	 * 
	 * @param botIntelligence intelligence of the bot players
	 * @param mapParams       map generation parameters
	 */
	public void generateGameState(BotAi.Intelligence botIntelligence, MapParameters mapParams) {
		gameState = new GameState();
		gameState.setBotIntelligence(botIntelligence);
		ArrayList<Player> players = new ArrayList<>();
		int remainingHumanPlayers = mapParams.getHumanPlayerNo();
		int remainingBotPlayers = mapParams.getBotPlayerNo();
		for (Color color : PLAYER_COLORS) {
			if (remainingHumanPlayers > 0) {
				remainingHumanPlayers--;
				players.add(new Player(color, Type.LOCAL_PLAYER));
			} else if (remainingBotPlayers > 0) {
				players.add(new Player(color, Type.LOCAL_BOT));
			} else {
				break;
			}
		}
		GameStateHelper.initializeMap(gameState, players, mapParams.getLandMass().getAmountOfTiles(),
				mapParams.getDensity().getDensityFloat(), null, mapParams.getSeed());
		eventBus.post(new GameStateChangeEvent(gameState, false, true));
	}

	/**
	 * Prints debug info about a tile.
	 * 
	 * @param hexCoords coords of the tile
	 */
	public void printTileInfo(Vector2 hexCoords) {
		Gdx.app.debug(TAG, String.format("clicked tile position %s: %s", hexCoords,
				String.valueOf(gameState.getMap().get(hexCoords))));
	}

	/**
	 * Activates a kingdom.
	 * 
	 * @param kingdom kingdom to activate
	 */
	public void activateKingdom(Kingdom kingdom) {
		GameStateHelper.activateKingdom(gameState, kingdom);
		autosave();
		// save first because is is relevant for the undo button status
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/**
	 * Picks up an object.
	 * 
	 * @param tile tile that contains the object
	 */
	public void pickupObject(HexTile tile) {
		GameStateHelper.pickupObject(gameState, tile);
		autosave();
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/**
	 * Places a held object on a tile in the own kingdom.
	 * 
	 * @param tile tile to place to object on
	 */
	public void placeOwn(HexTile tile) {
		GameStateHelper.placeOwn(gameState, tile);
		autosave();
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/**
	 * Combines the held unit with a unit on the map.
	 * 
	 * @param tile tile that contains the unit on the map
	 */
	public void combineUnits(HexTile tile) {
		GameStateHelper.combineUnits(gameState, tile);
		autosave();
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/**
	 * Conquers an enemy tile.
	 * 
	 * @param tile tile to conquer
	 */
	public void conquer(HexTile tile) {
		GameStateHelper.conquer(gameState, tile);
		autosave();
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/** Ends the turn. */
	public void endTurn() {
		// remember old winner
		endTurn(gameState.getWinner());
	}

	/**
	 * Ends the turn. Takes the winner at the time when the player ended their turn
	 * to determine whether it changed in the meantime.
	 * 
	 * @param oldWinner winner of the game when the last player ended their turn
	 */
	void endTurn(Player oldWinner) {
		// update gameState
		gameState = GameStateHelper.endTurn(gameState);
		// make bots act
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			gameState = botAi.doTurn(gameState, gameState.getBotIntelligence());
			endTurn(oldWinner);
		} else {
			// autosave when a player turn begins
			autosave();
			// clear autosaves from previous turn
			PreferencesHelper.deleteAllAutoSaveExceptLatestN(1);
			eventBus.post(new GameStateChangeEvent(gameState, gameState.getWinner() != oldWinner, false));
		}
	}

	/** Buys a peasant. */
	public void buyPeasant() {
		GameStateHelper.buyPeasant(gameState);
		autosave();
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/** Buys a castle. */
	public void buyCastle() {
		GameStateHelper.buyCastle(gameState);
		autosave();
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/** Undoes the last action. */
	public void undoLastAction() {
		if (PreferencesHelper.getNoOfAutoSaves() > 1) {
			// 1 means the current state is the only one saved
			// remove the current state from autosaves
			PreferencesHelper.deleteLatestAutoSave();
			// load the previous state
			GameState loaded = PreferencesHelper.getLatestAutoSave();
			gameState = loaded;
		}
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	public GameState getGameState() {
		return gameState;
	}
}
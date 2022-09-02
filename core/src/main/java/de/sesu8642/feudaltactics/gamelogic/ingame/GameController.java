// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.gamelogic.ingame;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.events.GameStateChangeEvent;
import de.sesu8642.feudaltactics.gamelogic.MapParameters;
import de.sesu8642.feudaltactics.gamelogic.gamestate.GameState;
import de.sesu8642.feudaltactics.gamelogic.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.gamelogic.gamestate.HexTile;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Kingdom;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Player;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Player.Type;
import de.sesu8642.feudaltactics.preferences.PreferencesHelper;

/** Controller for playing the game. */
@Singleton
public class GameController {

	private static final String TAG = GameController.class.getName();

	public static final Color[] PLAYER_COLORS = { new Color(0.2F, 0.45F, 0.8F, 1), new Color(0.75F, 0.5F, 0F, 1),
			new Color(1F, 0.67F, 0.67F, 1), new Color(1F, 1F, 0F, 1), new Color(1F, 1F, 1F, 1),
			new Color(0F, 1F, 0F, 1) };

	private EventBus eventBus;
	private BotAi botAi;
	private ExecutorService botTurnExecutor;
	private GameState gameState;
	private Future<?> botTurnFuture;

	/**
	 * Winner of the game before the bot players acted. Used to determine whether
	 * the winner changed in order to display a message.
	 */
	private Player lastWinner;

	/**
	 * Constructor.
	 * 
	 * @param eventBus event bus
	 * @param botAi    bot AI
	 */
	@Inject
	public GameController(EventBus eventBus, ExecutorService botTurnExecutor, BotAi botAi) {
		this.eventBus = eventBus;
		this.botTurnExecutor = botTurnExecutor;
		this.botAi = botAi;
		gameState = new GameState();
	}

	/** Starts the game. Bots will do their turns if they are first. */
	public void startGame() {
		Gdx.app.log(TAG, "starting game");
		// if a bot begins, make it act
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			startBotTurn();
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
		Gdx.app.log(TAG, "loading latest autosave");
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
		Gdx.app.log(TAG, String.format("generating a new game state with bot intelligence %s and %s", botIntelligence,
				mapParams));
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
		Gdx.app.debug(TAG, String.format("clicked: %s", gameState.getMap().get(hexCoords)));
	}

	/**
	 * Activates a kingdom.
	 * 
	 * @param kingdom kingdom to activate
	 */
	public void activateKingdom(Kingdom kingdom) {
		Gdx.app.debug(TAG, String.format("activating %s", kingdom));
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
		Gdx.app.debug(TAG, String.format("picking up object from %s", tile));
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
		Gdx.app.debug(TAG, String.format("placing held object on own %s", tile));
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
		Gdx.app.debug(TAG, String.format("combining held unit with unit on %s", tile));
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
		Gdx.app.debug(TAG, String.format("conquering %s", tile));
		GameStateHelper.conquer(gameState, tile);
		autosave();
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/**
	 * Ends the turn. Takes the winner at the time when the player ended their turn
	 * to determine whether it changed in the meantime.
	 * 
	 * @param oldWinner winner of the game when the last player ended their turn
	 */
	void endTurn() {
		Gdx.app.debug(TAG, String.format("ending turn of %s", gameState.getActivePlayer()));
		if (gameState.getActivePlayer().getType() == Type.LOCAL_PLAYER) {
			// remember the winner as it might change during bot turns
			lastWinner = gameState.getWinner();
		}
		// update gameState
		gameState = GameStateHelper.endTurn(gameState);
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			// make bots act
			startBotTurn();
		} else {
			Gdx.app.log(TAG, "player turn begins");
			// autosave when a player turn begins
			autosave();
			// clear autosaves from previous turn
			PreferencesHelper.deleteAllAutoSaveExceptLatestN(1);
			eventBus.post(new GameStateChangeEvent(gameState,
					(lastWinner != null && !lastWinner.equals(gameState.getWinner())), false));
		}
	}

	private void startBotTurn() {
		botTurnFuture = botTurnExecutor.submit(() -> {
			try {
				botAi.doTurn(gameState, gameState.getBotIntelligence());
			} catch (InterruptedException e) {
				Gdx.app.log(TAG, "bot turn was canceled");
				Thread.currentThread().interrupt();
			}
		});
	}

	/** Cancels a bot turn by canceling the future. */
	public void cancelBotTurn() {
		if (botTurnFuture != null) {
			botTurnFuture.cancel(true);
		}
	}

	/** Buys a peasant. */
	public void buyPeasant() {
		Gdx.app.debug(TAG, "buying peasant");
		GameStateHelper.buyPeasant(gameState);
		autosave();
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/** Buys a castle. */
	public void buyCastle() {
		Gdx.app.debug(TAG, "buying castle");
		GameStateHelper.buyCastle(gameState);
		autosave();
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/** Undoes the last action. */
	public void undoLastAction() {
		Gdx.app.debug(TAG, "undoing last action");
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

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

}
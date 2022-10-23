// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.backend.ingame;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.backend.MapParameters;
import de.sesu8642.feudaltactics.backend.gamestate.GameState;
import de.sesu8642.feudaltactics.backend.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.backend.gamestate.HexTile;
import de.sesu8642.feudaltactics.backend.gamestate.Kingdom;
import de.sesu8642.feudaltactics.backend.gamestate.Player;
import de.sesu8642.feudaltactics.backend.gamestate.Player.Type;
import de.sesu8642.feudaltactics.backend.ingame.botai.BotAi;
import de.sesu8642.feudaltactics.backend.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.backend.persistence.AutoSaveRepository;
import de.sesu8642.feudaltactics.events.GameStateChangeEvent;

/** Controller for playing the game. */
@Singleton
public class GameController {

	private static final String TAG = GameController.class.getName();

	public static final Color[] PLAYER_COLORS = { new Color(0.2F, 0.45F, 0.8F, 1), new Color(0.75F, 0.5F, 0F, 1),
			new Color(1F, 0.67F, 0.67F, 1), new Color(1F, 1F, 0F, 1), new Color(1F, 1F, 1F, 1),
			new Color(0F, 1F, 0F, 1) };

	private final EventBus eventBus;
	private final ExecutorService botTurnExecutor;
	private final BotAi botAi;
	private final AutoSaveRepository autoSaveRepo;
	private Future<?> botTurnFuture;

	/** State of the currently running game. */
	private GameState gameState;

	/**
	 * Constructor.
	 * 
	 * @param eventBus event bus
	 * @param botAi    bot AI
	 */
	@Inject
	public GameController(EventBus eventBus, ExecutorService botTurnExecutor, BotAi botAi,
			AutoSaveRepository autoSaveRepo) {
		this.eventBus = eventBus;
		this.botTurnExecutor = botTurnExecutor;
		this.botAi = botAi;
		this.autoSaveRepo = autoSaveRepo;
		gameState = new GameState();
	}

	/** Starts the game. Bots will do their turns if they are first. */
	public void startGame() {
		Gdx.app.log(TAG, "starting game");
		// if a bot begins, make it act
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			startBotTurn();
		}
		autoSaveRepo.deleteAllAutoSaveExceptLatestN(0);
		autosave();
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	private void autosave() {
		autoSaveRepo.autoSaveGameState(gameState);
	}

	/** Loads the latest autosave. */
	public void loadLatestAutosave() {
		Gdx.app.log(TAG, "loading latest autosave");
		gameState = autoSaveRepo.getLatestAutoSave();
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			startBotTurn();
		}
		eventBus.post(new GameStateChangeEvent(gameState, true));
	}

	/**
	 * Generates a map.
	 * 
	 * @param botIntelligence intelligence of the bot players
	 * @param mapParams       map generation parameters
	 */
	public void generateGameState(Intelligence botIntelligence, MapParameters mapParams) {
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
		GameStateHelper.initializeMap(gameState, players, mapParams.getLandMass(), mapParams.getDensity(), null,
				mapParams.getSeed());
		eventBus.post(new GameStateChangeEvent(gameState, true));
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
		// update gameState
		gameState = GameStateHelper.endTurn(gameState);
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			// make bots act
			startBotTurn();
		} else {
			Gdx.app.log(TAG, "player turn begins");
			botAi.setSkipDisplayingTurn(false);
			autosave();
			// clear autosaves from previous turn
			autoSaveRepo.deleteAllAutoSaveExceptLatestN(1);
			eventBus.post(new GameStateChangeEvent(gameState, false));
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

	/** Skips a bot turn by finishing it instantly. */
	public void skipBotTurn() {
		botAi.setSkipDisplayingTurn(true);
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
		if (autoSaveRepo.getNoOfAutoSaves() > 1) {
			// 1 means the current state is the only one saved
			// remove the current state from autosaves
			autoSaveRepo.deleteLatestAutoSave();
			// load the previous state
			GameState loaded = autoSaveRepo.getLatestAutoSave();
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
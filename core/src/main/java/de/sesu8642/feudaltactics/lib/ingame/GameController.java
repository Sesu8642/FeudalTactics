// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.ingame;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Queue;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.events.GameStateChangeEvent;
import de.sesu8642.feudaltactics.ingame.AutoSaveRepository;
import de.sesu8642.feudaltactics.ingame.MapParameters;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.lib.gamestate.HexTile;
import de.sesu8642.feudaltactics.lib.gamestate.Kingdom;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;
import de.sesu8642.feudaltactics.lib.ingame.botai.BotAi;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

/** Controller for playing the game. */
public class GameController {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	/** All colors available to bots and user in the game. */
	public static final Color BLUE = new Color(0.2F, 0.45F, 0.8F, 1);
	public static final Color ORANGE = new Color(0.75F, 0.5F, 0F, 1);
	public static final Color PINK = new Color(1F, 0.67F, 0.67F, 1);
	public static final Color YELLOW = new Color(1F, 1F, 0F, 1);
	public static final Color WHITE = new Color(1F, 1F, 1F, 1);
	public static final Color GREEN = new Color(0F, 1F, 0F, 1);
	public static final Color[] COLOR_BANK = { BLUE, ORANGE, PINK, YELLOW, WHITE, GREEN };

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
		logger.info("starting game");
		// autosave before starting the AI thread, to avoid potential
		// ConcurrentModificationException
		autoSaveRepo.deleteAllAutoSaveExceptLatestN(0);
		autosave();
		// if a bot begins, make it act
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			startBotTurn();
		}
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	private void autosave() {
		autoSaveRepo.autoSaveGameState(gameState);
	}

	/** Loads the latest autosave. */
	public void loadLatestAutosave() {
		logger.info("loading latest autosave");
		gameState = autoSaveRepo.getLatestAutoSave();
		// posting the event must happen before starting the AI thread cause the data
		// for the renderer will be updated and the AI must not change the gamestate
		// while it is
		eventBus.post(new GameStateChangeEvent(gameState));
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			startBotTurn();
		}
	}

	/**
	 * Generates a map.
	 * 
	 * @param botIntelligence intelligence of the bot players
	 * @param mapParams       map generation parameters
	 */
	public void generateGameState(Intelligence botIntelligence, MapParameters mapParams) {
		logger.info("generating a new game state with bot intelligence {} and {}", botIntelligence, mapParams);
		gameState = new GameState();
		gameState.setBotIntelligence(botIntelligence);
		ArrayList<Player> players = new ArrayList<>();
		int remainingHumanPlayers = mapParams.getHumanPlayerNo();
		int remainingBotPlayers = mapParams.getBotPlayerNo();

		Color[] playerColors = determinePlayerColors(mapParams.getUserColor());

		for (Color color : playerColors) {
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
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/**
	 * Set the color order depending on the user color choice.
	 * 
	 * @param userColor The color the user has chosen for their kingdom.
	 * @return Array with the user color in the zero index.
	 */
	private Color[] determinePlayerColors(Color userColor) {
		Color[] colors = new Color[COLOR_BANK.length];
		Queue<Color> colorQueue = new Queue<>();

		for (Color color : COLOR_BANK) {
			if (color.equals(userColor)) {
				colorQueue.addFirst(color);
			} else {
				colorQueue.addLast(color);
			}
		}

		for (int i = 0; i < colorQueue.size; i++) {
			colors[i] = colorQueue.get(i);
		}

		return colors;
	}

	/**
	 * Prints debug info about a tile.
	 * 
	 * @param hexCoords coords of the tile
	 */
	public void printTileInfo(Vector2 hexCoords) {
		logger.debug("clicked: {}", gameState.getMap().get(hexCoords));
	}

	/**
	 * Activates a kingdom.
	 * 
	 * @param kingdom kingdom to activate
	 */
	public void activateKingdom(Kingdom kingdom) {
		logger.debug("activating {}", kingdom);
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
		logger.debug("picking up object from {}", tile);
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
		logger.debug("placing held object on own {}", tile);
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
		logger.debug("combining held unit with unit on {}", tile);
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
		logger.debug("conquering {}", tile);
		GameStateHelper.conquer(gameState, tile);
		autosave();
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/**
	 * Ends the turn.
	 */
	public void endTurn() {
		logger.debug("ending turn of {}", gameState.getActivePlayer());
		// update gameState
		gameState = GameStateHelper.endTurn(gameState);
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			// make bots act
			startBotTurn();
		} else {
			logger.info("human player turn begins");
			botAi.setSkipDisplayingTurn(false);
			autosave();
			// clear autosaves from previous turn
			autoSaveRepo.deleteAllAutoSaveExceptLatestN(1);
			eventBus.post(new GameStateChangeEvent(gameState));
		}
	}

	private void startBotTurn() {
		botTurnFuture = botTurnExecutor.submit(() -> {
			try {
				botAi.doTurn(gameState, gameState.getBotIntelligence());
			} catch (InterruptedException e) {
				logger.info("bot turn was canceled");
				Thread.currentThread().interrupt();
			} catch (Exception e) {
				logger.error("an error happened during the enemy turn", e);
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
		logger.debug("buying peasant");
		GameStateHelper.buyPeasant(gameState);
		autosave();
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/** Buys a castle. */
	public void buyCastle() {
		logger.debug("buying castle");
		GameStateHelper.buyCastle(gameState);
		autosave();
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/** Undoes the last action. */
	public void undoLastAction() {
		logger.debug("undoing last action");
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
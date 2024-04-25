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
		autoSaveRepo.autoSaveFullGameState(gameState);
		// if a bot begins, make it act
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			startBotTurn();
		}
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	/** Loads the latest autosave. */
	public void loadLatestAutosave() {
		logger.info("loading latest autosave");
		gameState = autoSaveRepo.getCombinedAutoSave();
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

	public void carryOutPlayerMove(PlayerMove move) {
		logger.debug("carrying out player move {}", move);
		switch (move.getPlayerActionType()) {
		// fall-through intentional
		case PICK_UP:
		case PLACE_OWN:
		case COMBINE_UNITS:
		case CONQUER:
		case BUY_PEASANT:
		case BUY_CASTLE:
		case BUY_AND_PLACE_PEASANT:
		case BUY_AND_PLACE_CASTLE:
		case ACTIVATE_KINGDOM:
			GameStateHelper.applyPlayerMove(gameState, move);
			autoSaveRepo.autoSaveIncrementalPlayerMove(move);
			// save first because is is relevant for the undo button status
			eventBus.post(new GameStateChangeEvent(gameState));
			break;
		case UNDO_LAST_MOVE:
			undoLastMove();
			break;
		case END_TURN:
			endTurn();
			break;
		default:
			throw new IllegalStateException("Unknown player move type " + move.getPlayerActionType());
		}

	}

	/**
	 * Ends the turn.
	 */
	void endTurn() {
		logger.debug("ending turn of {}", gameState.getActivePlayer());
		// update gameState
		gameState = GameStateHelper.endTurn(gameState);
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			// make bots act
			startBotTurn();
		} else {
			logger.info("human player turn begins");
			botAi.setSkipDisplayingTurn(false);
			autoSaveRepo.autoSaveFullGameState(gameState);
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

	/** Undoes the last action. */
	private void undoLastMove() {
		logger.debug("undoing last move");
		autoSaveRepo.deleteLatestIncrementalSave();
		GameState loaded = autoSaveRepo.getCombinedAutoSave();
		gameState = loaded;
		eventBus.post(new GameStateChangeEvent(gameState));
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

}
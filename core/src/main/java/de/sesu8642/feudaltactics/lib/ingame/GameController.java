// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.ingame;

import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.events.GameStateChangeEvent;
import de.sesu8642.feudaltactics.ingame.AutoSaveRepository;
import de.sesu8642.feudaltactics.ingame.GameParameters;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioGameStateLoader;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
import de.sesu8642.feudaltactics.lib.ingame.botai.BotAi;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 * Controller for playing the game.
 */
public class GameController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private final EventBus eventBus;
    private final ExecutorService botTurnExecutor;
    private final BotAi botAi;
    private final AutoSaveRepository autoSaveRepo;
    private final ScenarioGameStateLoader scenarioGameStateLoader;
    private Future<?> botTurnFuture;

    /**
     * State of the currently running game.
     */
    @Getter
    @Setter
    private GameState gameState;

    public GameController(EventBus eventBus, ExecutorService botTurnExecutor, BotAi botAi,
                          AutoSaveRepository autoSaveRepo, ScenarioGameStateLoader scenarioGameStateLoader) {
        this.eventBus = eventBus;
        this.botTurnExecutor = botTurnExecutor;
        this.botAi = botAi;
        this.autoSaveRepo = autoSaveRepo;
        this.scenarioGameStateLoader = scenarioGameStateLoader;
        gameState = new GameState();
    }

    /**
     * Starts the game. Bots will do their turns if they are first.
     */
    public void startGame() {
        logger.info("starting game");
        if (gameState.getScenarioMap() != ScenarioMap.NONE) {
            progressObjective();
            eventBus.post(new GameStateChangeEvent(gameState));
        }
        // autosave before starting the AI thread, to avoid potential
        // ConcurrentModificationException
        autoSaveRepo.autoSaveFullGameState(gameState);
        // if a bot begins, make it act
        if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
            startBotTurn();
        }
        eventBus.post(new GameStateChangeEvent(gameState));
    }

    /**
     * Loads the latest autosave.
     */
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
     */
    public void generateGameState(GameParameters gameParams) {
        logger.info("generating a new game state with {}", gameParams);
        gameState = new GameState();
        gameState.setBotIntelligence(gameParams.getBotIntelligence());

        GameStateHelper.initializeMap(gameState, gameParams.getPlayers(), gameParams.getLandMass(),
                gameParams.getDensity(), null, gameParams.getSeed());
        eventBus.post(new GameStateChangeEvent(gameState));
    }

    /**
     * Loads a scenario map.
     *
     * @param botIntelligence intelligence of the bot players
     * @param scenarioMap     map to load
     */
    public void initializeScenario(Intelligence botIntelligence, ScenarioMap scenarioMap) {
        logger.info("initializing a game state with bot intelligence {} and scenario map {}", botIntelligence,
                scenarioMap);

        gameState = scenarioGameStateLoader.loadScenarioGameState(scenarioMap);

        gameState.setBotIntelligence(botIntelligence);
        gameState.setScenarioMap(scenarioMap);
        eventBus.post(new GameStateChangeEvent(gameState));
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
     * Carries out the given move.
     */
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

    /**
     * Cancels a bot turn by canceling the future.
     */
    public void cancelBotTurn() {
        if (botTurnFuture != null) {
            botTurnFuture.cancel(true);
        }
    }

    /**
     * Skips a bot turn by finishing it instantly.
     */
    public void skipBotTurn() {
        botAi.setSkipDisplayingTurn(true);
    }

    /**
     * Undoes the last action.
     */
    private void undoLastMove() {
        logger.debug("undoing last move");
        autoSaveRepo.deleteLatestIncrementalSave();
        GameState loaded = autoSaveRepo.getCombinedAutoSave();
        gameState = loaded;
        eventBus.post(new GameStateChangeEvent(gameState));
    }

    /**
     * Progresses the current objective.
     */
    public void progressObjective() {
        gameState.setObjectiveProgress(gameState.getObjectiveProgress() + 1);
        eventBus.post(new GameStateChangeEvent(gameState));
    }

}
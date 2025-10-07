// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.ingame;

import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.events.*;
import de.sesu8642.feudaltactics.ingame.AutoSaveRepository;
import de.sesu8642.feudaltactics.lib.ingame.botai.BotAi;

import javax.inject.Inject;

/**
 * Handles events (except player inputs).
 **/
public class GameControllerEventHandler {

    private final GameController gameController;
    private final BotAi botAi;
    private final AutoSaveRepository autoSaveRepo;

    /**
     * Constructor.
     *
     * @param gameController game controller
     */
    @Inject
    public GameControllerEventHandler(GameController gameController, BotAi botAi, AutoSaveRepository autoSaveRepo) {
        this.gameController = gameController;
        this.botAi = botAi;
        this.autoSaveRepo = autoSaveRepo;
    }

    /**
     * Event handler for game exited events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleGameExited(GameExitedEvent event) {
        gameController.cancelBotTurn();
        autoSaveRepo.deleteAllAutoSaves();
    }

    /**
     * Event handler for game resumed events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleGameResumed(GameResumedEvent event) {
        gameController.loadLatestAutosave();
    }

    /**
     * Event handler for finished bot turn events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleBotTurnFinished(BotTurnFinishedEvent event) {
        gameController.setGameState(event.getGameState());
        gameController.endTurn();
    }

    /**
     * Event handler for bot speed change events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleBotTurnSpeedChanged(BotTurnSpeedChangedEvent event) {
        botAi.setCurrentSpeed(event.getSpeed());
    }

    /**
     * Event handler for bot turn skip events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleBotTurnSkipped(BotTurnSkippedEvent event) {
        botAi.setSkipDisplayingTurn(true);
    }

    /**
     * Event handler for scenario initialization events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleInitializeScenario(InitializeScenarioEvent event) {
        gameController.initializeScenario(event.getBotIntelligence(), event.getScenarioMap());
    }

}

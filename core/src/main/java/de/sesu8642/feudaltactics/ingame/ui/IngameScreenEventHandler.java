// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import javax.inject.Inject;

import com.badlogic.gdx.Gdx;
import com.google.common.eventbus.Subscribe;

import de.sesu8642.feudaltactics.events.CenterMapUIEvent;
import de.sesu8642.feudaltactics.events.CloseMenuEvent;
import de.sesu8642.feudaltactics.events.EndTurnUnconfirmedEvent;
import de.sesu8642.feudaltactics.events.ExitGameEvent;
import de.sesu8642.feudaltactics.events.GameResumedEvent;
import de.sesu8642.feudaltactics.events.GameStateChangeEvent;
import de.sesu8642.feudaltactics.events.OpenMenuEvent;
import de.sesu8642.feudaltactics.events.RetryGameUnconfirmedEvent;
import de.sesu8642.feudaltactics.events.input.EscInputEvent;
import de.sesu8642.feudaltactics.events.moves.GameStartEvent;
import de.sesu8642.feudaltactics.ingame.ui.IngameScreen.IngameStages;

/** Handles events for the ingame screen. **/
public class IngameScreenEventHandler {

	private IngameScreen ingameScreen;

	/**
	 * Constructor.
	 * 
	 * @param ingameScreen ingame screen
	 */
	@Inject
	public IngameScreenEventHandler(IngameScreen ingameScreen) {
		this.ingameScreen = ingameScreen;
	}

	/**
	 * Event handler for ESC key events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleEscInput(EscInputEvent event) {
		ingameScreen.togglePause();
	}

	/**
	 * Event handler for end turn attempt events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleEndTurnAttempt(EndTurnUnconfirmedEvent event) {
		ingameScreen.handleEndTurnAttempt();
	}

	/**
	 * Event handler for open menu events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleOpenMenuAttempt(OpenMenuEvent event) {
		ingameScreen.activateStage(IngameStages.MENU);
	}

	/**
	 * Event handler for close menu events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleCloseMenuAttempt(CloseMenuEvent event) {
		ingameScreen.activateStage(IngameStages.HUD);
	}

	/**
	 * Event handler for game start events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleGameStart(GameStartEvent event) {
		ingameScreen.activateStage(IngameStages.HUD);
	}

	/**
	 * Event handler for game resumed events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleGameResumed(GameResumedEvent event) {
		// need to do this in postRunnable
		// the ingame screen is set in postRunnable as well and in
		// IngameScreen#show, the parameter input stage is activated which MUST happen
		// before this, otherwise the wrong state will be shown
		Gdx.app.postRunnable(() -> {
			ingameScreen.activateStage(IngameStages.HUD);
		});
	}

	/**
	 * Event handler for unconfirmed retry game events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleUnconfirmedRetryGame(RetryGameUnconfirmedEvent event) {
		ingameScreen.handleUnconfirmedRetryGame();
	}

	/**
	 * Event handler for exit game events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleExitGameAttempt(ExitGameEvent event) {
		ingameScreen.handleExitGameAttempt();
	}

	/**
	 * Event handler for gameState changes.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleGameStateChange(GameStateChangeEvent event) {
		ingameScreen.handleGameStateChange(event.getGameState());
	}

	/**
	 * Event handler for gameState changes.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleMapCenteringUiEvent(CenterMapUIEvent event) {
		ingameScreen.centerMap();
	}

}

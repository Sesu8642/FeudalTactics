// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ui.screens;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.sesu8642.feudaltactics.dagger.qualifierannotations.IngameCamera;
import de.sesu8642.feudaltactics.dagger.qualifierannotations.IngameRenderer;
import de.sesu8642.feudaltactics.dagger.qualifierannotations.MenuCamera;
import de.sesu8642.feudaltactics.dagger.qualifierannotations.MenuViewport;
import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.events.GameResumedEvent;
import de.sesu8642.feudaltactics.events.GameStateChangeEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.events.input.EscInputEvent;
import de.sesu8642.feudaltactics.events.moves.EndTurnEvent;
import de.sesu8642.feudaltactics.events.moves.GameStartEvent;
import de.sesu8642.feudaltactics.events.moves.RegenerateMapUiEvent;
import de.sesu8642.feudaltactics.gamelogic.MapParameters;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Castle;
import de.sesu8642.feudaltactics.gamelogic.gamestate.GameState;
import de.sesu8642.feudaltactics.gamelogic.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Kingdom;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Player.Type;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Unit;
import de.sesu8642.feudaltactics.input.CombinedInputProcessor;
import de.sesu8642.feudaltactics.input.InputValidationHelper;
import de.sesu8642.feudaltactics.preferences.PreferencesHelper;
import de.sesu8642.feudaltactics.renderer.MapRenderer;
import de.sesu8642.feudaltactics.ui.DialogFactory;
import de.sesu8642.feudaltactics.ui.Margin;
import de.sesu8642.feudaltactics.ui.events.CloseMenuEvent;
import de.sesu8642.feudaltactics.ui.events.EndTurnUnconfirmedUiEvent;
import de.sesu8642.feudaltactics.ui.events.ExitGameUiEvent;
import de.sesu8642.feudaltactics.ui.events.OpenMenuUiEvent;
import de.sesu8642.feudaltactics.ui.events.RetryGameUnconfirmedUiEvent;
import de.sesu8642.feudaltactics.ui.stages.HudStage;
import de.sesu8642.feudaltactics.ui.stages.MenuStage;
import de.sesu8642.feudaltactics.ui.stages.ParameterInputStage;

/** {@link Screen} for playing a map. */
@Singleton
public class IngameScreen extends GameScreen {

	private OrthographicCamera ingameCamera;

	private MapRenderer mapRenderer;
	private InputMultiplexer inputMultiplexer;
	private EventBus eventBus;
	private CombinedInputProcessor inputProcessor;

	private ParameterInputStage parameterInputStage;
	private HudStage hudStage;
	private MenuStage menuStage;

	private DialogFactory dialogFactory;

	/** Cached version of the game state from the game controller. */
	private GameState cachedGameState;

	/** Stages that can be displayed. */
	public enum IngameStages {
		PARAMETERS, HUD, MENU
	}

	private static final long BUTTON_HEIGHT_PX = 110;
	private static final long INPUT_HEIGHT_PX = 79;
	private static final long INPUT_WIDTH_PX = 419;

	/**
	 * Constructor.
	 * 
	 * @param ingameCamera         camera for viewing the map
	 * @param viewport             viewport for the menus
	 * @param menuCamera           camera for the menus
	 * @param mapRenderer          renderer for the map
	 * @param confirmDialogFactory factory for creating confirm dialogs
	 * @param eventBus             event bus
	 * @param inputProcessor       input processor for user inputs that is added to
	 *                             the input multiplexer
	 * @param inputMultiplexer     input multiplexer that stages are added to as
	 *                             processors
	 * @param hudStage             stage for heads up display UI
	 * @param menuStage            stage for the pause menu UI
	 * @param parameterInputStage  stage for the new game parameter input UI
	 */
	@Inject
	public IngameScreen(@IngameCamera OrthographicCamera ingameCamera, @MenuViewport Viewport viewport,
			@MenuCamera OrthographicCamera menuCamera, @IngameRenderer MapRenderer mapRenderer,
			DialogFactory confirmDialogFactory, EventBus eventBus, CombinedInputProcessor inputProcessor,
			InputMultiplexer inputMultiplexer, HudStage hudStage, MenuStage menuStage,
			ParameterInputStage parameterInputStage) {
		super(ingameCamera, viewport, hudStage);
		this.ingameCamera = ingameCamera;
		this.mapRenderer = mapRenderer;
		this.dialogFactory = confirmDialogFactory;
		this.inputMultiplexer = inputMultiplexer;
		this.eventBus = eventBus;
		this.inputProcessor = inputProcessor;
		this.hudStage = hudStage;
		this.menuStage = menuStage;
		this.parameterInputStage = parameterInputStage;
	}

	/**
	 * Event handler for ESC key events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleEscInput(EscInputEvent event) {
		togglePause();
	}

	/**
	 * Event handler for end turn attempt events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleEndTurnAttempt(EndTurnUnconfirmedUiEvent event) {
		if (GameStateHelper.hasActivePlayerlikelyForgottenKingom(cachedGameState)) {
			Dialog confirmDialog = dialogFactory.createConfirmDialog(
					"You might have forgotten to do your moves for a kingdom.\nAre you sure you want to end your turn?\n",
					() -> eventBus.post(new EndTurnEvent()));
			confirmDialog.show(hudStage);
		} else {
			eventBus.post(new EndTurnEvent());
		}
	}

	/**
	 * Event handler for open menu events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleOpenMenuAttempt(OpenMenuUiEvent event) {
		activateStage(IngameStages.MENU);
	}

	/**
	 * Event handler for close menu events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleCloseMenuAttempt(CloseMenuEvent event) {
		activateStage(IngameStages.HUD);
	}

	/**
	 * Event handler for unconfirmed retry game events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleUnconfirmedRetryGame(RetryGameUnconfirmedUiEvent event) {
		Dialog confirmDialog = dialogFactory.createConfirmDialog("Your progress will be lost. Are you sure?\n", () -> {
			resetGame();
		});
		confirmDialog.show(menuStage);
	}

	private void resetGame() {
		eventBus.post(new GameExitedEvent());
		eventBus.post(new RegenerateMapUiEvent(parameterInputStage.getBotIntelligence(),
				new MapParameters(parameterInputStage.getSeedParam(), parameterInputStage.getMapSizeParam(),
						parameterInputStage.getMapDensityParam())));
		activateStage(IngameStages.PARAMETERS);
	}

	/**
	 * Event handler for exit game events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleExitGameAttempt(ExitGameUiEvent event) {
		Dialog confirmDialog = dialogFactory.createConfirmDialog("Your progress will be lost. Are you sure?\n", () -> {
			PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
			eventBus.post(new GameExitedEvent());
			eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.MAIN_MENU_SCREEN));
		});
		confirmDialog.show(menuStage);
	}

	/**
	 * Event handler for game start events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleGameStart(GameStartEvent event) {
		activateStage(IngameStages.HUD);
	}

	/**
	 * Event handler for gameState change. Adjusts all the UI elements that need to
	 * be adjusted and displays dialogs if appropriate.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleGameStateChange(GameStateChangeEvent event) {
		// cache the new state
		cachedGameState = event.getGameState();
		// update the UI when there is a gameState change
		GameState newGameState = event.getGameState();
		// hand content
		if (newGameState.getHeldObject() != null) {
			hudStage.updateHandContent(newGameState.getHeldObject().getSpriteName());
		} else {
			hudStage.updateHandContent(null);
		}
		// info text
		Kingdom kingdom = newGameState.getActiveKingdom();
		if (kingdom == null) {
			hudStage.setInfoText("");
		} else {
			int income = GameStateHelper.getKingdomIncome(kingdom);
			int salaries = GameStateHelper.getKingdomSalaries(newGameState, kingdom);
			int result = income - salaries;
			int savings = kingdom.getSavings();
			String resultText = result < 0 ? String.valueOf(result) : "+" + result;
			String infoText = "Savings: " + savings + " (" + resultText + ")";
			hudStage.setInfoText(infoText);
		}
		// seed
		menuStage.setBottomRightLabelText("Seed: " + newGameState.getSeed().toString());
		// buttons
		boolean canUndo = InputValidationHelper.checkUndoAction(newGameState,
				GameStateHelper.determineActingLocalPlayer(newGameState));
		boolean canBuyPeasant = InputValidationHelper.checkBuyObject(newGameState,
				GameStateHelper.determineActingLocalPlayer(newGameState), Unit.COST);
		boolean canBuyCastle = InputValidationHelper.checkBuyObject(newGameState,
				GameStateHelper.determineActingLocalPlayer(newGameState), Castle.COST);
		boolean canEndTurn = InputValidationHelper.checkEndTurn(newGameState,
				GameStateHelper.determineActingLocalPlayer(newGameState));
		hudStage.setButtonEnabledStatus(canUndo, canBuyPeasant, canBuyCastle, canEndTurn);
		// display messages
		// check if player lost
		if (newGameState.getActivePlayer().getType() == Type.LOCAL_PLAYER
				&& newGameState.getActivePlayer().isDefeated()) {
			showLostMessage();
		} else {
			// check if winner changed
			if (event.isWinnerChanged()) {
				showGiveUpGameMessage(newGameState.getWinner().getType() == Type.LOCAL_PLAYER,
						newGameState.getWinner().getColor());
			}
		}
		if (event.isMapDimensionsChanged()) {
			// dimensions changed means that the seed also changed
			parameterInputStage.updateSeed(event.getGameState().getSeed());
		}
	}

	/**
	 * Event handler for game resumed events.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleGameResumed(GameResumedEvent event) {
		activateStage(IngameStages.HUD);
	}

	/** Toggles the pause menu. */
	public void togglePause() {
		if (getActiveStage() == menuStage) {
			activateStage(IngameStages.HUD);
		} else if (getActiveStage() == hudStage) {
			activateStage(IngameStages.MENU);
		}
	}

	/**
	 * Calculates where the map should be placed to have the most room while not
	 * being behind the UI elements.
	 * 
	 * @return Vector of margin to the left and margin to the bottom where the map
	 *         should not be rendered. The rest of the screen can be used.
	 */
	public Margin calculateMapScreenArea() {
		// calculate what is the bigger rectangular area for the map to fit: above the
		// inputs or to their right
		float aboveArea = ingameCamera.viewportWidth
				* (ingameCamera.viewportHeight - BUTTON_HEIGHT_PX - ParameterInputStage.NO_OF_INPUTS * INPUT_HEIGHT_PX);
		float rightArea = (ingameCamera.viewportWidth - INPUT_WIDTH_PX)
				* (ingameCamera.viewportHeight - BUTTON_HEIGHT_PX);
		if (aboveArea > rightArea) {
			return new Margin(0, BUTTON_HEIGHT_PX + ParameterInputStage.NO_OF_INPUTS * INPUT_HEIGHT_PX, 0, 0);
		} else {
			return new Margin(INPUT_WIDTH_PX, BUTTON_HEIGHT_PX, 0, 0);
		}
	}

	private void showGiveUpGameMessage(boolean win, Color winningPlayerColor) {
		// TODO: make this nicer and display the color of the winning player
		Dialog endDialog = dialogFactory.createDialog(result -> {
			switch ((byte) result) {
			case 1:
				// exit button
				eventBus.post(new GameExitedEvent());
				eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.MAIN_MENU_SCREEN));
				PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
				break;
			case 2:
				// retry button
				resetGame();
				break;
			case 0:
				// do nothing on continue button
			default:
				break;
			}
		});
		endDialog.button("Exit", (byte) 1);
		if (win) {
			endDialog.text("VICTORY! Your Enemies give up.\n\nDo you wish to continue?");
			endDialog.button("Replay", (byte) 2);
		} else {
			endDialog.text("Your Enemy conquered a majority of the territory.\n\nDo you wish to continue?");
			endDialog.button("Retry", (byte) 2);
		}
		endDialog.button("Continue", (byte) 0);
		endDialog.show(hudStage);
	}

	private void showLostMessage() {
		Dialog endDialog = dialogFactory.createDialog(result -> {
			if ((boolean) result) {
				eventBus.post(new GameExitedEvent());
				eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.MAIN_MENU_SCREEN));
				PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
			} else {
				resetGame();
			}
		});
		endDialog.button("Exit", true);
		endDialog.button("Retry", false);
		endDialog.text("DEFEAT! All of your kingdoms were conquered by the enemy.");
		endDialog.show(hudStage);
	}

	private void activateStage(IngameStages ingameStage) {
		inputMultiplexer.clear();
		switch (ingameStage) {
		case MENU:
			inputMultiplexer.addProcessor(menuStage);
			inputMultiplexer.addProcessor(inputProcessor);
			setActiveStage(menuStage);
			break;
		case HUD:
			inputMultiplexer.addProcessor(hudStage);
			inputMultiplexer.addProcessor(new GestureDetector(inputProcessor));
			inputMultiplexer.addProcessor(inputProcessor);
			setActiveStage(hudStage);
			break;
		case PARAMETERS:
			inputMultiplexer.addProcessor(parameterInputStage);
			inputMultiplexer.addProcessor(new GestureDetector(inputProcessor));
			inputMultiplexer.addProcessor(inputProcessor);
			setActiveStage(parameterInputStage);
			break;
		default:
			throw new IllegalStateException("Unknown stage " + ingameStage);
		}
		// the super class only applies the resizing to the active stage
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		activateStage(IngameStages.PARAMETERS);
	}

	@Override
	public void render(float delta) {
		getViewport().apply();
		mapRenderer.render();
		ingameCamera.update();
		getActiveStage().draw();
		getActiveStage().act();
	}

	@Override
	public void dispose() {
		mapRenderer.dispose();
		parameterInputStage.dispose();
		hudStage.dispose();
		menuStage.dispose();
		// might try to dispose the same stage twice
		super.dispose();
	}

	public OrthographicCamera getCamera() {
		return ingameCamera;
	}

	public HudStage getHudStage() {
		return hudStage;
	}

	public MenuStage getMenuStage() {
		return menuStage;
	}

}

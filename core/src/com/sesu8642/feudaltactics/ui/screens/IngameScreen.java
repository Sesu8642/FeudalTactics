package com.sesu8642.feudaltactics.ui.screens;

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
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.MapRenderer;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.IngameCamera;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.IngameRenderer;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.MainMenuScreen;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.MenuCamera;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.MenuViewport;
import com.sesu8642.feudaltactics.events.EscInputEvent;
import com.sesu8642.feudaltactics.events.GameStateChangeEvent;
import com.sesu8642.feudaltactics.gamelogic.GameController;
import com.sesu8642.feudaltactics.gamelogic.LocalIngameInputHandler;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Castle;
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameState;
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameStateHelper;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Player.Type;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Unit;
import com.sesu8642.feudaltactics.input.CombinedInputProcessor;
import com.sesu8642.feudaltactics.input.InputValidationHelper;
import com.sesu8642.feudaltactics.preferences.NewGamePreferences;
import com.sesu8642.feudaltactics.preferences.PreferencesHelper;
import com.sesu8642.feudaltactics.ui.DialogFactory;
import com.sesu8642.feudaltactics.ui.stages.HudStage;
import com.sesu8642.feudaltactics.ui.stages.MenuStage;
import com.sesu8642.feudaltactics.ui.stages.ParameterInputStage;
import com.sesu8642.feudaltactics.ui.stages.ParameterInputStage.EventTypes;

/** {@link Screen} for playing a map. */
@Singleton
public class IngameScreen extends GameScreen {

	private OrthographicCamera ingameCamera;

	private MapRenderer mapRenderer;
	private InputMultiplexer inputMultiplexer;
	private EventBus eventBus;
	private CombinedInputProcessor inputProcessor;
	private LocalIngameInputHandler inputHandler;
	private GameController gameController;

	private ParameterInputStage parameterInputStage;
	private HudStage hudStage;
	private MenuStage menuStage;

	private Screen mainMenuScreen;
	private DialogFactory dialogFactory;

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
	 * @param mainMenuScreen       main menu screen that can be returned to
	 * @param confirmDialogFactory factory for creating confirm dialogs
	 * @param gameController       controller for playing the game
	 * @param eventBus             event bus
	 * @param inputProcessor       input processor for user inputs that is added to
	 *                             the input multiplexer
	 * @param inputMultiplexer     input multiplexer that stages are added to as
	 *                             processors
	 * @param inputHandler         input handler to be registered to the event bus
	 * @param hudStage             stage for heads up display UI
	 * @param menuStage            stage for the pause menu UI
	 * @param parameterInputStage  stage for the new game parameter input UI
	 */
	@Inject
	public IngameScreen(@IngameCamera OrthographicCamera ingameCamera, @MenuViewport Viewport viewport,
			@MenuCamera OrthographicCamera menuCamera, @IngameRenderer MapRenderer mapRenderer,
			@MainMenuScreen GameScreen mainMenuScreen, DialogFactory confirmDialogFactory,
			GameController gameController, EventBus eventBus, CombinedInputProcessor inputProcessor,
			InputMultiplexer inputMultiplexer, LocalIngameInputHandler inputHandler, HudStage hudStage,
			MenuStage menuStage, ParameterInputStage parameterInputStage) {
		super(ingameCamera, viewport, hudStage);
		this.gameController = gameController;
		this.ingameCamera = ingameCamera;
		this.mapRenderer = mapRenderer;
		this.mainMenuScreen = mainMenuScreen;
		this.dialogFactory = confirmDialogFactory;
		this.inputMultiplexer = inputMultiplexer;
		this.eventBus = eventBus;
		this.inputProcessor = inputProcessor;
		this.inputHandler = inputHandler;
		this.hudStage = hudStage;
		this.menuStage = menuStage;
		this.parameterInputStage = parameterInputStage;
		registerEventListeners();
	}

	private void registerEventListeners() {
		// parameter input stage
		parameterInputStage.registerEventListener(EventTypes.PLAY, () -> {
			activateStage(IngameStages.HUD);
			gameController.startGame();
		});
		parameterInputStage.registerEventListener(EventTypes.REGEN, () -> {
			gameController.generateMap(1, 5, parameterInputStage.getBotIntelligenceParam(),
					parameterInputStage.getSeedParam(), parameterInputStage.getMapSizeParam(),
					parameterInputStage.getMapDensityParam());
			// place the camera for full map view
			// calculate what is the bigger rectangular area for the map to fit: above the
			// inputs or to their right
			float aboveArea = ingameCamera.viewportWidth * (ingameCamera.viewportHeight - BUTTON_HEIGHT_PX
					- ParameterInputStage.NO_OF_INPUTS * INPUT_HEIGHT_PX);
			float rightArea = (ingameCamera.viewportWidth - INPUT_WIDTH_PX)
					* (ingameCamera.viewportHeight - BUTTON_HEIGHT_PX);
			if (aboveArea > rightArea) {
				gameController.placeCameraForFullMapView(0,
						BUTTON_HEIGHT_PX + ParameterInputStage.NO_OF_INPUTS * INPUT_HEIGHT_PX, 0, 0);
			} else {
				gameController.placeCameraForFullMapView(INPUT_WIDTH_PX, BUTTON_HEIGHT_PX, 0, 0);
			}
		});
		parameterInputStage.registerEventListener(EventTypes.CHANGE,
				() -> PreferencesHelper
						.saveNewGamePreferences(new NewGamePreferences(parameterInputStage.getBotIntelligence(),
								parameterInputStage.getMapSize(), parameterInputStage.getMapDensity())));

		// hud stage
		hudStage.registerEventListener(HudStage.EventTypes.UNDO, () -> gameController.undoLastAction());
		hudStage.registerEventListener(HudStage.EventTypes.BUY_PEASANT, () -> gameController.buyPeasant());
		hudStage.registerEventListener(HudStage.EventTypes.BUY_CASTLE, () -> gameController.buyCastle());
		hudStage.registerEventListener(HudStage.EventTypes.END_TURN, () -> {
			if (GameStateHelper.hasActivePlayerlikelyForgottenKingom(gameController.getGameState())) {
				Dialog confirmDialog = dialogFactory.createConfirmDialog(
						"You might have forgotten to do your moves for a kingdom.\nAre you sure you want to end your turn?\n",
						() -> gameController.endTurn());
				confirmDialog.show(hudStage);
			} else {
				gameController.endTurn();
			}
		});
		hudStage.registerEventListener(HudStage.EventTypes.MENU, () -> activateStage(IngameStages.MENU));

		// menu stage
		menuStage.addButton("Exit", () -> {
			Dialog confirmDialog = dialogFactory.createConfirmDialog("Your progress will be lost. Are you sure?\n",
					() -> {
						PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
						FeudalTactics.game.setScreen(mainMenuScreen);
					});
			confirmDialog.show(menuStage);
		});
		menuStage.addButton("Retry", () -> {
			Dialog confirmDialog = dialogFactory.createConfirmDialog("Your progress will be lost. Are you sure?\n",
					() -> {
						parameterInputStage.regenerateMap(gameController.getGameState().getSeed());
						activateStage(IngameStages.PARAMETERS);
					});
			confirmDialog.show(menuStage);
		});
		menuStage.addButton("Continue", () -> activateStage(IngameStages.HUD));
	}

	@Subscribe
	public void handleEscInput(EscInputEvent event) {
		togglePause();
	}

	/**
	 * Event handler for gameState change. Adjusts all the UI elements that need to
	 * be adjusted and displays dialogs if appropriate.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleGameStateChange(GameStateChangeEvent event) {
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
			int income = kingdom.getIncome();
			int salaries = kingdom.getSalaries();
			int result = income - salaries;
			int savings = kingdom.getSavings();
			String resultText = result < 0 ? String.valueOf(result) : "+" + result;
			String infoText = "Savings: " + savings + " (" + resultText + ")";
			hudStage.setInfoText(infoText);
		}
		// seed
		menuStage.setBottomLabelText("Seed: " + newGameState.getSeed().toString());
		// buttons
		boolean canUndo = InputValidationHelper.checkUndoAction();
		boolean canBuyPeasant = InputValidationHelper.checkBuyObject(newGameState, Unit.COST);
		boolean canBuyCastle = InputValidationHelper.checkBuyObject(newGameState, Castle.COST);
		boolean canEndTurn = InputValidationHelper.checkEndTurn(newGameState);
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
	}

	/** Loads the latest autosave and centers the camera. */
	public void loadAutoSave() {
		activateStage(IngameStages.HUD);
		gameController.loadLatestAutosave();
		gameController.placeCameraForFullMapView(0, 0, 0, 0);
	}

	/** Toggles the pause menu. */
	public void togglePause() {
		if (getActiveStage() == menuStage) {
			activateStage(IngameStages.HUD);
		} else if (getActiveStage() == hudStage) {
			activateStage(IngameStages.MENU);
		}
	}

	private void showGiveUpGameMessage(boolean win, Color winningPlayerColor) {
		// TODO: make this nicer and display the color of the winning player
		Dialog endDialog = dialogFactory.createDialog(result -> {
			switch ((byte) result) {
			case 1:
				// exit button
				FeudalTactics.game.setScreen(mainMenuScreen);
				PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
				break;
			case 2:
				// retry button
				parameterInputStage.regenerateMap(gameController.getGameState().getSeed());
				activateStage(IngameStages.PARAMETERS);
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
				FeudalTactics.game.setScreen(mainMenuScreen);
				PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
			} else {
				parameterInputStage.regenerateMap(gameController.getGameState().getSeed());
				activateStage(IngameStages.PARAMETERS);
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
		eventBus.register(this);
		eventBus.register(inputHandler);
		activateStage(IngameStages.PARAMETERS);
		parameterInputStage.regenerateMap(System.currentTimeMillis());
	}

	@Override
	public void hide() {
		eventBus.unregister(this);
		eventBus.unregister(inputHandler);
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

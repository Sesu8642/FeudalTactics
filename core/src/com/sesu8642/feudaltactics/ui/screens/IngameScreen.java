package com.sesu8642.feudaltactics.ui.screens;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Optional;

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
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.GameController;
import com.sesu8642.feudaltactics.MapRenderer;
import com.sesu8642.feudaltactics.dagger.IngameCamera;
import com.sesu8642.feudaltactics.dagger.IngameInputProcessor;
import com.sesu8642.feudaltactics.dagger.IngameRenderer;
import com.sesu8642.feudaltactics.dagger.MainMenuScreen;
import com.sesu8642.feudaltactics.dagger.MenuCamera;
import com.sesu8642.feudaltactics.dagger.MenuViewport;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.GameStateHelper;
import com.sesu8642.feudaltactics.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamestate.Player.Type;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Castle;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;
import com.sesu8642.feudaltactics.input.CombinedInputProcessor;
import com.sesu8642.feudaltactics.input.InputValidationHelper;
import com.sesu8642.feudaltactics.preferences.NewGamePreferences;
import com.sesu8642.feudaltactics.preferences.PreferencesHelper;
import com.sesu8642.feudaltactics.ui.DialogFactory;
import com.sesu8642.feudaltactics.ui.stages.MenuStage;
import com.sesu8642.feudaltactics.ui.stages.HudStage;
import com.sesu8642.feudaltactics.ui.stages.ParameterInputStage;
import com.sesu8642.feudaltactics.ui.stages.ParameterInputStage.EventTypes;

@Singleton
public class IngameScreen extends GameScreen implements PropertyChangeListener {

	private OrthographicCamera ingameCamera;

	private MapRenderer mapRenderer;
	private InputMultiplexer inputMultiplexer;
	private CombinedInputProcessor inputProcessor;
	private GameController gameController;

	private ParameterInputStage parameterInputStage;
	private HudStage hudStage;
	private MenuStage menuStage;

	private Screen mainMenuScreen;
	private DialogFactory dialogFactory;

	public enum IngameStages {
		PARAMETERS, HUD, MENU
	}

	private static final long BUTTON_HEIGHT_PX = 110;
	private static final long INPUT_HEIGHT_PX = 79;
	private static final long INPUT_WIDTH_PX = 419;

	@Inject
	public IngameScreen(@IngameCamera OrthographicCamera ingameCamera, @MenuViewport Viewport viewport,
			@MenuCamera OrthographicCamera menuCamera, @IngameRenderer MapRenderer mapRenderer,
			@MainMenuScreen GameScreen mainMenuScreen, DialogFactory confirmDialogFactory,
			GameController gameController, @IngameInputProcessor CombinedInputProcessor inputProcessor,
			InputMultiplexer inputMultiplexer, HudStage hudStage, MenuStage menuStage,
			ParameterInputStage parameterInputStage) {
		super(ingameCamera, viewport, hudStage);
		this.gameController = gameController;
		this.ingameCamera = ingameCamera;
		this.mapRenderer = mapRenderer;
		this.mainMenuScreen = mainMenuScreen;
		this.dialogFactory = confirmDialogFactory;
		this.inputMultiplexer = inputMultiplexer;
		this.inputProcessor = inputProcessor;
		this.hudStage = hudStage;
		this.menuStage = menuStage;
		this.parameterInputStage = parameterInputStage;
		gameController.addPropertyChangeListener(GameController.GAME_STATE_OBSERVABLE_PROPERTY_NAME, this);
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
			if (GameStateHelper.hasActivePlayerlikelyForgottenAKingom(gameController.getGameState())) {
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

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(GameController.GAME_STATE_OBSERVABLE_PROPERTY_NAME)) {
			// update the UI when there is a gameState change
			Optional<GameState> optionalOldGameState = Optional.ofNullable((GameState) evt.getOldValue());
			GameState newGameState = (GameState) evt.getNewValue();
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
				optionalOldGameState.ifPresent(oldGameState -> {
					if (oldGameState.getWinner() != newGameState.getWinner()) {
						showGiveUpGameMessage(newGameState.getWinner().getType() == Type.LOCAL_PLAYER,
								newGameState.getWinner().getColor());
					}
				});
			}
		}
	}

	public void loadAutoSave() {
		activateStage(IngameStages.HUD);
		gameController.loadLatestAutosave();
		gameController.placeCameraForFullMapView(0, 0, 0, 0);
	}

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
			break;
		}
		// the super class only applies the resizing to the active stage
		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		activateStage(IngameStages.PARAMETERS);
		parameterInputStage.regenerateMap(System.currentTimeMillis());
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

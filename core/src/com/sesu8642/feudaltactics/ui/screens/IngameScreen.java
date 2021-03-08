package com.sesu8642.feudaltactics.ui.screens;

import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.GameController;
import com.sesu8642.feudaltactics.MapRenderer;
import com.sesu8642.feudaltactics.gamestate.GameStateHelper;
import com.sesu8642.feudaltactics.input.CombinedInputProcessor;
import com.sesu8642.feudaltactics.input.LocalInputHandler;
import com.sesu8642.feudaltactics.preferences.NewGamePreferences;
import com.sesu8642.feudaltactics.preferences.PreferencesHelper;
import com.sesu8642.feudaltactics.ui.ConfirmDialog;
import com.sesu8642.feudaltactics.ui.FeudalTacticsDialog;
import com.sesu8642.feudaltactics.ui.stages.GenericMenuStage;
import com.sesu8642.feudaltactics.ui.stages.HudStage;
import com.sesu8642.feudaltactics.ui.stages.ParameterInputStage;
import com.sesu8642.feudaltactics.ui.stages.HudStage.ActionUIElements;

public class IngameScreen implements Screen {

	private OrthographicCamera camera;
	private MapRenderer mapRenderer;
	private InputMultiplexer multiplexer;
	private LocalInputHandler inputHandler;
	private CombinedInputProcessor inputProcessor;
	GameController gameController;

	private ParameterInputStage parameterInputStage;
	private HudStage hudStage;
	private GenericMenuStage menuStage;
	private Stage activeStage;
	private Viewport viewport;

	public enum IngameStages {
		PARAMETERS, HUD, MENU
	}

	private static final long BUTTON_HEIGHT_PX = 110;
	private static final long INPUT_HEIGHT_PX = 79;
	private static final long INPUT_WIDTH_PX = 419;

	public IngameScreen(boolean loadAutoSave) {
		gameController = new GameController();
		inputHandler = new LocalInputHandler(gameController);
		gameController.setIngameScreen(this);
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		mapRenderer = new MapRenderer(camera);
		gameController.setMapRenderer(mapRenderer);
		inputProcessor = new CombinedInputProcessor(inputHandler, camera);
		multiplexer = new InputMultiplexer();
		initUI();
		if (loadAutoSave) {
			activateStage(IngameStages.HUD);
			gameController.loadLatestAutosave();
			gameController.placeCameraForFullMapView(0, 0, 0, 0);
		} else {
			activateStage(IngameStages.PARAMETERS);
			parameterInputStage.regenerateMap(null);
		}
	}

	public IngameScreen() {
		this(false);
	}

	private void initUI() {
		Camera camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);

		// parameter input
		Map<ParameterInputStage.ActionUIElements, Runnable> paramActions = new LinkedHashMap<ParameterInputStage.ActionUIElements, Runnable>();
		paramActions.put(ParameterInputStage.ActionUIElements.PLAY, () -> {
			activateStage(IngameStages.HUD);
			gameController.startGame();
		});
		paramActions.put(ParameterInputStage.ActionUIElements.REGEN, () -> {
			gameController.generateMap(1, 5, parameterInputStage.getBotIntelligenceParam(),
					parameterInputStage.getSeedParam(), parameterInputStage.getMapSizeParam(),
					parameterInputStage.getMapDensityParam());
			// place the camera for full map view
			// calculate what is the bigger rectangular area for the map to fit: above the
			// inputs or to their right
			float aboveArea = camera.viewportWidth
					* (camera.viewportHeight - BUTTON_HEIGHT_PX - ParameterInputStage.NO_OF_INPUTS * INPUT_HEIGHT_PX);
			float rightArea = (camera.viewportWidth - INPUT_WIDTH_PX) * (camera.viewportHeight - BUTTON_HEIGHT_PX);
			if (aboveArea > rightArea) {
				gameController.placeCameraForFullMapView(0,
						BUTTON_HEIGHT_PX + ParameterInputStage.NO_OF_INPUTS * INPUT_HEIGHT_PX, 0, 0);
			} else {
				gameController.placeCameraForFullMapView(INPUT_WIDTH_PX, BUTTON_HEIGHT_PX, 0, 0);
			}
		});
		paramActions.put(ParameterInputStage.ActionUIElements.CHANGE,
				() -> PreferencesHelper
						.saveNewGamePreferences(new NewGamePreferences(parameterInputStage.getBotIntelligence(),
								parameterInputStage.getMapSize(), parameterInputStage.getMapDensity())));
		parameterInputStage = new ParameterInputStage(viewport, paramActions);

		// hud
		Map<ActionUIElements, Runnable> hudActions = new LinkedHashMap<HudStage.ActionUIElements, Runnable>();
		hudActions.put(HudStage.ActionUIElements.UNDO, () -> inputHandler.inputUndo());
		hudActions.put(HudStage.ActionUIElements.BUY_PEASANT, () -> inputHandler.inputBuyPeasant());
		hudActions.put(HudStage.ActionUIElements.BUY_CASTLE, () -> inputHandler.inputBuyCastle());
		hudActions.put(HudStage.ActionUIElements.END_TURN, () -> {
			if (GameStateHelper.hasActivePlayerlikelyForgottenAKingom(gameController.getGameState())) {
				Dialog confirmDialog = new ConfirmDialog(
						"You might have forgotten to do your moves for a kingdom.\nAre you sure you want to end your turn?\n", () -> {
							inputHandler.inputEndTurn();
						});
				confirmDialog.show(hudStage);
			} else {
				inputHandler.inputEndTurn();
			}
		});
		hudActions.put(HudStage.ActionUIElements.MENU, () -> {
			activateStage(IngameStages.MENU);
		});
		hudStage = new HudStage(viewport, hudActions);

		// menu
		LinkedHashMap<String, Runnable> buttonData = new LinkedHashMap<String, Runnable>();
		buttonData.put("Exit", () -> {
			Dialog confirmDialog = new ConfirmDialog("Your progress will be lost. Are you sure?\n", () -> {
				PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
				FeudalTactics.game.setScreen(new MainMenuScreen());
			});
			confirmDialog.show(menuStage);
		});
		buttonData.put("Retry", () -> {
			Dialog confirmDialog = new ConfirmDialog("Your progress will be lost. Are you sure?\n", () -> {
				parameterInputStage.regenerateMap(gameController.getGameState().getSeed());
				activateStage(IngameStages.PARAMETERS);
			});
			confirmDialog.show(menuStage);
		});
		buttonData.put("Continue", () -> activateStage(IngameStages.HUD));
		menuStage = new GenericMenuStage(viewport, buttonData);
	}

	public void tooglePause() {
		if (activeStage == menuStage) {
			activateStage(IngameStages.HUD);
		} else if (activeStage == hudStage) {
			activateStage(IngameStages.MENU);
		}
	}

	public void showGiveUpGameMessage(boolean win, Color winningPlayerColor) {
		Dialog endDialog = new FeudalTacticsDialog() {
			public void result(Object result) {
				switch ((byte) result) {
				case 1:
					// exit button
					FeudalTactics.game.setScreen(new MainMenuScreen());
					PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
					break;
				case 2:
					// retry button
					parameterInputStage.regenerateMap(gameController.getGameState().getSeed());
					activateStage(IngameStages.PARAMETERS);
					this.remove();
					break;
				case 0:
					// do nothing on continue button
				default:
					break;
				}
			}
		};
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

	public void showLostMessage() {
		Dialog endDialog = new FeudalTacticsDialog() {
			public void result(Object result) {
				if ((boolean) result) {
					FeudalTactics.game.setScreen(new MainMenuScreen());
					PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
				} else {
					parameterInputStage.regenerateMap(gameController.getGameState().getSeed());
					activateStage(IngameStages.PARAMETERS);
					this.remove();
				}
			}
		};
		endDialog.button("Exit", true);
		endDialog.button("Retry", false);
		endDialog.text("DEFEAT! All of your kingdoms were conquered by the enemy.");
		endDialog.show(hudStage);
	}

	private void activateStage(IngameStages ingameStage) {
		switch (ingameStage) {
		case MENU:
			multiplexer.clear();
			multiplexer.addProcessor(menuStage);
			multiplexer.addProcessor(inputProcessor);
			activeStage = menuStage;
			break;
		case HUD:
			multiplexer.clear();
			multiplexer.addProcessor(hudStage);
			multiplexer.addProcessor(new GestureDetector(inputProcessor));
			multiplexer.addProcessor(inputProcessor);
			activeStage = hudStage;
			break;
		case PARAMETERS:
			multiplexer.clear();
			multiplexer.addProcessor(parameterInputStage);
			multiplexer.addProcessor(new GestureDetector(inputProcessor));
			multiplexer.addProcessor(inputProcessor);
			activeStage = parameterInputStage;
			break;
		default:
			break;
		}
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(multiplexer);
	}

	@Override
	public void render(float delta) {
		camera.update();
		Gdx.gl.glClearColor(FeudalTactics.backgroundColor.r, FeudalTactics.backgroundColor.g,
				FeudalTactics.backgroundColor.b, FeudalTactics.backgroundColor.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mapRenderer.render();
		viewport.apply();
		activeStage.draw();
		activeStage.act();
	}

	@Override
	public void resize(int width, int height) {
		hudStage.setFontScale(height / 1000F);
		menuStage.updateOnResize(width, height);
		viewport.update(width, height, true);
		viewport.apply();
		((Table) parameterInputStage.getActors().get(0)).pack(); // VERY IMPORTANT!!! makes everything scale correctly
																	// on startup and going fullscreen etc.; took me
																	// hours to find out
		hudStage.updateOnResize();
		((Table) menuStage.getActors().get(0)).pack();
		camera.viewportHeight = height;
		camera.viewportWidth = width;
		camera.update();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		mapRenderer.dispose();
		parameterInputStage.dispose();
		hudStage.dispose();
		menuStage.dispose();
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

	public HudStage getHudStage() {
		return hudStage;
	}

	public GenericMenuStage getMenuStage() {
		return menuStage;
	}

}

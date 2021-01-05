package com.sesu8642.feudaltactics.screens;

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
import com.sesu8642.feudaltactics.engine.CombinedInputProcessor;
import com.sesu8642.feudaltactics.engine.GameController;
import com.sesu8642.feudaltactics.engine.LocalInputHandler;
import com.sesu8642.feudaltactics.engine.MapRenderer;
import com.sesu8642.feudaltactics.engine.NewGamePreferences;
import com.sesu8642.feudaltactics.engine.PreferencesHelper;
import com.sesu8642.feudaltactics.stages.GenericMenuStage;
import com.sesu8642.feudaltactics.stages.HudStage;
import com.sesu8642.feudaltactics.stages.HudStage.ActionUIElements;
import com.sesu8642.feudaltactics.stages.ParameterInputStage;

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
			parameterInputStage.regenerateMap();
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
		hudActions.put(HudStage.ActionUIElements.END_TURN, () -> inputHandler.inputEndTurn());
		hudActions.put(HudStage.ActionUIElements.MENU, () -> {
			activateStage(IngameStages.MENU);
			PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
		});
		hudStage = new HudStage(viewport, hudActions);

		// menu
		LinkedHashMap<String, Runnable> buttonData = new LinkedHashMap<String, Runnable>();
		buttonData.put("Exit", () -> {
			Dialog confirmDialog = new Dialog("", FeudalTactics.skin) {
				public void result(Object result) {
					if ((boolean) result) {
						FeudalTactics.game.setScreen(new MainMenuScreen());
					}
				}
			};
			confirmDialog.getColor().a = 0; // fixes pop-in; see https://github.com/libgdx/libgdx/issues/3920
			confirmDialog.setMovable(false);
			confirmDialog.pad(20);
			confirmDialog.text("All unsaved progress will be lost. Are you sure?\n");
			confirmDialog.button("OK", true);
			confirmDialog.button("Cancel", false);
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
		Dialog endDialog = new Dialog("", FeudalTactics.skin) {
			public void result(Object result) {
				switch ((byte) result) {
				case 1:
					// exit button
					FeudalTactics.game.setScreen(new MainMenuScreen());
					PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
					break;
				case 2:
					// retry button
					activateStage(IngameStages.PARAMETERS);
					gameController.generateMap(1, 5, parameterInputStage.getBotIntelligenceParam(),
							parameterInputStage.getSeedParam(), parameterInputStage.getMapSizeParam(),
							parameterInputStage.getMapDensityParam());
					this.remove();
					break;
				case 0:
					// do nothing on continue button
				default:
					break;
				}
			}
		};
		endDialog.getColor().a = 0;
		endDialog.pad(20);
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
		Dialog endDialog = new Dialog("", FeudalTactics.skin) {
			public void result(Object result) {
				if ((boolean) result) {
					FeudalTactics.game.setScreen(new MainMenuScreen());
					PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
				} else {
					activateStage(IngameStages.PARAMETERS);
					gameController.generateMap(1, 5, parameterInputStage.getBotIntelligenceParam(),
							parameterInputStage.getSeedParam(), parameterInputStage.getMapSizeParam(),
							parameterInputStage.getMapDensityParam());
					this.remove();
				}
			}
		};
		endDialog.getColor().a = 0;
		endDialog.pad(20);
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
		Gdx.gl.glClearColor(0, 0.2f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mapRenderer.render();
		viewport.apply();
		activeStage.draw();
		activeStage.act();
	}

	@Override
	public void resize(int width, int height) {
		hudStage.setFontScale(height / 1000F);
		menuStage.setFontScale(height / 1000F);
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
		activeStage.dispose();
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

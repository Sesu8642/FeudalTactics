// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.events.BotTurnSkippedEvent;
import de.sesu8642.feudaltactics.events.BotTurnSpeedChangedEvent;
import de.sesu8642.feudaltactics.events.CenterMapEvent;
import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.events.moves.BuyCastleEvent;
import de.sesu8642.feudaltactics.events.moves.BuyPeasantEvent;
import de.sesu8642.feudaltactics.events.moves.EndTurnEvent;
import de.sesu8642.feudaltactics.events.moves.GameStartEvent;
import de.sesu8642.feudaltactics.events.moves.UndoMoveEvent;
import de.sesu8642.feudaltactics.ingame.AutoSaveRepository;
import de.sesu8642.feudaltactics.ingame.MapParameters;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.NewGamePreferencesDao;
import de.sesu8642.feudaltactics.ingame.dagger.IngameCamera;
import de.sesu8642.feudaltactics.ingame.dagger.IngameRenderer;
import de.sesu8642.feudaltactics.input.CombinedInputProcessor;
import de.sesu8642.feudaltactics.input.FeudalTacticsGestureDetector;
import de.sesu8642.feudaltactics.lib.gamestate.Castle;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.lib.gamestate.InputValidationHelper;
import de.sesu8642.feudaltactics.lib.gamestate.Kingdom;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;
import de.sesu8642.feudaltactics.lib.gamestate.Unit;
import de.sesu8642.feudaltactics.lib.ingame.botai.Speed;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.common.ui.Margin;
import de.sesu8642.feudaltactics.menu.common.ui.MenuStage;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

/** {@link Screen} for playing a map. */
@Singleton
public class IngameScreen extends GameScreen {

	private TextureAtlas textureAtlas;

	private AutoSaveRepository autoSaveRepo;
	private MainPreferencesDao mainPrefsDao;

	private OrthographicCamera ingameCamera;

	private MapRenderer mapRenderer;
	private InputMultiplexer inputMultiplexer;
	private EventBus eventBus;
	private CombinedInputProcessor inputProcessor;
	private final FeudalTacticsGestureDetector gestureDetector;

	private ParameterInputStage parameterInputStage;
	private HudStage hudStage;
	private MenuStage menuStage;

	private DialogFactory dialogFactory;

	/** Cached version of the game state from the game controller. */
	private GameState cachedGameState;

	/**
	 * Winner of the game before the bot players acted. Used to determine whether
	 * the winner changed in order to display a message.
	 */
	private Player winnerBeforeBotTurn;

	/** Whether it is the local player's turn. */
	private boolean isLocalPlayerTurn = true;

	/**
	 * Whether local player has elected to spectate the bots after being defeated.
	 */
	private boolean isSpectateMode = false;

	/**
	 * Interactions with the UI must happen in the same thread that does the
	 * rendering because the UI libs aren't thread-safe. To do that, Runnables can
	 * be placed here and will be executed when the next render happens.
	 */
	private ConcurrentLinkedQueue<Runnable> uiChangeActions = new ConcurrentLinkedQueue<>();
	private NewGamePreferencesDao newGamePrefDao;

	/** Stages that can be displayed. */
	public enum IngameStages {
		PARAMETERS, HUD, MENU
	}

	// current speed that the enemy turns are displayed in
	private Speed currentBotSpeed = Speed.NORMAL;

	/**
	 * Constructor.
	 * 
	 * @param autoSaveRepo         repo for interacting with autosave persistence
	 * @param mainPrefsDao         dao for main preferences
	 * @param newGamePrefDao       dao for new game preferences
	 * @param ingameCamera         camera for viewing the map
	 * @param viewport             viewport for the menus
	 * @param menuCamera           camera for the menus
	 * @param mapRenderer          renderer for the map
	 * @param confirmDialogFactory factory for creating confirm dialogs
	 * @param eventBus             event bus
	 * @param inputProcessor       input processor for user inputs that is added to
	 *                             the input multiplexer
	 * @param gestureDetector      gesture detector
	 * @param inputMultiplexer     input multiplexer that stages are added to as
	 *                             processors
	 * @param hudStage             stage for heads up display UI
	 * @param menuStage            stage for the pause menu UI
	 * @param parameterInputStage  stage for the new game parameter input UI
	 */
	@Inject
	public IngameScreen(TextureAtlas textureAtlas, AutoSaveRepository autoSaveRepo, MainPreferencesDao mainPrefsDao,
			NewGamePreferencesDao newGamePrefDao, @IngameCamera OrthographicCamera ingameCamera,
			@MenuViewport Viewport viewport, @MenuCamera OrthographicCamera menuCamera,
			@IngameRenderer MapRenderer mapRenderer, DialogFactory confirmDialogFactory, EventBus eventBus,
			CombinedInputProcessor inputProcessor, FeudalTacticsGestureDetector gestureDetector,
			InputMultiplexer inputMultiplexer, HudStage hudStage, IngameMenuStage menuStage,
			ParameterInputStage parameterInputStage) {
		super(ingameCamera, viewport, hudStage);
		this.textureAtlas = textureAtlas;
		this.autoSaveRepo = autoSaveRepo;
		this.mainPrefsDao = mainPrefsDao;
		this.newGamePrefDao = newGamePrefDao;
		this.ingameCamera = ingameCamera;
		this.mapRenderer = mapRenderer;
		this.dialogFactory = confirmDialogFactory;
		this.inputMultiplexer = inputMultiplexer;
		this.gestureDetector = gestureDetector;
		this.eventBus = eventBus;
		this.inputProcessor = inputProcessor;
		this.hudStage = hudStage;
		this.menuStage = menuStage;
		this.parameterInputStage = parameterInputStage;
		addIngameMenuListeners();
		addParameterInputListeners();
		addHudListeners();
		loadNewGameParameterValues();
	}

	/**
	 * Checks if there should be some warning before ending the turn, potentially
	 * displays it and then ends the turn if confirmed.
	 */
	public void handleEndTurnAttempt() {
		if (GameStateHelper.hasActivePlayerlikelyForgottenKingom(cachedGameState)
				&& mainPrefsDao.getMainPreferences().isWarnAboutForgottenKingdoms()) {
			Dialog confirmDialog = dialogFactory.createConfirmDialog(
					"You might have forgotten to do your moves for a kingdom.\nAre you sure you want to end your turn?\n",
					this::endHumanPlayerTurn);
			confirmDialog.show(hudStage);
		} else {
			endHumanPlayerTurn();
		}
	}

	private void endHumanPlayerTurn() {
		winnerBeforeBotTurn = cachedGameState.getWinner();
		// isLocalPlayerTurn needs to be set here because if the bot turns are not
		// shown, this class would never notice that a bot player was active
		isLocalPlayerTurn = false;
		eventBus.post(new EndTurnEvent());
	}

	private void resetGame() {
		clearCache();
		eventBus.post(new GameExitedEvent());
		eventBus.post(new RegenerateMapEvent(parameterInputStage.getBotIntelligence(),
				new MapParameters(parameterInputStage.getSeedParam(),
						parameterInputStage.getMapSizeParam().getAmountOfTiles(),
						parameterInputStage.getMapDensityParam().getDensityFloat(),
						parameterInputStage.getUserColor().getKingdomColor())));
		centerMap();
		activateStage(IngameStages.PARAMETERS);
	}

	private void exitToMenu() {
		eventBus.post(new GameExitedEvent());
		eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.MAIN_MENU_SCREEN));
		clearCache();
	}

	private void clearCache() {
		cachedGameState = null;
		winnerBeforeBotTurn = null;
		isSpectateMode = false;
	}

	/**
	 * Adjusts all the UI elements that need to be adjusted and displays dialogs if
	 * appropriate.
	 * 
	 * @param gameState new game state
	 */
	public void handleGameStateChange(GameState gameState) {
		boolean isLocalPlayerTurnNew = gameState.getActivePlayer().getType() == Type.LOCAL_PLAYER;
		boolean humanPlayerTurnJustStarted = !isLocalPlayerTurn && isLocalPlayerTurnNew;
		isLocalPlayerTurn = isLocalPlayerTurnNew;
		boolean winnerChanged = winnerBeforeBotTurn != gameState.getWinner();

		cachedGameState = gameState;
		// update the UI
		GameState newGameState = gameState;
		// hand content
		if (newGameState.getHeldObject() != null) {
			hudStage.updateHandContent(newGameState.getHeldObject().getSpriteName());
		} else {
			hudStage.updateHandContent(null);
		}
		// seed
		menuStage.setBottomRightLabelText("Seed: " + newGameState.getSeed().toString());
		String infoText = "";
		if (newGameState.getActivePlayer().getType() == Type.LOCAL_PLAYER) {
			Player localPlayer = newGameState.getActivePlayer();
			// info text
			Kingdom kingdom = newGameState.getActiveKingdom();
			if (kingdom != null) {
				int income = GameStateHelper.getKingdomIncome(kingdom);
				int salaries = GameStateHelper.getKingdomSalaries(newGameState, kingdom);
				int result = income - salaries;
				int savings = kingdom.getSavings();
				String resultText = result < 0 ? String.valueOf(result) : "+" + result;
				infoText = "Savings: " + savings + " (" + resultText + ")";
			} else {
				infoText = "Your turn";
			}
			// buttons
			if (hudStage.isEnemyTurnButtonsShown()) {
				uiChangeActions.add(() -> hudStage.showPlayerTurnButtons());
			}
			Optional<Player> playerOptional = GameStateHelper.determineActingLocalPlayer(newGameState);
			if (playerOptional.isPresent()) {
				Player player = playerOptional.get();
				// TODO: the repo shouldn't be needed here, would be better to create a
				// viewmodel somewhere else
				boolean canUndo = InputValidationHelper.checkUndoAction(newGameState, player,
						autoSaveRepo.getNoOfAutoSaves());
				boolean canBuyPeasant = InputValidationHelper.checkBuyObject(newGameState, player, Unit.class);
				boolean canBuyCastle = InputValidationHelper.checkBuyObject(newGameState, player, Castle.class);
				boolean canEndTurn = InputValidationHelper.checkEndTurn(newGameState, player);
				hudStage.setActiveTurnButtonEnabledStatus(canUndo, canBuyPeasant, canBuyCastle, canEndTurn);
			}
			// display messages
			if (newGameState.getPlayers().stream().filter(player -> !player.isDefeated()).count() == 1) {
				if (localPlayer.isDefeated()) {
					// Game is over; player lost
					uiChangeActions.add(this::showLostMessageWithoutSpectate);
				} else {
					// Game is over; player won
					uiChangeActions.add(this::showAllEnemiesDefeatedMessage);
				}
			} else if (localPlayer.isDefeated() && !isSpectateMode) {
				// Local player lost but game isn't over; offer a spectate option
				uiChangeActions.add(this::showLostMessage);
			} else if (humanPlayerTurnJustStarted && winnerChanged && !isSpectateMode) {
				// winner changed
				uiChangeActions.add(() -> showGiveUpGameMessage(newGameState.getWinner().getType() == Type.LOCAL_PLAYER,
						newGameState.getWinner().getColor()));
			}
		} else {
			infoText = "Enemy turn";
			if (!hudStage.isEnemyTurnButtonsShown()) {
				uiChangeActions.add(() -> hudStage.showEnemyTurnButtons());
			}
		}
		hudStage.setInfoText(infoText);
		parameterInputStage.updateSeed(newGameState.getSeed());
	}

	/** Toggles the pause menu. */
	public void togglePause() {
		if (getActiveStage() == menuStage) {
			activateStage(IngameStages.HUD);
		} else if (getActiveStage() == hudStage) {
			activateStage(IngameStages.MENU);
		}
	}

	/** Centers the map in the available screen space. */
	void centerMap() {
		Margin centeringMargin = calculateMapScreenArea();
		eventBus.post(new CenterMapEvent(cachedGameState, centeringMargin.marginBottom, centeringMargin.marginLeft,
				centeringMargin.marginTop, centeringMargin.marginRight));
	}

	/**
	 * Calculates where the map should be placed to have the most room while not
	 * being behind the UI elements.
	 * 
	 * @return Vector of margin to the left and margin to the bottom where the map
	 *         should not be rendered. The rest of the screen can be used.
	 */
	private Margin calculateMapScreenArea() {
		// calculate what is the bigger rectangular area for the map to fit: above the
		// inputs or to their right
		float aboveArea = ingameCamera.viewportWidth
				* (ingameCamera.viewportHeight - ParameterInputStage.TOTAL_INPUT_HEIGHT);
		float rightArea = (ingameCamera.viewportWidth - ParameterInputStage.TOTAL_INPUT_WIDTH)
				* (ingameCamera.viewportHeight - ParameterInputStage.BUTTON_HEIGHT_PX
						- ParameterInputStage.OUTER_PADDING_PX);
		if (aboveArea > rightArea) {
			return new Margin(0, ParameterInputStage.TOTAL_INPUT_HEIGHT, 0, 0);
		} else {
			return new Margin(ParameterInputStage.TOTAL_INPUT_WIDTH,
					ParameterInputStage.BUTTON_HEIGHT_PX + ParameterInputStage.OUTER_PADDING_PX, 0, 0);
		}
	}

	private void showGiveUpGameMessage(boolean win, Color winningPlayerColor) {
		// TODO: make this nicer and display the color of the winning player
		Dialog endDialog = dialogFactory.createDialog(result -> {
			switch ((byte) result) {
			case 1:
				// exit button
				exitToMenu();
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

	private void showAllEnemiesDefeatedMessage() {
		Dialog endDialog = dialogFactory.createDialog(result -> exitToMenu());
		endDialog.button("Exit");
		endDialog.text("VICTORY! You deafeated all your enemies.");
		endDialog.show(hudStage);
	}

	private void showLostMessage() {
		Dialog endDialog = dialogFactory.createDialog(result -> {
			switch ((byte) result) {
				case 1:
					// exit button
					exitToMenu();
					break;
				case 2:
					// retry button
					resetGame();
					break;
				case 0:
					// spectate button
					isSpectateMode = true;
					break;
				default:
					break;
			}
		});
		endDialog.button("Exit", (byte) 1);
		if (!isSpectateMode) {
			endDialog.button("Spectate", (byte) 0);
		}
		endDialog.button("Retry", (byte) 2);
		endDialog.text("DEFEAT! All of your kingdoms were conquered by the enemy.");
		endDialog.show(hudStage);
	}

	private void showLostMessageWithoutSpectate() {
		// Set isSpectateMode to true so that the dialog spectate option is not offered.
		isSpectateMode = true;
		showLostMessage();
	}

	void activateStage(IngameStages ingameStage) {
		inputMultiplexer.clear();
		switch (ingameStage) {
		case MENU:
			inputMultiplexer.addProcessor(menuStage);
			inputMultiplexer.addProcessor(inputProcessor);
			setActiveStage(menuStage);
			break;
		case HUD:
			inputMultiplexer.addProcessor(hudStage);
			inputMultiplexer.addProcessor(gestureDetector);
			inputMultiplexer.addProcessor(inputProcessor);
			setActiveStage(hudStage);
			break;
		case PARAMETERS:
			inputMultiplexer.addProcessor(parameterInputStage);
			inputMultiplexer.addProcessor(gestureDetector);
			inputMultiplexer.addProcessor(inputProcessor);
			setActiveStage(parameterInputStage);
			break;
		default:
			throw new IllegalStateException("Unknown stage " + ingameStage);
		}
		// the super class only applies the resizing to the active stage
		Gdx.app.postRunnable(() -> resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(inputMultiplexer);
		activateStage(IngameStages.PARAMETERS);
		centerMap();
	}

	@Override
	public void render(float delta) {
		while (!uiChangeActions.isEmpty()) {
			Runnable action = uiChangeActions.poll();
			action.run();
		}
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

	private void addIngameMenuListeners() {
		// exit button
		List<TextButton> buttons = menuStage.getButtons();
		buttons.get(0).addListener(new ExceptionLoggingChangeListener(() -> {
			Dialog confirmDialog = dialogFactory.createConfirmDialog("Your progress will be lost. Are you sure?\n",
					() -> exitToMenu());
			confirmDialog.show(menuStage);
		}));
		// retry button
		buttons.get(1).addListener(new ExceptionLoggingChangeListener(() -> {
			Dialog confirmDialog = dialogFactory.createConfirmDialog("Your progress will be lost. Are you sure?\n",
					() -> resetGame());
			confirmDialog.show(menuStage);
		}));
		// continue button
		buttons.get(2).addListener(new ExceptionLoggingChangeListener(() -> activateStage(IngameStages.HUD)));
	}

	private void addParameterInputListeners() {
		parameterInputStage.randomButton.addListener(new ExceptionLoggingChangeListener(
				() -> parameterInputStage.seedTextField.setText(String.valueOf(System.currentTimeMillis()))));

		Stream.of(parameterInputStage.seedTextField, parameterInputStage.randomButton, parameterInputStage.sizeSelect,
				parameterInputStage.densitySelect, parameterInputStage.colorSelect)
				.forEach(actor -> actor.addListener(new ExceptionLoggingChangeListener(() -> {
					eventBus.post(new RegenerateMapEvent(parameterInputStage.getBotIntelligence(),
							new MapParameters(parameterInputStage.getSeedParam(),
									parameterInputStage.getMapSizeParam().getAmountOfTiles(),
									parameterInputStage.getMapDensityParam().getDensityFloat(),
									parameterInputStage.getUserColor().getKingdomColor())));
					centerMap();
					newGamePrefDao
							.saveNewGamePreferences(new NewGamePreferences(parameterInputStage.getBotIntelligence(),
									parameterInputStage.getMapSizeParam(), parameterInputStage.getMapDensityParam(),
									parameterInputStage.getUserColor()));
				})));

		parameterInputStage.playButton
				.addListener(new ExceptionLoggingChangeListener(() -> eventBus.post(new GameStartEvent())));
	}

	private void addHudListeners() {
		hudStage.undoButton.addListener(new ExceptionLoggingChangeListener(() -> eventBus.post(new UndoMoveEvent())));

		hudStage.endTurnButton.addListener(new ExceptionLoggingChangeListener(() -> handleEndTurnAttempt()));

		hudStage.buyPeasantButton
				.addListener(new ExceptionLoggingChangeListener(() -> eventBus.post(new BuyPeasantEvent())));

		hudStage.buyCastleButton
				.addListener(new ExceptionLoggingChangeListener(() -> eventBus.post(new BuyCastleEvent())));

		hudStage.menuButton.addListener(new ExceptionLoggingChangeListener(() -> activateStage(IngameStages.MENU)));

		hudStage.speedButton.addListener(new ExceptionLoggingChangeListener(() -> {
			// determine the next speed level with overflow, skipping Speed.INSTANT which is
			// used for the other button
			int currentSpeedIndex = currentBotSpeed.ordinal();
			int nextSpeedIndex = currentSpeedIndex + 1;
			if (nextSpeedIndex >= Speed.values().length) {
				nextSpeedIndex = 0;
			}
			currentBotSpeed = Speed.values()[nextSpeedIndex];
			eventBus.post(new BotTurnSpeedChangedEvent(currentBotSpeed));
			hudStage.speedButton.setStyle(new ImageButtonStyle(null, null, null,
					new SpriteDrawable(
							textureAtlas.createSprite(HudStage.SPEED_BUTTON_TEXTURE_NAMES.get(currentBotSpeed))),
					new SpriteDrawable(textureAtlas
							.createSprite(HudStage.SPEED_BUTTON_TEXTURE_NAMES.get(currentBotSpeed) + "_pressed")),
					null));
		}));

		hudStage.skipButton
				.addListener(new ExceptionLoggingChangeListener(() -> eventBus.post(new BotTurnSkippedEvent())));

	}

	private void loadNewGameParameterValues() {
		NewGamePreferences prefs = newGamePrefDao.getNewGamePreferences();
		parameterInputStage.difficultySelect.setSelectedIndex(prefs.getBotIntelligence().ordinal());
		parameterInputStage.sizeSelect.setSelectedIndex(prefs.getMapSize().ordinal());
		parameterInputStage.densitySelect.setSelectedIndex(prefs.getDensity().ordinal());
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

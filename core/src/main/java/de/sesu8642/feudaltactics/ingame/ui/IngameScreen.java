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
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
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
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
import de.sesu8642.feudaltactics.lib.gamestate.Unit;
import de.sesu8642.feudaltactics.lib.ingame.PlayerMove;
import de.sesu8642.feudaltactics.lib.ingame.botai.Speed;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.CopyButton;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.FeudalTacticsDialog;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.common.ui.Margin;
import de.sesu8642.feudaltactics.menu.common.ui.MenuStage;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

/** {@link Screen} for playing a map. */
@Singleton
public class IngameScreen extends GameScreen {

	private final MainPreferencesDao mainPrefsDao;

	private final OrthographicCamera ingameCamera;

	private final MapRenderer mapRenderer;
	private final InputMultiplexer inputMultiplexer;
	private final EventBus eventBus;
	private final CombinedInputProcessor inputProcessor;
	private final FeudalTacticsGestureDetector gestureDetector;
	private final InputValidationHelper inputValidationHelper;

	private final ParameterInputStage parameterInputStage;
	private final HudStage hudStage;
	private final IngameMenuStage menuStage;

	private final DialogFactory dialogFactory;
	private final TutorialDialogFactory tutorialDialogFactory;

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
	 * @param mainPrefsDao        dao for main preferences
	 * @param newGamePrefDao      dao for new game preferences
	 * @param ingameCamera        camera for viewing the map
	 * @param viewport            viewport for the menus
	 * @param menuCamera          camera for the menus
	 * @param mapRenderer         renderer for the map
	 * @param dialogFactory       factory for creating confirm dialogs
	 * @param eventBus            event bus
	 * @param inputProcessor      input processor for user inputs that is added to
	 *                            the input multiplexer
	 * @param gestureDetector     gesture detector
	 * @param inputMultiplexer    input multiplexer that stages are added to as
	 *                            processors
	 * @param hudStage            stage for heads up display UI
	 * @param menuStage           stage for the pause menu UI
	 * @param parameterInputStage stage for the new game parameter input UI
	 */
	@Inject
	public IngameScreen(TextureAtlas textureAtlas, MainPreferencesDao mainPrefsDao,
			NewGamePreferencesDao newGamePrefDao, @IngameCamera OrthographicCamera ingameCamera,
			@MenuViewport Viewport viewport, @MenuCamera OrthographicCamera menuCamera,
			@IngameRenderer MapRenderer mapRenderer, DialogFactory dialogFactory,
			TutorialDialogFactory tutorialDialogFactory, EventBus eventBus, CombinedInputProcessor inputProcessor,
			FeudalTacticsGestureDetector gestureDetector, InputValidationHelper inputValidationHelper,
			InputMultiplexer inputMultiplexer, HudStage hudStage, IngameMenuStage menuStage,
			ParameterInputStage parameterInputStage) {
		super(ingameCamera, viewport, hudStage);
		this.mainPrefsDao = mainPrefsDao;
		this.newGamePrefDao = newGamePrefDao;
		this.ingameCamera = ingameCamera;
		this.mapRenderer = mapRenderer;
		this.dialogFactory = dialogFactory;
		this.tutorialDialogFactory = tutorialDialogFactory;
		this.inputMultiplexer = inputMultiplexer;
		this.gestureDetector = gestureDetector;
		this.inputValidationHelper = inputValidationHelper;
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
					"You might have forgotten to do your moves for a kingdom.\n\nAre you sure you want to end your turn?\n",
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
				new MapParameters(parameterInputStage.getStartingPosition(), parameterInputStage.getSeedParam(),
						parameterInputStage.getMapSizeParam().getAmountOfTiles(),
						parameterInputStage.getMapDensityParam().getDensityFloat())));
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

		boolean objectiveProgressed = cachedGameState != null
				&& gameState.getObjectiveProgress() > cachedGameState.getObjectiveProgress();

		cachedGameState = GameStateHelper.getCopy(gameState);
		// update the UI
		GameState newGameState = gameState;
		// hand content
		if (newGameState.getHeldObject() != null) {
			hudStage.updateHandContent(newGameState.getHeldObject().getSpriteName());
		} else {
			hudStage.updateHandContent(null);
		}
		// seed
		menuStage.bottomRightLabel.setText("Seed " + newGameState.getSeed().toString());
		String hudStageInfoText = "";
		if (newGameState.getActivePlayer().getType() == Type.LOCAL_PLAYER) {
			hudStageInfoText = handleGameStateChangeHumanPlayerTurn(humanPlayerTurnJustStarted, winnerChanged,
					newGameState);
		} else {
			hudStageInfoText = "Enemy turn";
			if (!hudStage.isEnemyTurnButtonsShown()) {
				uiChangeActions.add(hudStage::showEnemyTurnButtons);
			}
		}
		hudStage.infoTextLabel.setText(hudStageInfoText);
		hudStage.infoHexagonLabel.setText(String.format("[#%s]h",
				MapRenderer.PLAYER_COLOR_PALETTE.get(gameState.getActivePlayer().getPlayerIndex())));
		parameterInputStage.updateSeed(newGameState.getSeed());

		if (objectiveProgressed) {
			showGameOrObjectiveInfo();
		}
	}

	private String handleGameStateChangeHumanPlayerTurn(boolean humanPlayerTurnJustStarted, boolean winnerChanged,
			GameState newGameState) {
		String infoText;
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
			uiChangeActions.add(hudStage::showPlayerTurnButtons);
		}
		Optional<Player> playerOptional = GameStateHelper.determineActingLocalPlayer(newGameState);
		if (playerOptional.isPresent()) {
			Player player = playerOptional.get();
			boolean canUndo = inputValidationHelper.checkPlayerMove(newGameState, player, PlayerMove.undoLastMove());
			boolean canBuyPeasant = InputValidationHelper.checkBuyObject(newGameState, player, Unit.class);
			boolean canBuyCastle = InputValidationHelper.checkBuyObject(newGameState, player, Castle.class);
			boolean canEndTurn = InputValidationHelper.checkEndTurn(newGameState, player);
			hudStage.setActiveTurnButtonEnabledStatus(canUndo, canBuyPeasant, canBuyCastle, canEndTurn);
		}
		// display messages
		if (newGameState.getPlayers().stream().filter(player -> !player.isDefeated()).count() == 1) {
			if (localPlayer.isDefeated()) {
				// Game is over; player lost
				uiChangeActions.add(this::showEnemyWonMessage);
			} else {
				// Game is over; player won
				uiChangeActions.add(this::showAllEnemiesDefeatedMessage);
			}
		} else if (localPlayer.isDefeated() && !isSpectateMode) {
			// Local player lost but game isn't over; offer a spectate option
			uiChangeActions.add(this::showPlayerDefeatedMessage);
		} else if (humanPlayerTurnJustStarted && winnerChanged && !isSpectateMode) {
			// winner changed
			uiChangeActions.add(() -> showGiveUpGameMessage(newGameState.getWinner().getType() == Type.LOCAL_PLAYER));
		}
		return infoText;
	}

	private void showGameOrObjectiveInfo() {
		System.out.println(cachedGameState.getScenarioMap());
		if (cachedGameState.getScenarioMap() == ScenarioMap.NONE) {
			// regular sandbox game
			showGameDetails();
		} else if (cachedGameState.getScenarioMap() == ScenarioMap.TUTORIAL) {
			showTutorialObjectiveMessage(cachedGameState.getObjectiveProgress());
		}
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

	private void showGiveUpGameMessage(boolean win) {
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
			endDialog.text("VICTORY! Your Enemies surrender.\n\nDo you wish to continue?\n");
			endDialog.button("Replay", (byte) 2);
		} else {
			endDialog.text("Your Enemy conquered a majority of the territory.\n\nDo you wish to continue?\n");
			endDialog.button("Retry", (byte) 2);
		}
		endDialog.button("Continue", (byte) 0);
		endDialog.show(hudStage);
	}

	private void showAllEnemiesDefeatedMessage() {
		Dialog endDialog = dialogFactory.createDialog(result -> exitToMenu());
		endDialog.button("Exit");
		endDialog.text("VICTORY! You defeated all your enemies.\n");
		endDialog.show(hudStage);
	}

	private void showPlayerDefeatedMessage() {
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
		endDialog.button("Spectate", (byte) 0);
		endDialog.button("Retry", (byte) 2);
		endDialog.text("DEFEAT! All your kingdoms were conquered by the enemy.\n");
		endDialog.show(hudStage);
	}

	private void showEnemyWonMessage() {
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
			default:
				break;
			}
		});
		endDialog.button("Exit", (byte) 1);
		endDialog.button("Retry", (byte) 2);
		endDialog.text("DEFEAT! Your enemy won the game.\n");
		endDialog.show(hudStage);
	}

	private void showGameDetails() {
		NewGamePreferences prefs = newGamePrefDao.getNewGamePreferences();
		StringBuilder gameDetailsBuilder = new StringBuilder();
		gameDetailsBuilder.append(String.format("Round: %s", cachedGameState.getRound()));
		gameDetailsBuilder.append(String.format("\nSeed: %s", cachedGameState.getSeed()));
		gameDetailsBuilder.append(String.format("\nStarting Position: %s", prefs.getStartingPosition() + 1));
		gameDetailsBuilder.append(String.format("\nCPU Difficulty: %s",
				EnumDisplayNameProvider.getDisplayName(prefs.getBotIntelligence())));
		gameDetailsBuilder
				.append(String.format("\nMap Size: %s", EnumDisplayNameProvider.getDisplayName(prefs.getMapSize())));
		gameDetailsBuilder
				.append(String.format("\nMap Density: %s", EnumDisplayNameProvider.getDisplayName(prefs.getDensity())));
		FeudalTacticsDialog dialog = dialogFactory.createInformationDialogWithCopyButton(gameDetailsBuilder.toString(), () -> {
		});
		dialog.show(hudStage);
	}

	private void showTutorialObjectiveMessage(int newProgress) {
		Dialog dialog = tutorialDialogFactory.createDialog(newProgress);
		dialog.show(hudStage);
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
					this::exitToMenu);
			confirmDialog.show(menuStage);
		}));
		// retry button
		buttons.get(1).addListener(new ExceptionLoggingChangeListener(() -> {
			Dialog confirmDialog = dialogFactory.createConfirmDialog("Your progress will be lost. Are you sure?\n",
					this::resetGame);
			confirmDialog.show(menuStage);
		}));
		// continue button
		buttons.get(2).addListener(new ExceptionLoggingChangeListener(() -> activateStage(IngameStages.HUD)));
		// copy button
		menuStage.copyButton.addListener(new ExceptionLoggingChangeListener(
				() -> Gdx.app.getClipboard().setContents(menuStage.bottomRightLabel.getText().toString())));
	}

	private void addParameterInputListeners() {
		parameterInputStage.randomButton.addListener(new ExceptionLoggingChangeListener(
				() -> parameterInputStage.seedTextField.setText(String.valueOf(System.currentTimeMillis()))));

		parameterInputStage.pasteButton.addListener(new ExceptionLoggingChangeListener(() -> {
			parameterInputStage.seedTextField.setText(Gdx.app.getClipboard().getContents());
		}));

		parameterInputStage.copyButton.addListener(new ExceptionLoggingChangeListener(
				() -> Gdx.app.getClipboard().setContents(parameterInputStage.seedTextField.getText())));

		Stream.of(parameterInputStage.seedTextField, parameterInputStage.randomButton, parameterInputStage.sizeSelect,
				parameterInputStage.densitySelect, parameterInputStage.startingPositionSelect,
				parameterInputStage.pasteButton, parameterInputStage.difficultySelect)
				.forEach(actor -> actor.addListener(new ExceptionLoggingChangeListener(() -> {
					eventBus.post(new RegenerateMapEvent(parameterInputStage.getBotIntelligence(),
							new MapParameters(parameterInputStage.getStartingPosition(),
									parameterInputStage.getSeedParam(),
									parameterInputStage.getMapSizeParam().getAmountOfTiles(),
									parameterInputStage.getMapDensityParam().getDensityFloat())));
					newGamePrefDao.saveNewGamePreferences(new NewGamePreferences(
							parameterInputStage.getBotIntelligence(), parameterInputStage.getMapSizeParam(),
							parameterInputStage.getMapDensityParam(), parameterInputStage.getStartingPosition()));
				})));
		// only the settings that visually change the map need to cause centering
		Stream.of(parameterInputStage.seedTextField, parameterInputStage.randomButton, parameterInputStage.sizeSelect,
				parameterInputStage.densitySelect, parameterInputStage.pasteButton)
				.forEach(actor -> actor.addListener(new ExceptionLoggingChangeListener(this::centerMap)));
		parameterInputStage.playButton
				.addListener(new ExceptionLoggingChangeListener(() -> eventBus.post(new GameStartEvent())));
	}

	private void addHudListeners() {
		hudStage.undoButton.addListener(new ExceptionLoggingChangeListener(() -> eventBus.post(new UndoMoveEvent())));

		hudStage.endTurnButton.addListener(new ExceptionLoggingChangeListener(this::handleEndTurnAttempt));

		hudStage.buyPeasantButton
				.addListener(new ExceptionLoggingChangeListener(() -> eventBus.post(new BuyPeasantEvent())));

		hudStage.buyCastleButton
				.addListener(new ExceptionLoggingChangeListener(() -> eventBus.post(new BuyCastleEvent())));

		hudStage.menuButton.addListener(new ExceptionLoggingChangeListener(() -> activateStage(IngameStages.MENU)));

		hudStage.infoButton.addListener(new ExceptionLoggingChangeListener(this::showGameOrObjectiveInfo));

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
			ImageButtonStyle newStyle = null;
			switch (nextSpeedIndex) {
			case 0:
				newStyle = hudStage.halfSpeedButtonStyle;
				break;
			case 1:
				newStyle = hudStage.regularSpeedButtonStyle;
				break;
			case 2:
				newStyle = hudStage.doubleSpeedButtonStyle;
				break;
			default:
				throw new IllegalStateException("Unknown speed index " + currentSpeedIndex);
			}
			hudStage.speedButton.setStyle(newStyle);
		}));

		hudStage.skipButton
				.addListener(new ExceptionLoggingChangeListener(() -> eventBus.post(new BotTurnSkippedEvent())));

	}

	private void loadNewGameParameterValues() {
		NewGamePreferences prefs = newGamePrefDao.getNewGamePreferences();
		parameterInputStage.difficultySelect.setSelectedIndex(prefs.getBotIntelligence().ordinal());
		parameterInputStage.sizeSelect.setSelectedIndex(prefs.getMapSize().ordinal());
		parameterInputStage.densitySelect.setSelectedIndex(prefs.getDensity().ordinal());
		parameterInputStage.startingPositionSelect.setSelectedIndex(prefs.getStartingPosition());
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

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.events.*;
import de.sesu8642.feudaltactics.events.moves.*;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.NewGamePreferencesDao;
import de.sesu8642.feudaltactics.ingame.dagger.IngameCamera;
import de.sesu8642.feudaltactics.ingame.dagger.IngameRenderer;
import de.sesu8642.feudaltactics.input.CombinedInputProcessor;
import de.sesu8642.feudaltactics.input.FeudalTacticsGestureDetector;
import de.sesu8642.feudaltactics.lib.gamestate.*;
import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;
import de.sesu8642.feudaltactics.lib.ingame.PlayerMove;
import de.sesu8642.feudaltactics.lib.ingame.botai.Speed;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.*;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
import de.sesu8642.feudaltactics.renderer.MapRenderer;
import de.sesu8642.feudaltactics.renderer.TextureAtlasHelper;
import lombok.Setter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

/**
 * {@link Screen} for playing a map.
 */
@Singleton
public class IngameScreen extends GameScreen {

    private final MainPreferencesDao mainPrefsDao;

    private final OrthographicCamera ingameCamera;

    private final MapRenderer mapRenderer;
    private final InputMultiplexer inputMultiplexer;
    private final EventBus eventBus;
    private final ScreenNavigationController screenNavigationController;
    private final CombinedInputProcessor inputProcessor;
    private final FeudalTacticsGestureDetector gestureDetector;
    private final InputValidationHelper inputValidationHelper;
    private final IngameScreenDialogHelper ingameScreenDialogHelper;
    private final TextureAtlasHelper textureAtlasHelper;

    private final ParameterInputStage parameterInputStage;
    private final PlatformInsetsProvider platformInsetsProvider;
    private final IngameHudStage ingameHudStage;
    private final IngameMenuStage menuStage;

    private final DialogFactory dialogFactory;

    private final NewGamePreferencesDao newGamePrefDao;

    @Setter
    private NewGamePreferences cachedNewGamePreferences;
    /**
     * Cached version of the game state from the game controller.
     */
    private GameState cachedGameState;
    /**
     * Winner of the game before the bot players acted. Used to determine whether
     * the winner changed in order to display a message.
     */
    private int winnerBeforeBotTurnPlayerIndex = -1;
    /**
     * Whether it is the local player's turn.
     */
    private boolean isLocalPlayerTurn = true;
    /**
     * Whether local player has elected to spectate the bots after being defeated.
     */
    private boolean isSpectateMode = false;
    // current speed that the enemy turns are displayed in
    private Speed currentBotSpeed = Speed.NORMAL;

    /**
     * Constructor.
     */
    @Inject
    public IngameScreen(MainPreferencesDao mainPrefsDao, NewGamePreferencesDao newGamePrefDao,
                        @IngameCamera OrthographicCamera ingameCamera, @MenuViewport Viewport viewport,
                        @IngameRenderer MapRenderer mapRenderer, DialogFactory dialogFactory, EventBus eventBus,
                        ScreenNavigationController screenNavigationController,
                        CombinedInputProcessor inputProcessor, FeudalTacticsGestureDetector gestureDetector,
                        InputValidationHelper inputValidationHelper, InputMultiplexer inputMultiplexer,
                        IngameScreenDialogHelper ingameScreenDialogHelper, TextureAtlasHelper textureAtlasHelper,
                        IngameHudStage ingameHudStage,
                        IngameMenuStage menuStage, ParameterInputStage parameterInputStage,
                        PlatformInsetsProvider platformInsetsProvider) {
        super(ingameCamera, viewport, ingameHudStage);
        this.mainPrefsDao = mainPrefsDao;
        this.newGamePrefDao = newGamePrefDao;
        this.ingameCamera = ingameCamera;
        this.mapRenderer = mapRenderer;
        this.dialogFactory = dialogFactory;
        this.screenNavigationController = screenNavigationController;
        this.inputMultiplexer = inputMultiplexer;
        this.gestureDetector = gestureDetector;
        this.inputValidationHelper = inputValidationHelper;
        this.eventBus = eventBus;
        this.inputProcessor = inputProcessor;
        this.ingameScreenDialogHelper = ingameScreenDialogHelper;
        this.textureAtlasHelper = textureAtlasHelper;
        this.ingameHudStage = ingameHudStage;
        this.menuStage = menuStage;
        this.parameterInputStage = parameterInputStage;
        this.platformInsetsProvider = platformInsetsProvider;
        // load before adding the listeners because they will trigger persisting the preferences on each update
        loadNewGameParameterValues();
        addIngameMenuListeners();
        addParameterInputListeners();
        addHudListeners();
    }

    /**
     * Checks if there should be some warning before ending the turn, potentially
     * displays it and then ends the turn if confirmed.
     */
    public void handleEndTurnAttempt() {
        final Optional<Player> playerOptional = GameStateHelper.determineActingLocalPlayer(cachedGameState);
        if (!playerOptional.isPresent()) {
            return;
        }
        final boolean canEndTurn = InputValidationHelper.checkEndTurn(cachedGameState, playerOptional.get());
        if (!canEndTurn) {
            ingameScreenDialogHelper.showCannotEndTurnMessage(getActiveStage());
        } else if (mainPrefsDao.getMainPreferences().isWarnAboutForgottenKingdoms()) {
            final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(cachedGameState);
            if (forgottenKingdom.isPresent()) {
                final Dialog confirmDialog = dialogFactory.createConfirmDialog(
                    "You might have forgotten to do your moves for a kingdom.\n\nAre you sure you want to" +
                        " end your turn?\n",
                    this::endHumanPlayerTurn, () -> {
                        final Kingdom kingdom = forgottenKingdom.get();
                        eventBus.post(new FocusKingdomEvent(cachedGameState,
                            kingdom));
                        eventBus.post(new ActivateKingdomEvent(kingdom));
                    });
                confirmDialog.show(ingameHudStage);
                return;
            }
        }
        endHumanPlayerTurn();
    }

    private void endHumanPlayerTurn() {
        if (cachedGameState.getWinner() != null) {
            winnerBeforeBotTurnPlayerIndex = cachedGameState.getWinner().getPlayerIndex();
        }
        // isLocalPlayerTurn needs to be set here because if the bot turns are not
        // shown, this class would never notice that a bot player was active
        isLocalPlayerTurn = false;
        eventBus.post(new EndTurnEvent());
    }

    private void resetGame() {
        eventBus.post(new GameExitedEvent(cachedGameState));
        final GameState previousCachedGameState = cachedGameState;
        clearCache();
        if (previousCachedGameState.getScenarioMap() == ScenarioMap.NONE) {
            eventBus.post(new RegenerateMapEvent(cachedNewGamePreferences.toGameParameters()));
            activateStage(IngameStages.PARAMETERS);
        } else {
            eventBus.post(new InitializeScenarioEvent(previousCachedGameState.getBotIntelligence(),
                previousCachedGameState.getScenarioMap()));
            eventBus.post(new GameStartEvent());
            activateStage(IngameStages.HUD);
        }
        centerMap();
    }

    private void exitToMenu() {
        eventBus.post(new GameExitedEvent(cachedGameState));
        screenNavigationController.transitionToMainMenuScreen();
        clearCache();
    }

    private void clearCache() {
        cachedGameState = null;
        winnerBeforeBotTurnPlayerIndex = -1;
        isSpectateMode = false;
    }

    /**
     * Adjusts all the UI elements that need to be adjusted and displays dialogs if
     * appropriate.
     *
     * @param gameState new game state
     */
    public void handleGameStateChange(GameState gameState) {
        final boolean isLocalPlayerTurnNew = gameState.getActivePlayer().getType() == Type.LOCAL_PLAYER;
        final boolean humanPlayerTurnJustStarted = !isLocalPlayerTurn && isLocalPlayerTurnNew;
        isLocalPlayerTurn = isLocalPlayerTurnNew;
        final boolean winnerChanged =
            gameState.getWinner() != null
                && winnerBeforeBotTurnPlayerIndex != gameState.getWinner().getPlayerIndex();

        final boolean objectiveProgressed = cachedGameState != null
            && gameState.getObjectiveProgress() > cachedGameState.getObjectiveProgress();

        cachedGameState = GameStateHelper.getCopy(gameState);
        // update the UI
        final GameState newGameState = gameState;
        // hand content
        if (newGameState.getHeldObject() != null) {
            ingameHudStage.updateHandContent(textureAtlasHelper.createSpriteForTileContent(newGameState.getHeldObject()));
        } else {
            ingameHudStage.updateHandContent(null);
        }
        // seed
        String hudStageInfoText = "";
        if (newGameState.getActivePlayer().getType() == Type.LOCAL_PLAYER) {
            hudStageInfoText = handleGameStateChangeHumanPlayerTurn(humanPlayerTurnJustStarted, winnerChanged,
                newGameState);
        } else {
            hudStageInfoText = "Enemy turn";
            if (!ingameHudStage.isEnemyTurnButtonsShown()) {
                Gdx.app.postRunnable(ingameHudStage::showEnemyTurnButtons);
            }
        }
        ingameHudStage.infoTextLabel.setText(hudStageInfoText);
        ingameHudStage.infoHexagonLabel.setText(String.format("[#%s]h",
            MapRenderer.PLAYER_COLOR_PALETTE.get(gameState.getActivePlayer().getPlayerIndex())));
        parameterInputStage.updateSeed(newGameState.getSeed());

        if (objectiveProgressed) {
            ingameScreenDialogHelper.showGameOrObjectiveInfo(ingameHudStage, cachedGameState.getRound(),
                cachedGameState.getScenarioMap(), cachedGameState.getObjectiveProgress(), cachedNewGamePreferences);
        }
    }

    private String handleGameStateChangeHumanPlayerTurn(boolean humanPlayerTurnJustStarted, boolean winnerChanged,
                                                        GameState newGameState) {
        String infoText = "";
        final Player localPlayer = newGameState.getActivePlayer();
        // info text
        final Kingdom kingdom = newGameState.getActiveKingdom();
        if (kingdom != null) {
            final int income = GameStateHelper.getKingdomIncome(kingdom);
            final int salaries = GameStateHelper.getKingdomSalaries(newGameState, kingdom);
            final int budgetBalance = income - salaries;
            final int savings = kingdom.getSavings();
            final String budgetBalanceText = budgetBalance < 0 ? String.valueOf(budgetBalance) : "+" + budgetBalance;
            if (savings + budgetBalance < 0) {
                // warn the user with red text
                infoText += "[RED]";
            }
            infoText += "Savings: " + savings + " (" + budgetBalanceText + ")";
        } else {
            infoText = "Your turn – select a kingdom.";
        }
        // buttons
        if (ingameHudStage.isEnemyTurnButtonsShown()) {
            Gdx.app.postRunnable(ingameHudStage::showPlayerTurnButtons);
        }
        final Optional<Player> playerOptional = GameStateHelper.determineActingLocalPlayer(newGameState);
        if (playerOptional.isPresent()) {
            final Player player = playerOptional.get();
            final boolean canUndo = inputValidationHelper.checkPlayerMove(newGameState, player,
                PlayerMove.undoLastMove());
            final boolean canBuyPeasant = InputValidationHelper.checkBuyObject(newGameState, player, Unit.class);
            final boolean canBuyCastle = InputValidationHelper.checkBuyObject(newGameState, player, Castle.class);
            final boolean canEndTurn = InputValidationHelper.checkEndTurn(newGameState, player);
            ingameHudStage.setActiveTurnButtonEnabledStatus(canUndo, canBuyPeasant, canBuyCastle, canEndTurn);
        }
        // display messages
        if (newGameState.getPlayers().stream().filter(player -> !player.isDefeated()).count() == 1) {
            if (localPlayer.isDefeated()) {
                // Game is over; player lost
                Gdx.app.postRunnable(() -> ingameScreenDialogHelper.showEnemyWonMessage(ingameHudStage,
                    cachedGameState.getActivePlayer().getRoundOfDefeat(), cachedGameState.getScenarioMap(),
                    cachedNewGamePreferences, this::exitToMenu, this::resetGame));
            } else {
                // Game is over; player won
                final boolean botsGaveUpPreviously =
                    cachedGameState.getWinner() != null
                        && cachedGameState.getWinner().getType() == Player.Type.LOCAL_PLAYER;
                Gdx.app.postRunnable(() -> ingameScreenDialogHelper.showAllEnemiesDefeatedMessage(ingameHudStage,
                    botsGaveUpPreviously, getEarliestRoundOfGameEnd(cachedGameState),
                    cachedGameState.getScenarioMap(), cachedNewGamePreferences, this::exitToMenu, this::resetGame));
            }
        } else if (localPlayer.isDefeated() && !isSpectateMode) {
            // Local player lost but game isn't over; offer a spectate option
            Gdx.app.postRunnable(() -> ingameScreenDialogHelper.showPlayerDefeatedMessage(ingameHudStage,
                cachedGameState.getRound(), cachedGameState.getScenarioMap(), cachedNewGamePreferences,
                this::exitToMenu, this::resetGame, () -> isSpectateMode = true));
        } else if (humanPlayerTurnJustStarted && winnerChanged && !isSpectateMode) {
            // winner changed
            final boolean humanWins = newGameState.getWinner().getType() == Type.LOCAL_PLAYER;
            Gdx.app.postRunnable(() -> ingameScreenDialogHelper.showGiveUpGameMessage(ingameHudStage, humanWins,
                cachedGameState.getWinningRound(), cachedGameState.getScenarioMap(), cachedNewGamePreferences,
                this::exitToMenu, this::resetGame));
        }
        return infoText;
    }

    /**
     * This method assumes that the game ends in this turn.
     *
     * @return the round in which the player won/lost, meaning either the current round or the round in which someone
     * conquered most of the map. In case of both, returns the latter.
     */
    Integer getEarliestRoundOfGameEnd(GameState gameState) {
        if (gameState.getWinningRound() != null) {
            return gameState.getWinningRound();
        } else {
            return gameState.getRound();
        }
    }

    /**
     * Toggles the pause menu.
     */
    public void togglePause() {
        if (getActiveStage() == menuStage) {
            activateStage(IngameStages.HUD);
        } else if (getActiveStage() == ingameHudStage) {
            activateStage(IngameStages.MENU);
        }
    }

    /**
     * Centers the map in the available screen space.
     */
    void centerMap() {
        final Margin centeringMargin = calculateMapScreenArea();
        eventBus.post(new CenterMapEvent(cachedGameState, centeringMargin.marginBottom, centeringMargin.marginLeft,
            centeringMargin.marginTop, centeringMargin.marginRight));
    }

    /**
     * Calculates where the map should be placed to have the most room while not
     * being behind the UI elements.
     *
     * @return Vector of margin to the left and margin to the bottom where the map
     * should not be rendered. The rest of the screen can be used.
     */
    private Margin calculateMapScreenArea() {
        // calculate what is the bigger rectangular area for the map to fit: above the
        // inputs or to their right
        final float aboveArea = ingameCamera.viewportWidth
            * (ingameCamera.viewportHeight - ParameterInputStage.TOTAL_INPUT_HEIGHT - platformInsetsProvider.getInsets(Gdx.app).getBottomInset());
        final float rightArea = (ingameCamera.viewportWidth - ParameterInputStage.TOTAL_INPUT_WIDTH)
            * (ingameCamera.viewportHeight - ParameterInputStage.BUTTON_HEIGHT_PX
            - ParameterInputStage.OUTER_PADDING_PX);
        if (aboveArea > rightArea) {
            return new Margin(0,
                ParameterInputStage.TOTAL_INPUT_HEIGHT + platformInsetsProvider.getInsets(Gdx.app).getBottomInset(),
                0, 0);
        } else {
            return new Margin(ParameterInputStage.TOTAL_INPUT_WIDTH,
                ParameterInputStage.BUTTON_HEIGHT_PX + ParameterInputStage.OUTER_PADDING_PX, 0, 0);
        }
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
                inputMultiplexer.addProcessor(ingameHudStage);
                inputMultiplexer.addProcessor(gestureDetector);
                inputMultiplexer.addProcessor(inputProcessor);
                setActiveStage(ingameHudStage);
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
        ingameHudStage.dispose();
        menuStage.dispose();
        // might try to dispose the same stage twice
        super.dispose();
    }

    private void addIngameMenuListeners() {
        // exit button
        final List<TextButton> buttons = menuStage.getButtons();
        buttons.get(0).addListener(new ExceptionLoggingChangeListener(() -> {
            final Dialog confirmDialog = dialogFactory.createConfirmDialog("Your progress will be lost. Are you " +
                    "sure?\n",
                this::exitToMenu);
            confirmDialog.show(menuStage);
        }));
        // retry button
        buttons.get(1).addListener(new ExceptionLoggingChangeListener(() -> {
            final Dialog confirmDialog = dialogFactory.createConfirmDialog("Your progress will be lost. Are you " +
                    "sure?\n",
                this::resetGame);
            confirmDialog.show(menuStage);
        }));
        // continue button
        buttons.get(2).addListener(new ExceptionLoggingChangeListener(() -> activateStage(IngameStages.HUD)));
    }

    private void addParameterInputListeners() {
        parameterInputStage.randomButton.addListener(new ExceptionLoggingChangeListener(
            () -> {
                final long newSeed = System.currentTimeMillis();
                parameterInputStage.seedTextField.setText(String.valueOf(newSeed));
                cachedNewGamePreferences.setSeed(newSeed);
                newGamePrefDao.saveNewGamePreferences(cachedNewGamePreferences);
                eventBus.post(new SeedGeneratedEvent());
            }));

        parameterInputStage.pasteButton.addListener(new ExceptionLoggingChangeListener(() -> {
            final String clipboardContents = Gdx.app.getClipboard().getContents();
            if (clipboardContents != null) {
                final NewGamePreferences pastedPreferences =
                    NewGamePreferences.fromSharableString(clipboardContents);
                updateParameterInputsFromNewGamePrefs(pastedPreferences);
            }
        }));

        parameterInputStage.copyButton.addListener(new ExceptionLoggingChangeListener(
            () -> Gdx.app.getClipboard().setContents(cachedNewGamePreferences.toSharableString())));

        Stream.of(parameterInputStage.seedTextField, parameterInputStage.randomButton, parameterInputStage.sizeSelect,
                parameterInputStage.densitySelect, parameterInputStage.startingPositionSelect,
                parameterInputStage.pasteButton, parameterInputStage.difficultySelect)
            .forEach(actor -> actor.addListener(new ExceptionLoggingChangeListener(() -> {
                cachedNewGamePreferences.setSeed(parameterInputStage.getSeedParam());
                cachedNewGamePreferences.setMapSize(parameterInputStage.getMapSizeParam());
                cachedNewGamePreferences.setDensity(parameterInputStage.getMapDensityParam());
                cachedNewGamePreferences.setBotIntelligence(parameterInputStage.getBotIntelligence());
                cachedNewGamePreferences.setStartingPosition(parameterInputStage.getStartingPosition());
                newGamePrefDao.saveNewGamePreferences(cachedNewGamePreferences);
                eventBus.post(new RegenerateMapEvent(cachedNewGamePreferences.toGameParameters()));
            })));
        // only the settings that visually change the map need to cause centering
        Stream.of(parameterInputStage.seedTextField, parameterInputStage.randomButton, parameterInputStage.sizeSelect,
                parameterInputStage.densitySelect, parameterInputStage.pasteButton)
            .forEach(actor -> actor.addListener(new ExceptionLoggingChangeListener(this::centerMap)));
        parameterInputStage.backButton
            .addListener(new ExceptionLoggingChangeListener(screenNavigationController::transitionToPlayMenuScreen));
        parameterInputStage.playButton
            .addListener(new ExceptionLoggingChangeListener(() -> eventBus.post(new GameStartEvent())));
    }

    private void addHudListeners() {
        ingameHudStage.undoButton.addListener(new ExceptionLoggingChangeListener(() ->
            eventBus.post(new UndoMoveEvent())));

        ingameHudStage.endTurnButton.addListener(new ExceptionLoggingChangeListener(this::handleEndTurnAttempt));

        ingameHudStage.buyPeasantButton
            .addListener(new ExceptionLoggingChangeListener(() -> eventBus.post(new BuyPeasantEvent())));

        ingameHudStage.buyCastleButton
            .addListener(new ExceptionLoggingChangeListener(() -> eventBus.post(new BuyCastleEvent())));

        ingameHudStage.menuButton.addListener(new ExceptionLoggingChangeListener(() ->
            activateStage(IngameStages.MENU)));

        ingameHudStage.infoButton.addListener(new ExceptionLoggingChangeListener(() ->
            ingameScreenDialogHelper.showGameOrObjectiveInfo(ingameHudStage, cachedGameState.getRound(),
                cachedGameState.getScenarioMap(), cachedGameState.getObjectiveProgress(),
                cachedNewGamePreferences)));

        ingameHudStage.speedButton.addListener(new ExceptionLoggingChangeListener(() -> {
            // determine the next speed level with overflow, skipping Speed.INSTANT which is
            // used for the other button
            final int currentSpeedIndex = currentBotSpeed.ordinal();
            int nextSpeedIndex = currentSpeedIndex + 1;
            if (nextSpeedIndex >= Speed.values().length) {
                nextSpeedIndex = 0;
            }
            currentBotSpeed = Speed.values()[nextSpeedIndex];
            eventBus.post(new BotTurnSpeedChangedEvent(currentBotSpeed));
            ImageButtonStyle newStyle = null;
            switch (nextSpeedIndex) {
                case 0:
                    newStyle = ingameHudStage.halfSpeedButtonStyle;
                    break;
                case 1:
                    newStyle = ingameHudStage.regularSpeedButtonStyle;
                    break;
                case 2:
                    newStyle = ingameHudStage.doubleSpeedButtonStyle;
                    break;
                default:
                    throw new IllegalStateException("Unknown speed index " + currentSpeedIndex);
            }
            ingameHudStage.speedButton.setStyle(newStyle);
        }));

        ingameHudStage.skipButton
            .addListener(new ExceptionLoggingChangeListener(() -> eventBus.post(new BotTurnSkippedEvent())));

    }

    private void loadNewGameParameterValues() {
        cachedNewGamePreferences = newGamePrefDao.getNewGamePreferences();
        updateParameterInputsFromNewGamePrefs(cachedNewGamePreferences);
    }

    private void updateParameterInputsFromNewGamePrefs(NewGamePreferences newGamePreferences) {
        // not ideal: when called after the event listeners are registered, the preferences will be persisted once
        // per call
        cachedNewGamePreferences.setNumberOfBotPlayers(newGamePreferences.getNumberOfBotPlayers());
        parameterInputStage.seedTextField.setText(String.valueOf(newGamePreferences.getSeed()));
        parameterInputStage.difficultySelect.setSelectedIndex(newGamePreferences.getBotIntelligence().ordinal());
        parameterInputStage.sizeSelect.setSelectedIndex(newGamePreferences.getMapSize().ordinal());
        parameterInputStage.densitySelect.setSelectedIndex(newGamePreferences.getDensity().ordinal());
        parameterInputStage.updateNumberOfStartingPositions(newGamePreferences.getNumberOfBotPlayers() + 1);
        parameterInputStage.startingPositionSelect.setSelectedIndex(newGamePreferences.getStartingPosition());
    }

    public OrthographicCamera getCamera() {
        return ingameCamera;
    }

    public IngameHudStage getHudStage() {
        return ingameHudStage;
    }

    public MenuStage getMenuStage() {
        return menuStage;
    }

    /**
     * Stages that can be displayed.
     */
    public enum IngameStages {
        PARAMETERS, HUD, MENU
    }

}

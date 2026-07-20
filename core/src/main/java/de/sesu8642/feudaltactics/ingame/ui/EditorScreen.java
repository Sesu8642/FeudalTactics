// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.ingame.dagger.IngameCamera;
import de.sesu8642.feudaltactics.ingame.dagger.IngameRenderer;
import de.sesu8642.feudaltactics.input.CombinedInputProcessor;
import de.sesu8642.feudaltactics.input.FeudalTacticsGestureDetector;
import de.sesu8642.feudaltactics.lib.gamestate.*;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.FeudalTacticsDialog;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.renderer.MapRenderer;
import de.sesu8642.feudaltactics.renderer.TextureAtlasHelper;
import de.sesu8642.feudaltactics.shared.events.EditorHandContentUpdatedEvent;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;
import de.sesu8642.feudaltactics.shared.events.GameStatePastedEvent;
import lombok.extern.slf4j.Slf4j;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * {@link Screen} for playing a map.
 */
@Singleton
@Slf4j
public class EditorScreen extends GameScreen {

    private final OrthographicCamera ingameCamera;
    private final MapRenderer mapRenderer;
    private final InputMultiplexer inputMultiplexer;
    private final EventBus eventBus;
    private final ScreenNavigationController screenNavigationController;
    private final CombinedInputProcessor inputProcessor;
    private final FeudalTacticsGestureDetector gestureDetector;
    private final GameStateJsonHelper gameStateJsonHelper;
    private final EditorHudStage editorHudStage;
    private final EditorMenuStage menuStage;
    private final DialogFactory dialogFactory;
    private final TextureAtlasHelper textureAtlasHelper;
    private final LocalizationManager localizationManager;

    List<TileContent> possibleTileContents = ImmutableList.of(new Unit(Unit.UnitTypes.PEASANT),
        new Unit(Unit.UnitTypes.SPEARMAN), new Unit(Unit.UnitTypes.KNIGHT), new Unit(Unit.UnitTypes.BARON),
        new Tree(), new Castle());

    /**
     * Tile content for the hand, if a tile content is to be placed.
     */
    private TileContent heldTileContent;

    /**
     * Tile player index for the hand, if a tile is to be placed.
     */
    private Integer heldTilePlayerIndex;

    /**
     * Cached version of the game state from the editor controller.
     */
    private GameState cachedGameState;

    @Inject
    public EditorScreen(@IngameCamera OrthographicCamera ingameCamera, @MenuViewport Viewport viewport,
                        ScreenNavigationController screenNavigationController,
                        @IngameRenderer MapRenderer mapRenderer, EventBus eventBus,
                        InputMultiplexer inputMultiplexer, CombinedInputProcessor inputProcessor,
                        FeudalTacticsGestureDetector gestureDetector, GameStateJsonHelper gameStateJsonHelper,
                        EditorHudStage editorHudStage,
                        EditorMenuStage menuStage, DialogFactory dialogFactory,
                        TextureAtlasHelper textureAtlasHelper, LocalizationManager localizationManager) {
        super(ingameCamera, viewport, editorHudStage);
        this.ingameCamera = ingameCamera;
        this.screenNavigationController = screenNavigationController;
        this.mapRenderer = mapRenderer;
        this.inputMultiplexer = inputMultiplexer;
        this.eventBus = eventBus;
        this.inputProcessor = inputProcessor;
        this.gestureDetector = gestureDetector;
        this.gameStateJsonHelper = gameStateJsonHelper;
        this.editorHudStage = editorHudStage;
        this.menuStage = menuStage;
        this.dialogFactory = dialogFactory;
        this.textureAtlasHelper = textureAtlasHelper;
        this.localizationManager = localizationManager;
        addHudListeners();
        addEditorMenuListeners();
    }

    private void exitToMenu() {
        eventBus.post(new GameExitedEvent(null, null));   // Passing null makes sure no statistics are recorded
        screenNavigationController.transitionToMainMenuScreen();
        clearCache();
    }

    private void clearCache() {
        cachedGameState = null;
    }

    /**
     * Adjusts all the UI elements that need to be adjusted and displays dialogs if
     * appropriate.
     *
     * @param newGameState new game state
     */
    public void handleGameStateChange(GameState newGameState) {

        cachedGameState = newGameState;
        // update the UI

        final String hudStageInfoText = localizationManager.localizeText(TranslationKeys.EDITOR_HUD_TEXT_MAP_SIZE_INFO,
            newGameState.getMap().size());

        editorHudStage.infoTextLabel.setText(hudStageInfoText);
    }

    /**
     * Toggles the pause menu.
     */
    public void togglePause() {
        if (getActiveStage() == menuStage) {
            activateStage(EditorStages.HUD);
        } else if (getActiveStage() == editorHudStage) {
            activateStage(EditorStages.MENU);
        }
    }

    void activateStage(EditorStages stage) {
        inputMultiplexer.clear();
        switch (stage) {
            case MENU:
                inputMultiplexer.addProcessor(menuStage);
                inputMultiplexer.addProcessor(inputProcessor);
                setActiveStage(menuStage);
                break;
            case HUD:
                inputMultiplexer.addProcessor(editorHudStage);
                inputMultiplexer.addProcessor(gestureDetector);
                inputMultiplexer.addProcessor(inputProcessor);
                setActiveStage(editorHudStage);
                break;
            default:
                throw new IllegalStateException("Unknown stage " + stage);
        }
        // the super class only applies the resizing to the active stage
        Gdx.app.postRunnable(() -> resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight()));
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
        activateStage(EditorStages.HUD);
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
        editorHudStage.dispose();
        // might try to dispose the same stage twice
        super.dispose();
    }

    private void addHudListeners() {
        editorHudStage.menuButton.addListener(new ExceptionLoggingChangeListener(() ->
            activateStage(EditorStages.MENU)));
        editorHudStage.tileContentButton.addListener(new ExceptionLoggingChangeListener(() -> {
            heldTilePlayerIndex = null;
            if (heldTileContent == null) {
                heldTileContent = possibleTileContents.get(0);
            } else if (possibleTileContents.indexOf(heldTileContent) + 1 == possibleTileContents.size()) {
                heldTileContent = null;
            } else {
                heldTileContent = possibleTileContents.get(possibleTileContents.indexOf(heldTileContent) + 1);
            }
            eventBus.post(new EditorHandContentUpdatedEvent(heldTileContent));
            if (heldTileContent != null) {
                editorHudStage.updateHandContent(textureAtlasHelper.createSpriteForTileContent(heldTileContent));
            } else {
                editorHudStage.updateHandContent(null);
            }
        }));
        editorHudStage.tileButton.addListener(new ExceptionLoggingChangeListener(() -> {
            heldTileContent = null;
            if (heldTilePlayerIndex == null) {
                heldTilePlayerIndex = 0;
            } else if (heldTilePlayerIndex + 1 == MapRenderer.PLAYER_COLOR_PALETTE.size()) {
                heldTilePlayerIndex = null;
            } else {
                heldTilePlayerIndex++;
            }
            eventBus.post(new EditorHandContentUpdatedEvent(heldTilePlayerIndex));
            if (heldTilePlayerIndex != null) {
                editorHudStage.updateHandContent(textureAtlasHelper.getTileSprite(),
                    MapRenderer.PLAYER_COLOR_PALETTE.get(heldTilePlayerIndex));
            } else {
                editorHudStage.updateHandContent(null);
            }
        }));
    }

    private void addEditorMenuListeners() {
        // exit button
        final List<TextButton> buttons = menuStage.getButtons();
        buttons.get(0).addListener(new ExceptionLoggingChangeListener(() -> {
            final FeudalTacticsDialog confirmDialog =
                dialogFactory.createConfirmDialog(localizationManager.localizeText(TranslationKeys.DIALOG_TEXT_CONFIRM_LOST_PROGRESS),
                    this::exitToMenu);
            confirmDialog.show(menuStage);
        }));
        // continue button
        buttons.get(1).addListener(new ExceptionLoggingChangeListener(() -> activateStage(EditorStages.HUD)));
        // copy button
        buttons.get(2).addListener(new ExceptionLoggingChangeListener(
            () -> Gdx.app.getClipboard().setContents(gameStateJsonHelper.toJsonString(cachedGameState))));
        // paste button
        buttons.get(3).addListener(new ExceptionLoggingChangeListener(() -> {
            final String clipboardContents = Gdx.app.getClipboard().getContents();
            if (clipboardContents == null) {
                return;
            }
            final String trimmedClipboardContents = clipboardContents.trim();
            // try to parse into gameState
            tryToLoadGameState(trimmedClipboardContents);
        }));
    }

    private void tryToLoadGameState(String clipboardContents) {
        try {
            final GameState gameState = gameStateJsonHelper.fromJson(clipboardContents);
            eventBus.post(new GameStatePastedEvent(gameState));
        } catch (Exception e) {
            // unable to parse or validate, don't change anything
            log.info("unable to load or validate pasted game state", e);
        }
    }

    /**
     * Stages that can be displayed.
     */
    public enum EditorStages {
        HUD, MENU
    }

}

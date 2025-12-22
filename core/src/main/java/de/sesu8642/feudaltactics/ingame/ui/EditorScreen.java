// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.events.CenterMapEvent;
import de.sesu8642.feudaltactics.events.EditorHandContentUpdatedEvent;
import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.ingame.dagger.IngameCamera;
import de.sesu8642.feudaltactics.ingame.dagger.IngameRenderer;
import de.sesu8642.feudaltactics.input.CombinedInputProcessor;
import de.sesu8642.feudaltactics.input.FeudalTacticsGestureDetector;
import de.sesu8642.feudaltactics.lib.gamestate.*;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.common.ui.Margin;
import de.sesu8642.feudaltactics.renderer.MapRenderer;
import de.sesu8642.feudaltactics.renderer.TextureAtlasHelper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * {@link Screen} for playing a map.
 */
@Singleton
public class EditorScreen extends GameScreen {

    private final ScreenNavigationController screenNavigationController;
    private final OrthographicCamera ingameCamera;
    private final MapRenderer mapRenderer;
    private final InputMultiplexer inputMultiplexer;
    private final EventBus eventBus;
    private final EditorHudStage editorHudStage;
    private final TextureAtlasHelper textureAtlasHelper;

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
                        CombinedInputProcessor inputProcessor, FeudalTacticsGestureDetector gestureDetector,
                        InputMultiplexer inputMultiplexer, EditorHudStage editorHudStage,
                        TextureAtlasHelper textureAtlasHelper) {
        super(ingameCamera, viewport, editorHudStage);
        this.ingameCamera = ingameCamera;
        this.screenNavigationController = screenNavigationController;
        this.mapRenderer = mapRenderer;
        this.inputMultiplexer = inputMultiplexer;
        this.eventBus = eventBus;
        this.editorHudStage = editorHudStage;
        this.textureAtlasHelper = textureAtlasHelper;
        addHudListeners();

        inputMultiplexer.addProcessor(editorHudStage);
        inputMultiplexer.addProcessor(gestureDetector);
        inputMultiplexer.addProcessor(inputProcessor);
    }

    private void exitToMenu() {
        eventBus.post(new GameExitedEvent(null));
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

        cachedGameState = GameStateHelper.getCopy(newGameState);
        // update the UI

        final String hudStageInfoText = "Map Size: " + newGameState.getMap().size();

        editorHudStage.infoTextLabel.setText(hudStageInfoText);
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
    // TODO: extract this duplicate code
    private Margin calculateMapScreenArea() {
        // calculate what is the bigger rectangular area for the map to fit: above the
        // inputs or to their right
        final float aboveArea =
            ingameCamera.viewportWidth * (ingameCamera.viewportHeight - ParameterInputStage.TOTAL_INPUT_HEIGHT);
        final float rightArea =
            (ingameCamera.viewportWidth - ParameterInputStage.TOTAL_INPUT_WIDTH) * (ingameCamera.viewportHeight - ParameterInputStage.BUTTON_HEIGHT_PX - ParameterInputStage.OUTER_PADDING_PX);
        if (aboveArea > rightArea) {
            return new Margin(0, ParameterInputStage.TOTAL_INPUT_HEIGHT, 0, 0);
        } else {
            return new Margin(ParameterInputStage.TOTAL_INPUT_WIDTH,
                ParameterInputStage.BUTTON_HEIGHT_PX + ParameterInputStage.OUTER_PADDING_PX, 0, 0);
        }
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(inputMultiplexer);
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

}

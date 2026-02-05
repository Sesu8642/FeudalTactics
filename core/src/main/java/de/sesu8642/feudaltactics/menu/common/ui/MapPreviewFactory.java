// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.renderer.GameStateConverter;
import de.sesu8642.feudaltactics.renderer.TextureAtlasHelper;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Factory for creating map preview widgets. This factory makes it easy to create the previews without needing to
 * inject several of the rendering related dependencies.
 */
@Singleton
public class MapPreviewFactory {

    private final GameStateConverter gameStateConverter;
    private final TextureAtlasHelper textureAtlasHelper;
    private final ShapeRenderer shapeRenderer;
    private final SpriteBatch spriteBatch;

    /**
     * Constructor.
     */
    @Inject
    public MapPreviewFactory(GameStateConverter gameStateConverter, TextureAtlasHelper textureAtlasHelper,
                             ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
        this.gameStateConverter = gameStateConverter;
        this.textureAtlasHelper = textureAtlasHelper;
        this.shapeRenderer = shapeRenderer;
        this.spriteBatch = spriteBatch;
    }

    /**
     * Creates a map preview widget.
     *
     * @param gameState game state to be displayed
     */
    public MapPreviewWidget createPreviewWidget(GameState gameState) {
        return new MapPreviewWidget(gameState, gameStateConverter, textureAtlasHelper, shapeRenderer, spriteBatch);
    }

}

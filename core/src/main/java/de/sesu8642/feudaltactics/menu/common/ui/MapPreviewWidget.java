// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.renderer.GameStateConverter;
import de.sesu8642.feudaltactics.renderer.MapRenderer;
import de.sesu8642.feudaltactics.renderer.TextureAtlasHelper;

/**
 * Widget displaying a rendered map as part of the UI.
 */
public class MapPreviewWidget extends Image {

    private final OrthographicCamera camera;
    private final MapRenderer mapRenderer;
    private final GameState gameState;

    /**
     * Constructor.
     */
    public MapPreviewWidget(GameState gameState, GameStateConverter gameStateConverter,
                            TextureAtlasHelper textureAtlasHelper, ShapeRenderer shapeRenderer,
                            SpriteBatch spriteBatch) {
        this.gameState = gameState;
        camera = new OrthographicCamera();
        mapRenderer = new MapRenderer(camera, textureAtlasHelper, shapeRenderer, spriteBatch, gameStateConverter, true);
        mapRenderer.updateGameState(gameState);
    }

    private Texture createMapTexture() {
        // make the mapRenderer draw into the frameBuffer, then create a texture from what was drawn to use as the image
        final FrameBuffer frameBuffer = new FrameBuffer(Pixmap.Format.RGBA8888, (int) getWidth(), (int) getHeight(),
            false);

        frameBuffer.begin();
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        mapRenderer.render();

        frameBuffer.end();
        return frameBuffer.getColorBufferTexture();
    }

    @Override
    protected void sizeChanged() {
        super.sizeChanged();
        camera.viewportHeight = getHeight();
        camera.viewportWidth = getWidth();
        camera.update();
        mapRenderer.placeCameraForFullMapView(gameState, 10, 10, 10, 10);
        final TextureRegionDrawable drawable = new TextureRegionDrawable(createMapTexture());
        // flip the image because it's upside down otherwise for some reason
        drawable.getRegion().flip(false, true);
        setDrawable(drawable);
    }
}

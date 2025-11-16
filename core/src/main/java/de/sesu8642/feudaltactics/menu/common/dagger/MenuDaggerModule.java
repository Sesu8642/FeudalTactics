// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.dagger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.renderer.GameStateConverter;
import de.sesu8642.feudaltactics.renderer.MapRenderer;
import de.sesu8642.feudaltactics.renderer.TextureAtlasHelper;

import javax.inject.Singleton;

/**
 * Dagger module for things common to the menus.
 */
@Module
public class MenuDaggerModule {

    private MenuDaggerModule() {
        // prevent instantiation
        throw new AssertionError();
    }

    @Provides
    @Singleton
    @MenuCamera
    static OrthographicCamera provideMenuCamera() {
        return new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
    }

    @Provides
    @Singleton
    @MenuViewport
    static Viewport provideMenuViewport(@MenuCamera OrthographicCamera camera) {
        return new ScreenViewport(camera);
    }

    @Provides
    @Singleton
    @MenuBackgroundCamera
    static OrthographicCamera provideMenuBgCamera() {
        final OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        camera.zoom = 0.2F;
        return camera;
    }

    @Provides
    @Singleton
    @MenuBackgroundRenderer
    static MapRenderer provideMenuMapRenderer(@MenuBackgroundCamera OrthographicCamera camera,
                                              TextureAtlasHelper textureAtlasHelper, ShapeRenderer shapeRenderer,
                                              GameStateConverter gameStateConverter,
                                              SpriteBatch spriteBatch) {
        return new MapRenderer(camera, textureAtlasHelper, shapeRenderer, spriteBatch, gameStateConverter, true);
    }

}

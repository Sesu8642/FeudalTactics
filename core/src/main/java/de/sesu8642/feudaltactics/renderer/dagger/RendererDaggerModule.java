// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.renderer.dagger;

import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

import dagger.Module;
import dagger.Provides;

/** Dagger module for rendering. */
@Module
public class RendererDaggerModule {

	private RendererDaggerModule() {
		// prevent instantiation
		throw new AssertionError();
	}

	// the actual renderers are created in the modules where they are used

	@Provides
	@Singleton
	static TextureAtlas provideTextureAtlas() {
		return new TextureAtlas(Gdx.files.internal("textures.atlas"));
	}

	@Provides
	static ShapeRenderer provideShapeRenderer() {
		return new ShapeRenderer();
	}

	@Provides
	static SpriteBatch provideSpriteBatch() {
		return new SpriteBatch();
	}

}

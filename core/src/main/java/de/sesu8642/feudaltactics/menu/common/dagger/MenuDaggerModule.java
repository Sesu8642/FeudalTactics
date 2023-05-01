// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.dagger;

import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.dagger.VersionProperty;
import de.sesu8642.feudaltactics.events.CloseMenuEvent;
import de.sesu8642.feudaltactics.events.ExitGameEvent;
import de.sesu8642.feudaltactics.events.RetryGameUnconfirmedEvent;
import de.sesu8642.feudaltactics.menu.common.ui.MenuStage;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

/** Dagger module for things common to the menus. */
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
		OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.zoom = 0.2F;
		return camera;
	}

	@Provides
	@Singleton
	@MenuBackgroundRenderer
	static MapRenderer provideMenuMapRenderer(@MenuBackgroundCamera OrthographicCamera camera,
			TextureAtlas textureAtlas, ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
		return new MapRenderer(camera, textureAtlas, shapeRenderer, spriteBatch, true);
	}

	@Provides
	static MenuStage provideIngameMenuStage(EventBus eventBus, @MenuViewport Viewport viewport,
			@MenuBackgroundCamera OrthographicCamera camera, @MenuBackgroundRenderer MapRenderer mapRenderer, Skin skin,
			@VersionProperty String gameVersion) {
		MenuStage stage = new MenuStage(viewport, camera, mapRenderer, skin);
		stage.addButton("Exit", () -> eventBus.post(new ExitGameEvent()));
		stage.addButton("Retry", () -> eventBus.post(new RetryGameUnconfirmedEvent()));
		stage.addButton("Continue", () -> eventBus.post(new CloseMenuEvent()));
		stage.setBottomRightLabelText(String.format("Version %s", gameVersion));
		return stage;
	}

}

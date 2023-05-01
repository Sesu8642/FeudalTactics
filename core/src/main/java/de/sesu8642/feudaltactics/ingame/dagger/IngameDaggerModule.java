// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.dagger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.google.common.eventbus.EventBus;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.dagger.EnableDeepWaterRenderingProperty;
import de.sesu8642.feudaltactics.dagger.PreferencesPrefixProperty;
import de.sesu8642.feudaltactics.ingame.AutoSaveRepository;
import de.sesu8642.feudaltactics.ingame.NewGamePreferencesDao;
import de.sesu8642.feudaltactics.lib.ingame.GameController;
import de.sesu8642.feudaltactics.lib.ingame.botai.BotAi;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

/** Dagger module for ingame things. */
@Module
public class IngameDaggerModule {

	private IngameDaggerModule() {
		// prevent instantiation
		throw new AssertionError();
	}

	@Provides
	@Singleton
	static BotAi provideBoaAi(EventBus eventBus, MainPreferencesDao mainPrefsDao) {
		return new BotAi(eventBus, mainPrefsDao);
	}

	@Provides
	@Singleton
	static GameController provideGameController(EventBus eventBus, ExecutorService botTurnExecutor, BotAi botAi,
			AutoSaveRepository autoSaveRepo) {
		return new GameController(eventBus, botTurnExecutor, botAi, autoSaveRepo);
	}

	@Provides
	@Singleton
	@IngameCamera
	static OrthographicCamera provideIngameCamera() {
		OrthographicCamera camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// rotate if making art like the logo
//		camera.rotate(30);
		return camera;
	}

	@Provides
	@Singleton
	@IngameRenderer
	static MapRenderer provideIngameMapRenderer(@IngameCamera OrthographicCamera camera, TextureAtlas textureAtlas,
			ShapeRenderer shapeRenderer, SpriteBatch spriteBatch,
			@EnableDeepWaterRenderingProperty boolean enableDeepWaterRendering) {
		return new MapRenderer(camera, textureAtlas, shapeRenderer, spriteBatch, enableDeepWaterRendering);
	}

	@Provides
	@Singleton
	@NewGamePrefsPrefStore
	static Preferences provideNewGamePrefsPrefStore(@PreferencesPrefixProperty String prefix) {
		return Gdx.app.getPreferences(prefix + NewGamePreferencesDao.NEW_GAME_PREFERENCES_NAME);
	}

	@Provides
	@Singleton
	@AutoSavePrefStore
	static Preferences provideAutoSavePrefStore(@PreferencesPrefixProperty String prefix) {
		return Gdx.app.getPreferences(prefix + AutoSaveRepository.AUTO_SAVE_PREFERENCES_NAME);
	}

	@Provides
	@Singleton
	static ExecutorService provideBotAiExecutor() {
		return Executors.newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("botai-%d").build());
	}

}

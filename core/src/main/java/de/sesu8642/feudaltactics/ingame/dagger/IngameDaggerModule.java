// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.dagger;

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
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioGameStateLoader;
import de.sesu8642.feudaltactics.lib.ingame.GameController;
import de.sesu8642.feudaltactics.lib.ingame.botai.BotAi;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

import javax.inject.Singleton;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Dagger module for ingame things.
 */
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
    static ScenarioGameStateLoader provideScenarioGameStateLoader() {
        return new ScenarioGameStateLoader();
    }

    @Provides
    @Singleton
    static GameController provideGameController(EventBus eventBus, ExecutorService botTurnExecutor, BotAi botAi,
                                                AutoSaveRepository autoSaveRepo, ScenarioGameStateLoader scenarioGameStateLoader) {
        return new GameController(eventBus, botTurnExecutor, botAi, autoSaveRepo, scenarioGameStateLoader);
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
    @FullAutoSavePrefStore
    static Preferences provideFullAutoSavePrefStore(@PreferencesPrefixProperty String prefix) {
        return Gdx.app.getPreferences(prefix + AutoSaveRepository.FULL_AUTO_SAVE_PREFERENCES_NAME);
    }

    @Provides
    @Singleton
    @IncrementalAutoSavePrefStore
    static Preferences provideIncrementalAutoSavePrefStore(@PreferencesPrefixProperty String prefix) {
        return Gdx.app.getPreferences(prefix + AutoSaveRepository.INCREMENTAL_AUTO_SAVE_PREFERENCES_NAME);
    }

    @Provides
    @Singleton
    static ExecutorService provideBotAiExecutor() {
        return Executors
                .newSingleThreadExecutor(new ThreadFactoryBuilder().setNameFormat("botai-%d").setDaemon(true).build());
    }

}

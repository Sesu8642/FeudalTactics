// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.dagger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
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
import de.sesu8642.feudaltactics.backend.MapParameters;
import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.AboutScreen;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.AboutSlideStage;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.AboutSlides;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.DependencyLicenses;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.IngameCamera;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.IngameRenderer;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MainMenuScreen;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MainMenuStage;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MenuBackgroundCamera;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MenuBackgroundRenderer;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MenuCamera;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MenuViewport;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.PreferencesScreen;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.SplashScreenStage;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.TutorialScreen;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.TutorialSlideStage;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.TutorialSlides;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.VersionProperty;
import de.sesu8642.feudaltactics.frontend.events.CloseMenuEvent;
import de.sesu8642.feudaltactics.frontend.events.ExitGameEvent;
import de.sesu8642.feudaltactics.frontend.events.RetryGameUnconfirmedEvent;
import de.sesu8642.feudaltactics.frontend.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.frontend.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.frontend.persistence.NewGamePreferences;
import de.sesu8642.feudaltactics.frontend.persistence.NewGamePreferencesDao;
import de.sesu8642.feudaltactics.frontend.renderer.MapRenderer;
import de.sesu8642.feudaltactics.frontend.ui.screens.GameScreen;
import de.sesu8642.feudaltactics.frontend.ui.stages.MenuStage;
import de.sesu8642.feudaltactics.frontend.ui.stages.PreferencesStage;
import de.sesu8642.feudaltactics.frontend.ui.stages.ResizableResettableStage;
import de.sesu8642.feudaltactics.frontend.ui.stages.slidestage.AboutSlideFactory;
import de.sesu8642.feudaltactics.frontend.ui.stages.slidestage.Slide;
import de.sesu8642.feudaltactics.frontend.ui.stages.slidestage.SlideStage;
import de.sesu8642.feudaltactics.frontend.ui.stages.slidestage.TutorialSlideFactory;

/** Dagger module for the frontend. */
@Module
public class FrontendDaggerModule {

	private FrontendDaggerModule() {
		// prevent instantiation
		throw new AssertionError();
	}

	@Provides
	@Singleton
	static Properties provideGameConfig() {
		Properties config = new Properties();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try (InputStream inputStream = classLoader.getResourceAsStream("gameconfig.properties")) {
			config.load(inputStream);
		} catch (IOException e) {
			// modules can only throw unchecked exceptions so this needs to be converted
			throw new RuntimeException("Config cannot be read!", e);
		}
		return config;
	}

	@Provides
	@Singleton
	@DependencyLicenses
	static String provideDependencyLicensesText() {
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		StringBuilder resultBuilder = new StringBuilder();
		try (InputStream inputStream = classLoader.getResourceAsStream("licenses.txt")) {
			try (InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
					BufferedReader bufferedReader = new BufferedReader(inputStreamReader)) {
				resultBuilder.append(bufferedReader.lines().collect(Collectors.joining(System.lineSeparator())));
			}
		} catch (IOException e) {
			// modules can only throw unchecked exceptions so this needs to be converted
			throw new RuntimeException("Dependency licenses cannot be read!", e);
		}
		return resultBuilder.toString();
	}

	@Provides
	@Singleton
	@VersionProperty
	static String provideVersionProperty(Properties config) {
		return config.getProperty("version");
	}

	@Provides
	@Singleton
	static TextureAtlas provideTextureAtlas() {
		return new TextureAtlas(Gdx.files.internal("textures.atlas"));
	}

	@Provides
	@Singleton
	static Skin provideSkin() {
		return new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
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
			ShapeRenderer shapeRenderer, SpriteBatch spriteBatch) {
		return new MapRenderer(camera, textureAtlas, shapeRenderer, spriteBatch);
	}

	@Provides
	@Singleton
	@MenuCamera
	static OrthographicCamera provideMenuCamera() {
		return new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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
		return new MapRenderer(camera, textureAtlas, shapeRenderer, spriteBatch);
	}

	@Provides
	static InputMultiplexer provideInputMultiplexer() {
		return new InputMultiplexer();
	}

	@Provides
	static ShapeRenderer provideShapeRenderer() {
		return new ShapeRenderer();
	}

	@Provides
	static SpriteBatch provideSpriteBatch() {
		return new SpriteBatch();
	}

	@Provides
	@Singleton
	@MenuViewport
	static Viewport provideMenuViewport(@MenuCamera OrthographicCamera camera) {
		return new ScreenViewport(camera);
	}

	@Provides
	@Singleton
	@SplashScreenStage
	static ResizableResettableStage provideSplashScreenStage(@MenuViewport Viewport viewport,
			@MenuBackgroundCamera OrthographicCamera camera, @MainMenuScreen GameScreen mainMenuScreen,
			@MenuBackgroundRenderer MapRenderer mapRenderer, Skin skin) {
		// using a menu stage without buttons here
		MenuStage menuStage = new MenuStage(viewport, camera, mapRenderer, skin);
		menuStage.setBottomRightLabelText("By Sesu8642");
		return menuStage;
	}

	@Provides
	static MenuStage provideMenuStageWithVersion(EventBus eventBus, @MenuViewport Viewport viewport,
			@MenuBackgroundCamera OrthographicCamera camera, @MenuBackgroundRenderer MapRenderer mapRenderer, Skin skin,
			@VersionProperty String gameVersion) {
		MenuStage stage = new MenuStage(viewport, camera, mapRenderer, skin);
		stage.addButton("Exit", () -> eventBus.post(new ExitGameEvent()));
		stage.addButton("Retry", () -> eventBus.post(new RetryGameUnconfirmedEvent()));
		stage.addButton("Continue", () -> eventBus.post(new CloseMenuEvent()));
		stage.setBottomRightLabelText(String.format("Version %s", gameVersion));
		return stage;
	}

	@Provides
	@Singleton
	@MainMenuStage
	static MenuStage provideMainMenuWithVersion(EventBus eventBus, @MenuViewport Viewport viewport,
			@MenuBackgroundCamera OrthographicCamera camera, @MenuBackgroundRenderer MapRenderer mapRenderer, Skin skin,
			@VersionProperty String gameVersion, NewGamePreferencesDao newGamePreferencesDao) {
		// TODO: seems a little too much to do here
		MenuStage stage = new MenuStage(viewport, camera, mapRenderer, skin);
		stage.addButton("Play", () -> {
			NewGamePreferences savedPrefs = newGamePreferencesDao.getNewGamePreferences();
			eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INGAME_SCREEN));
			eventBus.post(new RegenerateMapEvent(savedPrefs.getBotIntelligence(),
					new MapParameters(System.currentTimeMillis(), savedPrefs.getMapSize().getAmountOfTiles(),
							savedPrefs.getDensity().getDensityFloat())));
		});
		// level editor was only used for creating the logo
//		stage.addButton("Level Editor",
//				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.EDITOR_SCREEN)));
		stage.addButton("Tutorial",
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.TUTORIAL_SCREEN)));
		stage.addButton("Preferences",
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.PREFERENCES_SCREEN)));
		stage.addButton("About",
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.ABOUT_SCREEN)));
		stage.setBottomLeftLabelText("Open Privacy Policy");
		stage.setBottomLeftLabelLink(
				"https://raw.githubusercontent.com/Sesu8642/FeudalTactics/master/privacy_policy.txt");
		stage.setBottomRightLabelText(String.format("Version %s", gameVersion));
		return stage;
	}

	@Provides
	@TutorialSlides
	static List<Slide> provideTutorialSlides(TutorialSlideFactory slideFactory) {
		return slideFactory.createAllSlides();
	}

	@Provides
	@Singleton
	@TutorialSlideStage
	static SlideStage provideTutorialSlideStage(EventBus eventBus, @MenuViewport Viewport viewport,
			@TutorialSlides List<Slide> tutorialSlides, @MenuBackgroundCamera OrthographicCamera camera,
			@MainMenuScreen GameScreen mainMenuScreen, Skin skin) {
		return new SlideStage(viewport, tutorialSlides,
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.MAIN_MENU_SCREEN)), camera,
				skin);
	}

	@Provides
	@Singleton
	@TutorialScreen
	static GameScreen provideTutorialScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
			@TutorialSlideStage SlideStage slideStage) {
		return new GameScreen(camera, viewport, slideStage);
	}

	@Provides
	@AboutSlides
	static List<Slide> provideAboutSlides(AboutSlideFactory slideFactory) {
		return slideFactory.createAllSlides();
	}

	@Provides
	@Singleton
	@AboutSlideStage
	static SlideStage provideAboutSlideStage(EventBus eventBus, @MenuViewport Viewport viewport,
			@AboutSlides List<Slide> aboutSlides, @MenuBackgroundCamera OrthographicCamera camera, Skin skin) {
		return new SlideStage(viewport, aboutSlides,
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.MAIN_MENU_SCREEN)), camera,
				skin);
	}

	@Provides
	@Singleton
	@AboutScreen
	static GameScreen provideAboutScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
			@AboutSlideStage SlideStage slideStage) {
		return new GameScreen(camera, viewport, slideStage);
	}

	@Provides
	@Singleton
	@MainMenuScreen
	static GameScreen provideMainMenuScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
			@MainMenuStage MenuStage menuStage) {
		return new GameScreen(camera, viewport, menuStage);
	}

	@Provides
	@Singleton
	@PreferencesScreen
	static GameScreen providePreferencesScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
			PreferencesStage preferencesStage) {
		return new GameScreen(camera, viewport, preferencesStage);
	}

}
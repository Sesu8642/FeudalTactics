package com.sesu8642.feudaltactics.dagger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import javax.inject.Provider;
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
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.MapRenderer;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.AboutScreen;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.AboutSlideStage;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.AboutSlides;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.DependencyLicenses;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.IngameCamera;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.IngameInputProcessor;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.IngameRenderer;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.MainMenuScreen;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.MainMenuStage;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.MenuBackgroundCamera;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.MenuBackgroundRenderer;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.MenuCamera;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.MenuViewport;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.SplashScreenStage;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.TutorialScreen;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.TutorialSlideStage;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.TutorialSlides;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.VersionProperty;
import com.sesu8642.feudaltactics.input.CombinedInputProcessor;
import com.sesu8642.feudaltactics.input.LocalIngameInputHandler;
import com.sesu8642.feudaltactics.ui.screens.EditorScreen;
import com.sesu8642.feudaltactics.ui.screens.GameScreen;
import com.sesu8642.feudaltactics.ui.screens.IngameScreen;
import com.sesu8642.feudaltactics.ui.stages.MenuStage;
import com.sesu8642.feudaltactics.ui.stages.ResizableResettableStage;
import com.sesu8642.feudaltactics.ui.stages.slidestage.AboutSlideFactory;
import com.sesu8642.feudaltactics.ui.stages.slidestage.Slide;
import com.sesu8642.feudaltactics.ui.stages.slidestage.SlideStage;
import com.sesu8642.feudaltactics.ui.stages.slidestage.TutorialSlideFactory;

import dagger.Module;
import dagger.Provides;

@Module
class DaggerModule {

	private DaggerModule() {
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
		return new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
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

	// will need the same for the editor later with a different input handler
	@Provides
	@Singleton
	@IngameInputProcessor
	static CombinedInputProcessor provideCombinedInputProcessor(LocalIngameInputHandler inputHandler,
			@IngameCamera OrthographicCamera camera) {
		return new CombinedInputProcessor(inputHandler, camera);
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
		menuStage.setBottomLabelText("By Sesu8642");
		return menuStage;
	}

	@Provides
	static MenuStage provideMenuStageWithVersion(@MenuViewport Viewport viewport,
			@MenuBackgroundCamera OrthographicCamera camera, @MenuBackgroundRenderer MapRenderer mapRenderer, Skin skin,
			@VersionProperty String gameVersion) {
		MenuStage stage = new MenuStage(viewport, camera, mapRenderer, skin);
		stage.setBottomLabelText(String.format("Version %s", gameVersion));
		return stage;
	}

	@Provides
	@Singleton
	@MainMenuStage
	static MenuStage provideMainMenuWithVersion(@MenuViewport Viewport viewport,
			@MenuBackgroundCamera OrthographicCamera camera, @MenuBackgroundRenderer MapRenderer mapRenderer, Skin skin,
			@VersionProperty String gameVersion, Provider<IngameScreen> ingameScreenProvider,
			Provider<EditorScreen> editorScreenProvider, @TutorialScreen Provider<GameScreen> tutorialScreenProvider,
			@AboutScreen Provider<GameScreen> aboutScreenProvider) {
		MenuStage stage = new MenuStage(viewport, camera, mapRenderer, skin);
		stage.addButton("Play", () -> FeudalTactics.game.setScreen(ingameScreenProvider.get()));
		stage.addButton("Tutorial", () -> FeudalTactics.game.setScreen(tutorialScreenProvider.get()));
		stage.addButton("About", () -> FeudalTactics.game.setScreen(aboutScreenProvider.get()));
		stage.setBottomLabelText(String.format("Version %s", gameVersion));
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
	static SlideStage provideTutorialSlideStage(@MenuViewport Viewport viewport,
			@TutorialSlides List<Slide> tutorialSlides, @MenuBackgroundCamera OrthographicCamera camera,
			@MainMenuScreen GameScreen mainMenuScreen, Skin skin) {
		return new SlideStage(viewport, tutorialSlides, () -> FeudalTactics.game.setScreen(mainMenuScreen), camera,
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
	static SlideStage provideAboutSlideStage(@MenuViewport Viewport viewport, @AboutSlides List<Slide> aboutSlides,
			@MenuBackgroundCamera OrthographicCamera camera, @MainMenuScreen GameScreen mainMenuScreen, Skin skin) {
		return new SlideStage(viewport, aboutSlides, () -> FeudalTactics.game.setScreen(mainMenuScreen), camera, skin);
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

}
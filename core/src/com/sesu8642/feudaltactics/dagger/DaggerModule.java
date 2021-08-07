package com.sesu8642.feudaltactics.dagger;

import java.util.List;

import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.sesu8642.feudaltactics.MapRenderer;
import com.sesu8642.feudaltactics.input.CombinedInputProcessor;
import com.sesu8642.feudaltactics.input.LocalInputHandler;
import com.sesu8642.feudaltactics.ui.stages.slidestage.Slide;
import com.sesu8642.feudaltactics.ui.stages.slidestage.TutorialSlideFactory;

import dagger.Module;
import dagger.Provides;

@Module
class DaggerModule {

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
	static CombinedInputProcessor provideCombinedInputProcessor(LocalInputHandler inputHandler,
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
	@TutorialSlides
	static List<Slide> provideTutorialSlides(TutorialSlideFactory slideFactory) {
		return slideFactory.createAllSlides();
	}
}
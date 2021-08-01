package com.sesu8642.feudaltactics.ui.stages;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.MapRenderer;
import com.sesu8642.feudaltactics.dagger.MenuBackgroundCamera;
import com.sesu8642.feudaltactics.dagger.MenuBackgroundRenderer;

@Singleton
public class StageFactory {

	private Skin skin;
	private TextureAtlas textureAtlas;
	private MapRenderer mapRenderer;
	private OrthographicCamera backgroundCamera;

	@Inject
	public StageFactory(Skin skin, TextureAtlas textureAtlas, @MenuBackgroundCamera OrthographicCamera backgroundCamera,
			@MenuBackgroundRenderer MapRenderer mapRenderer) {
		this.skin = skin;
		this.textureAtlas = textureAtlas;
		this.mapRenderer = mapRenderer;
		this.backgroundCamera = backgroundCamera;
	}

	public HudStage createHudStage(Viewport viewport,
			Map<com.sesu8642.feudaltactics.ui.stages.HudStage.ActionUIElements, Runnable> actions) {
		return new HudStage(viewport, actions, textureAtlas, skin);
	}

	public HudStage createHudStage(Viewport viewport, Batch batch,
			Map<com.sesu8642.feudaltactics.ui.stages.HudStage.ActionUIElements, Runnable> actions) {
		return new HudStage(viewport, batch, actions, textureAtlas, skin);
	}

	public ParameterInputStage createParameterInputStage(Viewport viewport,
			Map<com.sesu8642.feudaltactics.ui.stages.ParameterInputStage.ActionUIElements, Runnable> actions) {
		return new ParameterInputStage(viewport, actions, textureAtlas, skin);
	}

	public ParameterInputStage createParameterInputStage(Viewport viewport, Batch batch,
			Map<com.sesu8642.feudaltactics.ui.stages.ParameterInputStage.ActionUIElements, Runnable> actions) {
		return new ParameterInputStage(viewport, batch, actions, textureAtlas, skin);
	}

	public GenericMenuStage createMenuStage(Viewport viewport, LinkedHashMap<String, Runnable> buttonData) {
		return new GenericMenuStage(viewport, buttonData, backgroundCamera, mapRenderer, skin);
	}

	public GenericMenuStage createMenuStage(Viewport viewport, Batch batch,
			LinkedHashMap<String, Runnable> buttonData) {
		return new GenericMenuStage(viewport, batch, buttonData, backgroundCamera, mapRenderer, skin);
	}

	public GenericSlideStage createSlideStage(Viewport viewport, List<Slide> slides, Runnable finishedCallback) {
		return new GenericSlideStage(viewport, slides, finishedCallback, backgroundCamera, skin);
	}

	public GenericSlideStage createSlideStage(Viewport viewport, Batch batch, List<Slide> slides,
			Runnable finishedCallback) {
		return new GenericSlideStage(viewport, batch, slides, finishedCallback, backgroundCamera, skin);
	}

}

package com.sesu8642.feudaltactics.ui.screens;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.EditorController;
import com.sesu8642.feudaltactics.MapRenderer;
import com.sesu8642.feudaltactics.dagger.IngameCamera;
import com.sesu8642.feudaltactics.dagger.IngameRenderer;
import com.sesu8642.feudaltactics.dagger.MenuCamera;
import com.sesu8642.feudaltactics.input.CombinedInputProcessor;
import com.sesu8642.feudaltactics.input.EditorInputHandler;

@Singleton
public class EditorScreen implements Screen {

	private OrthographicCamera ingameCamera;
	private OrthographicCamera menuCamera;
	private MapRenderer mapRenderer;
	private InputMultiplexer multiplexer;
	private EditorInputHandler inputValidator;
	private EditorController editorController;
	
	private Stage stage;
	private Table rootTable;
	private Viewport viewport;

	@Inject
	public EditorScreen(@IngameCamera OrthographicCamera ingameCamera, @MenuCamera OrthographicCamera menuCamera, @IngameRenderer MapRenderer mapRenderer) {
		editorController = new EditorController();
		inputValidator = new EditorInputHandler(editorController);
		//editorController.setHud(this);
		this.ingameCamera = ingameCamera;
		this.menuCamera = menuCamera;
		this.mapRenderer = mapRenderer;
		editorController.setMapRenderer(mapRenderer);
		initUI();
		CombinedInputProcessor inputProcessor = new CombinedInputProcessor(inputValidator, ingameCamera);
		multiplexer = new InputMultiplexer();
		multiplexer.addProcessor(stage);
		multiplexer.addProcessor(new GestureDetector(inputProcessor));
		multiplexer.addProcessor(inputProcessor);
	}

	private void initUI() {
		viewport = new ScreenViewport(menuCamera);
		stage = new Stage(viewport);
		rootTable = new Table();
		rootTable.setDebug(true);
		rootTable.setFillParent(true);
		stage.addActor(rootTable);
	}
	
	@Override
	public void show() {
		Gdx.input.setInputProcessor(multiplexer);
		ingameCamera.position.set(ingameCamera.viewportWidth, ingameCamera.viewportHeight, 0);
		ingameCamera.update();
		ingameCamera.zoom = 0.2F;
		editorController.generateEmptyMap();
	}

	@Override
	public void render(float delta) {
		ingameCamera.update();
		mapRenderer.render();
		viewport.apply();
		stage.draw();
		stage.act();
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		viewport.apply();
		rootTable.pack(); // VERY IMPORTANT!!! makes everything scale correctly on startup and going fullscreen etc.; took me hours to find out
		ingameCamera.viewportHeight = height;
		ingameCamera.viewportWidth = width;
		ingameCamera.update();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		mapRenderer.dispose();
		stage.dispose();
	}

	public OrthographicCamera getCamera() {
		return ingameCamera;
	}

}

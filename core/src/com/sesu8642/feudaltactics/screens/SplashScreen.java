package com.sesu8642.feudaltactics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;

public class SplashScreen implements Screen {
	private Stage stage;
	private Table rootTable;
	private Viewport viewport;
	private FeudalTactics game;
	private long startTime;
	
	public SplashScreen(FeudalTactics game) {
		this.game = game;
		Camera camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);
		stage = new Stage(viewport);
		Image logo = new Image(new Texture(Gdx.files.internal("logo.png")));		
		rootTable = new Table();
		rootTable.setFillParent(true);
//		rootTable.setDebug(true);
		rootTable.defaults().minSize(0).fillX().expandY();
		rootTable.add(logo).prefHeight(Value.percentWidth(0.51F, rootTable)).width(Value.percentHeight(1.95F));
		stage.addActor(rootTable);
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0.2f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		viewport.apply();
		stage.draw();
		stage.act();
	    if(TimeUtils.timeSinceMillis(startTime) > 1000){
	    	game.setScreen(new MainMenuScreen(game));
	    }
	}

	@Override
	public void resize(int width, int height) {
		viewport.update(width, height, true);
		viewport.apply();
		rootTable.pack();
	}

	@Override
	public void show() {
		startTime=TimeUtils.millis();
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}

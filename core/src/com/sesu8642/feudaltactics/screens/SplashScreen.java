package com.sesu8642.feudaltactics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.TimeUtils;
import com.sesu8642.feudaltactics.FeudalTactics;

public class SplashScreen implements Screen {
	// https://www.youtube.com/watch?v=oNPD78okXUw&list=PLXY8okVWvwZ0JOwHiH1TntAdq-UDPnC2L&index=3
	
	private FeudalTactics game;
	private SpriteBatch batch;
	private Sprite splash;
	private long startTime;
	
	public SplashScreen(FeudalTactics game) {
		this.game = game;
	}
	
	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		splash.setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		batch.begin();
		splash.draw(batch);
		batch.end();
	    if(TimeUtils.timeSinceMillis(startTime) > 1000){
	    	game.setScreen(new IngameScreen(game));
	    }
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void show() {
		startTime=TimeUtils.millis();
		batch = new SpriteBatch();
		splash = new Sprite(new Texture("background.png"));
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
		batch.dispose();
		splash.getTexture().dispose();
	}

}

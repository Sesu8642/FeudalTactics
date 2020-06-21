package com.sesu8642.feudaltactics;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.sesu8642.feudaltactics.screens.SplashScreen;

public class FeudalTactics extends Game {

	static public Skin skin;
	static public TextureAtlas textureAtlas;

	@Override
	public void create() {
		textureAtlas = new TextureAtlas(Gdx.files.internal("textures.atlas"));
		skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
		setScreen(new SplashScreen(this));
//		setScreen(new MainMenuScreen(this));
	}

	@Override
	public void render() {
		super.render();
	}

	@Override
	public void resize(int width, int height) {
		super.resize(width, height);
	}

	@Override
	public void dispose() {
		skin.dispose();
		super.dispose();
	}

}
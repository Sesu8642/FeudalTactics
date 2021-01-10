package com.sesu8642.feudaltactics;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.sesu8642.feudaltactics.engine.PreferencesHelper;
import com.sesu8642.feudaltactics.ui.IngameScreen;
import com.sesu8642.feudaltactics.ui.SplashScreen;

public class FeudalTactics extends Game {

	static public Skin skin;
	static public TextureAtlas textureAtlas;
	static public FeudalTactics game;
	static public final Color buttonIconColor = new Color(1, 0.7F, 0.15F, 1);
	static public final Color disabledButtonIconColor = new Color(0.75F, 0.75F, 0.75F, 1);
	static public final Color backgroundColor = new Color(0, 0.2f, 0.8f, 1);

	@Override
	public void create() {
		game = this;
		textureAtlas = new TextureAtlas(Gdx.files.internal("textures.atlas"));
		skin = new Skin(Gdx.files.internal("skin/pixthulhu-ui.json"));
		if (PreferencesHelper.getNoOfAutoSaves() > 0) {
			setScreen(new IngameScreen(true));
		} else {
			setScreen(new SplashScreen());
		}
		// do not close on android back key
		Gdx.input.setCatchKey(Keys.BACK, true);
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
		super.dispose();
		skin.dispose();
		textureAtlas.dispose();
	}

}
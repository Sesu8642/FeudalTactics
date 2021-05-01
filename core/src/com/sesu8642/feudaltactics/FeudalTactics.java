package com.sesu8642.feudaltactics;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.sesu8642.feudaltactics.dagger.FeudalTacticsComponent;
import com.sesu8642.feudaltactics.dagger.DaggerFeudalTacticsComponent;
import com.sesu8642.feudaltactics.preferences.PreferencesHelper;
import com.sesu8642.feudaltactics.ui.screens.IngameScreen;
import com.sesu8642.feudaltactics.ui.screens.SplashScreen;

public class FeudalTactics extends Game {

	static public FeudalTactics game;
	static public final Color buttonIconColor = new Color(1, 0.7F, 0.15F, 1);
	static public final Color disabledButtonIconColor = new Color(0.75F, 0.75F, 0.75F, 1);
	static public final Color backgroundColor = new Color(0, 0.2f, 0.8f, 1);

	@Override
	public void create() {
		game = this;
		// if Eclipse cannot resolve this: https://stackoverflow.com/a/31669111 (note: too lazy to try it)
		FeudalTacticsComponent component = DaggerFeudalTacticsComponent.create();
		if (PreferencesHelper.getNoOfAutoSaves() > 0) {
			setScreen(component.getIngameScreen());
			component.getIngameScreen().loadAutoSave();
		} else {
			setScreen(component.getSplashScreen());
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
	}

}
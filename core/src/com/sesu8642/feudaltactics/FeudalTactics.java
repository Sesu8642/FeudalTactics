package com.sesu8642.feudaltactics;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.sesu8642.feudaltactics.dagger.DaggerFeudalTacticsComponent;
import com.sesu8642.feudaltactics.dagger.FeudalTacticsComponent;
import com.sesu8642.feudaltactics.preferences.PreferencesHelper;

/** The game's entry point. */
public class FeudalTactics extends Game {

	public static FeudalTactics game;
	// TODO: put those in a custom skin
	public static final Color buttonIconColor = new Color(1, 0.7F, 0.15F, 1);
	public static final Color disabledButtonIconColor = new Color(0.75F, 0.75F, 0.75F, 1);
	public static final Color backgroundColor = new Color(0, 0.2f, 0.8f, 1);

	@Override
	public void create() {
		game = this;
		// if Eclipse cannot resolve this: https://stackoverflow.com/a/31669111 (note:
		// too lazy to try it)
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
}
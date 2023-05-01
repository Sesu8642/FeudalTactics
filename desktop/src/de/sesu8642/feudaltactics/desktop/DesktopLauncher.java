// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.desktop;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

import de.sesu8642.feudaltactics.FeudalTactics;
import de.sesu8642.feudaltactics.lib.ingame.botai.BotAi;

public class DesktopLauncher {

	private static final String TAG = BotAi.class.getName();

	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Feudal Tactics");
		config.setWindowIcon("square_logo_64.png");
		config.setWindowedMode(1600, 900);
		try {
			new Lwjgl3Application(new FeudalTactics(), config);
		} catch (Exception e) {
			Gdx.app.error(TAG, "the game crashed because of an unexpected exception", e);
			System.exit(1);
		}
	}
}

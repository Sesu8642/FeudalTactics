package com.sesu8642.feudaltactics.desktop;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.sesu8642.feudaltactics.FeudalTactics;

public class DesktopLauncher {
	public static void main(String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setTitle("Feudal Tactics");
		config.setWindowIcon("square_logo_64.png");
		new Lwjgl3Application(new FeudalTactics(), config);
	}
}

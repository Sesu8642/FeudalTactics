package com.sesu8642.feudaltactics.desktop;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.sesu8642.feudaltactics.FeudalTactics;

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.title = "Feudal Tactics";
		// java.awt.Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		// config.width = (int) screenSize.getWidth();
		// config.height = (int) screenSize.getHeight();
		config.width = 1600;
		config.height = 900;
		config.fullscreen = false;
		config.addIcon("square_logo_64.png", FileType.Internal);
		new LwjglApplication(new FeudalTactics(), config);
	}
}

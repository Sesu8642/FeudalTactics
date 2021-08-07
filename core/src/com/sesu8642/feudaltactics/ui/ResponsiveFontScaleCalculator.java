package com.sesu8642.feudaltactics.ui;

import com.badlogic.gdx.Gdx;

public class ResponsiveFontScaleCalculator {

	public static float calculateFontScale() {
		return Gdx.graphics.getWidth() / 1000;
	}
	
}

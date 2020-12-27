package com.sesu8642.feudaltactics.engine;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.sesu8642.feudaltactics.engine.BotAI.Intelligence;
import com.sesu8642.feudaltactics.engine.NewGamePreferences.Densities;
import com.sesu8642.feudaltactics.engine.NewGamePreferences.MapSizes;

public class PreferencesHelper {
	
	public static void saveNewGamePreferences(NewGamePreferences prefs) {
		Preferences newGamePrefs = Gdx.app.getPreferences("newGamePreferences");
		newGamePrefs.putInteger("botIntelligence", prefs.getBotIntelligence().ordinal());
		newGamePrefs.putInteger("mapSize", prefs.getMapSize().ordinal());
		newGamePrefs.putInteger("density", prefs.getDensity().ordinal());
		newGamePrefs.flush();
	}
	
	public static NewGamePreferences getNewGamePreferences() {
		Preferences newGamePrefs = Gdx.app.getPreferences("newGamePreferences");
		Intelligence botIntelligence = Intelligence.values()[newGamePrefs.getInteger("botIntelligence", 0)];
		MapSizes mapSize = MapSizes.values()[newGamePrefs.getInteger("mapSize", 0)];
		Densities density= Densities.values()[newGamePrefs.getInteger("density", 0)];
		return new NewGamePreferences(botIntelligence, mapSize, density);
	}
}

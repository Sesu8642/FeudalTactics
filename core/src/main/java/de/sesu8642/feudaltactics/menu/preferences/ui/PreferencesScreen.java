// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.preferences.MainGamePreferences;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;

/** Screen for the preferences menu. */
@Singleton
public class PreferencesScreen extends GameScreen {

	private MainPreferencesDao mainPrefsDao;

	@Inject
	public PreferencesScreen(MainPreferencesDao mainPrefsDao, @MenuCamera OrthographicCamera camera,
			@MenuViewport Viewport viewport, PreferencesStage preferencesStage) {
		super(camera, viewport, preferencesStage);
		this.mainPrefsDao = mainPrefsDao;
	}

	void saveUpdatedPreferences(MainGamePreferences mainPreferences) {
		mainPrefsDao.saveMainPreferences(mainPreferences);
	}

}
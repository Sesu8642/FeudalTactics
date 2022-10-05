// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.ui.screens;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MenuCamera;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MenuViewport;
import de.sesu8642.feudaltactics.frontend.persistence.MainGamePreferences;
import de.sesu8642.feudaltactics.frontend.persistence.MainPreferencesDao;
import de.sesu8642.feudaltactics.frontend.ui.stages.slidestage.PreferencesStage;

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
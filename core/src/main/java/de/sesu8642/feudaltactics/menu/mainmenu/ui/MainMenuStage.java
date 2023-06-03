// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.mainmenu.ui;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.dagger.VersionProperty;
import de.sesu8642.feudaltactics.ingame.NewGamePreferencesDao;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundRenderer;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.MenuStage;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

@Singleton
public class MainMenuStage extends MenuStage {

	private static final List<String> BUTTON_TEXTS = ImmutableList.of("Play", "Tutorial", "Preferences", "Information");

	@Inject
	public MainMenuStage(EventBus eventBus, @MenuViewport Viewport viewport,
			@MenuBackgroundCamera OrthographicCamera camera, @MenuBackgroundRenderer MapRenderer mapRenderer, Skin skin,
			@VersionProperty String gameVersion, NewGamePreferencesDao newGamePreferencesDao) {
		super(viewport, BUTTON_TEXTS, camera, mapRenderer, skin);
		setBottomRightLabelText(String.format("Version %s", gameVersion));
	}

}

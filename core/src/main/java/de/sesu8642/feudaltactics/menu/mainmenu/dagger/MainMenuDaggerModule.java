// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.mainmenu.dagger;

import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;

import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.dagger.VersionProperty;
import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.ingame.MapParameters;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.NewGamePreferencesDao;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundRenderer;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.MenuStage;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

/** Dagger module for the main menu (but not its entries). */
@Module
public class MainMenuDaggerModule {

	private MainMenuDaggerModule() {
		// prevent instantiation
		throw new AssertionError();
	}

	@Provides
	@Singleton
	@MainMenuStage
	static MenuStage provideMainMenuWithVersion(EventBus eventBus, @MenuViewport Viewport viewport,
			@MenuBackgroundCamera OrthographicCamera camera, @MenuBackgroundRenderer MapRenderer mapRenderer, Skin skin,
			@VersionProperty String gameVersion, NewGamePreferencesDao newGamePreferencesDao) {
		// TODO: seems a little too much to do here
		MenuStage stage = new MenuStage(viewport, camera, mapRenderer, skin);
		stage.addButton("Play", () -> {
			NewGamePreferences savedPrefs = newGamePreferencesDao.getNewGamePreferences();
			eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INGAME_SCREEN));
			eventBus.post(new RegenerateMapEvent(savedPrefs.getBotIntelligence(),
					new MapParameters(System.currentTimeMillis(), savedPrefs.getMapSize().getAmountOfTiles(),
							savedPrefs.getDensity().getDensityFloat())));
		});
		// level editor was only used for creating the logo
//		stage.addButton("Level Editor",
//				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.EDITOR_SCREEN)));
		stage.addButton("Tutorial",
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.TUTORIAL_SCREEN)));
		stage.addButton("Preferences",
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.PREFERENCES_SCREEN)));
		stage.addButton("Information",
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INFORMATION_MENU_SCREEN)));
		stage.setBottomRightLabelText(String.format("Version %s", gameVersion));
		return stage;
	}

}

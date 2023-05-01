// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.splashscreen.dagger;

import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;

import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundRenderer;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.MenuStage;
import de.sesu8642.feudaltactics.menu.common.ui.ResizableResettableStage;
import de.sesu8642.feudaltactics.menu.mainmenu.ui.MainMenuScreen;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

/** Dagger module for the splash screen. */
@Module
public class SplashScreenDaggerModule {

	private SplashScreenDaggerModule() {
		// prevent instantiation
		throw new AssertionError();
	}

	@Provides
	@Singleton
	@SplashScreenStage
	static ResizableResettableStage provideSplashScreenStage(@MenuViewport Viewport viewport,
			@MenuBackgroundCamera OrthographicCamera camera, MainMenuScreen mainMenuScreen,
			@MenuBackgroundRenderer MapRenderer mapRenderer, Skin skin) {
		// using a menu stage without buttons here
		MenuStage menuStage = new MenuStage(viewport, camera, mapRenderer, skin);
		menuStage.setBottomRightLabelText("By Sesu8642");
		return menuStage;
	}

}

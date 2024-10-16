// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.splashscreen.ui;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.collect.ImmutableList;

import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundRenderer;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.MenuStage;
import de.sesu8642.feudaltactics.menu.common.ui.SkinConstants;
import de.sesu8642.feudaltactics.menu.mainmenu.ui.MainMenuScreen;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

/**
 * Stage for the splash screen.
 */
@Singleton
public class SplashScreenStage extends MenuStage {

	/**
	 * Constructor. See {@link MenuStage#MenuStage}
	 */
	@Inject
	public SplashScreenStage(@MenuViewport Viewport viewport, @MenuBackgroundCamera OrthographicCamera camera,
			MainMenuScreen mainMenuScreen, @MenuBackgroundRenderer MapRenderer mapRenderer, Skin skin) {
		// using a menu stage without buttons here
		super(viewport, ImmutableList.of(), camera, mapRenderer, skin);
		Label bottomRightLabel = new Label("By Sesu8642", skin.get(SkinConstants.FONT_OVERLAY, LabelStyle.class));
		getBottomRightTable().add(bottomRightLabel);
	}

}

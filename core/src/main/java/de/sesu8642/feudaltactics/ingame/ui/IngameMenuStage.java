// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.dagger.VersionProperty;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundRenderer;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.CopyButton;
import de.sesu8642.feudaltactics.menu.common.ui.MenuStage;
import de.sesu8642.feudaltactics.menu.common.ui.SkinConstants;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

/**
 * Stage for the in-game "pause" menu.
 */
@Singleton
public class IngameMenuStage extends MenuStage {

	private static final List<String> BUTTON_TEXTS = ImmutableList.of("Exit", "Retry", "Continue");
	public Label bottomRightLabel;
	public CopyButton copyButton;

	/**
	 * Constructor. See {@link MenuStage#MenuStage}
	 */
	@Inject
	public IngameMenuStage(EventBus eventBus, @MenuViewport Viewport viewport,
			@MenuBackgroundCamera OrthographicCamera camera, @MenuBackgroundRenderer MapRenderer mapRenderer, Skin skin,
			@VersionProperty String gameVersion) {
		super(viewport, BUTTON_TEXTS, camera, mapRenderer, skin);
		bottomRightLabel = new Label("", skin.get(SkinConstants.FONT_OVERLAY, LabelStyle.class));
		getBottomRightTable().add(bottomRightLabel);
		copyButton = new CopyButton("", skin, false);
		getBottomRightTable().add(copyButton);
	}

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.ui.crashreportscreen;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MenuCamera;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MenuViewport;
import de.sesu8642.feudaltactics.frontend.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.frontend.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;

/**
 * {@link Stage} that displays information about the last crash for reporting.
 * Meant to be displayed when the user opens it via the menu. Transitions back
 * to the menu.
 */
@Singleton
public class CrashReportStageForMenu extends AbstractCrashReportStage {

	/**
	 * Constructor.
	 * 
	 * @param eventBus event bus
	 * @param viewport viewport for the stage
	 * @param camera   camera to use
	 * @param skin     game skin
	 */
	@Inject
	public CrashReportStageForMenu(EventBus eventBus, CrashReportSlide crashReportSlide,
			@MenuViewport Viewport viewport, @MenuCamera OrthographicCamera camera, Skin skin) {
		super(crashReportSlide, viewport, Arrays.asList(crashReportSlide),
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INFORMATION_MENU_SCREEN)),
				camera, skin);
	}

}

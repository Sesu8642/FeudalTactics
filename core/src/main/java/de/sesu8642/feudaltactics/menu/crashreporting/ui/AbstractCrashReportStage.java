// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.crashreporting.ui;

import java.util.List;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;

import de.sesu8642.feudaltactics.menu.common.ui.Slide;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;

/**
 * Abstract crash report stage. To grant access to the slide.
 */
public abstract class AbstractCrashReportStage extends SlideStage {

	CrashReportSlide crashReportSlide;

	AbstractCrashReportStage(CrashReportSlide crashReportSlide, Viewport viewport, List<Slide> slides,
			Runnable finishedCallback, OrthographicCamera camera, Skin skin) {
		super(viewport, slides, finishedCallback, camera, skin);
		this.crashReportSlide = crashReportSlide;
	}

}

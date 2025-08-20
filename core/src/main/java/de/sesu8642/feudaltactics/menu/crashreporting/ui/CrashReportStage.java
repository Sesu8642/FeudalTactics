// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.crashreporting.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;
import de.sesu8642.feudaltactics.platformspecific.Insets;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;

/**
 * {@link Stage} that displays information about the last crash for reporting.
 * Meant to be displayed when the user opens it via the menu. Transitions back
 * to the menu.
 */
@Singleton
public class CrashReportStage extends SlideStage {

    final CrashReportSlide crashReportSlide;

    /**
     * Constructor.
     *
     * @param viewport viewport for the stage
     * @param camera   camera to use
     * @param skin     game skin
     */
    @Inject
    public CrashReportStage(CrashReportSlide crashReportSlide, Insets insets, @MenuViewport Viewport viewport,
                            @MenuCamera OrthographicCamera camera, Skin skin) {
        super(viewport, Collections.singletonList(crashReportSlide), insets, camera, skin);
        this.crashReportSlide = crashReportSlide;
    }

}

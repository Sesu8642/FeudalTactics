// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.information.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;

/**
 * {@link Stage} that displays all the dependencies. Clicking them should open
 * details about their licensing.
 */
@Singleton
public class DependencyLicensesStage extends SlideStage {

    final DependencyListSlide dependencyListSlide;

    /**
     * Constructor.
     */
    @Inject
    public DependencyLicensesStage(EventBus eventBus, DependencyListSlide dependencyListSlide,
                                   @MenuViewport Viewport viewport, @MenuCamera OrthographicCamera camera, Skin skin) {
        super(viewport, Collections.singletonList(dependencyListSlide), camera, skin);
        this.dependencyListSlide = dependencyListSlide;
    }

}

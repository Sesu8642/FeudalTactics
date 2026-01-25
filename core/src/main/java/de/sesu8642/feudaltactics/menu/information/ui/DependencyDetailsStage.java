// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.information.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;

/**
 * {@link Stage} that displays licensing details about a dependency.
 */
@Singleton
public class DependencyDetailsStage extends SlideStage {

    final DependencyDetailsSlide dependencyDetailsSlide;

    /**
     * Constructor.
     */
    @Inject
    public DependencyDetailsStage(DependencyDetailsSlide dependencyDetailsSlide,
                                  @MenuViewport Viewport viewport, PlatformInsetsProvider platformInsetsProvider,
                                  @MenuCamera OrthographicCamera camera, Skin skin,
                                  LocalizationManager localizationManager) {
        super(viewport, Collections.singletonList(dependencyDetailsSlide), platformInsetsProvider, camera, skin, localizationManager);
        this.dependencyDetailsSlide = dependencyDetailsSlide;
    }

}

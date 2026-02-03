// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.about.dagger;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.menu.about.ui.AboutSlideFactory;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;

import javax.inject.Singleton;
import java.util.Collections;

/**
 * Dagger module for the about menu.
 */
@Module
public final class AboutDaggerModule {

    private AboutDaggerModule() {
        // prevent instantiation
        throw new AssertionError();
    }

    @Provides
    @Singleton
    @AboutSlideStage
    static SlideStage provideAboutSlideStage(ScreenNavigationController screenNavigationController,
                                             @MenuViewport Viewport viewport,
                                             AboutSlideFactory slideFactory,
                                             PlatformInsetsProvider platformInsetsProvider,
                                             @MenuBackgroundCamera OrthographicCamera camera, Skin skin,
                                             LocalizationManager localizationManager) {
        return new SlideStage(viewport, Collections.singletonList(slideFactory.createAboutSlide()),
            platformInsetsProvider,
            screenNavigationController::transitionToInformationMenuScreenPage1,
            camera, skin, localizationManager);
    }

    @Provides
    @Singleton
    @AboutScreen
    static GameScreen provideAboutScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                                         @AboutSlideStage SlideStage slideStage) {
        return new GameScreen(camera, viewport, slideStage);
    }

}

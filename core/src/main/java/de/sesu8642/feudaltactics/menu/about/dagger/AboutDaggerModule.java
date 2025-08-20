// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.about.dagger;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;
import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.menu.about.ui.AboutSlideFactory;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;
import de.sesu8642.feudaltactics.platformspecific.Insets;

import javax.inject.Singleton;
import java.util.Collections;

/**
 * Dagger module for the about menu.
 */
@Module
public class AboutDaggerModule {

    private AboutDaggerModule() {
        // prevent instantiation
        throw new AssertionError();
    }

    @Provides
    @Singleton
    @AboutSlideStage
    static SlideStage provideAboutSlideStage(EventBus eventBus, @MenuViewport Viewport viewport,
                                             AboutSlideFactory slideFactory, Insets insets,
                                             @MenuBackgroundCamera OrthographicCamera camera, Skin skin) {
        return new SlideStage(viewport, Collections.singletonList(slideFactory.createAboutSlide()), insets,
            () -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INFORMATION_MENU_SCREEN)),
            camera, skin);
    }

    @Provides
    @Singleton
    @AboutScreen
    static GameScreen provideAboutScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                                         @AboutSlideStage SlideStage slideStage) {
        return new GameScreen(camera, viewport, slideStage);
    }

}

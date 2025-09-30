// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.changelog.dagger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;
import com.google.common.io.Resources;
import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.dagger.PreferencesPrefixProperty;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.exceptions.InitializationException;
import de.sesu8642.feudaltactics.menu.changelog.GameVersionDao;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;
import de.sesu8642.feudaltactics.platformspecific.Insets;

import javax.inject.Singleton;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

/**
 * Dagger module for the changelog.
 */
@Module
public class ChangelogDaggerModule {

    private ChangelogDaggerModule() {
        // prevent instantiation
        throw new AssertionError();
    }

    @Provides
    @Singleton
    @ChangelogText
    static String provideChangelogText() {
        try {
            URL url = Resources.getResource("changelog.txt");
            return Resources.toString(url, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new InitializationException("Changelog cannot be read!", e);
        }
    }

    @Provides
    @Singleton
    @GameVersionPrefStore
    static Preferences provideGameVersionPrefStore(@PreferencesPrefixProperty String prefix) {
        return Gdx.app.getPreferences(prefix + GameVersionDao.VERSION_PREFERENCES_NAME);
    }

    @Provides
    @Singleton
    @ChangelogScreen
    static GameScreen provideChangelogScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                                             @ChangelogSlideStage SlideStage slideStage) {
        return new GameScreen(camera, viewport, slideStage);
    }

    @Provides
    @Singleton
    @ChangelogSlideStage
    static SlideStage provideChangelogSlideStage(EventBus eventBus, @MenuViewport Viewport viewport, Insets insets,
                                                 @ChangelogText String changelogText,
                                                 @MenuBackgroundCamera OrthographicCamera camera, Skin skin) {
        Slide changelogSlide = new Slide(skin, "Changelog").addLabel("Join the Feudal Tactics Community on Matrix! " +
            "See Information menu.").addLabel(changelogText);
        return new SlideStage(viewport, Collections.singletonList(changelogSlide), insets,
            () -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INFORMATION_MENU_SCREEN_2)),
            camera, skin);
    }

}

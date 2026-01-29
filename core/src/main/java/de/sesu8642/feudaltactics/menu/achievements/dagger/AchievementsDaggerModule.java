// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.dagger;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.menu.achievements.ui.AchievementsEventHandler;
import de.sesu8642.feudaltactics.menu.achievements.ui.AchievementsScreen;
import de.sesu8642.feudaltactics.menu.achievements.ui.AchievementsSlide;
import de.sesu8642.feudaltactics.menu.achievements.ui.AchievementsStage;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;

import javax.inject.Singleton;

/**
 * Dagger module for the achievements menu.
 */
@Module
public final class AchievementsDaggerModule {

    private AchievementsDaggerModule() {
        // prevent instantiation
        throw new AssertionError();
    }

}

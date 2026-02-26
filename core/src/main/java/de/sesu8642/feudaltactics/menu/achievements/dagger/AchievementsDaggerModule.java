// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.dagger;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.dagger.PreferencesPrefixProperty;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

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

    @Provides
    @Singleton
    @AchievementsPrefStore
    static Preferences provideAchievementsPrefsPrefStore(@PreferencesPrefixProperty String prefix) {
        return Gdx.app.getPreferences(prefix + AchievementRepository.ACHIEVEMENTS_NAME);
    }
}

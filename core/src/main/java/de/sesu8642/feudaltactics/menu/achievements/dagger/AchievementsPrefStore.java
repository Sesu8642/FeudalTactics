// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.dagger;

import javax.inject.Qualifier;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Qualifier for the statistics preferences.
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface AchievementsPrefStore {
}
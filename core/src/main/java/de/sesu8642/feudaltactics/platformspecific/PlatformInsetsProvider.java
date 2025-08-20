// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.platformspecific;

import com.badlogic.gdx.Application;

/**
 * Interface for finding out display {@link Insets}.
 */
public interface PlatformInsetsProvider {

    /**
     * Returns the current display insets.
     */
    Insets getInsets(Application app);
}

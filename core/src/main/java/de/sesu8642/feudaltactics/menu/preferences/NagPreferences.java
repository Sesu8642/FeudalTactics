// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Value object: nag preferences.
 */
public class NagPreferences {

    @Getter
    @Setter
    private boolean showTutorialNag;

    /**
     * Constructor.
     *
     * @param showTutorialNag whether the player should be offered to play the tutorial
     */
    public NagPreferences(boolean showTutorialNag) {
        this.showTutorialNag = showTutorialNag;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NagPreferences that = (NagPreferences) o;
        return showTutorialNag == that.showTutorialNag;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(showTutorialNag);
    }
}

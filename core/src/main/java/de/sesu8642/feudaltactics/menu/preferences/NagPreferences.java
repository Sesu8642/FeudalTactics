// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences;

import java.util.Objects;

/**
 * Value object: nag preferences.
 */
public class NagPreferences {

    private boolean showTutorialNag;

    /**
     * Constructor.
     *
     * @param showTutorialNag whether the player should be offered to play the tutorial
     */
    public NagPreferences(boolean showTutorialNag) {
        super();
        this.showTutorialNag = showTutorialNag;
    }

    public boolean isShowTutorialNag() {
        return showTutorialNag;
    }

    public void setShowTutorialNag(boolean showTutorialNag) {
        this.showTutorialNag = showTutorialNag;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        NagPreferences that = (NagPreferences) o;
        return showTutorialNag == that.showTutorialNag;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(showTutorialNag);
    }
}

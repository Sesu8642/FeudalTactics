// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Value object: main preferences.
 */
public class MainGamePreferences {

    @Getter
    @Setter
    private boolean warnAboutForgottenKingdoms;
    @Getter
    @Setter
    private boolean showEnemyTurns;
    @Getter
    @Setter
    private String language;

    /**
     * Constructor.
     *
     * @param warnAboutForgottenKingdoms whether to display a warning about a
     *                                   potentially forgotten kingdom when ending
     *                                   the turn.
     * @param showEnemyTurns             whether to visualize the enemies doing
     *                                   their turns
     * @param language                   the language display name e.g "English"
     */
    public MainGamePreferences(boolean warnAboutForgottenKingdoms, boolean showEnemyTurns, String language) {
        this.warnAboutForgottenKingdoms = warnAboutForgottenKingdoms;
        this.showEnemyTurns = showEnemyTurns;
        this.language = language;
    }

    @Override
    public int hashCode() {
        return Objects.hash(showEnemyTurns, warnAboutForgottenKingdoms);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final MainGamePreferences other = (MainGamePreferences) obj;
        return showEnemyTurns == other.showEnemyTurns && warnAboutForgottenKingdoms == other.warnAboutForgottenKingdoms;
    }

}

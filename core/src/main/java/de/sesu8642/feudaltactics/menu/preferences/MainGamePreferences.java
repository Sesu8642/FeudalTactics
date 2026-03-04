// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

/**
 * Value object: main preferences.
 */
@EqualsAndHashCode
public class MainGamePreferences {

    @Getter
    @Setter
    private boolean warnAboutForgottenKingdoms;
    @Getter
    @Setter
    private boolean showEnemyTurns;
    @Getter
    @Setter
    private SupportedLanguages language;

    /**
     * Constructor.
     *
     * @param warnAboutForgottenKingdoms whether to display a warning about a
     *                                   potentially forgotten kingdom when ending
     *                                   the turn.
     * @param showEnemyTurns             whether to visualize the enemies doing
     *                                   their turns
     * @param language                   language for the UI
     */
    public MainGamePreferences(boolean warnAboutForgottenKingdoms, boolean showEnemyTurns,
                               SupportedLanguages language) {
        this.warnAboutForgottenKingdoms = warnAboutForgottenKingdoms;
        this.showEnemyTurns = showEnemyTurns;
        this.language = language;
    }

}

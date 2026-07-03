// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences;

import de.sesu8642.feudaltactics.lib.ingame.botai.Speed;
import de.sesu8642.feudaltactics.localization.SupportedLanguage;
import lombok.*;

/**
 * Value object: main preferences.
 */
@Data
@AllArgsConstructor
public class MainGamePreferences {

    /**
     * Whether to display a warning about a potentially forgotten kingdom when ending the turn.
     */
    private boolean warnAboutForgottenKingdoms;

    /**
     * Whether to visualize the enemies doing their turns.
     */
    private boolean showEnemyTurns;

    /**
     * Speed multiplier for visualized enemy turns. Only relevant if {@link MainGamePreferences#showEnemyTurns} is true.
     */
    private Speed enemyTurnSpeed;

    /**
     * Language for the UI.
     */
    private SupportedLanguage language;

    /**
     * Returns a copy of the given preferences.
     */
    public static MainGamePreferences copyOf(MainGamePreferences original) {
        return new MainGamePreferences(original.warnAboutForgottenKingdoms, original.showEnemyTurns, original.enemyTurnSpeed, original.language);
    }

}

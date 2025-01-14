// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events.moves;

import de.sesu8642.feudaltactics.lib.gamestate.Kingdom;

/**
 * Event: A kingdom is selected to be the active kingdom.
 */
public class ActivateKingdomEvent {

    private final Kingdom kingdom;

    public ActivateKingdomEvent(Kingdom kingdom) {
        this.kingdom = kingdom;
    }

    public Kingdom getKingdom() {
        return kingdom;
    }

}

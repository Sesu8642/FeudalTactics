// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import lombok.Getter;
import lombok.Setter;

/**
 * Event: Scenario needs to be initialized.
 */
public class InitializeScenarioEvent {

    @Getter
    @Setter
    private Intelligence botIntelligence;
    @Getter
    @Setter
    private ScenarioMap scenarioMap;

    /**
     * Constructor.
     *
     * @param botIntelligence bot intelligence
     * @param scenarioMap     map to be loaded
     */
    public InitializeScenarioEvent(Intelligence botIntelligence, ScenarioMap scenarioMap) {
        super();
        this.botIntelligence = botIntelligence;
        this.scenarioMap = scenarioMap;
    }

}

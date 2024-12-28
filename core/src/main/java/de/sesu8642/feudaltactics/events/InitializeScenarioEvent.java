// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

/**
 * Event: Scenario needs to be initialized.
 */
public class InitializeScenarioEvent {

    private Intelligence botIntelligence;
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

    public Intelligence getBotIntelligence() {
        return botIntelligence;
    }

    public void setBotIntelligence(Intelligence botIntelligence) {
        this.botIntelligence = botIntelligence;
    }

    public ScenarioMap getScenarioMap() {
        return scenarioMap;
    }

    public void setScenarioMap(ScenarioMap scenarioMap) {
        this.scenarioMap = scenarioMap;
    }

}

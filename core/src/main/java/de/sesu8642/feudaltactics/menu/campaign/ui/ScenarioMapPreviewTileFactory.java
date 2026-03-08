// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.campaign.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioGameStateLoader;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
import de.sesu8642.feudaltactics.menu.common.ui.MapPreviewFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Factory for creating {@link ScenarioMapPreviewTile}s. This factory makes it easy to create the previews without
 * needing to
 * inject several of the rendering related dependencies.
 */
@Singleton
public class ScenarioMapPreviewTileFactory {

    private final ScenarioGameStateLoader scenarioGameStateLoader;
    private final MapPreviewFactory mapPreviewFactory;
    private final Skin skin;

    /**
     * Constructor.
     */
    @Inject
    public ScenarioMapPreviewTileFactory(ScenarioGameStateLoader scenarioGameStateLoader,
                                         MapPreviewFactory mapPreviewFactory, Skin skin) {
        this.scenarioGameStateLoader = scenarioGameStateLoader;
        this.mapPreviewFactory = mapPreviewFactory;
        this.skin = skin;
    }

    /**
     * Creates a map {@link ScenarioMapPreviewTile}.
     */
    public ScenarioMapPreviewTile createScenarioMapPreviewTile(ScenarioMap scenarioMap, boolean unlocked,
                                                               Medals bestMedal, int displayIndex) {
        return new ScenarioMapPreviewTile(scenarioMap, scenarioGameStateLoader, mapPreviewFactory, unlocked, bestMedal,
            displayIndex, skin);
    }

}

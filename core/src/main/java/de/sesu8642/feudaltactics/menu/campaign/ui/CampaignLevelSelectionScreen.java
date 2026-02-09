// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.campaign.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Screen for level selection.
 */
@Singleton
public class CampaignLevelSelectionScreen extends GameScreen {

    private final LevelSelectionStage levelSelectionStage;

    /**
     * Constructor.
     */
    @Inject
    public CampaignLevelSelectionScreen(ScreenNavigationController screenNavigationController,
                                        @MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                                        LevelSelectionStage levelSelectionStage) {
        super(camera, viewport, levelSelectionStage);
        this.levelSelectionStage = levelSelectionStage;
        registerEventListeners(screenNavigationController);
    }

    private void registerEventListeners(ScreenNavigationController screenNavigationController) {
        levelSelectionStage.setFinishedCallback(screenNavigationController::transitionToMainMenuScreen);
    }

}

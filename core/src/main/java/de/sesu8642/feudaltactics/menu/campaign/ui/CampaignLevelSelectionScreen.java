// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.campaign.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.collect.ImmutableList;
import com.google.common.eventbus.EventBus;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.*;
import de.sesu8642.feudaltactics.shared.events.InitializeScenarioEvent;
import de.sesu8642.feudaltactics.shared.events.moves.GameStartEvent;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * Screen for level selection.
 */
@Singleton
public class CampaignLevelSelectionScreen extends GameScreen {

    private static final List<String> DIFFICULTIES_KEYS =
        ImmutableList.of(TranslationKeys.GAME_PARAMETER_DIFFICULTY_EASY,
            TranslationKeys.GAME_PARAMETER_DIFFICULTY_MEDIUM, TranslationKeys.GAME_PARAMETER_DIFFICULTY_HARD,
            TranslationKeys.GAME_PARAMETER_DIFFICULTY_VERY_HARD);

    private final LevelSelectionStage levelSelectionStage;
    private final DialogFactory dialogFactory;
    private final LocalizationManager localizationManager;
    private final EventBus eventBus;

    private Intelligence selectedIntelligence = Intelligence.LEVEL_1;

    /**
     * Constructor.
     */
    @Inject
    public CampaignLevelSelectionScreen(ScreenNavigationController screenNavigationController,
                                        @MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                                        LevelSelectionStage levelSelectionStage, DialogFactory dialogFactory, LocalizationManager localizationManager, EventBus eventBus) {
        super(camera, viewport, levelSelectionStage);
        this.levelSelectionStage = levelSelectionStage;
        this.dialogFactory = dialogFactory;
        this.localizationManager = localizationManager;
        this.eventBus = eventBus;
        registerEventListeners(screenNavigationController);
    }

    private void registerEventListeners(ScreenNavigationController screenNavigationController) {
        levelSelectionStage.setFinishedCallback(screenNavigationController::transitionToMainMenuScreen);
        for (ScenarioMapPreviewTile scenarioMapPreviewTile : levelSelectionStage.levelSelectionSlide.scenarioMapPreviewTiles) {
            scenarioMapPreviewTile.addListener(new ExceptionLoggingClickListener(() -> {
                if (!scenarioMapPreviewTile.isUnlocked()) {
                    return;
                }
                ScenarioMap scenarioMap = scenarioMapPreviewTile.getScenarioMap();
                FeudalTacticsDialog dialog = dialogFactory.createDialog(result -> {
                    switch ((byte) result) {
                        case 1:
                            // play button
                            screenNavigationController.transitionToIngameScreen();
                            eventBus.post(new InitializeScenarioEvent(selectedIntelligence, scenarioMap));
                            eventBus.post(new GameStartEvent());
                            break;
                        case 0:
                            // do nothing on back button
                        default:
                            break;
                    }
                });
                dialog.text(scenarioMap.toString());
                dialog.button("BACK", (byte) 0);
                dialog.button("PLAY", (byte) 1);
                InsetsRespectingSelectBox<String> difficultySelect = dialogFactory.addSelectBoxToDialog(dialog, localizationManager.localizeTextBatch(DIFFICULTIES_KEYS));
                difficultySelect.addListener(new ExceptionLoggingChangeListener(() -> selectedIntelligence = Intelligence.values()[difficultySelect.getSelectedIndex()]));
                dialog.show(levelSelectionStage);
            }));
        }
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.FeudalTacticsDialog;
import de.sesu8642.feudaltactics.platformspecific.PlatformSharing;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Helper for displaying dialogs in the {@link IngameScreen}.
 */
@Singleton
public class IngameScreenDialogHelper {

    private final DialogFactory dialogFactory;
    private final TutorialDialogFactory tutorialDialogFactory;
    private final PlatformSharing platformSharing;
    private final LocalizationManager localizationManager;

    @Inject
    public IngameScreenDialogHelper(DialogFactory dialogFactory, TutorialDialogFactory tutorialDialogFactory,
                                    PlatformSharing platformSharing, LocalizationManager localizationManager) {
        this.dialogFactory = dialogFactory;
        this.tutorialDialogFactory = tutorialDialogFactory;
        this.platformSharing = platformSharing;
        this.localizationManager = localizationManager;
    }

    void showGiveUpGameMessage(Stage stage, boolean win, int winningRound, ScenarioMap scenarioMap,
                               NewGamePreferences newGamePreferences, Runnable onExit, Runnable onRetry) {
        final Dialog endDialog = dialogFactory.createDialog(result -> {
            switch ((byte) result) {
                case 1:
                    // exit button
                    onExit.run();
                    break;
                case 2:
                    // retry button
                    onRetry.run();
                    break;
                case 0:
                    // do nothing on continue button
                default:
                    break;
            }
        });
        endDialog.button(localizationManager.localizeText(TranslationKeys.BUTTON_DIALOG_EXIT), (byte) 1);
        final String sharingPreemble;
        if (win) {
            sharingPreemble = localizationManager.localizeText(TranslationKeys.SHARING_PREEMBLE_VICTORY, winningRound);
            endDialog.text(localizationManager.localizeText(TranslationKeys.DIALOG_TEXT_VICTORY_SURRENDER,
                winningRound));
            endDialog.button(localizationManager.localizeText(TranslationKeys.BUTTON_DIALOG_REPLAY), (byte) 2);
        } else {
            sharingPreemble = localizationManager.localizeText(TranslationKeys.SHARING_PREEMBLE_SURRENDER);
            endDialog.text(localizationManager.localizeText(TranslationKeys.DIALOG_TEXT_DEFEAT_CONQUERED_MAJORITY,
                winningRound));
            endDialog.button(localizationManager.localizeText(TranslationKeys.BUTTON_DIALOG_RETRY), (byte) 2);
        }
        addShareOrCopyButtonToDialog(sharingPreemble, endDialog, newGamePreferences, scenarioMap);
        endDialog.button(localizationManager.localizeText(TranslationKeys.BUTTON_DIALOG_CONTINUE), (byte) 0);
        endDialog.show(stage);
    }


    void showAllEnemiesDefeatedMessage(Stage stage, boolean botsGaveUpPreviously, int winningRound,
                                       ScenarioMap scenarioMap, NewGamePreferences newGamePreferences,
                                       Runnable onExit, Runnable onRetry) {
        final Dialog endDialog = dialogFactory.createDialog(result -> {
            switch ((byte) result) {
                case 1:
                    // exit button
                    onExit.run();
                    break;
                case 2:
                    // retry button
                    onRetry.run();
                    break;
                case 0:
                    // do nothing on continue button
                default:
                    break;
            }
        });
        endDialog.button(localizationManager.localizeText(TranslationKeys.BUTTON_DIALOG_EXIT), (byte) 1);
        endDialog.button(localizationManager.localizeText(TranslationKeys.BUTTON_DIALOG_REPLAY), (byte) 2);
        final String sharingPreemble = localizationManager.localizeText(TranslationKeys.SHARING_PREEMBLE_VICTORY,
            winningRound);
        addShareOrCopyButtonToDialog(sharingPreemble, endDialog, newGamePreferences, scenarioMap);
        String dialogText;
        if (botsGaveUpPreviously) {
            dialogText =
                localizationManager.localizeText(TranslationKeys.DIALOG_TEXT_VICTORY_ALL_ENEMIES_DEFEATED_SURRENDERED,
                    winningRound);
        } else {
            // probably its somehow possible to defeat all enemies without triggering the win condition somehow...
            dialogText = localizationManager.localizeText(TranslationKeys.DIALOG_TEXT_VICTORY_ALL_ENEMIES_DEFEATED,
                winningRound);
        }
        dialogText += "\n";
        endDialog.text(dialogText);
        endDialog.show(stage);
    }

    void showPlayerDefeatedMessage(Stage stage, int currentRound, ScenarioMap scenarioMap,
                                   NewGamePreferences newGamePreferences,
                                   Runnable onExit, Runnable onRetry, Runnable onSpectate) {
        final Dialog endDialog = dialogFactory.createDialog(result -> {
            switch ((byte) result) {
                case 1:
                    // exit button
                    onExit.run();
                    break;
                case 2:
                    // retry button
                    onRetry.run();
                    break;
                case 0:
                    // spectate button
                    onSpectate.run();
                    break;
                default:
                    break;
            }
        });
        endDialog.button(localizationManager.localizeText(TranslationKeys.BUTTON_DIALOG_EXIT), (byte) 1);
        endDialog.button(localizationManager.localizeText(TranslationKeys.BUTTON_DIALOG_REPLAY), (byte) 2);
        final String sharingPreemble = localizationManager.localizeText(TranslationKeys.SHARING_PREEMBLE_DEFEAT,
            currentRound);
        addShareOrCopyButtonToDialog(sharingPreemble, endDialog, newGamePreferences, scenarioMap);
        endDialog.button(localizationManager.localizeText(TranslationKeys.BUTTON_DIALOG_SPECTATE), (byte) 0);
        String dialogText = localizationManager.localizeText(TranslationKeys.DIALOG_TEXT_DEFEAT_ALL_KINGDOMS_CONQUERED,
            currentRound);
        dialogText += "\n";
        endDialog.text(dialogText);
        endDialog.show(stage);
    }

    void showEnemyWonMessage(Stage stage, int roundOfDefeat, ScenarioMap scenarioMap,
                             NewGamePreferences newGamePreferences,
                             Runnable onExit, Runnable onRetry) {
        final Dialog endDialog = dialogFactory.createDialog(result -> {
            switch ((byte) result) {
                case 1:
                    // exit button
                    onExit.run();
                    break;
                case 2:
                    // retry button
                    onRetry.run();
                    break;
                default:
                    break;
            }
        });
        endDialog.button(localizationManager.localizeText(TranslationKeys.BUTTON_DIALOG_EXIT), (byte) 1);
        endDialog.button(localizationManager.localizeText(TranslationKeys.BUTTON_DIALOG_RETRY), (byte) 2);
        final String sharingPreemble = localizationManager.localizeText(TranslationKeys.SHARING_PREEMBLE_DEFEAT,
            roundOfDefeat);
        addShareOrCopyButtonToDialog(sharingPreemble, endDialog, newGamePreferences, scenarioMap);

        String dialogText = localizationManager.localizeText(TranslationKeys.DIALOG_TEXT_ENEMY_DEFEATED_ALL,
            roundOfDefeat);
        dialogText += "\n";
        endDialog.text(dialogText);
        endDialog.show(stage);
    }


    void showTutorialObjectiveMessage(Stage stage, int newProgress) {
        final Dialog dialog = tutorialDialogFactory.createDialog(newProgress);
        dialog.show(stage);
    }

    void showCannotEndTurnMessage(Stage stage) {
        final Dialog dialog =
            dialogFactory.createInformationDialog(localizationManager.localizeText(TranslationKeys.DIALOG_TEXT_CANNOT_END_TURN), () -> {
            });
        dialog.show(stage);
    }

    void showGameOrObjectiveInfo(Stage stage, int currentRound, ScenarioMap scenarioMap, int objectiveProgress,
                                 NewGamePreferences newGamePreferences) {
        if (scenarioMap == ScenarioMap.NONE) {
            // regular sandbox game
            showGameDetails(stage, currentRound, newGamePreferences);
        } else if (scenarioMap == ScenarioMap.TUTORIAL) {
            showTutorialObjectiveMessage(stage, objectiveProgress);
        }
    }

    private void showGameDetails(Stage stage, int currentRound, NewGamePreferences newGamePreferences) {
        final String gameDetails =
            localizationManager.localizeText(TranslationKeys.GAME_DETAILS_ROUND) + ": " + currentRound + "\n"
                + newGamePreferences.toDisplayString(localizationManager);
        final FeudalTacticsDialog dialog = dialogFactory.createInformationDialog(gameDetails, () -> {
        });
        addShareOrCopyButtonToDialog(localizationManager.localizeText(TranslationKeys.SHARING_PREEMBLE_ONGOING), dialog,
            newGamePreferences
            , ScenarioMap.NONE);
        dialog.show(stage);
    }

    void addShareOrCopyButtonToDialog(String preemble, Dialog endDialog, NewGamePreferences newGamePreferences,
                                      ScenarioMap scenarioMap) {
        if (scenarioMap != ScenarioMap.NONE) {
            // dont offer the option in the tutorial
            return;
        }
        final String fullSharedMessage = preemble + "\n" + newGamePreferences.toSharableString(localizationManager);
        final String buttonText = localizationManager.localizeText(TranslationKeys.BUTTON_DIALOG_SHARE);
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            dialogFactory.addNonClosingTextButtonToDialog(endDialog, buttonText,
                () -> platformSharing.shareText(fullSharedMessage));
        } else {
            dialogFactory.addCopyButtonToDialog(() -> fullSharedMessage, endDialog, buttonText);
        }
    }
}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
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
        endDialog.button(localizationManager.localizeText("button-dialog-exit"), (byte) 1);
        final String sharingPreemble;
        if (win) {
            sharingPreemble = localizationManager.localizeText("sharing-preemble-victory", winningRound);
            endDialog.text(localizationManager.localizeText("dialog-text-victory-surrender", winningRound));
            endDialog.button(localizationManager.localizeText("button-dialog-replay"), (byte) 2);
        } else {
            sharingPreemble = localizationManager.localizeText("sharing-preemble-surrender");
            endDialog.text(localizationManager.localizeText("dialog-text-defeat-conquered-majority", winningRound));
            endDialog.button(localizationManager.localizeText("button-dialog-retry"), (byte) 2);
        }
        addShareOrCopyButtonToDialog(sharingPreemble, endDialog, newGamePreferences, scenarioMap);
        endDialog.button(localizationManager.localizeText("button-dialog-continue"), (byte) 0);
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
        endDialog.button(localizationManager.localizeText("button-dialog-exit"), (byte) 1);
        endDialog.button(localizationManager.localizeText("button-dialog-replay"), (byte) 2);
        final String sharingPreemble = localizationManager.localizeText("sharing-preemble-victory", winningRound);
        addShareOrCopyButtonToDialog(sharingPreemble, endDialog, newGamePreferences, scenarioMap);
        String dialogText;
        if (botsGaveUpPreviously) {
            dialogText = localizationManager.localizeText("dialog-text-victory-all-enemies-defeated-surrendered",
                winningRound);
        } else {
            // probably its somehow possible to defeat all enemies without triggering the win condition somehow...
            dialogText = localizationManager.localizeText("dialog-text-victory-all-enemies-defeated",
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
        endDialog.button(localizationManager.localizeText("button-dialog-exit"), (byte) 1);
        endDialog.button(localizationManager.localizeText("button-dialog-retry"), (byte) 2);
        final String sharingPreemble = localizationManager.localizeText("sharing-preemble-defeat", currentRound);
        addShareOrCopyButtonToDialog(sharingPreemble, endDialog, newGamePreferences, scenarioMap);
        endDialog.button(localizationManager.localizeText("button-dialog-spectate"), (byte) 0);
        String dialogText = localizationManager.localizeText("dialog-text-defeat-all-kingdoms-conquered",
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
        endDialog.button(localizationManager.localizeText("button-dialog-exit"), (byte) 1);
        endDialog.button(localizationManager.localizeText("button-dialog-retry"), (byte) 2);
        final String sharingPreemble = localizationManager.localizeText("sharing-preemble-defeat", roundOfDefeat);
        addShareOrCopyButtonToDialog(sharingPreemble, endDialog, newGamePreferences, scenarioMap);

        String dialogText = localizationManager.localizeText("dialog-text-enemy-defeated-all",
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
        final Dialog dialog = dialogFactory.createInformationDialog(localizationManager.localizeText("cannot-end-turn" +
            "-text"), () -> {
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
        final String gameDetails = localizationManager.localizeText("game-details-round") + ": " + currentRound + "\n"
            + newGamePreferences.toDisplayString(localizationManager);
        final FeudalTacticsDialog dialog = dialogFactory.createInformationDialog(gameDetails, () -> {
        });
        addShareOrCopyButtonToDialog(localizationManager.localizeText("sharing-preemble-ongoing"), dialog,
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
        final String fullSharedMessage = preemble + "\n" + newGamePreferences.toSharableString();
        final String buttonText = localizationManager.localizeText("button-dialog-share");
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            dialogFactory.addNonClosingTextButtonToDialog(endDialog, buttonText,
                () -> platformSharing.shareText(fullSharedMessage));
        } else {
            dialogFactory.addCopyButtonToDialog(() -> fullSharedMessage, endDialog, buttonText);
        }
    }
}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
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

    public static final String SHARING_PREEMBLE_ONGOING = "I'm playing this FeudalTactics game. Can you beat it?";
    public static final String SHARING_PREEMBLE_SURRENDER = "I lost this FeudalTactics game. Can you beat it?";
    public static final String SHARING_PREEMBLE_DEFEAT = "I lost this FeudalTactics game in round %s. Can you do " +
        "better?";
    public static final String SHARING_PREEMBLE_VICTORY = "I won this FeudalTactics game in round %s. Can you do " +
        "better?";

    private final DialogFactory dialogFactory;
    private final TutorialDialogFactory tutorialDialogFactory;
    private final PlatformSharing platformSharing;

    @Inject
    public IngameScreenDialogHelper(DialogFactory dialogFactory, TutorialDialogFactory tutorialDialogFactory,
                                    PlatformSharing platformSharing) {
        this.dialogFactory = dialogFactory;
        this.tutorialDialogFactory = tutorialDialogFactory;
        this.platformSharing = platformSharing;
    }

    void showGiveUpGameMessage(Stage stage, boolean win, int winningRound, ScenarioMap scenarioMap,
                               NewGamePreferences newGamePreferences, Runnable onExit, Runnable onRetry) {
        Dialog endDialog = dialogFactory.createDialog(result -> {
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
        endDialog.button("Exit", (byte) 1);
        String sharingPreemble;
        if (win) {
            sharingPreemble = String.format(SHARING_PREEMBLE_VICTORY, winningRound);
            endDialog.text(String.format("VICTORY! Your Enemies surrendered in round %s.\n\nDo you wish to " +
                "continue?\n", winningRound));
            endDialog.button("Replay", (byte) 2);
        } else {
            sharingPreemble = SHARING_PREEMBLE_SURRENDER;
            endDialog.text(String.format("Your Enemy conquered a majority of the territory.\n\nDo you " +
                "wish to continue?\n", winningRound));
            endDialog.button("Retry", (byte) 2);
        }
        addShareOrCopyButtonToDialog(sharingPreemble, endDialog, newGamePreferences, scenarioMap);
        endDialog.button("Continue", (byte) 0);
        endDialog.show(stage);
    }


    void showAllEnemiesDefeatedMessage(Stage stage, boolean botsGaveUpPreviously, int winningRound,
                                       ScenarioMap scenarioMap, NewGamePreferences newGamePreferences,
                                       Runnable onExit, Runnable onRetry) {
        Dialog endDialog = dialogFactory.createDialog(result -> {
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
        endDialog.button("Exit", (byte) 1);
        endDialog.button("Replay", (byte) 2);
        String sharingPreemble = String.format(SHARING_PREEMBLE_VICTORY, winningRound);
        addShareOrCopyButtonToDialog(sharingPreemble, endDialog, newGamePreferences, scenarioMap);
        String dialogText;
        if (botsGaveUpPreviously) {
            dialogText = String.format("You defeated all your enemies. They surrendered in round %s.",
                winningRound);
        } else {
            // probably its somehow possible to defeat all enemies without triggering the win condition somehow...
            dialogText = String.format("VICTORY! You defeated all your enemies in round %s.",
                winningRound);
        }
        dialogText += "\n";
        endDialog.text(dialogText);
        endDialog.show(stage);
    }

    void showPlayerDefeatedMessage(Stage stage, int currentRound, ScenarioMap scenarioMap,
                                   NewGamePreferences newGamePreferences,
                                   Runnable onExit, Runnable onRetry, Runnable onSpectate) {
        Dialog endDialog = dialogFactory.createDialog(result -> {
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
        endDialog.button("Exit", (byte) 1);
        endDialog.button("Retry", (byte) 2);
        String sharingPreemble = String.format(SHARING_PREEMBLE_DEFEAT, currentRound);
        addShareOrCopyButtonToDialog(sharingPreemble, endDialog, newGamePreferences, scenarioMap);
        endDialog.button("Spectate", (byte) 0);
        String dialogText = String.format("DEFEAT! All your kingdoms were conquered by the enemy in round %s.",
            currentRound);
        dialogText += "\n";
        endDialog.text(dialogText);
        endDialog.show(stage);
    }

    void showEnemyWonMessage(Stage stage, int roundOfDefeat, ScenarioMap scenarioMap,
                             NewGamePreferences newGamePreferences,
                             Runnable onExit, Runnable onRetry) {
        Dialog endDialog = dialogFactory.createDialog(result -> {
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
        endDialog.button("Exit", (byte) 1);
        endDialog.button("Retry", (byte) 2);
        String sharingPreemble = String.format(SHARING_PREEMBLE_DEFEAT, roundOfDefeat);
        addShareOrCopyButtonToDialog(sharingPreemble, endDialog, newGamePreferences, scenarioMap);

        String dialogText = String.format("Your enemy defeated all other players. You lost in round %s.",
            roundOfDefeat);
        dialogText += "\n";
        endDialog.text(dialogText);
        endDialog.show(stage);
    }


    void showTutorialObjectiveMessage(Stage stage, int newProgress) {
        Dialog dialog = tutorialDialogFactory.createDialog(newProgress);
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
        String gameDetails = String.format("Round: %s\n", currentRound)
            + newGamePreferences.toSharableString();
        FeudalTacticsDialog dialog = dialogFactory.createInformationDialog(gameDetails, () -> {
        });
        addShareOrCopyButtonToDialog(SHARING_PREEMBLE_ONGOING, dialog, newGamePreferences
            , ScenarioMap.NONE);
        dialog.show(stage);
    }

    void addShareOrCopyButtonToDialog(String preemble, Dialog endDialog, NewGamePreferences newGamePreferences,
                                      ScenarioMap scenarioMap) {
        if (scenarioMap != ScenarioMap.NONE) {
            // dont offer the option in the tutorial
            return;
        }
        String fullSharedMessage = preemble + "\n" + newGamePreferences.toSharableString();
        String buttonText = "Share";
        if (Gdx.app.getType() == Application.ApplicationType.Android) {
            dialogFactory.addNonClosingTextButtonToDialog(endDialog, buttonText,
                () -> platformSharing.shareText(fullSharedMessage));
        } else {
            dialogFactory.addCopyButtonToDialog(() -> fullSharedMessage, endDialog, buttonText);
        }
    }


}

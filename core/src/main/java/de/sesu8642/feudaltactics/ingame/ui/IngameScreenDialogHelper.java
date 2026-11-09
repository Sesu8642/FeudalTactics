// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.HexTile;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
import de.sesu8642.feudaltactics.lib.gamestate.Unit;

import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.FeudalTacticsDialog;
import de.sesu8642.feudaltactics.platformspecific.PlatformSharing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        final FeudalTacticsDialog endDialog = dialogFactory.createDialog(result -> {
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
        final FeudalTacticsDialog endDialog = dialogFactory.createDialog(result -> {
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
        final FeudalTacticsDialog endDialog = dialogFactory.createDialog(result -> {
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
        endDialog.button(localizationManager.localizeText(TranslationKeys.BUTTON_DIALOG_RETRY), (byte) 2);
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
        final FeudalTacticsDialog endDialog = dialogFactory.createDialog(result -> {
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


    void showIngameLeaderboard(Stage stage, GameState gameState) {
        final Map<Integer, Player> playersByIndex = new HashMap<>();
        final Map<Integer, Integer> claimedTilesByPlayer = new HashMap<>();
        final Map<Integer, Integer> armyStrengthByPlayer = new HashMap<>();

        for (Player player : gameState.getPlayers()) {
            playersByIndex.put(player.getPlayerIndex(), player);
            claimedTilesByPlayer.put(player.getPlayerIndex(), 0);
            armyStrengthByPlayer.put(player.getPlayerIndex(), 0);
        }

        for (HexTile tile : gameState.getMap().values()) {
            final Player owner = tile.getPlayer();
            if (owner == null || !playersByIndex.containsKey(owner.getPlayerIndex())) {
                continue;
            }

            final int playerIndex = owner.getPlayerIndex();
            claimedTilesByPlayer.put(playerIndex, claimedTilesByPlayer.get(playerIndex) + 1);
            if (tile.getContent() instanceof Unit) {
                armyStrengthByPlayer.put(playerIndex,
                    armyStrengthByPlayer.get(playerIndex) + tile.getContent().getStrength());
            }
        }

        final List<Player> rankedPlayers = new ArrayList<>(gameState.getPlayers());
        rankedPlayers.sort(Comparator
            .comparingInt((Player player) -> claimedTilesByPlayer.get(player.getPlayerIndex())).reversed()
            .thenComparing(Comparator.comparingInt(
                (Player player) -> armyStrengthByPlayer.get(player.getPlayerIndex())).reversed())
            .thenComparingInt(Player::getPlayerIndex));

        final FeudalTacticsDialog dialog = dialogFactory.createInformationDialog(() -> { });
        dialog.headline(localizationManager.localizeText(TranslationKeys.INGAME_LEADERBOARD_HEADLINE));
        dialog.text(localizationManager.localizeText(TranslationKeys.INGAME_LEADERBOARD_ROUND, gameState.getRound())
            + "\n");
        final List<List<String>> rows = new ArrayList<>();
        rows.add(List.of(
            localizationManager.localizeText(TranslationKeys.INGAME_LEADERBOARD_COLUMN_PLAYER),
            localizationManager.localizeText(TranslationKeys.INGAME_LEADERBOARD_COLUMN_CLAIMED_TILES),
            localizationManager.localizeText(TranslationKeys.INGAME_LEADERBOARD_COLUMN_ARMY_STRENGTH),
            localizationManager.localizeText(TranslationKeys.INGAME_LEADERBOARD_COLUMN_STATUS)));
        int rank = 1;
        for (Player player : rankedPlayers) {
            final int playerIndex = player.getPlayerIndex();
            final String playerName = player.getType() == Player.Type.LOCAL_PLAYER
                ? localizationManager.localizeText(TranslationKeys.INGAME_LEADERBOARD_PLAYER_YOU)
                : localizationManager.localizeText(TranslationKeys.INGAME_LEADERBOARD_PLAYER_NUMBER, playerIndex + 1);
            final String status = player.isDefeated()
                ? localizationManager.localizeText(TranslationKeys.INGAME_LEADERBOARD_STATUS_DEFEATED)
                : localizationManager.localizeText(TranslationKeys.INGAME_LEADERBOARD_STATUS_ACTIVE);
            rows.add(List.of(
                localizationManager.localizeText(TranslationKeys.INGAME_LEADERBOARD_PLAYER_RANK, rank++, playerName),
                String.valueOf(claimedTilesByPlayer.get(playerIndex)),
                String.valueOf(armyStrengthByPlayer.get(playerIndex)), status));
        }
        dialog.addTable(rows);
        dialog.show(stage);
    }

    void showTutorialObjectiveMessage(Stage stage, int newProgress) {
        final FeudalTacticsDialog dialog = tutorialDialogFactory.createDialog(newProgress);
        dialog.show(stage);
    }

    void showCannotEndTurnMessage(Stage stage) {
        final FeudalTacticsDialog dialog =
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

    void addShareOrCopyButtonToDialog(String preemble, FeudalTacticsDialog endDialog,
                                      NewGamePreferences newGamePreferences,
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

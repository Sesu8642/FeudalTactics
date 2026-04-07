// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.google.common.collect.ImmutableList;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.lib.gamestate.Unit;
import de.sesu8642.feudaltactics.lib.gamestate.Unit.UnitTypes;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.FeudalTacticsDialog;
import de.sesu8642.feudaltactics.menu.common.ui.SkinConstants;
import de.sesu8642.feudaltactics.renderer.TextureAtlasHelper;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Factory for the dialogs displayed in the tutorial.
 */
@Singleton
public class TutorialDialogFactory {

    private static final List<String> UNIT_KEYS =
        ImmutableList.of(TranslationKeys.TUTORIAL_UNIT_TABLE_UNIT_TYPE_PEASANT,
            TranslationKeys.TUTORIAL_UNIT_TABLE_UNIT_TYPE_SPEARMAN,
            TranslationKeys.TUTORIAL_UNIT_TABLE_UNIT_TYPE_KNIGHT,
            TranslationKeys.TUTORIAL_UNIT_TABLE_UNIT_TYPE_BARON);
    private final DialogFactory dialogFactory;
    private final TextureAtlasHelper textureAtlasHelper;
    private final LocalizationManager localizationManager;

    /**
     * Constructor.
     */
    @Inject
    public TutorialDialogFactory(DialogFactory dialogFactory, TextureAtlasHelper textureAtlasHelper,
                                 LocalizationManager localizationManager) {
        this.dialogFactory = dialogFactory;
        this.textureAtlasHelper = textureAtlasHelper;
        this.localizationManager = localizationManager;
    }

    private static String unitTypeToLocalizedDisplayName(Unit.UnitTypes unitType,
                                                         LocalizationManager localizationManager) {
        return localizationManager.localizeText(UNIT_KEYS.get(unitType.ordinal()));
    }

    /**
     * Creates the tutorial dialog for the given objective progress.
     */
    public Dialog createDialog(int objectiveProgress) {

        final FeudalTacticsDialog dialog = dialogFactory.createDialog(result -> {
        });

        switch (objectiveProgress) {
            case 1:
                fillTutorialDialog1(dialog);
                break;
            case 2:
                fillTutorialDialog2(dialog);
                break;
            case 3:
                fillTutorialDialog3(dialog);
                break;
            case 4:
                fillTutorialDialog4(dialog);
                break;
            case 5:
                fillTutorialDialog5(dialog);
                break;
            case 6:
                fillTutorialDialog6(dialog);
                break;
            case 7:
                fillTutorialDialog7(dialog);
                break;
            case 8:
                fillTutorialDialog8(dialog);
                break;
            case 9:
                fillTutorialDialog9(dialog);
                break;
            case 10:
                fillTutorialDialog10(dialog);
                break;
            case 11:
                fillTutorialDialog11(dialog);
                break;
            case 12:
                fillTutorialDialog12(dialog);
                break;
            default:
                throw new IllegalStateException("Tutorial objective index is higher than expected: " + objectiveProgress);
        }
        dialog.button(localizationManager.localizeText(TranslationKeys.BUTTON_DIALOG_OK));
        return dialog;
    }

    private void fillTutorialDialog1(FeudalTacticsDialog dialog) {
        final String text = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_GAME_GOAL);
        dialog.text(text).addButtonImage(SkinConstants.BUTTON_INFO);
    }

    private void fillTutorialDialog2(FeudalTacticsDialog dialog) {
        final String text1 = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_FINANCES);
        dialog.text(text1).addButtonImage(SkinConstants.BUTTON_BUY_PEASANT);
    }

    private void fillTutorialDialog3(FeudalTacticsDialog dialog) {
        final String text1 =
            localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_AFTER_UNIT_PURCHASE_PART_1);
        final String text2 =
            localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_AFTER_UNIT_PURCHASE_PART_2);
        dialog.text(text1).addButtonImage(SkinConstants.SPRITE_HAND).text(text2);
    }

    private void fillTutorialDialog4(FeudalTacticsDialog dialog) {
        final String text1 = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_END_TURN_PART_1);
        final String text2 = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_END_TURN_PART_2);
        dialog.text(text1).addButtonImage(SkinConstants.BUTTON_END_TURN).text(text2);
    }

    private void fillTutorialDialog5(FeudalTacticsDialog dialog) {
        final String text = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_SELECT_KINGDOM);
        dialog.text(text);
    }

    private void fillTutorialDialog6(FeudalTacticsDialog dialog) {
        final String text1 = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_PROTECTION_PART_1);
        final String text2 = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_PROTECTION_PART_2);
        dialog.text(text1).addSpriteImage(textureAtlasHelper.getShieldSprite()).text(text2);
    }

    private void fillTutorialDialog7(FeudalTacticsDialog dialog) {
        final String text1 = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_UNIT_OVERVIEW_PART_1);
        final List<List<String>> tableData = new ArrayList<>();
        tableData.add(Arrays.asList(
            localizationManager.localizeText(TranslationKeys.TUTORIAL_UNIT_TABLE_HEADLINE_UNIT_NAME),
            localizationManager.localizeText(TranslationKeys.TUTORIAL_UNIT_TABLE_HEADLINE_STRENGTH),
            localizationManager.localizeText(TranslationKeys.TUTORIAL_UNIT_TABLE_HEADLINE_SALARY)
        ));
        for (UnitTypes unitType : UnitTypes.values()) {
            tableData.add(Arrays.asList(unitTypeToLocalizedDisplayName(unitType, localizationManager),
                String.valueOf(unitType.strength()),
                String.valueOf(unitType.salary())));
        }
        final String text2 = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_UNIT_OVERVIEW_PART_1);
        dialog.text(text1).addTable(tableData).text(text2);
    }

    private void fillTutorialDialog8(FeudalTacticsDialog dialog) {
        final String text = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_TREES);
        dialog.text(text);
    }

    private void fillTutorialDialog9(FeudalTacticsDialog dialog) {
        final String text = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_CASTLE_PART_1);
        final String text2 = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_CASTLE_PART_2);
        dialog.text(text).addButtonImage(SkinConstants.BUTTON_BUY_CASTLE).text(text2);
    }

    private void fillTutorialDialog10(FeudalTacticsDialog dialog) {
        final String text = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_HINT_COLLECTION);
        dialog.text(text);
    }

    private void fillTutorialDialog11(FeudalTacticsDialog dialog) {
        final String text1 = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_UNDOING_PART_1);
        final String text2 = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_UNDOING_PART_2);
        dialog.text(text1).addButtonImage(SkinConstants.BUTTON_UNDO).text(text2);
    }

    private void fillTutorialDialog12(FeudalTacticsDialog dialog) {
        final String text = localizationManager.localizeText(TranslationKeys.TUTORIAL_OBJECTIVE_SHORTCUTS);
        dialog.text(text);
    }

}

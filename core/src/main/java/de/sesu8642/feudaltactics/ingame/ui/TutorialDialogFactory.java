// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
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
        dialog.button(localizationManager.localizeText("button-dialog-ok"));
        return dialog;
    }


    private void fillTutorialDialog1(FeudalTacticsDialog dialog) {
        final String text = localizationManager.localizeText("tutorial-objective-game-goal");
        dialog.text(text).addButtonImage(SkinConstants.BUTTON_INFO);
    }

    private void fillTutorialDialog2(FeudalTacticsDialog dialog) {
        final String text1 = localizationManager.localizeText("tutorial-objective-finances");
        dialog.text(text1).addButtonImage(SkinConstants.BUTTON_BUY_PEASANT);
    }

    private void fillTutorialDialog3(FeudalTacticsDialog dialog) {
        final String text1 = localizationManager.localizeText("tutorial-objective-after-unit-purchase-part-1");
        final String text2 = localizationManager.localizeText("tutorial-objective-after-unit-purchase-part-2");
        dialog.text(text1).addButtonImage(SkinConstants.SPRITE_HAND).text(text2);
    }

    private void fillTutorialDialog4(FeudalTacticsDialog dialog) {
        final String text1 = localizationManager.localizeText("tutorial-objective-end-turn-part-1");
        final String text2 = localizationManager.localizeText("tutorial-objective-end-turn-part-2");
        dialog.text(text1).addButtonImage(SkinConstants.BUTTON_END_TURN).text(text2);
    }

    private void fillTutorialDialog5(FeudalTacticsDialog dialog) {
        final String text1 = localizationManager.localizeText("tutorial-objective-protection-part-1");
        final String text2 = localizationManager.localizeText("tutorial-objective-protection-part-2");
        dialog.text(text1).addSpriteImage(textureAtlasHelper.getShieldSprite()).text(text2);
    }

    private void fillTutorialDialog6(FeudalTacticsDialog dialog) {
        final String text1 = localizationManager.localizeText("tutorial-objective-unit-overview-part-1");
        final List<List<String>> tableData = new ArrayList<>();
        tableData.add(Arrays.asList(
            localizationManager.localizeText("tutorial-unit-table-headline-unit-name"),
            localizationManager.localizeText("tutorial-unit-table-headline-strength"),
            localizationManager.localizeText("tutorial-unit-table-headline-salary")
        ));
        for (UnitTypes unitType : UnitTypes.values()) {
            tableData.add(Arrays.asList(GameStateEnumDisplayNameConverter.getLocalizedDisplayName(unitType,
                    localizationManager),
                String.valueOf(unitType.strength()),
                String.valueOf(unitType.salary())));
        }
        final String text2 = localizationManager.localizeText("tutorial-objective-unit-overview-part-2");
        dialog.text(text1).addTable(tableData).text(text2);
    }

    private void fillTutorialDialog7(FeudalTacticsDialog dialog) {
        final String text = localizationManager.localizeText("tutorial-objective-trees");
        dialog.text(text);
    }

    private void fillTutorialDialog8(FeudalTacticsDialog dialog) {
        final String text = localizationManager.localizeText("tutorial-objective-castle-part-1");
        final String text2 = localizationManager.localizeText("tutorial-objective-castle-part-2");
        dialog.text(text).addButtonImage(SkinConstants.BUTTON_BUY_CASTLE).text(text2);
    }

    private void fillTutorialDialog9(FeudalTacticsDialog dialog) {
        final String text = localizationManager.localizeText("tutorial-objective-hint-collection");
        dialog.text(text);
    }

    private void fillTutorialDialog10(FeudalTacticsDialog dialog) {
        final String text1 = localizationManager.localizeText("tutorial-objective-undoing-part-1");
        final String text2 = localizationManager.localizeText("tutorial-objective-undoing-part-2");
        dialog.text(text1).addButtonImage(SkinConstants.BUTTON_UNDO).text(text2);
    }

    private void fillTutorialDialog11(FeudalTacticsDialog dialog) {
        final String text = localizationManager.localizeText("tutorial-objective-shortcuts");
        dialog.text(text);
    }

    private void fillTutorialDialog12(FeudalTacticsDialog dialog) {
        final String text = localizationManager.localizeText("tutorial-objective-shortcuts-duplicate");
        dialog.text(text);
    }
}

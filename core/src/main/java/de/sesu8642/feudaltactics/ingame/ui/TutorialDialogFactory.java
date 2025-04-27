// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import de.sesu8642.feudaltactics.lib.gamestate.Unit.UnitTypes;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.FeudalTacticsDialog;
import de.sesu8642.feudaltactics.menu.common.ui.SkinConstants;

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

    /**
     * Constructor.
     */
    @Inject
    public TutorialDialogFactory(DialogFactory dialogFactory) {
        this.dialogFactory = dialogFactory;
    }

    /**
     * Creates the tutorial dialog for the given objective progress.
     */
    public Dialog createDialog(int objectiveProgress) {

        FeudalTacticsDialog dialog = dialogFactory.createDialog(result -> {
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
            default:
                throw new IllegalStateException("Tutorial objective index is higher than expected: " + objectiveProgress);
        }
        dialog.button("OK");
        return dialog;
    }


    private void fillTutorialDialog1(FeudalTacticsDialog dialog) {
        String text = "Feudal Tactics is a turn based strategy game. You play on an island composed of hexagonal " +
                "tiles. Each tile is colored to show which player owns it.\nTwo or more connected tiles of "
                + "the same color form a kingdom.\n\nObjective: Click/tap your big kingdom in the bottom right to " +
                "select it.\n\nHint: The turn indicator in the top left corner of the screen shows the color of the " +
                "player whose turn it is. Since it is your turn, this color matches the color of your tiles." + "\n" +
                "\nNote: You can always see your current objective using the button that looks like this:";
        dialog.text(text).addButtonImage(SkinConstants.BUTTON_INFO);
    }

    private void fillTutorialDialog2(FeudalTacticsDialog dialog) {
        String text1 = "After selecting your kingdom, you can see its finances in the top left corner of the screen" +
                ".\n\nExample: Savings: 14 (+12)\n14 is the amount of coins this kingdom has. They can be used to " +
                "buy units or castles.\n12 is the income per turn. Each kingdom gains one coin per tile. The income" +
                " is reduced by the salaries of its units.\n\nObjective: Click this button on the bottom of the " +
                "screen to buy a peasant for 10 coins:";
        dialog.text(text1).addButtonImage(SkinConstants.BUTTON_BUY_PEASANT);
    }

    private void fillTutorialDialog3(FeudalTacticsDialog dialog) {
        String text1 = "Note how your savings and your income have lowered.\nYou now have the peasant in your hand, " +
                "as indicated by the hand icon on the right side of the screen.";
        String text2 = "\nYou can place it in your own kingdom or conquer an enemy tile. A peasant can only conquer " +
                "tiles that are not protected, as it is the weakest unit.\n\nObjective: Conquer the unprotected tile " +
                "adjacent to your kingdom.";
        dialog.text(text1).addButtonImage(SkinConstants.SPRITE_HAND).text(text2);
    }

    private void fillTutorialDialog4(FeudalTacticsDialog dialog) {
        String text1 = "After conquering, units like your peasant can no longer be moved until the next turn" +
                ".\n\nPress this button to end your turn:";
        String text2 = "Your enemies will then do their turns.\n\nObjective: End your turn and re-select your kingdom.";
        dialog.text(text1).addButtonImage(SkinConstants.BUTTON_END_TURN).text(text2);
    }

    private void fillTutorialDialog5(FeudalTacticsDialog dialog) {
        String text = "The tiles of your enemy's adjacent kingdom are protected by the capital. Units, capitals and " +
                "castles protect the tile they are placed on as well as all adjacent tiles.\n\nTo conquer a protected" +
                " tile, you need a unit with greater strength. In this case, you need at least a spearman to conquer " +
                "a tile protected by a capital. To get stronger units, place a peasant on another unit.\n\nObjective:" +
                " Buy another peasant and place it on your existing one to create a spearman.\n";
        dialog.text(text);
    }

    private void fillTutorialDialog6(FeudalTacticsDialog dialog) {
        String text1 = "The spearman you created takes a higher salary than a peasant. Here is an overview of all the" +
                " units.";
        List<List<String>> tableData = new ArrayList<>();
        tableData.add(Arrays.asList("Unit", "Strength", "Salary"));
        for (UnitTypes unitType : UnitTypes.values()) {
            tableData.add(Arrays.asList(EnumDisplayNameConverter.getDisplayName(unitType),
                    String.valueOf(unitType.strength()),
                    String.valueOf(unitType.salary())));
        }
        String text2 = "Objective: Attack your enemy with the spearman.\n";
        dialog.text(text1).addTable(tableData).text(text2);
    }

    private void fillTutorialDialog7(FeudalTacticsDialog dialog) {
        String text = "Since you reduced the size of your enemy's kingdom to one tile, its capital was destroyed and " +
                "turned into a tree.\n\nThere are two types of trees: palm trees and oak trees. Palm trees grow on " +
                "coast tiles. They spread in every turn. Oak trees grow on all other tiles. They only spread when " +
                "there are two oaks adjacent to each other. When there is a tree on a tile, you will not gain any " +
                "income from it. Trees can be chopped by placing a unit on them. After that, the unit can no " +
                "longer be moved until the next turn, even when the tree is growing in your own kingdom" +
                ".\n\nObjective: Chop the tree.\n\nNote: You need to end your turn so that your spearman can move " +
                "again.\n";
        dialog.text(text);
    }

    private void fillTutorialDialog8(FeudalTacticsDialog dialog) {
        String text = "It's now time to buy a castle. Since they don't need salary, they are ideal for long term " +
                "defense. Only knights and barons can destroy castles.\n\nUse this button to buy a castle for 15 " +
                "coins:";
        String text2 = "Objective: Buy and place a castle.\n\nNote: You might need to end your turn once or more to " +
                "collect enough coins.\n\nHint: Place the castle near your enemies, as it only protects the adjacent " +
                "tiles.\n";
        dialog.text(text).addButtonImage(SkinConstants.BUTTON_BUY_CASTLE).text(text2);
    }

    private void fillTutorialDialog9(FeudalTacticsDialog dialog) {
        String text = "Now you know the basics.\n\nBeware of your " +
                "capital being destroyed, as you will then lose all your money.\nAlso make sure your enemy doesn't " +
                "split your kingdom into two, as only the part with the capital will keep your money and the other " +
                "part will start from 0.\n\nIf you cannot pay your units at the start of your turn for one reason or " +
                "another, they will die and leave gravestones. They, in turn, will become trees after one turn" +
                ".\n\nObjective: conquer the whole island to win the game.\n";
        dialog.text(text);
    }

    private void fillTutorialDialog10(FeudalTacticsDialog dialog) {
        String text1 = "You can use this button to undo moves, but only within the current turn:";
        String text2 = "Objective: conquer the whole island to win the game.\n";
        dialog.text(text1).addButtonImage(SkinConstants.BUTTON_UNDO).text(text2);
    }

    private void fillTutorialDialog11(FeudalTacticsDialog dialog) {
        String text = "There are several shortcuts available:\n- undo move: Android or mouse back button\n- buy " +
                "peasant: right mouse button click\n- buy and place peasant in own kingdom: double tap/click\n- buy " +
                "castle: middle mouse button click\n- buy and place castle: long press/click\n\nObjective: conquer " +
                "the whole island to win the game.\n";
        dialog.text(text);
    }

}

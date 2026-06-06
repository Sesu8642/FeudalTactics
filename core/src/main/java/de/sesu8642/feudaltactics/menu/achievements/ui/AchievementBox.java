// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.FeudalTacticsDialog;
import de.sesu8642.feudaltactics.menu.common.ui.SkinConstants;
import lombok.Getter;

import java.util.List;

/**
 * A UI element that shows the status of an achievement. It is used in the {@link AchievementsSlide} in a table.
 * If you tap on it, it shows a dialog with details.
 */
public class AchievementBox {

    /* Width of a single achievement box */
    private final static float ACHIEVEMENT_WINDOW_WIDTH = 300f;
    /* Height of a single achievement box */
    private final static float ACHIEVEMENT_WINDOW_HEIGHT = 200f;
    private final Skin skin;
    private final AbstractAchievement achievement;
    private final DialogFactory dialogFactory;
    private final LocalizationManager localizationManager;
    private final Drawable achievementBackgroundLockedDrawable;
    private final Drawable achievementBackgroundUnlockedDrawable;
    private final Drawable rowBorderDrawable;
    @Getter
    private final Window achievementWindow;

    public AchievementBox(
        Skin skin,
        AbstractAchievement achievement,
        DialogFactory dialogFactory,
        LocalizationManager localizationManager) {

        this.skin = skin;
        this.achievement = achievement;
        this.dialogFactory = dialogFactory;
        this.localizationManager = localizationManager;

        achievementBackgroundLockedDrawable = skin.newDrawable(SkinConstants.DRAWABLE_WHITE, Color.GRAY);
        achievementBackgroundUnlockedDrawable = skin.newDrawable(SkinConstants.DRAWABLE_WHITE, Color.LIME);
        rowBorderDrawable = skin.newDrawable(SkinConstants.DRAWABLE_WHITE, Color.BLACK);

        achievementWindow = createAchievementWindow();
    }

    /**
     * Creates the scaffolding Window for the achievement box. The content is added in displayAchievement(),
     * as it may change based on the achievement's state (unlocked vs locked).
     */
    private Window createAchievementWindow() {
        final Window achievementWindow = new Window(localizationManager.localizeText(achievement.getNameKey()), skin);

        // The user shall not move around the achievement boxes. They align themselves
        achievementWindow.setMovable(false);
        // This window is rendered inside a ScrollPane. Keep-within-stage clamping makes
        // windows stick to the bottom while scrolling instead of being clipped out.
        achievementWindow.setKeepWithinStage(false);

        // Set fixed size for the achievement window
        achievementWindow.setSize(ACHIEVEMENT_WINDOW_WIDTH, ACHIEVEMENT_WINDOW_HEIGHT);

        achievementWindow.pad(20f);

        // Enable title label wrapping with width constraint
        final Label titleLabel = achievementWindow.getTitleLabel();
        if (titleLabel != null) {
            titleLabel.setEllipsis(null); // Disable ellipsis (the "..." truncation)
            titleLabel.setWrap(true);
            titleLabel.setWidth(ACHIEVEMENT_WINDOW_WIDTH - 40); // Leave padding for window decorations
            // Get the title table and set the width
            final Table titleTable = achievementWindow.getTitleTable();
            if (titleTable != null) {
                titleTable.getCell(titleLabel).width(ACHIEVEMENT_WINDOW_WIDTH - 40);
            }
        }

        final WindowStyle previousStyle = achievementWindow.getStyle();
        final Window.WindowStyle windowStyle = new Window.WindowStyle(previousStyle.titleFont, Color.BLACK,
            achievementBackgroundLockedDrawable);
        achievementWindow.setStyle(windowStyle);

        // Reserve space at the top for the title
        achievementWindow.padTop(80);

        return achievementWindow;
    }

    /**
     * Displays a single achievement in a summarized form in the UI.
     */
    public Actor displayAchievement() {
        achievementWindow.clearChildren();

        if (achievement.isUnlocked()) {
            achievementWindow.background(achievementBackgroundUnlockedDrawable);

            final Label progressLabel = new Label("Unlocked", skin);
            achievementWindow.add(progressLabel).row();
        } else {
            achievementWindow.background(achievementBackgroundLockedDrawable);

            final ProgressBar progressBar = new ProgressBar(0, achievement.getGoal(), 1, false, skin);
            progressBar.setValue(achievement.getProgress());
            achievementWindow.add(progressBar).row();
        }

        final Actor achievementBox = wrapInBorder(achievementWindow);

        achievementBox.setSize(ACHIEVEMENT_WINDOW_WIDTH, ACHIEVEMENT_WINDOW_HEIGHT);

        return achievementBox;
    }

    private Actor wrapInBorder(Actor innerContent) {
        final Container<Actor> container = new Container<Actor>(innerContent) {
            @Override
            public float getPrefWidth() {
                return ACHIEVEMENT_WINDOW_WIDTH;
            }

            @Override
            public float getPrefHeight() {
                return ACHIEVEMENT_WINDOW_HEIGHT;
            }
        };
        container.background(rowBorderDrawable);
        container.pad(3); // Border width
        container.fill();
        return container;
    }

    /**
     * Shows a modal dialog with detailed information about the achievement.
     */
    public FeudalTacticsDialog createAchievementDetailsDialog() {
        final FeudalTacticsDialog detailsDialog = dialogFactory.createDialog(result -> {
            // Dialog closed
        });

        final LabelStyle baseStyle = skin.get(SkinConstants.FONT_MENU_HEADING, LabelStyle.class);
        final Label titleLabel = new Label(localizationManager.localizeText(achievement.getNameKey()),
            new LabelStyle(baseStyle));
        detailsDialog.text(titleLabel);
        detailsDialog.getContentTable().row();
        final String description = getTranslatedDescription();
        detailsDialog.text(description);
        detailsDialog.getContentTable().row();

        // Show progress information
        final String progressText;
        if (achievement.isUnlocked()) {
            progressText = "Status: Unlocked";
        } else {
            progressText = String.format("Progress: %d / %d", achievement.getProgress(), achievement.getGoal());
        }
        detailsDialog.text(progressText);

        detailsDialog.button("Close", true);

        return detailsDialog;
    }

    private String getTranslatedDescription() {
        final List<String> descriptionParagraphKeys = achievement.getDescriptionParagraphKeys();
        final StringBuilder descriptionTextBuilder = new StringBuilder();
        for (String descriptionParagraphKey : descriptionParagraphKeys) {
            descriptionTextBuilder.append(localizationManager.localizeText(descriptionParagraphKey));
            if (!descriptionParagraphKey.equals(descriptionParagraphKeys.get(descriptionParagraphKeys.size() - 1))) {
                // if not the last paragraph, add line break
                descriptionTextBuilder.append("\n\n");
            }
        }
        return descriptionTextBuilder.toString();
    }
}

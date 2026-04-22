// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.ui.Window.WindowStyle;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;

import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingClickListener;
import de.sesu8642.feudaltactics.menu.common.ui.SkinConstants;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Represents the slide in the achievements screen.
 */
@Singleton
public class AchievementsSlide extends Slide {

    private final Skin skin;
    private final HorizontalGroup achievementTileGroup;
    private final AchievementRepository achievementRepository;
    private final DialogFactory dialogFactory;
    private final Drawable achievementBackgroundLockedDrawable;
    private final Drawable achievementBackgroundUnlockedDrawable;
    private final Drawable rowBorderDrawable;

    /**
     * Constructor.
     *
     * @param skin game skin
     */
    @Inject
    public AchievementsSlide(Skin skin, AchievementRepository achievementRepository, DialogFactory dialogFactory) {
        super(skin, "Achievements");
        this.skin = skin;
        this.achievementRepository = achievementRepository;
        this.dialogFactory = dialogFactory;

        this.achievementBackgroundLockedDrawable = skin.newDrawable(SkinConstants.DRAWABLE_WHITE, Color.GRAY);
        this.achievementBackgroundUnlockedDrawable = skin.newDrawable(SkinConstants.DRAWABLE_WHITE, Color.LIME);
        this.rowBorderDrawable = skin.newDrawable(SkinConstants.DRAWABLE_WHITE, Color.BLACK);

        achievementTileGroup =  new HorizontalGroup();
        achievementTileGroup.wrap();
        achievementTileGroup.space(10);
        achievementTileGroup.wrapSpace(10);
        achievementTileGroup.align(Align.center);

        List<AbstractAchievement> achievements = achievementRepository.getAchievements();
        for (AbstractAchievement achievement : achievements) {
            Actor achievementBox = displayAchievement(achievement);
            achievementBox.setSize(ACHIEVEMENT_WINDOW_WIDTH, ACHIEVEMENT_WINDOW_HEIGHT);
            achievementTileGroup.addActor(achievementBox);
        }

        getTable().add(achievementTileGroup).fill().expand();
    }

    /* Width of a single achievement box */
    private final static float ACHIEVEMENT_WINDOW_WIDTH = 300f;
    /* Height of a single achievement box */
    private final static float ACHIEVEMENT_WINDOW_HEIGHT = 200f;

    /**
     * Displays a single achievement in a summarized for in the UI. Will be used as part of a table.
     */
    private Actor displayAchievement(AbstractAchievement achievement) {
        Window achievementWindow = new Window(achievement.getName(), skin);

        // The user shall not move around the achievement boxes. They align themselves
        achievementWindow.setMovable(false);
        // This window is rendered inside a ScrollPane. Keep-within-stage clamping makes
        // windows stick to the bottom while scrolling instead of being clipped out.
        achievementWindow.setKeepWithinStage(false);

        // Set fixed size for the achievement window
        achievementWindow.setSize(ACHIEVEMENT_WINDOW_WIDTH, ACHIEVEMENT_WINDOW_HEIGHT);

        achievementWindow.pad(20f);

        // Enable title label wrapping with width constraint
        Label titleLabel = achievementWindow.getTitleLabel();
        if (titleLabel != null) {
            titleLabel.setEllipsis(null); // Disable ellipsis (the "..." truncation)
            titleLabel.setWrap(true);
            titleLabel.setWidth(ACHIEVEMENT_WINDOW_WIDTH - 40); // Leave padding for window decorations
            // Get the title table and set the width
            Table titleTable = achievementWindow.getTitleTable();
            if (titleTable != null) {
                titleTable.getCell(titleLabel).width(ACHIEVEMENT_WINDOW_WIDTH - 40);
            }
        }

        WindowStyle previousStyle = achievementWindow.getStyle();
        Window.WindowStyle windowStyle = new Window.WindowStyle(previousStyle.titleFont, Color.BLACK, achievementBackgroundLockedDrawable);
        achievementWindow.setStyle(windowStyle);

        // Reserve space at the top for the title
        achievementWindow.padTop(80);

        if (achievement.isUnlocked()) {
            achievementWindow.background(achievementBackgroundUnlockedDrawable);

            Label progressLabel = new Label("Unlocked", skin);
            achievementWindow.add(progressLabel).row();
        } else {
            achievementWindow.background(achievementBackgroundLockedDrawable);

            ProgressBar progressBar = new ProgressBar(0, achievement.getGoal(), 1, false, skin);
            progressBar.setValue(achievement.getProgress());
            achievementWindow.add(progressBar).row();
        }

        // Add click listener to open detail dialog
        achievementWindow.addListener(new ExceptionLoggingClickListener(() ->
            showAchievementDetails(achievement)
        ));

        return wrapInBorder(achievementWindow);
    }

    private Actor wrapInBorder(Actor innerContent) {
        final Container<Actor> container = new Container<>(innerContent);
        container.background(rowBorderDrawable);
        container.pad(3); // Border width
        container.fill();
        return container;
    }

    /**
     * Shows a modal dialog with detailed information about the achievement.
     */
    private void showAchievementDetails(AbstractAchievement achievement) {
        Dialog detailsDialog = dialogFactory.createDialog(result -> {
            // Dialog closed
        });

        LabelStyle baseStyle = skin.get(SkinConstants.FONT_MENU_HEADING, LabelStyle.class);
        Label titleLabel = new Label(achievement.getName(), new LabelStyle(baseStyle));
        detailsDialog.text(titleLabel);
        detailsDialog.getContentTable().row();
        detailsDialog.text(achievement.getDescription());
        detailsDialog.getContentTable().row();

        // Show progress information
        String progressText;
        if (achievement.isUnlocked()) {
            progressText = "Status: Unlocked";
        } else {
            progressText = String.format("Progress: %d / %d", achievement.getProgress(), achievement.getGoal());
        }
        detailsDialog.text(progressText);

        detailsDialog.button("Close", true);
        detailsDialog.show(getTable().getStage());
    }
}

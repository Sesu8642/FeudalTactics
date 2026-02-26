// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Window;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

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
    private final Table achievementsTable;
    private final AchievementRepository achievementRepository;
    private final DialogFactory dialogFactory;
    private final Drawable achievementBackgroundLockedDrawable;
    private final Drawable achievementBackgroundUnlockedDrawable;

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

        achievementsTable = new Table();
        getTable().add(achievementsTable).fill().expand();
        refreshAchievements();
    }

    /* Width of a single achievement box */
    private final static float ACHIEVEMENT_WINDOW_WIDTH = 300f;
    /* Height of a single achievement box */
    private final static float ACHIEVEMENT_WINDOW_HEIGHT = 160f;

    /**
     * Refreshes the achievements UI with the latest values.
     */
    public void refreshAchievements() {
        achievementsTable.clear();

        List<AbstractAchievement> achievements = achievementRepository.getAchievements();

        // Calculate how many achievements fit per row based on screen width
        // Estimate achievement window width + padding: ~300px per achievement window
        final float padding = 10f;
        final float screenWidth = Gdx.graphics.getWidth();
        final int achievementsPerRow = Math.max(1, (int) (screenWidth / (ACHIEVEMENT_WINDOW_WIDTH + 2 * padding)));

        int columnCount = 0;
        for (AbstractAchievement achievement : achievements) {
            Window achievementWindow = displayAchievement(achievement);
            achievementsTable.add(achievementWindow)
                .width(ACHIEVEMENT_WINDOW_WIDTH)
                .height(ACHIEVEMENT_WINDOW_HEIGHT)
                .pad(padding);

            columnCount++;
            if (columnCount >= achievementsPerRow) {
                achievementsTable.row();
                columnCount = 0;
            }
        }
    }

    /**
     * Displays a single achievement in a summarized for in the UI. Will be used as part of a table.
     */
    private Window displayAchievement(AbstractAchievement achievement) {
        Window achievementWindow = new Window(achievement.getName(), skin);

        achievementWindow.setMovable(false);


        // Set fixed size for the achievement window
        achievementWindow.setSize(ACHIEVEMENT_WINDOW_WIDTH, ACHIEVEMENT_WINDOW_HEIGHT);

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

        if (achievement.isUnlocked()) {
            achievementWindow.background(achievementBackgroundUnlockedDrawable);

            // Create a label with a white font color
            LabelStyle whiteLabelStyle = new LabelStyle(skin.get(LabelStyle.class));
            whiteLabelStyle.fontColor = Color.WHITE;
            Label progressLabel = new Label("Unlocked", whiteLabelStyle);
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

        return achievementWindow;
    }

    /**
     * Shows a modal dialog with detailed information about the achievement.
     */
    private void showAchievementDetails(AbstractAchievement achievement) {
        Dialog detailsDialog = dialogFactory.createDialog(result -> {
            // Dialog closed
        });

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

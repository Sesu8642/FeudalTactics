// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import de.sesu8642.feudaltactics.menu.common.ui.SkinConstants;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static de.sesu8642.feudaltactics.menu.common.ui.SkinConstants.COLOR_HIGHLIGHT;
import static de.sesu8642.feudaltactics.menu.common.ui.SkinConstants.PROGRESS_BAR_STYLE_ACHIEVEMENT;
import static de.sesu8642.feudaltactics.menu.common.ui.UiScalingConstants.UI_SCALING_FACTOR;

/**
 * UI element for an achievement and its progress.
 */
@Slf4j
public class AchievementTile extends Container<Actor> {

    /**
     * Width of this.
     */
    public static final float ACHIEVEMENT_TILE_WIDTH = Gdx.graphics.getDensity() * 500 * UI_SCALING_FACTOR;
    private static final int BORDER_WIDTH = (int) (Gdx.graphics.getDensity() * 5 * UI_SCALING_FACTOR);
    private static final int OUTER_PAD = (int) (Gdx.graphics.getDensity() * 10);
    private static final int INNER_PAD = (int) (Gdx.graphics.getDensity() * 10);
    private final Drawable backgroundDrawableLocked;
    private final Drawable backgroundDrawableUnlocked;

    @Getter
    private final AbstractAchievement achievement;
    private final LocalizationManager localizationManager;
    private Table contentTable;
    private Label progressBarLabel;
    private ProgressBar progressBar;

    public AchievementTile(AbstractAchievement achievement, Skin skin, LocalizationManager localizationManager) {
        this.achievement = achievement;
        backgroundDrawableLocked = skin.newDrawable(SkinConstants.DRAWABLE_WHITE,
            skin.getColor(COLOR_HIGHLIGHT));
        backgroundDrawableUnlocked = skin.newDrawable(SkinConstants.DRAWABLE_WHITE,
            skin.getColor(SkinConstants.COLOR_GOLD));
        this.localizationManager = localizationManager;
        initUi(achievement, skin);
    }

    private void initUi(AbstractAchievement achievement, Skin skin) {
        setBackground(skin.newDrawable(SkinConstants.DRAWABLE_WHITE, Color.BLACK));
        pad(BORDER_WIDTH);
        fill();

        contentTable = new Table();
        contentTable.pad(OUTER_PAD);
        setActor(contentTable);

        contentTable.add().width(ACHIEVEMENT_TILE_WIDTH).row();

        final Label titleLabel = new Label(achievement.getTranslatedName(localizationManager), skin,
            SkinConstants.FONT_H2);

        titleLabel.setWrap(true);
        contentTable.add(titleLabel).fillX().padBottom(INNER_PAD).minHeight(Gdx.graphics.getDensity() * 250 * UI_SCALING_FACTOR);
        contentTable.row();

        progressBarLabel = new Label("", skin);
        progressBarLabel.setAlignment(Align.center);

        progressBar = new ProgressBar(0, achievement.getGoal(), 1, false, skin);

        final ProgressBar.ProgressBarStyle progressBarStyle = skin.get(PROGRESS_BAR_STYLE_ACHIEVEMENT,
            ProgressBar.ProgressBarStyle.class);
        // hack: scale the drawables to scale the progress bar
        final float foreGroundBackgroundSizeDifference =
            progressBarStyle.background.getMinHeight() - progressBarStyle.knobBefore.getMinHeight();
        progressBarStyle.background.setMinHeight(progressBarLabel.getStyle().font.getLineHeight() * 1.5F);
        progressBarStyle.knobBefore.setMinHeight(progressBarStyle.background.getMinHeight() - foreGroundBackgroundSizeDifference);
        progressBar.setStyle(progressBarStyle);

        final Stack progressBarStack = new Stack();
        progressBarStack.add(progressBar);
        progressBarStack.add(progressBarLabel);
        contentTable.add(progressBarStack).fillX();
    }

    /**
     * Updates the displayed information. Relies on the achievement object being updated by another component.
     */
    public void updateData() {
        final String progressText;
        if (achievement.isUnlocked()) {
            contentTable.setBackground(backgroundDrawableUnlocked);
            progressText = localizationManager.localizeText(TranslationKeys.ACHIEVEMENTS_SUMMARY_IS_UNLOCKED);
        } else {
            contentTable.setBackground(backgroundDrawableLocked);
            progressText = achievement.getProgress() + " / " + achievement.getGoal();
        }
        progressBarLabel.setText(progressText);
        progressBar.setValue(achievement.getProgress());
    }

}

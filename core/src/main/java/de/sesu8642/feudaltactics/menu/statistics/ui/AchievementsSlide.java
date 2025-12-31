// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.sesu8642.feudaltactics.menu.common.ui.Slide;

/**
 * Represents the slide for displaying achievements.
 * This is currently a placeholder for future implementation.
 */
@Singleton
public class AchievementsSlide extends Slide {

    private final Skin skin;
    private final Table achievementsTable;

    /**
     * Constructor.
     *
     * @param skin game skin
     */
    @Inject
    public AchievementsSlide(Skin skin) {
        super(skin, "Achievements");
        this.skin = skin;
        this.achievementsTable = new Table();
        getTable().add(achievementsTable).fill().expand();
        refreshAchievements();
    }

    /**
     * Refreshes the achievements UI.
     */
    public void refreshAchievements() {
        achievementsTable.clear();

        final Label placeholderLabel = new Label("Achievements coming soon!", skin);
        placeholderLabel.setWrap(true);
        achievementsTable.add(placeholderLabel).fill().expand();
    }
}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Represents the slide in the achievements screen.
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
        achievementsTable = new Table();
        getTable().add(achievementsTable).fill().expand();
        refreshAchievements();
    }

    /**
     * Refreshes the achievements UI with the latest values.
     */
    public void refreshAchievements() {
        achievementsTable.clear();
        // TODO: Add actual achievements here
        final Label placeholder = new Label("Achievements coming soon", skin);
        placeholder.setWrap(true);
        achievementsTable.add(placeholder).left().fill().expandX();
    }
}

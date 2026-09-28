// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Align;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.menu.achievements.AchievementsService;
import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import de.sesu8642.feudaltactics.menu.common.ui.EvenlySpacedHorizontalGroup;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the slide in the achievements screen.
 */
@Singleton
public class AchievementsSlide extends Slide {

    /**
     * Minimum horizontal space between tiles.
     */
    public static final float TILE_SPACING = Gdx.graphics.getDensity() * 40;

    @Getter
    private final List<AchievementTile> achievementTiles = new ArrayList<>();

    @Inject
    public AchievementsSlide(Skin skin, AchievementsService achievementsService,
                             LocalizationManager localizationManager) {
        super(skin, localizationManager.localizeText(TranslationKeys.ACHIEVEMENTS_PAGE_HEADLINE));

        final EvenlySpacedHorizontalGroup achievementTileGroup =
            new EvenlySpacedHorizontalGroup(AchievementTile.ACHIEVEMENT_TILE_WIDTH);
        achievementTileGroup.wrap();
        achievementTileGroup.rowLeft();
        achievementTileGroup.space(TILE_SPACING);
        achievementTileGroup.wrapSpace(TILE_SPACING);
        achievementTileGroup.align(Align.center);

        final List<AbstractAchievement> achievements = achievementsService.getAchievements();
        for (AbstractAchievement achievement : achievements) {
            final AchievementTile achievementTile = new AchievementTile(achievement, skin, localizationManager);
            achievementTiles.add(achievementTile);
            achievementTileGroup.addActor(achievementTile);
        }

        getTable().add(achievementTileGroup).fill().expand();
    }

    /**
     * Refresh the achievement tiles from the current service state.
     * Call this when the screen becomes visible to update the UI.
     */
    public void updateAchievementData() {
        for (AchievementTile achievementTile : achievementTiles) {
            achievementTile.updateData();
        }
    }
}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.campaign.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.Align;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.menu.common.ui.EvenlySpacedHorizontalGroup;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;

import javax.inject.Inject;
import javax.inject.Singleton;


// TODO: check this
// this is not just a slide created by a factory because it needs the additional accessors for text
// it is not created by the LevelSelectionStageForMenu because that could only use static methods as the slide needs to
// be passed to the super constructor

/**
 * UI for level selection.
 */
@Singleton
public class LevelSelectionSlide extends Slide {

    public static final float PREVIEW_TILE_SIZE = Gdx.graphics.getDensity() * 500;
    public static final float TILE_SPACING = Gdx.graphics.getDensity() * 40;
    final TextArea textArea;
    final EvenlySpacedHorizontalGroup levelTileGroup;

    /**
     * Constructor.
     */
    @Inject
    public LevelSelectionSlide(Skin skin, ScenarioMapPreviewTileFactory scenarioMapPreviewTileFactory,
                               LocalizationManager localizationManager) {
        super(skin, localizationManager.localizeText(TranslationKeys.CAMPAIGN_LEVEL_SELECTION_PAGE_HEADLINE));

        textArea = new TextArea("", skin);
        textArea.setDisabled(true);

        levelTileGroup = new EvenlySpacedHorizontalGroup(PREVIEW_TILE_SIZE);
        levelTileGroup.wrap();
        levelTileGroup.rowLeft();
        levelTileGroup.space(TILE_SPACING);
        levelTileGroup.wrapSpace(TILE_SPACING);
        levelTileGroup.align(Align.center);

        for (int i = 0; i < 50; i++) {
            final ScenarioMapPreviewTile scenarioMapPreviewTile =
                scenarioMapPreviewTileFactory.createScenarioMapPreviewTile(ScenarioMap.TUTORIAL, i < 5,
                    Medals.values()[Math.min(i, Medals.values().length - 1)], i + 1);
            scenarioMapPreviewTile.setWidth(PREVIEW_TILE_SIZE);
            levelTileGroup.addActor(scenarioMapPreviewTile);
        }

        getTable().add(levelTileGroup).fill().expand();
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;
import de.sesu8642.feudaltactics.menu.statistics.CountByAiLevel;
import de.sesu8642.feudaltactics.menu.statistics.Statistics;
import de.sesu8642.feudaltactics.menu.statistics.StatisticsDao;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Represents the slide in the statistics screen.
 */
@Singleton

public class StatisticsSlide extends Slide {

    private final StatisticsDao statisticsDao;
    private final Skin skin;
    private final LocalizationManager localizationManager;
    private final Table statisticsTable;


    /**
     * Constructor.
     *
     * @param skin game skin
     */
    @Inject
    public StatisticsSlide(Skin skin, StatisticsDao statisticsDao, LocalizationManager localizationManager) {
        super(skin, localizationManager.localizeText(TranslationKeys.STATISTICS_PAGE_HEADLINE));
        this.statisticsDao = statisticsDao;
        this.skin = skin;
        this.localizationManager = localizationManager;
        statisticsTable = new Table();
        getTable().add(statisticsTable).fill().expand();
        refreshStatistics();
    }

    private void placeIntegerWithLabel(String labelText, int value) {
        final Label newLabel = new Label(labelText, skin);
        newLabel.setWrap(true);
        statisticsTable.add(newLabel).left().fill().expandX().prefWidth(200);

        final Label valueLabel = new Label(Integer.toString(value), skin);
        statisticsTable.add(valueLabel).colspan(2).center().fillX().expandX().padLeft(10);
        statisticsTable.row();
        statisticsTable.add().height(20);
        statisticsTable.row();
    }

    private void placeHeading(String headingText) {
        statisticsTable.row();
        statisticsTable.add().height(10);
        statisticsTable.row();
        final Label headingLabel = new Label(headingText, skin);
        headingLabel.setWrap(true);
        statisticsTable.add(headingLabel).colspan(3).left().fill().expandX();
        statisticsTable.row();
        statisticsTable.add().height(10);
        statisticsTable.row();
    }

    /**
     * Refreshes the statistics UI with the latest values.
     */
    public void refreshStatistics() {
        statisticsTable.clear();
        final Statistics statistics = statisticsDao.getStatistics();
        placeIntegerWithLabel(localizationManager.localizeText(TranslationKeys.STATISTICS_PAGE_TOTAL_GAMES_PLAYED),
            statistics.getGamesPlayed());
        final CountByAiLevel gamesWon = statistics.getGamesWon();
        placeIntegerWithLabel(localizationManager.localizeText(TranslationKeys.STATISTICS_PAGE_TOTAL_GAMES_WON),
            gamesWon.getTotalCount());
        final CountByAiLevel gamesLost = statistics.getGamesLost();
        placeIntegerWithLabel(localizationManager.localizeText(TranslationKeys.STATISTICS_PAGE_TOTAL_GAMES_WON),
            gamesLost.getTotalCount());
        final CountByAiLevel gamesAborted = statistics.getGamesAborted();
        placeIntegerWithLabel(localizationManager.localizeText(TranslationKeys.STATISTICS_PAGE_TOTAL_GAMES_ABORTED),
            gamesAborted.getTotalCount());
        placeIntegerWithLabel(localizationManager.localizeText(TranslationKeys.STATISTICS_PAGE_TOTAL_MAPS_GENERATED),
            statistics.getMapsGenerated());

        statisticsTable.row();

        final Intelligence[] intelligenceLevels = Intelligence.values();
        for (Intelligence level : intelligenceLevels) {
            final String sectionTitle = intelligenceLevelToSectionTitle(level);
            placeHeading(sectionTitle);
            placeIntegerWithLabel("  " + localizationManager.localizeText(TranslationKeys.STATISTICS_PAGE_GAMES_WON),
                gamesWon.getCountByAiLevel().get(level));
            placeIntegerWithLabel("  " + localizationManager.localizeText(TranslationKeys.STATISTICS_PAGE_GAMES_LOST),
                gamesLost.getCountByAiLevel().get(level));
            placeIntegerWithLabel("  " + localizationManager.localizeText(TranslationKeys.STATISTICS_PAGE_GAMES_ABORTED),
                gamesAborted.getCountByAiLevel().get(level));
            statisticsTable.row();
        }

    }

    private String intelligenceLevelToSectionTitle(Intelligence level) {
        final String sectionTitleKey;
        switch (level) {
            case LEVEL_1:
                sectionTitleKey = TranslationKeys.STATISTICS_PAGE_DIFFICULTY_SECTION_TITLE_EASY;
                break;
            case LEVEL_2:
                sectionTitleKey = TranslationKeys.STATISTICS_PAGE_DIFFICULTY_SECTION_TITLE_MEDIUM;
                break;
            case LEVEL_3:
                sectionTitleKey = TranslationKeys.STATISTICS_PAGE_DIFFICULTY_SECTION_TITLE_HARD;
                break;
            case LEVEL_4:
                sectionTitleKey = TranslationKeys.STATISTICS_PAGE_DIFFICULTY_SECTION_TITLE_VERY_HARD;
                break;
            default:
                throw new IllegalStateException("Unknown bot intelligence level " + level);
        }
        return localizationManager.localizeText(sectionTitleKey);
    }
}

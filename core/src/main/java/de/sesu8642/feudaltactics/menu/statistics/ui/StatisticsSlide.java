// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.ingame.ui.EnumDisplayNameConverter;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
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
        super(skin, localizationManager.localizeText("statistics"));
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
        placeIntegerWithLabel(localizationManager.localizeText("total-games-played"), statistics.getGamesPlayed());
        final CountByAiLevel gamesWon = statistics.getGamesWon();
        placeIntegerWithLabel(localizationManager.localizeText("total-games-won"), gamesWon.getTotalCount());
        final CountByAiLevel gamesLost = statistics.getGamesLost();
        placeIntegerWithLabel(localizationManager.localizeText("total-games-lost"), gamesLost.getTotalCount());
        final CountByAiLevel gamesAborted = statistics.getGamesAborted();
        placeIntegerWithLabel(localizationManager.localizeText("total-games-aborted"), gamesAborted.getTotalCount());
        placeIntegerWithLabel(localizationManager.localizeText("total-maps-generated"), statistics.getMapsGenerated());

        statisticsTable.row();

        final Intelligence[] aiLevels = Intelligence.values();
        for (Intelligence level : aiLevels) {
            placeHeading(localizationManager.localizeText("ai-statistics",
                EnumDisplayNameConverter.getLocalizedDisplayName(level, localizationManager)));
            placeIntegerWithLabel("  " + localizationManager.localizeText("games-won"),
                gamesWon.getCountByAiLevel().get(level));
            placeIntegerWithLabel("  " + localizationManager.localizeText("games-lost"),
                gamesLost.getCountByAiLevel().get(level));
            placeIntegerWithLabel("  " + localizationManager.localizeText("games-aborted"),
                gamesAborted.getCountByAiLevel().get(level));
            statisticsTable.row();
        }

    }
}

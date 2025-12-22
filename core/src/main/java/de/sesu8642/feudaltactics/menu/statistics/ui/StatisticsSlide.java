// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.sesu8642.feudaltactics.menu.common.ui.Slide;
import de.sesu8642.feudaltactics.menu.statistics.Statistics;
import de.sesu8642.feudaltactics.menu.statistics.StatisticsDao;

/**
 * Represents the slide in the statistics screen.
 */
@Singleton

public class StatisticsSlide extends Slide {

    private final StatisticsDao statisticsDao;
    private final Skin skin;
    private final Table statisticsTable;


    /**
     * Constructor.
     *
     * @param skin game skin
     */
    @Inject
    public StatisticsSlide(Skin skin, StatisticsDao statisticsDao) {
        super(skin, "Statistics");
        this.statisticsDao = statisticsDao;
        this.skin = skin;
        this.statisticsTable = new Table();
        getTable().add(statisticsTable).fill().expand();
        refreshStatistics();
    }


    private void placeIntegerWithLabel(Table statisticsTable, String labelText, int value) {
        final Label newLabel = new Label(labelText, skin);
        newLabel.setWrap(true);
        statisticsTable.add(newLabel).left().fill().expandX().prefWidth(200);

        final Label valueLabel = new Label(Integer.toString(value), skin);
        statisticsTable.add(valueLabel).center().fillX().expandX();
        statisticsTable.row();
        statisticsTable.add().height(20);
        statisticsTable.row();
    }

    /**
     * Refreshes the statistics UI with the latest values.
     */
    public void refreshStatistics() {
        statisticsTable.clear();
        Statistics statistics = statisticsDao.getStatistics();
        placeIntegerWithLabel(statisticsTable, "Total games played", statistics.getGamesPlayed());
        // Add more statistics here as needed
        statisticsTable.row();
        statisticsTable.add().fill().expand();
    }
}
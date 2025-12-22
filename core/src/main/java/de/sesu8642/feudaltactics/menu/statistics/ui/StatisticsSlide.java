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

    private final Statistics statistics;

    /**
     * Constructor.
     *
     * @param skin game skin
     */
     @Inject
    public StatisticsSlide(Skin skin, StatisticsDao statisticsDao) {
        super(skin, "Statistics");

        final Table statisticsTable = new Table();
        statistics = statisticsDao.getStatistics();

        placeIntegerWithLabel(statisticsTable, "Total games played", statistics.getGamesPlayed(), skin);
        // add a row to fill the rest of the space in order for the other options to be
        // at the top of the page

        statisticsTable.row();
        statisticsTable.add().fill().expand();
        getTable().add(statisticsTable).fill().expand();
    }

    private void placeIntegerWithLabel(Table statisticsTable, String labelText, int value, Skin skin) {
        final Label newLabel = new Label(labelText, skin);
        newLabel.setWrap(true);
        statisticsTable.add(newLabel).left().fill().expandX().prefWidth(200);

        final Label valueLabel = new Label(Integer.toString(value), skin);
        statisticsTable.add(valueLabel).center().fillX().expandX();
        statisticsTable.row();
        statisticsTable.add().height(20);
        statisticsTable.row();
    }
}
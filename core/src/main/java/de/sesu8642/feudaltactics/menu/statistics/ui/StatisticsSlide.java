// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;

import de.sesu8642.feudaltactics.ingame.ui.EnumDisplayNameConverter;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.common.ui.ButtonFactory;
import de.sesu8642.feudaltactics.menu.common.ui.CopyButton;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;
import de.sesu8642.feudaltactics.menu.statistics.CountByAiLevel;
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

    private void placeIntegerWithLabel(String labelText, int value) {
        final Label newLabel = new Label(labelText, skin);
        newLabel.setWrap(true);
        statisticsTable.add(newLabel).left().fill().expandX().prefWidth(200);

        final Label valueLabel = new Label(Integer.toString(value), skin);
        statisticsTable.add(valueLabel).colspan(2).center().fillX().expandX();
        statisticsTable.row();
        statisticsTable.add().height(20);
        statisticsTable.row();
    }

    private void placeSeedWithLabel(String labelText, long seed) {
        final Label newLabel = new Label(labelText, skin);
        newLabel.setWrap(true);
        statisticsTable.add(newLabel).left().fill().expandX().prefWidth(200);

        final String seedAsString = Long.toString(seed);
        final Label valueLabel = new Label(seedAsString, skin);
        final CopyButton copyButton = ButtonFactory.createCopyButton("Copy", skin, true);
        copyButton.addListener(new ExceptionLoggingChangeListener(
            () -> Gdx.app.getClipboard().setContents(seedAsString)));
        statisticsTable.add(valueLabel).center().fillX().expandX();
        statisticsTable.add(copyButton);
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
        Statistics statistics = statisticsDao.getStatistics();
        placeIntegerWithLabel("Total games played", statistics.getGamesPlayed());
        CountByAiLevel gamesWon = statistics.getGamesWon();
        placeIntegerWithLabel("Total games won", gamesWon.getTotalCount());
        CountByAiLevel gamesLost = statistics.getGamesLost();
        placeIntegerWithLabel("Total games lost", gamesLost.getTotalCount());
        CountByAiLevel gamesAborted = statistics.getGamesAborted();
        placeIntegerWithLabel("Total games aborted", gamesAborted.getTotalCount());

        statisticsTable.row();

        Intelligence[] aiLevels = Intelligence.values();
        for (Intelligence level : aiLevels) {
            placeHeading(EnumDisplayNameConverter.getDisplayName(level) + " AI Statistics");
            placeIntegerWithLabel("  Games won",
                gamesWon.getCountByAiLevel().get(level));
            placeIntegerWithLabel("  Games lost", 
                gamesLost.getCountByAiLevel().get(level));
            placeIntegerWithLabel("  Games aborted", 
                gamesAborted.getCountByAiLevel().get(level));
            statisticsTable.row();
        }

        placeHeading("Favorite Seed");
        placeSeedWithLabel("  Seed played most", statistics.getRecordSeed());
        placeIntegerWithLabel("  Number of games", statistics.getRecordSeedCount());
        statisticsTable.add().fill().expand();
    }
}
// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import de.sesu8642.feudaltactics.events.moves.GameStartEvent;
import de.sesu8642.feudaltactics.menu.statistics.StatisticsDao;

/**
 * Handles events for the statistics screen.
 */
public class StatisticsEventHandler {

    private final StatisticsDao statisticsDao;
    private final StatisticsSlide statisticsSlide;

    @Inject
    public StatisticsEventHandler(StatisticsDao statisticsDao, StatisticsSlide statisticsSlide) {
        this.statisticsDao = statisticsDao;
        this.statisticsSlide = statisticsSlide;
    }

    @Subscribe
    public void handleGameStart(GameStartEvent event) {
        statisticsDao.incrementGamesStarted();
        statisticsSlide.refreshStatistics();
    }
}

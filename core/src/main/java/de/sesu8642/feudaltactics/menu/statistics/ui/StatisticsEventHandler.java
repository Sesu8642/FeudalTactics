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

    public void onStatisticsScreenEvent() {
        // Logic to handle events on the statistics screen
    }

    @Inject
    public StatisticsEventHandler(StatisticsDao statisticsDao) {
        this.statisticsDao = statisticsDao;
    }

    @Subscribe
    public void handleGameStart(GameStartEvent event) {
        statisticsDao.incrementGamesStarted();
    }
}
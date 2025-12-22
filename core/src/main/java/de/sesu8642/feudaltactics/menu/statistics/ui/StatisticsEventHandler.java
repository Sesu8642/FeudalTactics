// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.events.moves.GameStartEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;
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

    @Subscribe
    public void handleGameExited(GameExitedEvent event) {
        GameState gameState = event.getGameState();
        if (null == gameState) {
            return;     // Ignore exits from editor or similar
        }

        switch (gameState.getWinner().getType()) {
            case LOCAL_PLAYER:
                statisticsDao.incrementGamesWon();
                break;
            case LOCAL_BOT:
            case REMOTE:
                statisticsDao.incrementGamesLost();
                break;
            default:    // No winner (e.g., exited mid-game)
                    // Find whether local player is already defeated
                if (gameState.getPlayers().stream()
                        .anyMatch(player -> player.getType() == Type.LOCAL_PLAYER && player.isDefeated())) {
                    statisticsDao.incrementGamesLost();
                }
                    // Otherwise, do not count as win/loss. Although the local player might likely have exited the game because they were losing.
                break;
        }

        statisticsSlide.refreshStatistics();
    }
}

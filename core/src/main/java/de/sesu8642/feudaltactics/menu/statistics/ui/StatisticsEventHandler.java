// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.events.moves.GameStartEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
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

        Intelligence aiDifficulty = gameState.getBotIntelligence();

        Player winnerOfTheGame = gameState.getWinner();
        if (null == winnerOfTheGame) {
            // Game exited without a winner
            if (gameState.getPlayers().stream()
                    .anyMatch(player -> player.getType() == Type.LOCAL_PLAYER && player.isDefeated())) {
                statisticsDao.incrementGamesLost(aiDifficulty);
            } else {
                statisticsDao.incrementGamesAborted(aiDifficulty);
            }
         } else  switch (winnerOfTheGame.getType()) {
            case LOCAL_PLAYER:
                statisticsDao.incrementGamesWon(aiDifficulty);
                break;
            case LOCAL_BOT:
            case REMOTE:
                statisticsDao.incrementGamesLost(aiDifficulty);
                break;
            default:    // Some unexpected player type
                // TODO: Log warning?
                break;
        }

        statisticsSlide.refreshStatistics();
    }
}

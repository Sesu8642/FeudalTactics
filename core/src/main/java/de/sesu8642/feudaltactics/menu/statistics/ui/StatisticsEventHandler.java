// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.statistics.HistoricGame;
import de.sesu8642.feudaltactics.menu.statistics.HistoricGame.GameResult;
import de.sesu8642.feudaltactics.menu.statistics.HistoryDao;
import de.sesu8642.feudaltactics.menu.statistics.StatisticsDao;

import javax.inject.Inject;

/**
 * Handles events for the statistics screen.
 */
public class StatisticsEventHandler {

    private final StatisticsDao statisticsDao;
    private final HistoryDao historyDao;

    @Inject
    public StatisticsEventHandler(StatisticsDao statisticsDao, HistoryDao historyDao) {
        this.statisticsDao = statisticsDao;
        this.historyDao = historyDao;
    }

    @Subscribe
    public void handleGameExited(GameExitedEvent event) {
        final GameState gameState = event.getGameState();
        if (gameState == null) {
            return;     // Ignore exits from editor or similar
        }

        final Intelligence aiDifficulty = gameState.getBotIntelligence();
        HistoricGame.GameResult gameResult = evaluateGameResult(gameState);

        statisticsDao.registerPlayedGame(aiDifficulty, gameResult);
        historyDao.registerPlayedGame(gameState, gameResult);
    }

    private GameResult evaluateGameResult(GameState gameState) {
        final Player winner = gameState.getWinner();
        if (null != winner) {
            switch (winner.getType()) {
                case LOCAL_PLAYER:
                    return GameResult.WIN;
                case LOCAL_BOT:
                case REMOTE:
                    return GameResult.LOSS;
                default:    // Some unexpected player type
                    throw new IllegalStateException("Unknown Player Type " + winner.getType());
            }
        } else {
            boolean localPlayerDefeated = gameState.getPlayers().stream()
                .anyMatch(player -> player.getType() == Type.LOCAL_PLAYER && player.isDefeated());
            if (localPlayerDefeated) {
                return GameResult.LOSS;
            } else {
                return GameResult.ABORTED;
            }
        }
    }

    @Subscribe
    public void handleSeedGenerated(de.sesu8642.feudaltactics.events.SeedGeneratedEvent event) {
        statisticsDao.incrementSeedsGenerated();
    }
}

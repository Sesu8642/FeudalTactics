// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

public class WinOnMapSizeAchievement extends AbstractAchievement {
    private final MapSizes mapSize;

    public WinOnMapSizeAchievement(AchievementRepository achievementRepository, NewGamePreferences.MapSizes mapSize) {
        super(achievementRepository, 1);

        this.mapSize = mapSize;
    }

    @Override
    public String getId() {
        return "win-" + mapSize.name().toLowerCase();
    }

    @Override
    public String getName() {
        return "Win on a " + mapSize.name().toLowerCase() + " map";
    }

    @Override
    public String getDescription() {
        return "Win a game on a " + mapSize.name().toLowerCase() + " map, either by defeating your enemies or them giving up. Any difficulty is allowed.";
    }

    @Override
    public void onGameExited(GameExitedEvent event) {
        final GameState gameState = event.getGameState();
        if (gameState == null) {
            return;     // Ignore exits from editor or similar
        }

        final Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame != null && winnerOfTheGame.getType() == Player.Type.LOCAL_PLAYER) {
            // TODO: Check the map size. This is easy after merging PR #116
            storeProgress(1);   // unlock
        }
    }
}

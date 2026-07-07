// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements.model;

import com.google.common.collect.ImmutableList;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;

/**
 * Achievement: Win a game on a specified map size, either by defeating your enemies or them giving up. Any
 * difficulty is allowed.
 */
public class WinOnMapSizeAchievement extends AbstractAchievement {
    private final MapSizes mapSize;

    public WinOnMapSizeAchievement(NewGamePreferences.MapSizes mapSize) {
        super(1, TranslationKeys.ACHIEVEMENT_WIN_ON_MAP_SIZE_NAME,
            ImmutableList.of(mapSizeToNameTranslationParameter(mapSize)),
            TranslationKeys.ACHIEVEMENT_WIN_ON_MAP_SIZE_NAME,
            ImmutableList.of(mapSizeToNameDescriptionParameter(mapSize)), true);

        this.mapSize = mapSize;
    }

    private static String mapSizeToNameTranslationParameter(MapSizes mapSize) {
        switch (mapSize) {
            case SMALL:
                return TranslationKeys.ACHIEVEMENT_WIN_ON_MAP_SIZE_NAME_PARAM_SIZE_SMALL;
            case MEDIUM:
                return TranslationKeys.ACHIEVEMENT_WIN_ON_MAP_SIZE_NAME_PARAM_SIZE_MEDIUM;
            case LARGE:
                return TranslationKeys.ACHIEVEMENT_WIN_ON_MAP_SIZE_NAME_PARAM_SIZE_LARGE;
            case XLARGE:
                return TranslationKeys.ACHIEVEMENT_WIN_ON_MAP_SIZE_NAME_PARAM_SIZE_XLARGE;
            case XXLARGE:
                return TranslationKeys.ACHIEVEMENT_WIN_ON_MAP_SIZE_NAME_PARAM_SIZE_XXLARGE;
            default:
                throw new IllegalStateException("Unknown map size " + mapSize);

        }
    }

    private static String mapSizeToNameDescriptionParameter(MapSizes mapSize) {
        switch (mapSize) {
            case SMALL:
                return TranslationKeys.ACHIEVEMENT_WIN_ON_MAP_SIZE_DESCRIPTION_PARAM_SIZE_SMALL;
            case MEDIUM:
                return TranslationKeys.ACHIEVEMENT_WIN_ON_MAP_SIZE_DESCRIPTION_PARAM_SIZE_MEDIUM;
            case LARGE:
                return TranslationKeys.ACHIEVEMENT_WIN_ON_MAP_SIZE_DESCRIPTION_PARAM_SIZE_LARGE;
            case XLARGE:
                return TranslationKeys.ACHIEVEMENT_WIN_ON_MAP_SIZE_DESCRIPTION_PARAM_SIZE_XLARGE;
            case XXLARGE:
                return TranslationKeys.ACHIEVEMENT_WIN_ON_MAP_SIZE_DESCRIPTION_PARAM_SIZE_XXLARGE;
            default:
                throw new IllegalStateException("Unknown map size " + mapSize);

        }
    }

    @Override
    public String getId() {
        return "win-" + mapSize.name().toLowerCase();
    }

    @Override
    public boolean onGameExited(GameExitedEvent event) {
        final GameState gameState = event.getGameState();
        if (gameState == null) {
            return false;     // Ignore exits from editor or similar
        }

        final Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame != null && winnerOfTheGame.getType() == Player.Type.LOCAL_PLAYER) {
            final NewGamePreferences gamePreferences = event.getGamePreferences();
            if (gamePreferences.getMapSize() == mapSize) {
                storeProgress(1);   // unlock
                return true;
            }
        }
        return false;
    }
}

package de.sesu8642.feudaltactics.menu.achievements.model;

import com.google.common.collect.ImmutableList;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;

/**
 * Achievement: Win a game in a specified number of rounds or less. The AI level must be Very Strong, the map size
 * must be at least Medium.
 */
public class WinInNRoundsAchievement extends AbstractAchievement {

    private final int rounds;

    public WinInNRoundsAchievement(int rounds) {
        super(1, TranslationKeys.ACHIEVEMENT_WIN_IN_N_ROUNDS_NAME, ImmutableList.of(String.valueOf(rounds)),
            TranslationKeys.ACHIEVEMENT_WIN_IN_N_ROUNDS_DESCRIPTION, ImmutableList.of(String.valueOf(rounds)), false);
        this.rounds = rounds;
    }

    @Override
    public String getId() {
        return "win_in_" + rounds + "_rounds";
    }

    @Override
    public boolean onGameExited(GameExitedEvent event) {
        final GameState gameState = event.getGameState();
        if (gameState == null) {
            return false;     // Ignore exits from editor or similar
        }

        final Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame == null || winnerOfTheGame.getType() != Player.Type.LOCAL_PLAYER) {
            return false;     // Player didn't win, so ignore
        }

        final Intelligence aiLevel = gameState.getBotIntelligence();
        if (aiLevel != Intelligence.LEVEL_4) {
            return false;    // Not a Very Hard game, ignore
        }

        final NewGamePreferences gamePreferences = event.getGamePreferences();
        if (gamePreferences.getMapSize().getAmountOfTiles() < NewGamePreferences.MapSizes.MEDIUM.getAmountOfTiles()) {
            return false;    // Map size is too small, ignore
        }

        if (gameState.getWinningRound() <= rounds) {
            storeProgress(1);
            return true;
        }
        return false;
    }
}

package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;

/**
 * Achievement: Win a game against the very hard AI when starting last in the turn order
 */
public class WinWhenStartingLastAchievement extends AbstractAchievement {

    public WinWhenStartingLastAchievement() {
        super(1, TranslationKeys.ACHIEVEMENT_WIN_WHEN_STARTING_LAST_NAME,
            TranslationKeys.ACHIEVEMENT_WIN_WHEN_STARTING_LAST_DESCRIPTION);
    }

    @Override
    public boolean isSecret() {
        return true;
    }

    @Override
    public String getId() {
        return "win-when-starting-last";
    }
    
    @Override
    public boolean onGameExited(GameExitedEvent event) {
        final de.sesu8642.feudaltactics.lib.gamestate.GameState gameState = event.getGameState();
        if (gameState == null) {
            return false;     // Ignore exits from editor or similar
        }

        final de.sesu8642.feudaltactics.lib.gamestate.Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame == null || winnerOfTheGame.getType() != de.sesu8642.feudaltactics.lib.gamestate.Player.Type.LOCAL_PLAYER) {
            return false;     // Ignore games without a winner or where the local player didn't win
        }

        final Player lastPlayer = gameState.getPlayers().get(gameState.getPlayers().size() - 1);
        // There is event.getGamePreferences().getStartingPosition(), but that is only the player color, not the turn
        // order
        if (lastPlayer.getType() != Player.Type.LOCAL_PLAYER) {
            return false;     // Ignore games where the local player didn't start last
        }

        final Intelligence aiLevel = gameState.getBotIntelligence();
        if (aiLevel != Intelligence.LEVEL_4) {
            return false;    // Not the very hard AI, ignore
        }

        storeProgress(1); // unlock
        return true;
    }
}

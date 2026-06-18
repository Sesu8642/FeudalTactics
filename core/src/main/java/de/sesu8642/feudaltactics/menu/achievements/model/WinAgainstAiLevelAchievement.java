package de.sesu8642.feudaltactics.menu.achievements.model;

import com.google.common.collect.ImmutableList;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;

/**
 * Achievement: Win a game against AI opponents of a specified level. It must be exactly that AI level, not higher.
 */
public class WinAgainstAiLevelAchievement extends AbstractAchievement {

    private final Intelligence aiLevel;

    public WinAgainstAiLevelAchievement(Intelligence aiLevel) {
        super(1, TranslationKeys.ACHIEVEMENT_WIN_AGAINST_AI_LEVEL_NAME, ImmutableList.of(String.valueOf(aiLevel)),
            TranslationKeys.ACHIEVEMENT_WIN_AGAINST_AI_LEVEL_DESCRIPTION, ImmutableList.of(String.valueOf(aiLevel)));
        this.aiLevel = aiLevel;
    }

    @Override
    public String getId() {
        return "win-against-ai-level-" + aiLevel;
    }

    @Override
    public boolean onGameExited(GameExitedEvent event) {
        final GameState gameState = event.getGameState();
        if (gameState == null) {
            return false;     // Ignore exits from editor or similar
        }

        final Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame != null && winnerOfTheGame.getType() == Player.Type.LOCAL_PLAYER
            && gameState.getBotIntelligence() == aiLevel) {
            storeProgress(1); // unlock
            return true;
        }
        return false;
    }
}

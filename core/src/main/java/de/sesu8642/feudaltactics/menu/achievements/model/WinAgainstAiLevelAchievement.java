package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;

/**
 * Achievement: Win a game against AI opponents of a specified level. It must be exactly that AI level, not higher.
 */
public class WinAgainstAiLevelAchievement extends AbstractAchievement {

    private final Intelligence aiLevel;

    public WinAgainstAiLevelAchievement(Intelligence aiLevel) {
        super(1, "Win Against AI Level " + aiLevel);  // TODO: localization
        this.aiLevel = aiLevel;
    }

    @Override
    public String getId() {
        return "win-against-ai-level-" + aiLevel;
    }

    @Override
    public String getBaseDescription() {
        return "Win a game against AI opponents of level " + aiLevel + ". It must be exactly that AI level, not higher.";
    }

    @Override
    public boolean onGameExited(GameExitedEvent event) {
        final de.sesu8642.feudaltactics.lib.gamestate.GameState gameState = event.getGameState();
        if (gameState == null) {
            return false;     // Ignore exits from editor or similar
        }

        final de.sesu8642.feudaltactics.lib.gamestate.Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame != null && winnerOfTheGame.getType() == de.sesu8642.feudaltactics.lib.gamestate.Player.Type.LOCAL_PLAYER
            && gameState.getBotIntelligence() == aiLevel){
            storeProgress(1); // unlock
            return true;
        }
        return false;
    }
}

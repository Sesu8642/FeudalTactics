package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

public class WinAgainstAiLevelAchievement extends AbstractAchievement {

    private final Intelligence aiLevel;

    public WinAgainstAiLevelAchievement(AchievementRepository achievementRepository, Intelligence aiLevel) {
        super(achievementRepository, 1);
        this.aiLevel = aiLevel;
    }

    @Override
    public String getId() {
        return "win-against-ai-level-" + aiLevel;
    }

    @Override
    public String getName() {
        return "Win Against AI Level " + aiLevel;
    }

    @Override
    public String getDescription() {
        return "Win a game against AI opponents of level " + aiLevel;
    }

    @Override
    public void onGameExited(de.sesu8642.feudaltactics.events.GameExitedEvent event) {
        final de.sesu8642.feudaltactics.lib.gamestate.GameState gameState = event.getGameState();
        if (gameState == null) {
            return;     // Ignore exits from editor or similar
        }

        final de.sesu8642.feudaltactics.lib.gamestate.Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame != null && winnerOfTheGame.getType() == de.sesu8642.feudaltactics.lib.gamestate.Player.Type.LOCAL_PLAYER
            && gameState.getBotIntelligence() == aiLevel){
            storeProgress(1); // unlock
        }
    }
    
}

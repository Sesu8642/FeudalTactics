package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

public class WinWhenStartingLastAchievement extends AbstractAchievement {

    public WinWhenStartingLastAchievement(AchievementRepository achievementRepository) {
        super(achievementRepository, 1, "Win When Starting Last");
    }

    @Override
    public String getId() {
        return "win-when-starting-last";
    }

    @Override
    public String getBaseDescription() {
        return "Win a game against the very hard AI when starting last in the turn order.";
    }

    @Override
    public void onGameExited(de.sesu8642.feudaltactics.events.GameExitedEvent event) {
        final de.sesu8642.feudaltactics.lib.gamestate.GameState gameState = event.getGameState();
        if (gameState == null) {
            return;     // Ignore exits from editor or similar
        }

        final de.sesu8642.feudaltactics.lib.gamestate.Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame == null || winnerOfTheGame.getType() != de.sesu8642.feudaltactics.lib.gamestate.Player.Type.LOCAL_PLAYER){
            return;     // Ignore games without a winner or where the local player didn't win
        }

        if (event.getGamePreferences().getStartingPosition() != gameState.getPlayers().size() - 1) {
            return;     // Ignore games where the local player didn't start last
        }

        Intelligence aiLevel = gameState.getBotIntelligence();
        if (aiLevel != Intelligence.LEVEL_4) {
            return;    // Not the very hard AI, ignore
        }

        storeProgress(1); // unlock
    }
}

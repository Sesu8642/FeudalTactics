package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementGameStateTracker;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

public class WinWithOnlyNCastlesAchievement extends AbstractAchievement {

    private final int targetNumberOfCastles;
    private final AchievementGameStateTracker gameStateTracker;

    public WinWithOnlyNCastlesAchievement(
        AchievementRepository achievementRepository, 
        int targetNumberOfCastles,
        AchievementGameStateTracker gameStateTracker) {
        super(achievementRepository, 1);

        this.targetNumberOfCastles = targetNumberOfCastles;
        this.gameStateTracker = gameStateTracker;
    }

    @Override
    public String getId() {
        return "win_with_only_" + targetNumberOfCastles + "_castles";
    }

    @Override
    public String getName() {
        return targetNumberOfCastles + " castles victory";
    }

    @Override
    public String getDescription() {
        return "Win a game against the strongest AI. And build " + (targetNumberOfCastles > 0 ? "only " + targetNumberOfCastles + " Castles." : "no Castles at all!")
            + " In addition, the map size must be at least Large!";
    }

    @Override
    public void onGameExited(de.sesu8642.feudaltactics.events.GameExitedEvent event) {
        final GameState gameState = event.getGameState();
        final NewGamePreferences gamePreferences = event.getGamePreferences();
        
        if (gameState == null) {
            return;     // Ignore exits from editor or similar
        }

        int numberOfCastlesBuildInExitedGame = gameStateTracker.getCastlesBuiltInCurrentGame();

        if (numberOfCastlesBuildInExitedGame > targetNumberOfCastles) {
            return; // too many castles built
        }

        if (gamePreferences.getMapSize().getAmountOfTiles() < NewGamePreferences.MapSizes.LARGE.getAmountOfTiles()) {
            return; // map size too small
        }

        final Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame != null && winnerOfTheGame.getType() == Player.Type.LOCAL_PLAYER
            && gameState.getBotIntelligence() == Intelligence.LEVEL_4){
            storeProgress(1); // unlock
        }
    }
}

package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.menu.achievements.AchievementGameStateTracker;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

public class BuyNCastlesAchievement extends AbstractAchievement {

    private final AchievementGameStateTracker gameStateTracker;

    public BuyNCastlesAchievement(
        AchievementRepository achievementRepository,
        int targetNumberOfCastles, 
        AchievementGameStateTracker gameStateTracker) {
        super(achievementRepository, targetNumberOfCastles);
        this.gameStateTracker = gameStateTracker;
    }

    @Override
    public String getId() {
        return "buy_" + getGoal() + "_castles";
    }
    
    @Override
    public String getName() {
        return "Buy " + getGoal() + " Castles";
    }

    @Override
    public String getDescription() {
        return "Buy " + getGoal() + " castles during your games.";
    }

    @Override
    public void onGameExited(de.sesu8642.feudaltactics.events.GameExitedEvent event) {
        int numberOfCastlesBuilt = gameStateTracker.getCastlesBuiltInCurrentGame();
        storeProgress(getProgress() + numberOfCastlesBuilt);
    }
 
}

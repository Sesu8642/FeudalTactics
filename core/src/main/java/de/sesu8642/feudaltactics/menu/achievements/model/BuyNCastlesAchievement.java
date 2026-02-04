package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.ingame.AutoSaveRepository;
import de.sesu8642.feudaltactics.lib.ingame.PlayerMove;
import de.sesu8642.feudaltactics.lib.ingame.PlayerMove.PlayerMoveType;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

public class BuyNCastlesAchievement extends AbstractAchievement {

    private final AutoSaveRepository autoSaveRepository;

    public BuyNCastlesAchievement(AchievementRepository achievementRepository, int targetNumberOfCastles, AutoSaveRepository autoSaveRepository) {
        super(achievementRepository, targetNumberOfCastles);
        this.autoSaveRepository = autoSaveRepository;
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
    public void onBuyCastle() {
        storeProgress(getProgress() + 1);
    }

    @Override
    public void onBuyAndPlaceCastle() {
        storeProgress(getProgress() + 1);
    }

    @Override
    public void onUndoMove() {
            // Check whether the last move was a castle purchase
        if (autoSaveRepository.isUndoPossible()) {
            final PlayerMove lastMove = autoSaveRepository.peekLastMove();
            if (lastMove != null && (lastMove.getPlayerActionType() == PlayerMoveType.BUY_CASTLE ||
                lastMove.getPlayerActionType() == PlayerMoveType.BUY_AND_PLACE_CASTLE)) {
                    // User has undone a castle purchase. Decrease progress by 1.
                int castlesBought = getProgress();
                if (castlesBought > 0) {
                    castlesBought--;
                    storeProgress(castlesBought);
                }
            }
        }
    }
}

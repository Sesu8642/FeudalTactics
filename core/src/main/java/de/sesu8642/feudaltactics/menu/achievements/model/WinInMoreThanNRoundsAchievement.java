package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

public class WinInMoreThanNRoundsAchievement extends AbstractAchievement {

    private final int roundCount;

    public WinInMoreThanNRoundsAchievement(AchievementRepository repository, int roundCount) {
        super(repository, 1);
        this.roundCount = roundCount;
    }

    @Override
    public String getName() {
        return "Win using more than " + roundCount + " rounds";
    }

    @Override
    public String getDescription() {
        return "Win a game that lasts at least " + roundCount + " rounds.";
    }

    @Override
    public String getId() {
        return "win_in_more_than_" + roundCount + "_rounds";
    }

    @Override
    public void onGameExited(de.sesu8642.feudaltactics.events.GameExitedEvent event) {
        final de.sesu8642.feudaltactics.lib.gamestate.GameState gameState = event.getGameState();
        if (gameState == null) {
            return;     // Ignore exits from editor or similar
        }

        final de.sesu8642.feudaltactics.lib.gamestate.Player winnerOfTheGame = gameState.getWinner();
        if (winnerOfTheGame != null || winnerOfTheGame.getType() != de.sesu8642.feudaltactics.lib.gamestate.Player.Type.LOCAL_PLAYER)
            return;     // Player didn't win, so ignore

        if ( gameState.getWinningRound() >= roundCount) {
            storeProgress(1); // unlock
        }
    }
}

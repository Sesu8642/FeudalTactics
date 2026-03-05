package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.GameExitedEvent;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;

public class WinInNRoundsAchievement extends AbstractAchievement {

    private final int rounds;

    public WinInNRoundsAchievement(AchievementRepository repository, int rounds) {
        super(repository, 1, "Win in " + rounds + " Rounds");
        this.rounds = rounds;
    }

    @Override
    public String getId() {
        return "win_in_" + rounds + "_rounds";
    }

    @Override
    public String getDescription() {
        return "Win a game in " + rounds + " rounds or less. The AI level must be Very Strong, the map size must be at least Medium.";
    }

    @Override
    public void onGameExited(GameExitedEvent event) {
        final GameState gameState = event.getGameState();
        if (gameState == null) {
            return;     // Ignore exits from editor or similar
        }   
    
        final Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame == null || winnerOfTheGame.getType() != Player.Type.LOCAL_PLAYER) {
            return;     // Player didn't win, so ignore
        }

        Intelligence aiLevel = gameState.getBotIntelligence();
        if (aiLevel != Intelligence.LEVEL_4) {
            return;    // Not a Very Hard game, ignore
        }

        NewGamePreferences gamePreferences = event.getGamePreferences();
        if (gamePreferences.getMapSize().getAmountOfTiles() < NewGamePreferences.MapSizes.MEDIUM.getAmountOfTiles()) {
            return;    // Map size is too small, ignore
        }

        if (gameState.getWinningRound() <= rounds) {
            storeProgress(1);
        }
    }
}

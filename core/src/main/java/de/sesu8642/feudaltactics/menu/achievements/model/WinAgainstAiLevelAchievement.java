package de.sesu8642.feudaltactics.menu.achievements.model;

import com.google.common.collect.ImmutableList;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;
import lombok.extern.slf4j.Slf4j;

/**
 * Achievement: Win a game against AI opponents of a specified level. It must be exactly that AI level, not higher.
 */
@Slf4j
public class WinAgainstAiLevelAchievement extends AbstractAchievement {

    private final Intelligence botIntelligence;

    public WinAgainstAiLevelAchievement(Intelligence botIntelligence) {
        super(1, TranslationKeys.ACHIEVEMENT_WIN_AGAINST_AI_LEVEL_NAME,
            ImmutableList.of(botIntelligenceToNameTranslationParameter(botIntelligence)),
            TranslationKeys.ACHIEVEMENT_WIN_AGAINST_AI_LEVEL_DESCRIPTION,
            ImmutableList.of(botIntelligenceToDescriptionTranslationParameter(botIntelligence)), true);
        this.botIntelligence = botIntelligence;
    }

    private static  String botIntelligenceToNameTranslationParameter(Intelligence botIntelligence) {
        switch (botIntelligence) {
            case LEVEL_1:
                return TranslationKeys.ACHIEVEMENT_WIN_AGAINST_AI_LEVEL_NAME_PARAM_DIFFICULTY_EASY;
            case LEVEL_2:
                return TranslationKeys.ACHIEVEMENT_WIN_AGAINST_AI_LEVEL_NAME_PARAM_DIFFICULTY_MEDIUM;
            case LEVEL_3:
                return TranslationKeys.ACHIEVEMENT_WIN_AGAINST_AI_LEVEL_NAME_PARAM_DIFFICULTY_HARD;
            case LEVEL_4:
                return TranslationKeys.ACHIEVEMENT_WIN_AGAINST_AI_LEVEL_NAME_PARAM_DIFFICULTY_VERY_HARD;
            default:
                throw new IllegalStateException("Unknown bot intelligence " + botIntelligence);

        }
    }

    private static String botIntelligenceToDescriptionTranslationParameter(Intelligence botIntelligence) {
        switch (botIntelligence) {
            case LEVEL_1:
                return TranslationKeys.ACHIEVEMENT_WIN_AGAINST_AI_LEVEL_DESCRIPTION_PARAM_DIFFICULTY_EASY;
            case LEVEL_2:
                return TranslationKeys.ACHIEVEMENT_WIN_AGAINST_AI_LEVEL_DESCRIPTION_PARAM_DIFFICULTY_MEDIUM;
            case LEVEL_3:
                return TranslationKeys.ACHIEVEMENT_WIN_AGAINST_AI_LEVEL_DESCRIPTION_PARAM_DIFFICULTY_HARD;
            case LEVEL_4:
                return TranslationKeys.ACHIEVEMENT_WIN_AGAINST_AI_LEVEL_DESCRIPTION_PARAM_DIFFICULTY_VERY_HARD;
            default:
                throw new IllegalStateException("Unknown bot intelligence " + botIntelligence);

        }
    }

    @Override
    public String getId() {
        return "win-against-ai-level-" + botIntelligence;
    }

    @Override
    public boolean onGameExited(GameExitedEvent event) {
        final GameState gameState = event.getGameState();
        if (gameState == null) {
            return false;     // Ignore exits from editor or similar
        }

        final Player winnerOfTheGame = gameState.getWinner();

        if (winnerOfTheGame != null && winnerOfTheGame.getType() == Player.Type.LOCAL_PLAYER
            && gameState.getBotIntelligence() == botIntelligence) {
            storeProgress(1); // unlock
            return true;
        }
        return false;
    }
}

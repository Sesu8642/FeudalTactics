package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.TranslationKeys;
import lombok.Getter;

/**
 * Represents a historic person or event that is referenced by an achievement.
 */
public enum HistoricPersonOrEvent {
    CHARLEMAGNE(TranslationKeys.ACHIEVEMENT_WIN_N_GAMES_HISTORIC_TITLE,
        TranslationKeys.ACHIEVEMENT_WIN_N_GAMES_HISTORIC_DESCRIPTION),
    JEANNE_DARC(TranslationKeys.ACHIEVEMENT_WIN_IN_N_ROUNDS_HISTORIC_TITLE,
        TranslationKeys.ACHIEVEMENT_WIN_IN_N_ROUNDS_HISTORIC_DESCRIPTION),
    THIRTY_YEARS_WAR(TranslationKeys.ACHIEVEMENT_PLAY_MORE_THAN_N_ROUNDS_HISTORIC_TITLE,
        TranslationKeys.ACHIEVEMENT_PLAY_MORE_THAN_N_ROUNDS_HISTORIC_DESCRIPTION),
    HUNDRED_YEARS_WAR(TranslationKeys.ACHIEVEMENT_PLAY_MORE_THAN_N_ROUNDS_HISTORIC2_TITLE,
        TranslationKeys.ACHIEVEMENT_PLAY_MORE_THAN_N_ROUNDS_HISTORIC2_DESCRIPTION),
    ROAD_TO_CANOSSA(TranslationKeys.ACHIEVEMENT_LOSE_AGAINST_WEAKEST_AI_HISTORIC_TITLE,
        TranslationKeys.ACHIEVEMENT_LOSE_AGAINST_WEAKEST_AI_HISTORIC_DESCRIPTION),
    WILLIAM_THE_CONQUEROR(TranslationKeys.ACHIEVEMENT_WIN_VERY_HARD_GAMES_IN_A_ROW_HISTORIC_TITLE,
        TranslationKeys.ACHIEVEMENT_WIN_VERY_HARD_GAMES_IN_A_ROW_HISTORIC_DESCRIPTION),
    RICHARD_THE_LIONHEART(TranslationKeys.ACHIEVEMENT_WIN_ON_MAP_SIZE_HISTORIC_TITLE,
        TranslationKeys.ACHIEVEMENT_WIN_ON_MAP_SIZE_HISTORIC_DESCRIPTION),
    FREDERICK_THE_GREAT(TranslationKeys.ACHIEVEMENT_WIN_AGAINST_AI_LEVEL_HISTORIC_TITLE,
        TranslationKeys.ACHIEVEMENT_WIN_AGAINST_AI_LEVEL_HISTORIC_DESCRIPTION),
    LOUIS_XI(TranslationKeys.ACHIEVEMENT_WIN_AGAINST_MANY_ENEMIES_HISTORIC_TITLE,
        TranslationKeys.ACHIEVEMENT_WIN_AGAINST_MANY_ENEMIES_HISTORIC_DESCRIPTION),
    // Reserved for BuyNCastlesAchievement
    HENRY_VIII("Henry VIII", "Henry VIII (1491-1547) was King of England. He is known for his six marriages, but also" +
        " for building much military infrastructure."),
    // Reserved for WinWithOnlyNCastlesAchievement
    HRE_HENRY_VI("Holy Roman Emperor Henry VI", "Henry VI (1165-1197) was Holy Roman Emperor. He was a travelling " +
        "king who had no fixed castle, but still won many battles."),
    TOKUGAWA_IEYASU(TranslationKeys.ACHIEVEMENT_WIN_WHEN_STARTING_LAST_HISTORIC_TITLE,
        TranslationKeys.ACHIEVEMENT_WIN_WHEN_STARTING_LAST_HISTORIC_DESCRIPTION),
    JOHN_THE_POSTHUMOUS(TranslationKeys.ACHIEVEMENT_ABORT_GAME_HISTORIC_TITLE,
        TranslationKeys.ACHIEVEMENT_ABORT_GAME_HISTORIC_DESCRIPTION);

    /**
     * Translation key for the name of the historic person or event. The actual text shouldn't be too long, as it
     * needs to fit in the achievement overview.
     */
    @Getter
    private final String nameTranslationKey;
    /**
     * Translation key for the description of the historic person or event. It is shown in the achievement details.
     * For secret achievements, it hints at what needs to be done, because the exact requirements are not revealed
     * until the achievement is unlocked.
     */
    @Getter
    private final String descriptionTranslationKey;

    HistoricPersonOrEvent(String nameTranslationKey, String descriptionTranslationKey) {
        this.nameTranslationKey = nameTranslationKey;
        this.descriptionTranslationKey = descriptionTranslationKey;
    }

}

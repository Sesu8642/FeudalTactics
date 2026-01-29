package de.sesu8642.feudaltactics.menu.achievements;

import java.util.List;

import javax.inject.Inject;

import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import lombok.Getter;

/** Registry for all achievements available in the game.
 *  It is a Singleton service that provides access to all achievements.
 */

public class AchievementRegistry {

    @Getter
    private List<AbstractAchievement> achievements = List.of(
        new de.sesu8642.feudaltactics.menu.achievements.model.WinNGamesAchievement(1),
        new de.sesu8642.feudaltactics.menu.achievements.model.WinNGamesAchievement(10),
        new de.sesu8642.feudaltactics.menu.achievements.model.WinNGamesAchievement(50)
    );

    private final AchievementsDao achievementsDao;

    @Inject
    public AchievementRegistry(AchievementsDao achievementsDao ) {
        this.achievementsDao = achievementsDao;
    }
}

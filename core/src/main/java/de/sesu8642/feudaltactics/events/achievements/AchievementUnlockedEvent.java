package de.sesu8642.feudaltactics.events.achievements;

import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import lombok.Getter;

/**
 * Event triggered when an achievement has just been unlocked.
 */
public class AchievementUnlockedEvent {
    @Getter
    private final AbstractAchievement achievement;

     /**
     * Constructor.
     *
     * @param achievement the achievement that has been unlocked
     */
    public AchievementUnlockedEvent(AbstractAchievement achievement) {
        this.achievement = achievement;
    }
}

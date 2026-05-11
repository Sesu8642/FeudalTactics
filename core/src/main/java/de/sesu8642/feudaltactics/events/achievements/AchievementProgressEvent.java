package de.sesu8642.feudaltactics.events.achievements;

import de.sesu8642.feudaltactics.menu.achievements.model.AbstractAchievement;
import lombok.Getter;

/**
 * Event triggered when the progress of an achievement has changed, i.e. when the player has made progress towards unlocking it.
 */
public class AchievementProgressEvent {
    @Getter
    private final AbstractAchievement achievement;

     /**
     * Constructor.
     *
     * @param achievement the achievement for which the progress has changed
     */
    public AchievementProgressEvent(AbstractAchievement achievement) {
        this.achievement = achievement;
    }
}

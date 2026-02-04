package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractAchievement {
    public abstract String getId();

    public abstract String getName();

    public abstract String getDescription();

    @Getter
    @Setter
    private boolean unlocked;

    protected AchievementRepository achievementRepository;

    protected AbstractAchievement(AchievementRepository achievementRepository, int goal) {
        this.achievementRepository = achievementRepository;
        this.goal = goal;
    }

    protected void unlock() {
        if (!unlocked) {
            unlocked = true;
            achievementRepository.unlockAchievement(getId());
        }
    }

    @Getter
    private final int goal;

    /* How much of this achievement has been completed? */
    @Getter
    @Setter
    private int progress = 0;

    protected void storeProgress(int number) {
        this.progress = number;
        achievementRepository.storeProgress(getId(), number);
        if (progress >= goal) {
            unlock();
        }
    }

    /**
     * Called when a game is exited. Override to handle this event.
     */
    public void onGameExited(de.sesu8642.feudaltactics.events.GameExitedEvent event) {
        // No-op by default
    }
}

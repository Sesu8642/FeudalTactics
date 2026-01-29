package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.menu.achievements.AchievementsDao;
import lombok.Getter;
import lombok.Setter;

public abstract class AbstractAchievement {
    public abstract String getId();

    public abstract String getName();

    public abstract String getDescription();

    @Getter
    @Setter
    private boolean unlocked;

    protected AchievementsDao achievementsDao;

    protected AbstractAchievement(AchievementsDao achievementsDao, int goal) {
        this.achievementsDao = achievementsDao;
        this.goal = goal;
    }

    protected void unlock() {
        if (!unlocked) {
            unlocked = true;
            achievementsDao.unlockAchievement(getId());
        }
    }

    @Getter
    private final int goal;

    /* How much of this achievement has been completed? */
    @Getter
    private int progress = 0;

    protected void setProgress(int number) {
        this.progress = number;
        achievementsDao.storeProgress(getId(), number);
        if (progress >= goal) {
            unlock();
        }
    }
}

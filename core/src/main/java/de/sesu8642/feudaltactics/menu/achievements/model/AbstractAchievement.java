package de.sesu8642.feudaltactics.menu.achievements.model;

import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.menu.achievements.AchievementRepository;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public abstract class AbstractAchievement {
    public abstract String getId();

    public String getName() {
        if (historicConnection != null) {
            return historicConnection.getName();
        }
        else {
            return baseName;
        }
    }

    private String baseName;

    protected abstract String getBaseDescription();
    public String getDescription() {
        if (historicConnection != null) {
            if (isSecret && !unlocked) {
                return historicConnection.getDescription() + "\n\n" + "This achievement is still secret. Unlock it to see the full description.";
            }
            return historicConnection.getDescription() + "\n\n" + getBaseDescription();
        }
        else {
            return getBaseDescription();
        }
    }

    @Getter
    @Setter
    private boolean unlocked;

    @Getter
    @Setter
    private HistoricPersonOrEvent historicConnection;

    @Getter
    @Setter
    private boolean isSecret;

    protected AchievementRepository achievementRepository;
   
    protected AbstractAchievement(AchievementRepository achievementRepository, int goal, String name) {
        this.achievementRepository = achievementRepository;
        this.goal = goal;
        this.baseName = name;
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

    public void onMapRegeneration(RegenerateMapEvent event) {
        // No-op by default
    }
}

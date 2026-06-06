package de.sesu8642.feudaltactics.menu.achievements.model;

import com.google.common.collect.ImmutableList;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;
import de.sesu8642.feudaltactics.shared.events.RegenerateMapEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * Abstract base class for all achievements.
 * <p>
 * It forwards the GameExitedEvent to its subclasses, so they can react to it and check if the achievement should be
 * unlocked.
 * It also provides some helper methods to store progress and unlock the achievement.
 */
@Accessors(chain = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public abstract class AbstractAchievement {
    @Getter
    @EqualsAndHashCode.Include
    private final int goal;
    private final String baseName;
    /**
     * Indicates whether the achievement is unlocked = player has achieved it.
     */
    @Getter
    @Setter
    @EqualsAndHashCode.Include
    private boolean unlocked;
    /**
     * The historic person or event associated with this achievement, if any.
     */
    @Getter
    @Setter
    private HistoricPersonOrEvent historicConnection;
    /**
     * How much of this achievement has been completed?
     */
    @Getter
    @Setter
    @EqualsAndHashCode.Include
    private int progress = 0;

    protected AbstractAchievement(int goal, String name) {
        this.goal = goal;
        baseName = name;
    }

    /**
     * Unique ID of the achievement. It is used to store the achievement's state (progress and unlocked status) in
     * the repository,
     * so it must not change once released.
     */
    @EqualsAndHashCode.Include
    public abstract String getId();

    /**
     * Name of the achievement. It displays in the overview and also in the details window.
     */
    public String getNameKey() {
        if (historicConnection != null) {
            return historicConnection.getNameTranslationKey();
        } else {
            return baseName;
        }
    }

    protected abstract String getBaseDescription();

    /**
     * Keys of the description paragraphs for the achievement. In the achievements menu, it displays when
     * tapping/clicking on the achievement in
     * a window with details.
     */
    public List<String> getDescriptionParagraphKeys() {
        if (historicConnection != null) {
            if (isSecret() && !unlocked) {
                return ImmutableList.of(historicConnection.getDescriptionTranslationKey(), "This achievement is still" +
                    " secret. Unlock it to see the full description.");
            }
            return ImmutableList.of(historicConnection.getDescriptionTranslationKey(), getBaseDescription());
        } else {
            return ImmutableList.of(getBaseDescription());
        }
    }

    /**
     * Indicates whether the achievement is secret = its description is hidden until the player unlocks it.
     * It is used for achievements with historic connection that hint what needs to be done, but the player still
     * needs to figure out how to do it exactly.
     * It is false by default, but subclasses can override this method to return true if they want the achievement to
     * be secret.
     */
    public boolean isSecret() {
        return false;
    }

    protected void unlock() {
        if (!unlocked) {
            unlocked = true;
        }
    }

    protected void storeProgress(int number) {
        progress = number;
        if (progress >= goal) {
            unlock();
        }
    }

    /**
     * Called when a game is exited. Override to handle this event.
     * <p>
     * Returns whether the achievement's state has changed (e.g. progress updated or achievement unlocked) and thus
     * the achievement needs to be persisted.
     */
    public boolean onGameExited(GameExitedEvent event) {
        // No-op by default
        return false;
    }

    /**
     * Called when the map is regenerated. Override to handle this event.
     * <p>
     * Returns whether the achievement's state has changed (e.g. progress updated or achievement unlocked) and thus
     * the achievement needs to be persisted.
     */
    public boolean onMapRegeneration(RegenerateMapEvent event) {
        // No-op by default
        return false;
    }
}

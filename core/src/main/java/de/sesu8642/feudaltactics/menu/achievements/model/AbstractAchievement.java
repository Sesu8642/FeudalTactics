package de.sesu8642.feudaltactics.menu.achievements.model;

import com.google.common.collect.ImmutableList;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.shared.events.GameExitedEvent;
import de.sesu8642.feudaltactics.shared.events.RegenerateMapEvent;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public abstract class AbstractAchievement {
    @Getter
    @EqualsAndHashCode.Include
    private final int goal;
    private final String nameTranslationKey;
    private final List<String> nameTranslationParameters;
    private final String baseDescriptionTranslationKey;
    private final List<String> baseDescriptionTranslationParameters;
    private final boolean parametersAreTranslationKeys;
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

    /**
     * Constructor for achievements that don't have any parameters in the name or description translations.
     */
    protected AbstractAchievement(int goal, String nameTranslationKey, String baseDescriptionTranslationKey) {
        this(goal, nameTranslationKey, ImmutableList.of(), baseDescriptionTranslationKey, ImmutableList.of(), false);
    }

    /**
     * Unique ID of the achievement. It is used to store the achievement's state (progress and unlocked status) in
     * the repository,
     * so it must not change once released.
     */
    @EqualsAndHashCode.Include
    public abstract String getId();

    /**
     * Returns the translated name for the achievement.
     * It displays in the overview and also in the details window.
     */
    public String getTranslatedName(LocalizationManager localizationManager) {
        if (historicConnection != null) {
            // historic connections don't have parameters (so far)
            return localizationManager.localizeText(historicConnection.getNameTranslationKey());
        } else {
            return localizationManager.localizeText(nameTranslationKey, getNameTranslationParameterArray(localizationManager));
        }
    }

    private Object[] getNameTranslationParameterArray(LocalizationManager localizationManager) {
        if (parametersAreTranslationKeys) {
            return nameTranslationParameters.stream().map(localizationManager::localizeText).toArray(String[]::new);
        }
        return nameTranslationParameters.toArray(new String[0]);
    }

    /**
     * Returns the full, translated description for the achievement including progress information.
     * In the achievements menu, it displays when tapping/clicking on the achievement in a window with details.
     */
    public String getTranslatedDescription(LocalizationManager localizationManager) {
        final StringBuilder descriptionTextBuilder = new StringBuilder();
        if (historicConnection != null) {
            descriptionTextBuilder.append(localizationManager.localizeText(historicConnection.getDescriptionTranslationKey()));
            descriptionTextBuilder.append("\n\n");
            if (isSecret() && !unlocked) {
                descriptionTextBuilder.append(localizationManager.localizeText(TranslationKeys.ACHIEVEMENTS_DESCRIPTION_IS_SECRET));
            } else {
                descriptionTextBuilder.append(localizationManager.localizeText(baseDescriptionTranslationKey, getBaseDescriptionTranslationParameterArray(localizationManager)));
            }
        } else {
            descriptionTextBuilder.append(localizationManager.localizeText(baseDescriptionTranslationKey, getBaseDescriptionTranslationParameterArray(localizationManager)));
        }
        descriptionTextBuilder.append("\n\n");

        if (unlocked) {
            descriptionTextBuilder.append(localizationManager.localizeText(TranslationKeys.ACHIEVEMENTS_DESCRIPTION_IS_UNLOCKED));
        } else {
            descriptionTextBuilder.append(localizationManager.localizeText(TranslationKeys.ACHIEVEMENTS_DESCRIPTION_PROGRESS, progress, goal));
        }

        return descriptionTextBuilder.toString();
    }

    private Object[] getBaseDescriptionTranslationParameterArray(LocalizationManager localizationManager) {
        if (parametersAreTranslationKeys) {
            return baseDescriptionTranslationParameters.stream().map(localizationManager::localizeText).toArray(String[]::new);
        }
        return baseDescriptionTranslationParameters.toArray(new String[0]);
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

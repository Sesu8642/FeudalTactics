package de.sesu8642.feudaltactics.menu.achievements;

/**
 * Marker interface for achievements that require full storage
 */
public interface AchievementNeedsFullStorage {

    /**
     * Returns a JSON representation of the achievement progress.
     */
    String serializeToJson();

    /**
     * Takes a JSON representation of the achievement progress and loads it.
     */
    void deserializeFromJson(String serializedData);
}

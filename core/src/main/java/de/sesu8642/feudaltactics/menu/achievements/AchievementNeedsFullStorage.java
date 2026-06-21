package de.sesu8642.feudaltactics.menu.achievements;

/**
 *  Marker interface for achievements that require full storage 
 */
public interface AchievementNeedsFullStorage {
    
    public String serializeToJson();

    public void deserializeFromJson(String serializedData);
}

package de.sesu8642.feudaltactics.lib.gamestate;

/**
 * All the available scenario maps.
 */
public enum ScenarioMap {

    NONE(null, null),
    TUTORIAL("Tutorial", ScenarioMap.PATH_PREFIX + "tutorial/Tutorial.json");

    private static final String PATH_PREFIX = "scenarios/";

    /**
     * Display name of the map.
     */
    public final String displayName;

    /**
     * Path to the map file.
     */
    public final String mapPath;

    ScenarioMap(String displayName, String mapPath) {
        this.displayName = displayName;
        this.mapPath = mapPath;
    }


}

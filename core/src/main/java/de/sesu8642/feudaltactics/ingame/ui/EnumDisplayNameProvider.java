package de.sesu8642.feudaltactics.ingame.ui;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.gamestate.Unit;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

/**
 * Class to provide display names for some enums.
 */
public class EnumDisplayNameProvider {

    public static final String[] DIFFICULTIES = {"Easy", "Medium", "Hard", "Very hard"};
    public static final String[] MAP_SIZES = {"Small", "Medium   ", "Large", "XLarge", "XXLarge"};
    public static final String[] DENSITIES = {"Dense", "Medium   ", "Loose"};
    public static final String[] UNITS = {"Peasant", "Spearman", "Knight", "Baron"};

    /**
     * Returns the display name for bot intelligence.
     */
    public static String getDisplayName(Intelligence intelligence) {
        return DIFFICULTIES[intelligence.ordinal()];
    }

    /**
     * Returns the display name for map size.
     */
    public static String getDisplayName(MapSizes mapSize) {

        return MAP_SIZES[mapSize.ordinal()];
    }

    /**
     * Returns the display name for map density.
     */
    public static String getDisplayName(Densities density) {
        return DENSITIES[density.ordinal()];
    }

    /**
     * Returns the display name for unit types.
     */
    public static String getDisplayName(Unit.UnitTypes unitType) {
        return UNITS[unitType.ordinal()];
    }

}

package de.sesu8642.feudaltactics.ingame.ui;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

/**
 * Class to provide display names for some enums.
 */
public class EnumDisplayNameProvider {

    public final static String[] DIFFICULTIES = {"Easy", "Medium", "Hard", "Very hard"};
    public final static String[] MAP_SIZES = {"Small", "Medium   ", "Large", "XLarge", "XXLarge"};
    public final static String[] DENSITIES = {"Dense", "Medium   ", "Loose"};

    public static String getDisplayName(Intelligence intelligence) {
        return DIFFICULTIES[intelligence.ordinal()];
    }

    public static String getDisplayName(MapSizes mapSize) {
        return MAP_SIZES[mapSize.ordinal()];
    }

    public static String getDisplayName(Densities density) {
        return DENSITIES[density.ordinal()];
    }

}

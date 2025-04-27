package de.sesu8642.feudaltactics.ingame.ui;

import com.google.common.collect.ImmutableList;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.gamestate.Unit;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

import java.util.List;

/**
 * Class to convert enums to their display names and the other way around.
 */
public class EnumDisplayNameConverter {

    public static final List<String> DIFFICULTIES = ImmutableList.of("Easy", "Medium", "Hard", "Very hard");
    public static final List<String> MAP_SIZES = ImmutableList.of("Small", "Medium", "Large", "XLarge", "XXLarge");
    public static final List<String> DENSITIES = ImmutableList.of("Dense", "Medium", "Loose");
    public static final List<String> UNITS = ImmutableList.of("Peasant", "Spearman", "Knight", "Baron");

    /**
     * Returns the display name for bot intelligence.
     */
    public static String getDisplayName(Intelligence intelligence) {
        return DIFFICULTIES.get(intelligence.ordinal());
    }

    /**
     * Returns the bot intelligence from its display name.
     */
    public static Intelligence getBotIntelligenceFromDisplayName(String displayName) {
        return Intelligence.values()[DIFFICULTIES.indexOf(displayName)];
    }

    /**
     * Returns the display name for map size.
     */
    public static String getDisplayName(MapSizes mapSize) {

        return MAP_SIZES.get(mapSize.ordinal());
    }

    /**
     * Returns the map size from its display name.
     */
    public static MapSizes getMapSizeFromDisplayName(String displayName) {
        return MapSizes.values()[MAP_SIZES.indexOf(displayName)];
    }

    /**
     * Returns the display name for map density.
     */
    public static String getDisplayName(Densities density) {
        return DENSITIES.get(density.ordinal());
    }

    /**
     * Returns the map density from its display name.
     */
    public static Densities getMapDensityFromDisplayName(String displayName) {
        return Densities.values()[DENSITIES.indexOf(displayName)];
    }

    /**
     * Returns the display name for unit types.
     */
    public static String getDisplayName(Unit.UnitTypes unitType) {
        return UNITS.get(unitType.ordinal());
    }

    /**
     * Returns the unit type from its display name.
     */
    public static Unit.UnitTypes getUnitTypeFromDisplayName(String displayName) {
        return Unit.UnitTypes.values()[UNITS.indexOf(displayName)];
    }

}

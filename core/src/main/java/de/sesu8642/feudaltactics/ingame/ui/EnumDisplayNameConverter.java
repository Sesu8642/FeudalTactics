package de.sesu8642.feudaltactics.ingame.ui;

import com.google.common.collect.ImmutableList;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.gamestate.Unit;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

import java.util.List;

/**
 * Class to convert enums to their display names and the other way around.
 */
public class EnumDisplayNameConverter {

    public static final List<String> DIFFICULTIES = ImmutableList.of("easy", "medium", "hard", "very-hard");
    public static final List<String> MAP_SIZES = ImmutableList.of("small", "medium", "large", "xlarge", "xxlarge");
    public static final List<String> DENSITIES = ImmutableList.of("dense", "medium", "loose");
    public static final List<String> UNITS = ImmutableList.of("peasant", "spearman", "knight", "baron");

    /**
     * Returns the display name for bot intelligence.
     */
    public static String getDisplayName(Intelligence intelligence) {
        return DIFFICULTIES.get(intelligence.ordinal());
    }

    /**
     * Returns the localized display name for bot intelligence.
     */
    public static String getLocalizedDisplayName(Intelligence intelligence, LocalizationManager localizationManager) {
        return localizationManager.localizeText(getDisplayName(intelligence));
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
     * Returns the localized display name for map size.
     */
    public static String getLocalizedDisplayName(MapSizes mapSize, LocalizationManager localizationManager) {
        return localizationManager.localizeText(getDisplayName(mapSize));
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
     * Returns the localized display name for map density.
     */
    public static String getLocalizedDisplayName(Densities density, LocalizationManager localizationManager) {
        return localizationManager.localizeText(getDisplayName(density));
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
     * Returns the localized display name for unit types.
     */
    public static String getLocalizedDisplayName(Unit.UnitTypes unitType, LocalizationManager localizationManager) {
        return localizationManager.localizeText(getDisplayName(unitType));
    }
}

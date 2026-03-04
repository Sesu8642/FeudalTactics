package de.sesu8642.feudaltactics.ingame.ui;

import com.google.common.collect.ImmutableList;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.Unit;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

import java.util.List;

/**
 * Class to convert enums from the {@link GameState} to their string codes and the other way around.
 */
public class GameStateEnumDisplayNameConverter {

    public static final List<String> DIFFICULTIES = ImmutableList.of("easy", "medium", "hard", "very-hard");
    public static final List<String> MAP_SIZES = ImmutableList.of("small", "medium", "large", "xlarge", "xxlarge");
    public static final List<String> DENSITIES = ImmutableList.of("dense", "medium", "loose");
    public static final List<String> UNITS = ImmutableList.of("peasant", "spearman", "knight", "baron");

    /**
     * Returns the display name code for bot intelligence.
     */
    public static String getDisplayNameCode(Intelligence intelligence) {
        return DIFFICULTIES.get(intelligence.ordinal());
    }

    /**
     * Returns the localized display name code for bot intelligence.
     */
    public static String getLocalizedDisplayName(Intelligence intelligence, LocalizationManager localizationManager) {
        return localizationManager.localizeText(getDisplayNameCode(intelligence));
    }

    /**
     * Returns the bot intelligence code from its display name.
     */
    public static Intelligence getBotIntelligenceFromDisplayNameCode(String displayNameCode) {
        return Intelligence.values()[DIFFICULTIES.indexOf(displayNameCode)];
    }

    /**
     * Returns the display name code for map size.
     */
    public static String getDisplayNameCode(MapSizes mapSize) {

        return MAP_SIZES.get(mapSize.ordinal());
    }

    /**
     * Returns the map size from its display name code.
     */
    public static MapSizes getMapSizeFromDisplayNameCode(String displayNameCode) {
        return MapSizes.values()[MAP_SIZES.indexOf(displayNameCode)];
    }

    /**
     * Returns the display name code for map density.
     */
    public static String getDisplayNameCode(Densities density) {
        return DENSITIES.get(density.ordinal());
    }

    /**
     * Returns the map density from its display name code.
     */
    public static Densities getMapDensityFromDisplayNameCode(String displayNameCode) {
        return Densities.values()[DENSITIES.indexOf(displayNameCode)];
    }

    /**
     * Returns the display name code for unit types.
     */
    public static String getDisplayNameCode(Unit.UnitTypes unitType) {
        return UNITS.get(unitType.ordinal());
    }

    /**
     * Returns the localized display name for unit types.
     */
    public static String getLocalizedDisplayName(Unit.UnitTypes unitType, LocalizationManager localizationManager) {
        return localizationManager.localizeText(getDisplayNameCode(unitType));
    }
}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import com.google.common.collect.ImmutableList;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.localization.SupportedLanguage;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * Value object: preferences for a new game.
 */
@Slf4j
// for JSON serialization
@NoArgsConstructor
public class NewGamePreferences {

    public static final String PARAMETER_DISPLAY_FORMAT = "%s: %s";
    private static final List<String> DIFFICULTIES_KEYS = ImmutableList.of("game-parameter-difficulty-easy", "game" +
        "-parameter-difficulty-medium", "game-parameter-difficulty-hard", "game-parameter-difficulty-very-hard");
    private static final List<String> MAP_SIZES_KEYS = ImmutableList.of("game-parameter-size-small", "game-parameter" +
        "-size-medium", "game-parameter-size-large", "game-parameter-size-xlarge", "game-parameter-size-xxlarge");
    private static final List<String> DENSITIES_KEYS = ImmutableList.of("game-parameter-density-dense", "game" +
        "-parameter-density-medium", "game-parameter-density-loose");
    // Keys for UI localization
    private static final String SEED_KEY = "game-details-seed";
    private static final String BOT_INTELLIGENCE_KEY = "game-details-cpu-difficulty";
    private static final String MAP_SIZE_KEY = "game-details-map-size";
    private static final String DENSITY_KEY = "game-details-map-density";
    private static final String STARTING_POSITION_KEY = "game-details-starting-position";
    private static final String NUMBER_OF_BOT_PLAYERS_KEY = "game-details-number-of-bots";

    @Getter
    @Setter
    private long seed;
    @Getter
    @Setter
    private Intelligence botIntelligence;
    @Getter
    @Setter
    private MapSizes mapSize;
    @Getter
    @Setter
    private Densities density;
    @Getter
    @Setter
    private int startingPosition;
    @Getter
    @Setter
    private int numberOfBotPlayers;

    /**
     * Constructor.
     *
     * @param seed               seed for the map
     * @param botIntelligence    intelligence of the bot players for the game
     * @param mapSize            size of the map for this game
     * @param density            density of the map for this game
     * @param startingPosition   starting position index of the human player
     * @param numberOfBotPlayers number of bot players
     */
    public NewGamePreferences(long seed, Intelligence botIntelligence, MapSizes mapSize, Densities density,
                              int startingPosition, int numberOfBotPlayers) {
        this.seed = seed;
        this.botIntelligence = botIntelligence;
        this.mapSize = mapSize;
        this.density = density;
        this.startingPosition = startingPosition;
        this.numberOfBotPlayers = numberOfBotPlayers;
    }

    /**
     * See {@link #NewGamePreferences(long, Intelligence, MapSizes, Densities, int, int)} with a default of 5 bot
     * players.
     */
    public NewGamePreferences(long seed, Intelligence botIntelligence, MapSizes mapSize, Densities density,
                              int startingPosition) {
        this(seed, botIntelligence, mapSize, density, startingPosition, 5);
    }

    /**
     * Static factory method: creates preferences from a String representation. In case some information is missing,
     * default values are used.
     */
    public static NewGamePreferences fromSharableString(String sharedString, LocalizationManager localizationManager) {
        final NewGamePreferences preferences = new NewGamePreferences(0, Intelligence.LEVEL_1, MapSizes.SMALL,
            Densities.DENSE, 0);
        try {
            fillPreferences(sharedString, preferences, localizationManager);
        } catch (IOException e) {
            // no need to crash, just return what's there
            log.warn("error reading string from clipboard", e);
        }
        return preferences;
    }

    /**
     * Updates the given preferences from the given preferences string.
     */
    private static void fillPreferences(String sharedString, NewGamePreferences preferences,
                                        LocalizationManager localizationManager) throws IOException {
        // shared parameters are always in Englisch
        final String seedDisplayName = localizationManager.localizeTextInLanguage(SupportedLanguage.FALLBACK, SEED_KEY);
        final String botIntelligenceDisplayName =
            localizationManager.localizeTextInLanguage(SupportedLanguage.FALLBACK, BOT_INTELLIGENCE_KEY);
        final String mapSizeDisplayName = localizationManager.localizeTextInLanguage(SupportedLanguage.FALLBACK,
            MAP_SIZE_KEY);
        final String densityDisplayName = localizationManager.localizeTextInLanguage(SupportedLanguage.FALLBACK,
            DENSITY_KEY);
        final String startingPositionDisplayName =
            localizationManager.localizeTextInLanguage(SupportedLanguage.FALLBACK, STARTING_POSITION_KEY);
        final String numberOfBotPlayersDisplayName =
            localizationManager.localizeTextInLanguage(SupportedLanguage.FALLBACK, NUMBER_OF_BOT_PLAYERS_KEY);

        final BufferedReader reader = new BufferedReader(new StringReader(sharedString));
        String line;
        line = reader.readLine();
        while (line != null) {
            try {
                final String[] stringParts = line.split(":");
                if (stringParts.length < 2) {
                    continue;
                }
                final String firstStringPart = stringParts[0].trim();
                final String secondStringPart = stringParts[1].trim();
                if (firstStringPart.equals(seedDisplayName)) {
                    preferences.setSeed(Long.parseLong(secondStringPart));
                } else if (firstStringPart.equals(botIntelligenceDisplayName)) {
                    final Intelligence botIntelligence = getBotIntelligenceFromDisplayNameCode(secondStringPart);
                    preferences.setBotIntelligence(botIntelligence);
                } else if (firstStringPart.equals(mapSizeDisplayName)) {
                    final MapSizes mapSize = getMapSizeFromDisplayNameCode(secondStringPart);
                    preferences.setMapSize(mapSize);
                } else if (firstStringPart.equals(densityDisplayName)) {
                    final Densities mapDensity =
                        getMapDensityFromDisplayNameCode(secondStringPart);
                    preferences.setDensity(mapDensity);
                } else if (firstStringPart.equals(startingPositionDisplayName)) {
                    int startingPosition = Integer.parseInt(secondStringPart);
                    // compensate for displayed index starting at 1
                    startingPosition -= 1;
                    startingPosition = Math.min(5, Math.abs(startingPosition));
                    preferences.setStartingPosition(startingPosition);
                } else if (firstStringPart.equals(numberOfBotPlayersDisplayName)) {
                    int numberOfBotPlayers = Integer.parseInt(secondStringPart);
                    numberOfBotPlayers = Math.min(5, Math.abs(numberOfBotPlayers));
                    numberOfBotPlayers = Math.max(1, numberOfBotPlayers);
                    preferences.setNumberOfBotPlayers(numberOfBotPlayers);
                }
                // ignore non recognized lines
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                // continue with other lines
            } finally {
                line = reader.readLine();
            }
        }
    }

    private static String botIntelligenceToDisplayNameCode(Intelligence intelligence) {
        return DIFFICULTIES_KEYS.get(intelligence.ordinal());
    }

    private static Intelligence getBotIntelligenceFromDisplayNameCode(String displayNameCode) {
        return Intelligence.values()[DIFFICULTIES_KEYS.indexOf(displayNameCode)];
    }

    private static String mapSizeToDisplayNameCode(MapSizes mapSize) {
        return MAP_SIZES_KEYS.get(mapSize.ordinal());
    }

    private static MapSizes getMapSizeFromDisplayNameCode(String displayNameCode) {
        return MapSizes.values()[MAP_SIZES_KEYS.indexOf(displayNameCode)];
    }

    private static String mapDensityToDisplayNameCode(Densities density) {
        return DENSITIES_KEYS.get(density.ordinal());
    }

    private static Densities getMapDensityFromDisplayNameCode(String displayNameCode) {
        return Densities.values()[DENSITIES_KEYS.indexOf(displayNameCode)];
    }

    /**
     * Returns game parameters matching these preferences.
     */
    public GameParameters toGameParameters() {
        // starting position may not be larger than the number of players; needs to be corrected in case the number
        // of bot players is reduced from default
        final int correctedStartingPosition = Math.min(startingPosition, numberOfBotPlayers);
        return new GameParameters(correctedStartingPosition, seed, mapSize.amountOfTiles, density.densityFloat,
            botIntelligence, numberOfBotPlayers);
    }

    /**
     * Converts the preferences to a string for display in the UI.
     * Uses localized keys and values for display.
     */
    public String toDisplayString(LocalizationManager localizationManager) {
        return String.format(PARAMETER_DISPLAY_FORMAT, localizationManager.localizeText(SEED_KEY), seed)
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT,
            localizationManager.localizeText(STARTING_POSITION_KEY),
            startingPosition + 1)
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, localizationManager.localizeText(BOT_INTELLIGENCE_KEY),
            localizationManager.localizeText(botIntelligenceToDisplayNameCode(botIntelligence)))
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, localizationManager.localizeText(MAP_SIZE_KEY),
            localizationManager.localizeText(mapSizeToDisplayNameCode(mapSize)))
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, localizationManager.localizeText(DENSITY_KEY),
            localizationManager.localizeText(mapDensityToDisplayNameCode(density)));
    }

    /**
     * Converts the preferences to a sharable string.
     */
    public String toSharableString(LocalizationManager localizationManager) {
        // Parameter names for sharing (readable English)
        final String seedDisplayName = localizationManager.localizeTextInLanguage(SupportedLanguage.FALLBACK, SEED_KEY);
        final String botIntelligenceDisplayName =
            localizationManager.localizeTextInLanguage(SupportedLanguage.FALLBACK, BOT_INTELLIGENCE_KEY);
        final String mapSizeDisplayName = localizationManager.localizeTextInLanguage(SupportedLanguage.FALLBACK,
            MAP_SIZE_KEY);
        final String densityDisplayName = localizationManager.localizeTextInLanguage(SupportedLanguage.FALLBACK,
            DENSITY_KEY);
        final String startingPositionDisplayName =
            localizationManager.localizeTextInLanguage(SupportedLanguage.FALLBACK, STARTING_POSITION_KEY);

        return String.format(PARAMETER_DISPLAY_FORMAT, seedDisplayName, seed)
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, startingPositionDisplayName, startingPosition + 1)
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, botIntelligenceDisplayName,
            localizationManager.localizeTextInLanguage(SupportedLanguage.FALLBACK,
                botIntelligenceToDisplayNameCode(botIntelligence)))
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, mapSizeDisplayName,
            localizationManager.localizeTextInLanguage(SupportedLanguage.FALLBACK, mapSizeToDisplayNameCode(mapSize)))
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, densityDisplayName,
            localizationManager.localizeTextInLanguage(SupportedLanguage.FALLBACK,
                mapDensityToDisplayNameCode(density)));
    }

    /**
     * Map sizes that can be generated.
     */
    public enum MapSizes {
        SMALL(50), MEDIUM(150), LARGE(250), XLARGE(500), XXLARGE(1000);

        @Getter
        private final int amountOfTiles;

        MapSizes(int amountOfTiles) {
            this.amountOfTiles = amountOfTiles;
        }

    }

    /**
     * Map densities that can be generated.
     */
    public enum Densities {
        LOOSE(-3), MEDIUM(0), DENSE(3);

        @Getter
        private final float densityFloat;

        Densities(float densityFloat) {
            this.densityFloat = densityFloat;
        }
    }

}

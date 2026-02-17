// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.ingame.ui.EnumDisplayNameConverter;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Value object: preferences for a new game.
 */
@Slf4j
public class NewGamePreferences {

    public static final String PARAMETER_DISPLAY_FORMAT = "%s: %s";

    // Keys for UI localization
    private static final String SEED_KEY = "seed";
    private static final String BOT_INTELLIGENCE_KEY = "cpu-difficulty";
    private static final String MAP_SIZE_KEY = "map-size";
    private static final String DENSITY_KEY = "map-density";
    private static final String STARTING_POSITION_KEY = "starting-position";
    private static final String NUMBER_OF_BOT_PLAYERS_KEY = "number-of-bots";

    // Keys for sharing (readable English)
    private static final String SEED_DISPLAY_KEY = "Seed";
    private static final String BOT_INTELLIGENCE_DISPLAY_KEY = "CPU Difficulty";
    private static final String MAP_SIZE_DISPLAY_KEY = "Map Size";
    private static final String DENSITY_DISPLAY_KEY = "Map Density";
    private static final String STARTING_POSITION_DISPLAY_KEY = "Starting Position";
    private static final String NUMBER_OF_BOT_PLAYERS_DISPLAY_KEY = "Number of Bots";

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
    public static NewGamePreferences fromSharableString(String sharedString) {
        final NewGamePreferences preferences = new NewGamePreferences(0, Intelligence.LEVEL_1, MapSizes.SMALL,
            Densities.DENSE, 0);
        try {
            fillPreferences(sharedString, preferences);
        } catch (IOException e) {
            // no need to crash, just return what's there
            log.warn("error reading string from clipboard", e);
        }
        return preferences;
    }

    /**
     * Updates the given preferences from the given preferences string.
     */
    private static void fillPreferences(String sharedString, NewGamePreferences preferences) throws IOException {
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
                switch (firstStringPart) {
                    case SEED_DISPLAY_KEY:
                        preferences.setSeed(Long.parseLong(secondStringPart));
                        break;
                    case BOT_INTELLIGENCE_DISPLAY_KEY:
                        final Intelligence botIntelligence =
                            EnumDisplayNameConverter.getBotIntelligenceFromDisplayName(secondStringPart);
                        preferences.setBotIntelligence(botIntelligence);
                        break;
                    case MAP_SIZE_DISPLAY_KEY:
                        final MapSizes mapSize = EnumDisplayNameConverter.getMapSizeFromDisplayName(secondStringPart);
                        preferences.setMapSize(mapSize);
                        break;
                    case DENSITY_DISPLAY_KEY:
                        final Densities mapDensity =
                            EnumDisplayNameConverter.getMapDensityFromDisplayName(secondStringPart);
                        preferences.setDensity(mapDensity);
                        break;
                    case STARTING_POSITION_DISPLAY_KEY:
                        int startingPosition = Integer.parseInt(secondStringPart);
                        // compensate for displayed index starting at 1
                        startingPosition -= 1;
                        startingPosition = Math.min(5, Math.abs(startingPosition));
                        preferences.setStartingPosition(startingPosition);
                        break;
                    case NUMBER_OF_BOT_PLAYERS_DISPLAY_KEY:
                        int numberOfBotPlayers = Integer.parseInt(secondStringPart);
                        numberOfBotPlayers = Math.min(5, Math.abs(numberOfBotPlayers));
                        numberOfBotPlayers = Math.max(1, numberOfBotPlayers);
                        preferences.setNumberOfBotPlayers(numberOfBotPlayers);
                        break;
                    default:
                        // ignore non recognized lines
                        break;
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                // continue with other lines
            } finally {
                line = reader.readLine();
            }
        }
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
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, localizationManager.localizeText(STARTING_POSITION_KEY),
            startingPosition + 1)
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, localizationManager.localizeText(BOT_INTELLIGENCE_KEY),
            localizationManager.localizeText(EnumDisplayNameConverter.getDisplayName(botIntelligence)))
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, localizationManager.localizeText(MAP_SIZE_KEY),
            localizationManager.localizeText(EnumDisplayNameConverter.getDisplayName(mapSize)))
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, localizationManager.localizeText(DENSITY_KEY),
            localizationManager.localizeText(EnumDisplayNameConverter.getDisplayName(density)))
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, localizationManager.localizeText(NUMBER_OF_BOT_PLAYERS_KEY),
            numberOfBotPlayers);
    }

    /**
     * Converts the preferences to a sharable string.
     */
    public String toSharableString() {
        return String.format(PARAMETER_DISPLAY_FORMAT, SEED_DISPLAY_KEY, seed)
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, STARTING_POSITION_DISPLAY_KEY, startingPosition + 1)
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, BOT_INTELLIGENCE_DISPLAY_KEY,
            EnumDisplayNameConverter.getDisplayName(botIntelligence))
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, MAP_SIZE_DISPLAY_KEY, EnumDisplayNameConverter.getDisplayName(mapSize))
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, DENSITY_DISPLAY_KEY,
            EnumDisplayNameConverter.getDisplayName(density))
            + String.format("\n" + PARAMETER_DISPLAY_FORMAT, NUMBER_OF_BOT_PLAYERS_DISPLAY_KEY, numberOfBotPlayers);
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

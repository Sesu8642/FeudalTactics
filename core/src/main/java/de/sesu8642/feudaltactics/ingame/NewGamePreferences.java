// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import de.sesu8642.feudaltactics.ingame.ui.EnumDisplayNameConverter;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;

/**
 * Value object: preferences for a new game.
 */
public class NewGamePreferences {

    public static final String PARAMETER_DISPLAY_FORMAT = "%s: %s";
    private static final String SEED_DISPLAY_NAME = "Seed";
    private static final String BOT_INTELLIGENCE_DISPLAY_NAME = "CPU Difficulty";
    private static final String MAP_SIZE_DISPLAY_NAME = "Map Size";
    private static final String DENSITY_DISPLAY_NAME = "Map Density";
    private static final String STARTING_POSITION_DISPLAY_NAME = "Starting Position";
    private static final String NUMBER_OF_BOT_PLAYERS_DISPLAY_NAME = "Number of Bots";
    private static final Logger LOGGER = LoggerFactory.getLogger(NewGamePreferences.class);
    private long seed;
    private Intelligence botIntelligence;
    private MapSizes mapSize;
    private Densities density;
    private int startingPosition;
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
        NewGamePreferences preferences = new NewGamePreferences(0, Intelligence.LEVEL_1, MapSizes.SMALL,
                Densities.DENSE, 0);
        try {
            fillPreferences(sharedString, preferences);
        } catch (IOException e) {
            // no need to crash, just return what's there
            LOGGER.warn("error reading string from clipboard", e);
        }
        return preferences;
    }

    /**
     * Updates the given preferences from the given preferences string.
     */
    private static void fillPreferences(String sharedString, NewGamePreferences preferences) throws IOException {
        BufferedReader reader = new BufferedReader(new StringReader(sharedString));
        String line;
        line = reader.readLine();
        while (line != null) {
            try {
                String[] stringParts = line.split(":");
                if (stringParts.length < 2) {
                    continue;
                }
                String firstStringPart = stringParts[0].trim();
                String secondStringPart = stringParts[1].trim();
                switch (firstStringPart) {
                    case SEED_DISPLAY_NAME:
                        preferences.setSeed(Long.parseLong(secondStringPart));
                        break;
                    case BOT_INTELLIGENCE_DISPLAY_NAME:
                        Intelligence botIntelligence =
                                EnumDisplayNameConverter.getBotIntelligenceFromDisplayName(secondStringPart);
                        preferences.setBotIntelligence(botIntelligence);
                        break;
                    case MAP_SIZE_DISPLAY_NAME:
                        MapSizes mapSize = EnumDisplayNameConverter.getMapSizeFromDisplayName(secondStringPart);
                        preferences.setMapSize(mapSize);
                        break;
                    case DENSITY_DISPLAY_NAME:
                        Densities mapDensity =
                                EnumDisplayNameConverter.getMapDensityFromDisplayName(secondStringPart);
                        preferences.setDensity(mapDensity);
                        break;
                    case STARTING_POSITION_DISPLAY_NAME:
                        int startingPosition = Integer.parseInt(secondStringPart);
                        // compensate for displayed index starting at 1
                        startingPosition -= 1;
                        startingPosition = Math.min(5, Math.abs(startingPosition));
                        preferences.setStartingPosition(startingPosition);
                        break;
                    case NUMBER_OF_BOT_PLAYERS_DISPLAY_NAME:
                        int numberOfBotPlayers = Integer.parseInt(secondStringPart);
                        numberOfBotPlayers = Math.min(5, Math.abs(numberOfBotPlayers));
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
        int correctedStartingPosition = Math.min(startingPosition, numberOfBotPlayers);
        return new GameParameters(correctedStartingPosition, seed, mapSize.amountOfTiles, density.densityFloat,
                botIntelligence, numberOfBotPlayers);
    }

    /**
     * Converts the preferences to a sharable string.
     */
    public String toSharableString() {
        return String.format(PARAMETER_DISPLAY_FORMAT, SEED_DISPLAY_NAME, seed)
                + String.format("\n" + PARAMETER_DISPLAY_FORMAT, STARTING_POSITION_DISPLAY_NAME,
                startingPosition + 1)
                + String.format("\n" + PARAMETER_DISPLAY_FORMAT, BOT_INTELLIGENCE_DISPLAY_NAME,
                EnumDisplayNameConverter.getDisplayName(botIntelligence))
                + String.format("\n" + PARAMETER_DISPLAY_FORMAT, MAP_SIZE_DISPLAY_NAME,
                EnumDisplayNameConverter.getDisplayName(mapSize))
                + String.format("\n" + PARAMETER_DISPLAY_FORMAT, DENSITY_DISPLAY_NAME,
                EnumDisplayNameConverter.getDisplayName(density));
    }

    public long getSeed() {
        return seed;
    }

    public void setSeed(long seed) {
        this.seed = seed;
    }

    public Intelligence getBotIntelligence() {
        return botIntelligence;
    }

    public void setBotIntelligence(Intelligence botIntelligence) {
        this.botIntelligence = botIntelligence;
    }

    public MapSizes getMapSize() {
        return mapSize;
    }

    public void setMapSize(MapSizes mapSize) {
        this.mapSize = mapSize;
    }

    public Densities getDensity() {
        return density;
    }

    public void setDensity(Densities density) {
        this.density = density;
    }

    public int getStartingPosition() {
        return startingPosition;
    }

    public void setStartingPosition(int startingPosition) {
        this.startingPosition = startingPosition;
    }

    public int getNumberOfBotPlayers() {
        return numberOfBotPlayers;
    }

    public void setNumberOfBotPlayers(int numberOfBotPlayers) {
        this.numberOfBotPlayers = numberOfBotPlayers;
    }

    /**
     * Map sizes that can be generated.
     */
    public enum MapSizes {
        SMALL(50), MEDIUM(150), LARGE(250), XLARGE(500), XXLARGE(1000);

        private final int amountOfTiles;

        MapSizes(int amountOfTiles) {
            this.amountOfTiles = amountOfTiles;
        }

        public int getAmountOfTiles() {
            return this.amountOfTiles;
        }

    }

    /**
     * Map densities that can be generated.
     */
    public enum Densities {
        LOOSE(-3), MEDIUM(0), DENSE(3);

        private final float densityFloat;

        Densities(float densityFloat) {
            this.densityFloat = densityFloat;
        }

        public float getDensityFloat() {
            return this.densityFloat;
        }
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate.validation;

import com.badlogic.gdx.math.Vector2;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.*;
import lombok.extern.slf4j.Slf4j;

import java.util.*;

/**
 * Helper class that is used to ensure a {@link GameState}'s integrity.
 **/
@Slf4j
public final class GameStateValidator {

    private static final int BARELY_PLAUSIBLE_HIGH_INT = 10000;

    // prevent instantiation
    private GameStateValidator() {
        throw new AssertionError();
    }

    /**
     * Validates a gamestate to ensure its integrity.
     *
     * @param gameState game state to validate
     * @return whether the game state is valid
     */
    public static boolean isValid(GameState gameState) {
        log.info("checking if game state is valid in general");
        final boolean isValid = gameStateHasBotIntelligence(gameState)
            && gameStateHasMap(gameState)
            && heldObjectIsOnlyPresentWithActiveKingdom(gameState)
            && heldObjectIsAllowed(gameState)
            && roundIsValid(gameState)
            && playerTurnIsValid(gameState)
            && gameStateHasValidNumberOfPlayers(gameState)
            && winnerIsAmongPlayers(gameState)
            && winnerAndWinningRoundArePresentTogether(gameState)
            && playersHaveProperIndexes(gameState)
            && onlyDefeatedPlayersHaveRoundOfDefeat(gameState)
            && kingdomsAreNotNull(gameState)
            && activeKingdomIsAmongKingdoms(gameState)
            && eachKingdomsHasValidSavings(gameState)
            && eachKingdomHasOneCapital(gameState)
            && eachKingdomHasValidAmountOfTiles(gameState)
            && eachKingdomHasAPlayer(gameState)
            && connectedTilesFormKingdom(gameState)
            && allTilesInAKingdomAreConnected(gameState)
            && tilesHaveBackLinksInTheirKingdoms(gameState)
            && tileCoordinatesMatchMap(gameState)
            && mapIsNotTooLarge(gameState)
            && treesAreTheCorrectTypeBasedOnPosition(gameState)
            && capitalsAndCastlesAreOnlyInKingdoms(gameState);
        if (isValid) {
            log.info("game state is valid in general");
        } else {
            log.info("game state is NOT valid in general");
        }
        return isValid;
    }

    /**
     * Validates a gamestate to ensure its integrity and that it's suitable for a singleplayer game.
     *
     * @param gameState game state to validate
     * @return whether the game state is a valid singleplayer game
     */
    public static boolean isValidSingplayerGame(GameState gameState) {
        log.info("checking if game state is valid for a single player game");
        final boolean isValid = isValid(gameState)
            && hasNoScenarioMap(gameState)
            && hasNoRemotePlayers(gameState)
            && hasOneHumanPlayer(gameState);
        if (isValid) {
            log.info("game state is valid for a single player game");
        } else {
            log.info("game state is NOT valid for a single player game");
        }
        return isValid;

    }

    private static boolean gameStateHasBotIntelligence(GameState gameState) {
        final boolean result = gameState.getBotIntelligence() != null;
        if (!result) {
            log.info("game state has no bot intelligence");
        }
        return result;
    }

    private static boolean gameStateHasMap(GameState gameState) {
        final boolean result = gameState.getMap() != null;
        if (!result) {
            log.info("game state has no map");
        }
        return result;
    }

    private static boolean heldObjectIsOnlyPresentWithActiveKingdom(GameState gameState) {
        final boolean result = gameState.getHeldObject() == null || gameState.getActiveKingdom() != null;
        if (!result) {
            log.info("game state has held object but no active kingdom");
        }
        return result;
    }

    private static boolean heldObjectIsAllowed(GameState gameState) {
        final boolean result;
        if (gameState.getHeldObject() == null) {
            return true;
        }
        final Class<? extends TileContent> heldObjectClass = gameState.getHeldObject().getClass();
        result = Unit.class.isAssignableFrom(heldObjectClass) || Castle.class.isAssignableFrom(heldObjectClass);
        if (!result) {
            log.info("game state has invalid held object");
        }
        return result;
    }

    private static boolean roundIsValid(GameState gameState) {
        final boolean result = gameState.getRound() >= 1 && gameState.getRound() <= BARELY_PLAUSIBLE_HIGH_INT;
        if (!result) {
            log.info("game state has invalid round");
        }
        return result;
    }

    private static boolean playerTurnIsValid(GameState gameState) {
        final boolean result =
            gameState.getPlayerTurn() >= 0 && gameState.getPlayerTurn() < gameState.getPlayers().size();
        if (!result) {
            log.info("game state has invalid player turn");
        }
        return result;
    }

    private static boolean gameStateHasValidNumberOfPlayers(GameState gameState) {
        final boolean result =
            gameState.getPlayers() != null && gameState.getPlayers().size() >= 2 && gameState.getPlayers().size() <= 6;
        if (!result) {
            log.info("game state has invalid number of players");
        }
        return result;
    }

    private static boolean winnerIsAmongPlayers(GameState gameState) {
        final boolean result = gameState.getWinner() == null || gameState.getPlayers().contains(gameState.getWinner());
        if (!result) {
            log.info("game state's winner is not among players");
        }
        return result;
    }

    private static boolean winnerAndWinningRoundArePresentTogether(GameState gameState) {
        final boolean result =
            (gameState.getWinner() == null && gameState.getWinningRound() == null) || gameState.getWinner() != null && gameState.getWinningRound() != null;
        if (!result) {
            log.info("game state has only one of winner and winningRound");
        }
        return result;
    }

    private static boolean playersHaveProperIndexes(GameState gameState) {
        final List<Player> players = gameState.getPlayers();
        final Set<Integer> expectedIndexes = new HashSet<>();
        for (int i = 0; i < players.size(); i++) {
            expectedIndexes.add(i);
        }
        for (Player player : players) {
            if (!expectedIndexes.contains(player.getPlayerIndex())) {
                log.info("player indices are invalid");
                return false;
            }
            expectedIndexes.remove(player.getPlayerIndex());
        }
        final boolean noIndexLeftOver = expectedIndexes.isEmpty();
        if (!noIndexLeftOver) {
            log.info("player indices contain duplicate");
        }
        return noIndexLeftOver;
    }

    private static boolean onlyDefeatedPlayersHaveRoundOfDefeat(GameState gameState) {
        for (Player player : gameState.getPlayers()) {
            final boolean playerIsDefeated = player.getRoundOfDefeat() != null;
            if (playerIsDefeated) {
                for (Kingdom kingdom : gameState.getKingdoms()) {
                    if (kingdom.getPlayer() == player) {
                        log.info("defeated player has a kingdom");
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private static boolean kingdomsAreNotNull(GameState gameState) {
        final boolean result = gameState.getKingdoms() != null;
        if (!result) {
            log.info("kingdoms are null");
        }
        return result;
    }

    private static boolean activeKingdomIsAmongKingdoms(GameState gameState) {
        final boolean result =
            gameState.getActiveKingdom() == null || gameState.getKingdoms().contains(gameState.getActiveKingdom());
        if (!result) {
            log.info("active kingdom is not among kingdoms");
        }
        return result;
    }

    private static boolean eachKingdomsHasValidSavings(GameState gameState) {
        for (Kingdom kingdom : gameState.getKingdoms()) {
            if (kingdom.getSavings() < 0 || kingdom.getSavings() > BARELY_PLAUSIBLE_HIGH_INT) {
                log.info("kingdom has invalid savings amount");
                return false;
            }
        }
        return true;
    }

    private static boolean eachKingdomHasOneCapital(GameState gameState) {
        for (Kingdom kingdom : gameState.getKingdoms()) {
            final long numberOfCapitals =
                kingdom.getTiles().stream().filter(tile -> tile.getContent() != null && Capital.class.isAssignableFrom(tile.getContent().getClass())).count();
            if (numberOfCapitals != 1) {
                log.info("kingdom has not exactly one capital");
                return false;
            }
        }
        return true;
    }

    private static boolean eachKingdomHasValidAmountOfTiles(GameState gameState) {
        for (Kingdom kingdom : gameState.getKingdoms()) {
            if (kingdom.getTiles() == null || kingdom.getTiles().size() < 2 || kingdom.getTiles().size() > BARELY_PLAUSIBLE_HIGH_INT) {
                log.info("kingdom has invalid number of tiles");
                return false;
            }
        }
        return true;
    }

    private static boolean eachKingdomHasAPlayer(GameState gameState) {
        for (Kingdom kingdom : gameState.getKingdoms()) {
            if (kingdom.getPlayer() == null) {
                log.info("kingdom has no player");
                return false;
            }
        }
        return true;
    }

    private static boolean connectedTilesFormKingdom(GameState gameState) {
        for (HexTile tile : gameState.getMap().values()) {
            for (HexTile neighborTile : HexMapHelper.getNeighborTiles(gameState.getMap(), tile)) {
                if (neighborTile != null && neighborTile.getPlayer() == tile.getPlayer() && (tile.getKingdom() == null || tile.getKingdom() != neighborTile.getKingdom())) {
                    log.info("connected tiles don't form a kingdom");
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean allTilesInAKingdomAreConnected(GameState gameState) {
        for (Kingdom kingdom : gameState.getKingdoms()) {
            final Set<HexTile> seenTiles = new HashSet<>();
            final LinkedList<HexTile> tilesToExpandFrom = new LinkedList<>();
            tilesToExpandFrom.add(kingdom.getTiles().get(0));
            while (!tilesToExpandFrom.isEmpty()) {
                final HexTile currentTile = tilesToExpandFrom.removeFirst();
                seenTiles.add(currentTile);
                for (HexTile expandTile : HexMapHelper.getNeighborTiles(gameState.getMap(), currentTile)) {
                    if (expandTile != null && !seenTiles.contains(expandTile) && !tilesToExpandFrom.contains(expandTile)
                        && expandTile.getKingdom() == currentTile.getKingdom()) {
                        tilesToExpandFrom.add(expandTile);
                    }
                }
            }
            if (seenTiles.size() != kingdom.getTiles().size()) {
                log.info("not all tiles in a kingdom are connected");
                return false;
            }
        }
        return true;
    }

    private static boolean tilesHaveBackLinksInTheirKingdoms(GameState gameState) {
        for (HexTile tile : gameState.getMap().values()) {
            if (tile.getKingdom() != null && !tile.getKingdom().getTiles().contains(tile)) {
                log.info("kingdom of a tile does not contain the tile");
                return false;
            }
        }
        return true;
    }

    private static boolean tileCoordinatesMatchMap(GameState gameState) {
        for (Map.Entry<Vector2, HexTile> mapEntry : gameState.getMap().entrySet()) {
            if (!mapEntry.getKey().equals(mapEntry.getValue().getPosition())) {
                return false;
            }
        }
        return true;
    }

    private static boolean mapIsNotTooLarge(GameState gameState) {
        final boolean result = gameState.getMap().size() <= NewGamePreferences.MapSizes.XXLARGE.getAmountOfTiles();
        if (!result) {
            log.info("map is too large");
        }
        return result;
    }

    private static boolean treesAreTheCorrectTypeBasedOnPosition(GameState gameState) {
        for (HexTile tile : gameState.getMap().values()) {
            if (tile.getContent() == null) {
                continue;
            }
            final boolean isCoastTile =
                HexMapHelper.getNeighborTiles(gameState.getMap(), tile).stream().anyMatch(Objects::isNull);
            if (PalmTree.class.isAssignableFrom(tile.getContent().getClass()) && !isCoastTile) {
                log.info("palm tree is on non-coast tile");
                return false;
            }
            if (Tree.class.isAssignableFrom(tile.getContent().getClass()) && isCoastTile) {
                log.info("oak tree is on coast tile");
                return false;
            }
        }
        return true;
    }

    private static boolean capitalsAndCastlesAreOnlyInKingdoms(GameState gameState) {
        for (HexTile tile : gameState.getMap().values()) {
            if (tile.getContent() == null || tile.getKingdom() != null) {
                continue;
            }
            if (Capital.class.isAssignableFrom(tile.getContent().getClass())) {
                log.info("capital is on non-kingdom tile");
                return false;
            }
            if (Castle.class.isAssignableFrom(tile.getContent().getClass())) {
                log.info("castle is on non-kingdom tile");
                return false;
            }
        }
        return true;
    }

    private static boolean hasNoScenarioMap(GameState gameState) {
        final boolean result = gameState.getScenarioMap() == ScenarioMap.NONE;
        if (!result) {
            log.info("game state has scenario map");
        }
        return result;
    }

    private static boolean hasNoRemotePlayers(GameState gameState) {
        final boolean result =
            gameState.getPlayers().stream().noneMatch(player -> player.getType() == Player.Type.REMOTE);
        if (!result) {
            log.info("game state has remote player");
        }
        return result;
    }

    private static boolean hasOneHumanPlayer(GameState gameState) {
        final boolean result =
            gameState.getPlayers().stream().filter(player -> player.getType() == Player.Type.LOCAL_PLAYER).count() == 1;
        if (!result) {
            log.info("game state doesn't have exactly one human player");
        }
        return result;
    }

}

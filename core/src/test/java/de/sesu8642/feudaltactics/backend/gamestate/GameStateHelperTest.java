// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.backend.gamestate;

import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for GameStateHelper class.
 */
class GameStateHelperTest {

    List<Player> players;

    static Collection<Arguments> provideMapSizesAndSeeds() {
        final List<Integer> mapSizes = provideMapSizes().collect(Collectors.toList());
        final List<Long> seeds = provideSeeds().collect(Collectors.toList());
        final List<Arguments> sizesAndSeeds = new ArrayList<>();
        for (int i = 0; i < mapSizes.size() - 1; i++) {
            sizesAndSeeds.add(Arguments.of(mapSizes.get(i), seeds.get(i)));
        }
        return sizesAndSeeds;
    }

    static Stream<Integer> provideMapSizes() {
        // need at least 6 tiles for every player to get a kingdom
        return Stream.of(6, 7, 42, 69, 360, 420, 1024);
    }

    static Stream<Long> provideSeeds() {
        return Stream.of(1L, 42L, 69L, 360L, 420L, 1024L, 9999L);
    }

    @BeforeEach
    void init() {
        players = new ArrayList<>();
        players.add(new Player(0, Type.LOCAL_BOT));
        players.add(new Player(1, Type.LOCAL_BOT));
        players.add(new Player(2, Type.LOCAL_PLAYER));
    }

    @Test
    void copiedGameStateEqualsOriginal() {
        final GameState original = new GameState();
        GameStateHelper.initializeMap(original, players, 500, 2, 0.2F, 12345L);
        original.setActiveKingdom(original.getKingdoms().get(0));

        final GameState copy = GameStateHelper.getCopy(original);

        assertEquals(original, copy);
    }

    @ParameterizedTest
    @MethodSource("provideMapSizesAndSeeds")
    void initializedMapHasCorrectLandMass(int landMass, long seed) {
        final GameState gameState = new GameState();

        GameStateHelper.initializeMap(gameState, players, landMass, 2, 0.2F, seed);

        assertEquals(landMass, gameState.getMap().size());
    }

    @ParameterizedTest
    @MethodSource("provideSeeds")
    void initializedMapHasAtLeastOneKingdomPerPlayer(long seed) {
        final GameState gameState = new GameState();

        // generating a random map
        // use size of 6 for the lowest chance for every player to have a kingdom when
        GameStateHelper.initializeMap(gameState, players, 6, 2, 0.2F, seed);

        players.forEach((player) -> {
            assertTrue(gameState.getKingdoms().stream().anyMatch((kingdom) -> kingdom.getPlayer().equals(player)));
        });
    }

    @ParameterizedTest
    @MethodSource("provideMapSizesAndSeeds")
    void initializedMapHasTilesEvenlyDistributedAcrossPlayers(int mapSize, long seed) {
        final GameState gameState = new GameState();

        // generating a random map
        GameStateHelper.initializeMap(gameState, players, 6, 2, 0.2F, seed);

        final Player firstPlayer = gameState.getPlayers().get(0);
        final long firstPlayerTileAmount = gameState.getMap().values().stream()
            .filter(tile -> tile.getPlayer().equals(firstPlayer)).count();
        gameState.getPlayers().forEach(player -> {
            // the difference between the tiles of any player and the first one should not
            // be larger than 1
            final long otherPlayerTileAmount = gameState.getMap().values().stream()
                .filter(tile -> tile.getPlayer().equals(player)).count();
            final long tileDifference = Math.abs(firstPlayerTileAmount - otherPlayerTileAmount);
            assertTrue(tileDifference <= 1);
        });
    }

    @ParameterizedTest
    @MethodSource("provideSeeds")
    void initializedGameStateHasPlayersOrderedByincomeLowestFirst(long seed) {
        final GameState gameState = new GameState();

        // generating a random map
        GameStateHelper.initializeMap(gameState, players, 100, 2, 0.2F, seed);

        for (int i = 1; i < gameState.getPlayers().size(); i++) {
            final Player playerI = gameState.getPlayers().get(i);
            final Player prevPlayer = gameState.getPlayers().get(i - 1);
            final int playerIncome =
                gameState.getKingdoms().stream().filter(kingdom -> kingdom.getPlayer().equals(playerI))
                .mapToInt(GameStateHelper::getKingdomIncome).sum();
            final int prevPlayerIncome = gameState.getKingdoms().stream()
                .filter(kingdom -> kingdom.getPlayer().equals(prevPlayer))
                .mapToInt(GameStateHelper::getKingdomIncome).sum();
            assertTrue(prevPlayerIncome <= playerIncome);
        }
    }

}

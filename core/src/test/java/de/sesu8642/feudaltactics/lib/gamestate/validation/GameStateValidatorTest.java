// SPDX-License-Identifier: GPL-3.0-or-later

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate.validation;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.google.common.collect.ImmutableList;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;

import static de.sesu8642.feudaltactics.lib.gamestate.Unit.UnitTypes.BARON;
import static de.sesu8642.feudaltactics.lib.gamestate.Unit.UnitTypes.SPEARMAN;
import static de.sesu8642.feudaltactics.lib.gamestate.validation.GameStateValidator.isValid;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GameStateValidatorTest {

    List<Player> players;

    private static boolean isValidForSinglePlayer(GameState gs) {
        return GameStateValidator.isValidSingplayerGame(gs);
    }

    @BeforeEach
    void init() {
        players = new ArrayList<>();
        players.add(new Player(0, Player.Type.LOCAL_BOT));
        players.add(new Player(1, Player.Type.LOCAL_BOT));
        players.add(new Player(2, Player.Type.LOCAL_PLAYER));
    }

    private GameState createValidGameState() {
        final GameState gameState = new GameState();
        GameStateHelper.initializeMap(gameState, players, 20, -3, 0.2F, 234654L);
        final Kingdom activePlayersKingdom =
            gameState.getKingdoms().stream().filter(kingdom -> kingdom.getPlayer() == gameState.getActivePlayer()).findFirst().get();
        gameState.setActiveKingdom(activePlayersKingdom);
        gameState.setHeldObject(new Unit(SPEARMAN));
        final Json fullSaveJson = new Json(JsonWriter.OutputType.json);
        fullSaveJson.setSerializer(GameState.class, new GameStateSerializer());
        fullSaveJson.setIgnoreUnknownFields(true);
        final String json = fullSaveJson.toJson(gameState, GameState.class);
        System.out.println(json);
        return gameState;
    }

    @Test
    void isValid_valid() {
        final GameState gs = createValidGameState();
        assertTrue(isValid(gs));
    }

    @Test
    void gameStateHasBotIntelligence_invalidWhenNull() {
        final GameState gs = createValidGameState();
        gs.setBotIntelligence(null);
        assertFalse(isValid(gs));
    }

    @Test
    void gameStateHasMap_invalidWhenNull() {
        final GameState gs = createValidGameState();
        gs.setMap(null);
        assertFalse(isValid(gs));
    }

    @Test
    void heldObjectIsOnlyPresentWithActiveKingdom_invalidWhenHeldButNoActive() {
        final GameState gs = createValidGameState();
        gs.setHeldObject(new Unit(BARON));
        gs.setActiveKingdom(null);
        assertFalse(isValid(gs));
    }

    @ParameterizedTest
    @ValueSource(classes = {Tree.class, PalmTree.class, Capital.class, Gravestone.class})
    void heldObjectIsAllowed_invalidWhenDisallowed(Class<TileContent> heldObjectClass) throws ReflectionException {
        final GameState gs = createValidGameState();
        gs.setHeldObject(ClassReflection.newInstance(heldObjectClass));          // Tree is not a permitted held object
        assertFalse(isValid(gs));
    }

    @Test
    void roundIsValid_invalidWhenOutOfRange() {
        final GameState gs = createValidGameState();
        gs.setRound(0);                         // round must be >= 1
        assertFalse(isValid(gs));
    }

    @Test
    void gameStateHasValidNumberOfPlayers_invalidWhenTooFew() {
        final GameState gs = createValidGameState();
        gs.setPlayers(new ArrayList<>(ImmutableList.of(
            new Player(0, Player.Type.LOCAL_PLAYER))));
        assertFalse(isValid(gs));
    }

    @Test
    void gameStateHasValidNumberOfPlayers_invalidWhenTooMany() {
        final GameState gs = createValidGameState();
        gs.setPlayers(new ArrayList<>(ImmutableList.of(
            new Player(0, Player.Type.LOCAL_PLAYER), new Player(1, Player.Type.LOCAL_PLAYER), new Player(2,
                Player.Type.LOCAL_PLAYER), new Player(3, Player.Type.LOCAL_PLAYER), new Player(4,
                Player.Type.LOCAL_PLAYER), new Player(5, Player.Type.LOCAL_PLAYER), new Player(6,
                Player.Type.LOCAL_PLAYER))));
        assertFalse(isValid(gs));
    }

    @Test
    void gameStateHasValidNumberOfPlayers_invalidWhenNull() {
        final GameState gs = createValidGameState();
        gs.setPlayers(null);
        assertFalse(isValid(gs));
    }

    @Test
    void playerTurnIsValid_invalidWhenOutOfRange() {
        final GameState gs = createValidGameState();
        gs.setPlayerTurn(3);                   // only three players (indexes 0‑2)
        assertFalse(isValid(gs));
    }

    @Test
    void playerTurnIsValid_invalidWhenNegative() {
        final GameState gs = createValidGameState();
        gs.setPlayerTurn(-1);
        assertFalse(isValid(gs));
    }

    @Test
    void winnerIsAmongPlayers_invalidWhenWinnerNotInList() {
        final GameState gs = createValidGameState();
        final Player outsider = new Player(99, Player.Type.LOCAL_PLAYER);
        gs.setWinner(outsider);
        assertFalse(isValid(gs));
    }

    @Test
    void winnerAndWinningRoundArePresentTogether_invalidWhenOnlyWinnerSet() {
        final GameState gs = createValidGameState();
        gs.setWinner(gs.getPlayers().get(0));
        gs.setWinningRound(null);
        assertFalse(isValid(gs));
    }

    @Test
    void winnerAndWinningRoundArePresentTogether_invalidWhenOnlyWinningRoundSet() {
        final GameState gs = createValidGameState();
        gs.setWinner(null);
        gs.setWinningRound(42);
        assertFalse(isValid(gs));
    }

    @Test
    void playersHaveProperIndexes_invalidWhenDuplicate() {
        final GameState gs = createValidGameState();
        gs.getPlayers().get(1).setPlayerIndex(0);   // duplicate index
        assertFalse(isValid(gs));
    }

    @Test
    void onlyDefeatedPlayersHaveRoundOfDefeat_invalidWhenDefeatedHasKingdom() {
        final GameState gs = createValidGameState();
        final Player p = gs.getPlayers().get(0);
        p.setRoundOfDefeat(3);                     // mark player as defeated
        // kingdom for that player still present -> should be invalid
        assertFalse(isValid(gs));
    }

    @Test
    void kingdomsAreNotNull_invalidWhenNull() {
        final GameState gs = createValidGameState();
        gs.setKingdoms(null);
        assertFalse(isValid(gs));
    }

    @Test
    void activeKingdomIsAmongKingdoms_invalidWhenMissing() {
        final GameState gs = createValidGameState();
        final Kingdom foreign = new Kingdom(players.get(0));
        gs.setActiveKingdom(foreign);
        assertFalse(isValid(gs));
    }

    @Test
    void activeKingdomBelongsToActivePlayer_invalidWhenNot() {
        final GameState gs = createValidGameState();
        final Kingdom nonActivePlayersKingdom =
            gs.getKingdoms().stream().filter(kingdom -> kingdom.getPlayer() != gs.getActivePlayer()).findFirst().get();
        gs.setActiveKingdom(nonActivePlayersKingdom);
        assertFalse(isValid(gs));
    }

    @Test
    void eachKingdomsHasValidSavings_invalidWhenNegative() {
        final GameState gs = createValidGameState();
        gs.getKingdoms().get(0).setSavings(-42);
        assertFalse(isValid(gs));
    }

    @Test
    void eachKingdomHasOneCapital_invalidWhenZeroCapital() {
        final GameState gs = createValidGameState();
        // wipe all contents from the first kingdom's tiles
        gs.getKingdoms().get(0).getTiles().forEach(t -> t.setContent(null));
        assertFalse(isValid(gs));
    }

    @Test
    void eachKingdomHasValidAmountOfTiles_invalidWhenNullTiles() {
        final GameState gs = createValidGameState();
        final Kingdom kingdom = new Kingdom(gs.getActivePlayer());
        kingdom.setTiles(null);
        gs.getKingdoms().add(kingdom);
        assertFalse(isValid(gs));
    }

    @Test
    void eachKingdomHasValidAmountOfTiles_invalidWhenZeroTiles() {
        final GameState gs = createValidGameState();
        final Kingdom kingdom = new Kingdom(gs.getActivePlayer());
        gs.getKingdoms().add(kingdom);
        assertFalse(isValid(gs));
    }

    @Test
    void eachKingdomHasValidAmountOfTiles_invalidWhenOneTile() {
        final GameState gs = createValidGameState();
        final Kingdom kingdom = new Kingdom(gs.getActivePlayer());
        final HexTile tile = gs.getMap().get(new Vector2(0, 0));
        kingdom.getTiles().add(tile); // this tile has no kingdom so far
        tile.setContent(new Capital());
        gs.getKingdoms().add(kingdom);
        assertFalse(isValid(gs));
    }

    @Test
    void eachKingdomHasAPlayer_invalidWhenNullPlayer() {
        final GameState gs = createValidGameState();
        gs.getKingdoms().get(0).setPlayer(null);
        assertFalse(isValid(gs));
    }

    @Test
    void connectedTilesFormKingdom_invalidWhenNeighbourHasDifferentKingdom() {
        final GameState gs = createValidGameState();
        final HexTile t00 = gs.getMap().get(new Vector2(0, 0));
        final HexTile t01 = gs.getMap().get(new Vector2(0, 1));
        t01.setPlayer(t00.getPlayer());               // same player
        t01.setKingdom(gs.getKingdoms().get(1));      // different kingdom
        assertFalse(isValid(gs));
    }

    @Test
    void connectedTilesFormKingdom_invalidWhenNeighbourHasNullKingdom() {
        final GameState gs = createValidGameState();
        final HexTile t00 = gs.getMap().get(new Vector2(0, 0));
        final HexTile t01 = gs.getMap().get(new Vector2(0, 1));
        t01.setPlayer(t00.getPlayer());               // same player
        t01.setKingdom(null);      // no kingdom
        assertFalse(isValid(gs));
    }

    @Test
    void allTilesInAKingdomAreConnected_invalidWhenDisconnected() {
        final GameState gs = createValidGameState();
        final Kingdom blueKingdom = gs.getMap().get(new Vector2(4, 0)).getKingdom();
        final HexTile distantBlueTile = gs.getMap().get(new Vector2(0, 0));
        blueKingdom.getTiles().add(distantBlueTile);
        distantBlueTile.setKingdom(blueKingdom);
        assertFalse(isValid(gs));
    }

    @Test
    void tileCoordinatesMatchMap_invalidWhenNotMatching() {
        final GameState gs = createValidGameState();
        final HexTile tile = gs.getMap().get(new Vector2(0, 0));
        tile.setPosition(new Vector2(42, 42));
        assertFalse(isValid(gs));
    }

    @Test
    void tilesHaveBackLinksInTheirKingdoms_invalidWhenMissingBackLink() {
        final GameState gs = createValidGameState();
        // this tile is part of a kingdom
        final HexTile tile = gs.getMap().get(new Vector2(1, 1));
        tile.setKingdom(null);
        assertFalse(isValid(gs));
    }

    @Test
    void mapIsNotTooLarge_invalidWhenExceedsMaximum() {
        final GameState gs = new GameState();
        final int max = NewGamePreferences.MapSizes.XXLARGE.getAmountOfTiles();
        GameStateHelper.initializeMap(gs, players, max + 1, 2, 0.2F, 234654L);
        assertFalse(isValid(gs));
    }

    @Test
    void treesAreTheCorrectTypeBasedOnPosition_invalidPalmOnInland() {
        final GameState gs = createValidGameState();
        final HexTile inland = gs.getMap().get(new Vector2(-1, 3)); // interior tile
        inland.setContent(new PalmTree());
        assertFalse(isValid(gs));
    }

    @Test
    void treesAreTheCorrectTypeBasedOnPosition_invalidOakOnCoast() {
        final GameState gs = createValidGameState();
        final HexTile coast = gs.getMap().get(new Vector2(0, 0)); // coast tile
        coast.setContent(new Tree());
        assertFalse(isValid(gs));
    }

    @ParameterizedTest
    @ValueSource(classes = {Capital.class, Castle.class})
    void capitalsAndCastlesAreOnlyInKingdoms_invalidObjectOutsideKingdom(Class<TileContent> mapObjectClass) throws ReflectionException {
        final GameState gs = createValidGameState();
        final HexTile stray = gs.getMap().get(new Vector2(0, 0));
        stray.setContent(ClassReflection.newInstance(mapObjectClass));
        assertFalse(isValid(gs));
    }

    @Test
    void capitalsAndCastlesAreOnlyInKingdoms_validPalmTreeOutsideKingdom() {
        final GameState gs = createValidGameState();
        final HexTile stray = gs.getMap().get(new Vector2(0, 0));
        stray.setContent(new PalmTree());
        assertTrue(isValid(gs));
    }

    /* --------------------- single‑player specific tests --------------------- */

    @Test
    void isValidSingplayerGame_valid() {
        final GameState gs = createValidGameState();
        assertTrue(isValidForSinglePlayer(gs));
    }

    @Test
    void hasNoScenarioMap_invalidWhenScenarioPresent() {
        final GameState gs = createValidGameState();
        gs.setScenarioMap(ScenarioMap.TUTORIAL);
        assertFalse(isValidForSinglePlayer(gs));
    }

    @Test
    void hasNoRemotePlayers_invalidWhenRemotePresent() {
        players.add(new Player(3, Player.Type.REMOTE));
        final GameState gs = createValidGameState();
        assertFalse(isValidForSinglePlayer(gs));
    }

    @Test
    void hasOneHumanPlayer_invalidWhenZeroOrMultipleHumans() {
        // add second human player
        final GameState gs = createValidGameState();

        gs.getPlayers().add(new Player(3, Player.Type.LOCAL_PLAYER));
        assertFalse(isValidForSinglePlayer(gs));
    }
}

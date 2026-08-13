// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.reflect.ReflectionException;
import com.google.common.collect.ImmutableList;
import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static de.sesu8642.feudaltactics.lib.gamestate.Unit.UnitTypes.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Tests for GameStateHelper class related to detecting potentially forgotten kingdoms.
 */
class GameStateHelperForgottenKingdomTest {

    private static GameState createBaseGameState() {
        final GameState gameState = new GameState();
        final Player player1 = new Player(0, Type.LOCAL_BOT);
        final Player player2 = new Player(1, Type.LOCAL_PLAYER);
        final List<Player> players = new ArrayList<>(ImmutableList.of(player1, player2));
        GameStateHelper.initializeMap(gameState, players, 5, 10F, 0F, 1L);
        return gameState;
    }

    @Test
    void previouslyActiveKingdom_isNotForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setWasActiveInCurrentTurn(true);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertFalse(forgottenKingdom.isPresent());
    }

    @Test
    void unprotectedTileButCanAffordPeasant_isForgotten() {
        final GameState gameState = createBaseGameState();
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertTrue(forgottenKingdom.isPresent());
    }

    @Test
    void unprotectedTileButNoUnitOrEnoughMoney_isNotForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(9);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertFalse(forgottenKingdom.isPresent());
    }

    @Test
    void strength1ProtectedTileButCanAffordOnlyPeasant_isNotForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Unit(PEASANT));
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertFalse(forgottenKingdom.isPresent());
    }

    @Test
    void strength1ProtectedTileButCanAffordSpearman_isForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Unit(PEASANT));
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(20);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertTrue(forgottenKingdom.isPresent());
    }

    @Test
    void strength1ProtectedTileButCanCombineSpearman_isForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Unit(PEASANT));
        gameState.getMap().get(new Vector2(0, -2)).setContent(new Unit(PEASANT));
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(10);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertTrue(forgottenKingdom.isPresent());
    }

    @Test
    void strength2ProtectedTileButCanCombineSpearman_isNotForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Castle());
        gameState.getMap().get(new Vector2(0, -2)).setContent(new Unit(PEASANT));
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(10);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertFalse(forgottenKingdom.isPresent());
    }

    @Test
    void strength2ProtectedTileButCanCombineKnight_isForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Castle());
        gameState.getMap().get(new Vector2(0, -2)).setContent(new Unit(PEASANT));
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(20);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertTrue(forgottenKingdom.isPresent());
    }

    @Test
    void strength2ProtectedTileButCanAffordKnight_isForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Castle());
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(30);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertTrue(forgottenKingdom.isPresent());
    }

    @Test
    void strength3ProtectedTileButCanAffordKnight_isForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Unit(KNIGHT));
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(30);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertTrue(forgottenKingdom.isPresent());
    }

    @Test
    void strength3ProtectedTileButCanAffordBaron_isForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Unit(KNIGHT));
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(40);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertTrue(forgottenKingdom.isPresent());
    }

    @Test
    void strength3ProtectedTileButCanCombineBaron_isForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Unit(KNIGHT));
        gameState.getMap().get(new Vector2(0, -2)).setContent(new Unit(SPEARMAN));
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(20);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertTrue(forgottenKingdom.isPresent());
    }

    @Test
    void strength4ProtectedTileButIsRich_isNotForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Unit(BARON));
        gameState.getMap().get(new Vector2(0, -2)).setContent(new Unit(BARON));
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(1000);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertFalse(forgottenKingdom.isPresent());
    }

    @ParameterizedTest
    @ValueSource(classes = {Tree.class, PalmTree.class, Gravestone.class})
    void blockingObjectButCannotAffordUnit_isNotForgotten(Class<TileContent> tileContentClass) throws ReflectionException {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Castle());
        gameState.getMap().get(new Vector2(0, -2)).setContent(ClassReflection.newInstance(tileContentClass));
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(9);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertFalse(forgottenKingdom.isPresent());
    }

    @ParameterizedTest
    @ValueSource(classes = {Tree.class, PalmTree.class, Gravestone.class})
    void blockingObjectButCanAffordPeasant_isForgotten(Class<TileContent> tileContentClass) throws ReflectionException {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Castle());
        gameState.getMap().get(new Vector2(0, -2)).setContent(ClassReflection.newInstance(tileContentClass));
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(10);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertTrue(forgottenKingdom.isPresent());
    }

    @Test
    void emptyTileButCanAffordCastle_isForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Castle());
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(15);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertTrue(forgottenKingdom.isPresent());
    }

    @Test
    void emptyTileButCanNotAffordCastle_isNotForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Castle());
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(14);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertFalse(forgottenKingdom.isPresent());
    }

    @Test
    void noEmptyTileButCanAffordCastle_isNotForgotten() {
        final GameState gameState = createBaseGameState();
        gameState.getMap().get(new Vector2(1, -1)).setContent(new Castle());
        gameState.getMap().get(new Vector2(0, -2)).setContent(new Castle());
        gameState.getMap().get(new Vector2(0, -2)).getKingdom().setSavings(15);
        final Optional<Kingdom> forgottenKingdom = GameStateHelper.getFirstForgottenKingdom(gameState);
        assertFalse(forgottenKingdom.isPresent());
    }

}

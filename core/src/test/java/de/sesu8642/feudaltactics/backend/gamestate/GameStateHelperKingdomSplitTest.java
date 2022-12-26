// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.backend.gamestate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

import de.sesu8642.feudaltactics.ApplicationStub;
import de.sesu8642.feudaltactics.backend.gamestate.Player.Type;
import de.sesu8642.feudaltactics.backend.gamestate.Unit.UnitTypes;

/** Tests for GameStateHelper class related to splitting a kingdom. */
class GameStateHelperKingdomSplitTest {

	Player player1 = new Player(new Color(1, 0, 0, 0), Type.LOCAL_PLAYER);

	Player player2 = new Player(new Color(0, 0, 1, 0), Type.LOCAL_PLAYER);

	List<Player> players = Arrays.asList(player1, player2);

	@BeforeAll
	static void initAll() {
		Gdx.app = new ApplicationStub();
	}

	@ParameterizedTest
	@MethodSource("provideMapContents")
	void conqueringTransformsMapObjects(List<MapObject> mapContentsBefore, int tileIndexToConquer,
			List<MapObject> mapContentsExpected) {

		// create a kingdom of tiles in a straight line filled with the given contents
		// and create another kingdom next to it that attacks
		HexTile tileToConquer = null;
		GameState gameState = new GameState();
		gameState.setPlayers(players);
		gameState.setPlayerTurn(1);
		Kingdom attackedKingdom = new Kingdom(player1);
		Kingdom attackingKingdom = new Kingdom(player2);
		for (int i = 0; i < mapContentsBefore.size(); i++) {
			Vector2 attackedKingdomTilePosition = new Vector2(0, i);
			HexTile attackedKingdomTile = new HexTile(player1, attackedKingdomTilePosition);
			attackedKingdomTile.setKingdom(attackedKingdom);
			attackedKingdomTile.setContent(mapContentsBefore.get(i));
			gameState.getMap().put(attackedKingdomTilePosition, attackedKingdomTile);
			attackedKingdom.getTiles().add(attackedKingdomTile);
			if (i == tileIndexToConquer) {
				tileToConquer = attackedKingdomTile;
			}
			// for each tile, create a tile of the attacking kingdom next to it
			Vector2 attackingKingdomTilePosition = new Vector2(1, i);
			HexTile attackingKingdomTile = new HexTile(player2, attackingKingdomTilePosition);
			attackingKingdomTile.setKingdom(attackingKingdom);
			attackingKingdomTile.setContent(mapContentsBefore.get(i));
			gameState.getMap().put(attackingKingdomTilePosition, attackingKingdomTile);
			attackingKingdom.getTiles().add(attackingKingdomTile);
		}
		gameState.setKingdoms(Stream.of(attackedKingdom, attackingKingdom).collect(Collectors.toList()));

		// prepare a baron for conquering
		Unit conqueringUnit = new Unit(UnitTypes.BARON);
		gameState.setHeldObject(conqueringUnit);
		gameState.setActiveKingdom(attackingKingdom);

		// conquer with a baron
		GameStateHelper.conquer(gameState, tileToConquer);

		// assert the correct contents
		for (int i = 0; i < mapContentsExpected.size(); i++) {
			HexTile tile = gameState.getMap().get(new Vector2(0, i));
			assertEquals(mapContentsExpected.get(i), tile.getContent());
		}
	}

	static Stream<Arguments> provideMapContents() {
		Unit conqueringUnit = new Unit(UnitTypes.BARON);
		conqueringUnit.setCanAct(false);
		return Stream.of(
				// capital and empty tile, conquering capital --> capital destroyed
				Arguments.of(Arrays.asList(new Capital(), null), 0, Arrays.asList(conqueringUnit, null)),

				// capital and empty tile, conquering empty tile --> capital becomes (palm) tree
				Arguments.of(Arrays.asList(new Capital(), null), 1, Arrays.asList(new PalmTree(), conqueringUnit)),

				// capital and unit, conquering capital --> unit becomes gravestone
				Arguments.of(Arrays.asList(new Capital(), new Unit(UnitTypes.PEASANT)), 0,
						Arrays.asList(conqueringUnit, new Gravestone())),

				// capital and tree, conquering capital --> tree stays
				Arguments.of(Arrays.asList(new Capital(), new Tree()), 0, Arrays.asList(conqueringUnit, new Tree())),

				// capital and palm, conquering capital --> palm stays
				Arguments.of(Arrays.asList(new Capital(), new PalmTree()), 0,
						Arrays.asList(conqueringUnit, new PalmTree())),

				// capital and gravestone, conquering capital --> gravestone stays
				Arguments.of(Arrays.asList(new Capital(), new Gravestone()), 0,
						Arrays.asList(conqueringUnit, new Gravestone())),

				// capital and castle, conquering capital --> castle is destroyed
				Arguments.of(Arrays.asList(new Capital(), new Castle()), 0, Arrays.asList(conqueringUnit, null)),

				// capital, empty tile, empty tile, conquering capital --> capital moves one
				// tile
				Arguments.of(Arrays.asList(new Capital(), null, null), 0,
						Arrays.asList(conqueringUnit, new Capital(), null)),

				// capital, unit, empty tile, conquering capital --> capital moves to the right
				// tile
				Arguments.of(Arrays.asList(new Capital(), new Unit(UnitTypes.SPEARMAN), null), 0,
						Arrays.asList(conqueringUnit, new Unit(UnitTypes.SPEARMAN), new Capital())),

				// capital, unit, tree, conquering capital --> capital moves to where the unit
				// was
				Arguments.of(Arrays.asList(new Capital(), new Unit(UnitTypes.SPEARMAN), new Tree()), 0,
						Arrays.asList(conqueringUnit, new Capital(), new Tree())),

				// capital, empty tile, empty tile, conquering empty tile in the middle -->
				// capital becomes tree
				Arguments.of(Arrays.asList(new Capital(), null, null), 1,
						Arrays.asList(new PalmTree(), conqueringUnit, null)),

				// empty tile, capital, empty tile, conquering capital in the middle --> capital
				// destroyed
				Arguments.of(Arrays.asList(null, new Capital(), null), 1, Arrays.asList(null, conqueringUnit, null)));
	}

}
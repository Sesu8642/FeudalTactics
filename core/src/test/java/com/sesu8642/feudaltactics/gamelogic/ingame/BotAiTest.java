package com.sesu8642.feudaltactics.gamelogic.ingame;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.sesu8642.feudaltactics.ApplicationStub;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Castle;
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameState;
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameStateHelper;
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameStateSerializer;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Player;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Player.Type;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Unit;
import com.sesu8642.feudaltactics.gamelogic.ingame.BotAi.Intelligence;

/** Tests for BotAi class. */
class BotAiTest {

	private BotAi systemUnderTest = new BotAi();

	@BeforeAll
	static void init() {
		Gdx.app = new ApplicationStub();
	}

	@ParameterizedTest
	@MethodSource("provideMapParameters")
	void botsDoNotGainOrLoseValueDuringTurn(BotAi.Intelligence botIntelligence, Float landMass, Float density,
			Long seed) {
		GameState gameState = createGameState(landMass, density, seed);

		for (int i = 1; i <= 1000; i++) {
			if (gameState.getKingdoms().size() == 1) {
				return;
			}
			int activePlayerCapitalBeforeTurn = calculateActivePlayerCapital(gameState);
			String beforeJson = gameStateToJson(gameState);
			gameState = systemUnderTest.doTurn(gameState, botIntelligence);
			String afterJson = gameStateToJson(gameState);
			int activePlayerCapitalAfterTurn = calculateActivePlayerCapital(gameState);
			if (gameState.getKingdoms().size() > 1) {
				if (activePlayerCapitalAfterTurn != activePlayerCapitalBeforeTurn) {
					System.out.println("-------------------");
					System.out.println("value change at turn " + i);
					System.out.println("value before: " + activePlayerCapitalBeforeTurn);
					System.out.println("value after: " + activePlayerCapitalAfterTurn);
					System.out.println(beforeJson);
					System.out.println(afterJson);
					System.out.println("-------------------");
				}
				assertEquals(activePlayerCapitalAfterTurn, activePlayerCapitalBeforeTurn);
			}
			GameStateHelper.endTurn(gameState);
		}
		// game did not terminate; log game state as json for debugging
		System.out.println("GameState as JSON is " + gameStateToJson(gameState));
		throw new AssertionError("Game did not terminate.");
	}

	@ParameterizedTest
	@MethodSource("provideMapParameters")
	void botsActConsistentWithTheSameSeed(BotAi.Intelligence botIntelligence, Float landMass, Float density,
			Long seed) {
		GameState gameState1 = createGameState(landMass, density, seed);
		GameState gameState2 = createGameState(landMass, density, seed);

		for (int i = 1; i <= 1000; i++) {
			if (gameState1.getKingdoms().size() == 1) {
				return;
			}
			gameState1 = systemUnderTest.doTurn(gameState1, botIntelligence);
			gameState2 = systemUnderTest.doTurn(gameState2, botIntelligence);
			assertEquals(gameState1, gameState2);

			GameStateHelper.endTurn(gameState1);
			GameStateHelper.endTurn(gameState2);
		}
	}

	private String gameStateToJson(GameState gameState) {
		Json json = new Json(OutputType.json);
		json.setSerializer(GameState.class, new GameStateSerializer());
		return json.toJson(gameState, GameState.class);
	}

	/**
	 * Calculates the value of all assets and savings the active player owns.
	 * 
	 * @param gameState gamestate to calculate based on
	 * @return sum of player capital
	 */
	private int calculateActivePlayerCapital(GameState gameState) {
		// first sum up the value of all map objects the player owns
		int result = gameState.getMap().getTiles().values().stream()
				.filter(tile -> tile.getPlayer().equals(gameState.getActivePlayer())).mapToInt(tile -> {
					if (tile.getContent() == null) {
						return 0;
					}
					if (Unit.class.isAssignableFrom(tile.getContent().getClass())) {
						return ((Unit) tile.getContent()).getStrength() * Unit.COST;
					}
					if (Castle.class.isAssignableFrom(tile.getContent().getClass())) {
						return Castle.COST;
					}
					return 0;
				}).sum();
		// add cash savings
		result += gameState.getKingdoms().stream()
				.filter(kingdom -> kingdom.getPlayer().equals(gameState.getActivePlayer()))
				.mapToInt(kingdom -> kingdom.getSavings()).sum();
		return result;
	}

	private GameState createGameState(Float landMass, Float density, Long seed) {
		List<Player> players = new ArrayList<>();
		players.add(new Player(new Color(0.2F, 0.45F, 0.8F, 1), Type.LOCAL_BOT));
		players.add(new Player(new Color(0.75F, 0.5F, 0F, 1), Type.LOCAL_BOT));
		players.add(new Player(new Color(1F, 0.67F, 0.67F, 1), Type.LOCAL_BOT));
		players.add(new Player(new Color(1F, 1F, 0F, 1), Type.LOCAL_PLAYER));
		players.add(new Player(new Color(1F, 1F, 1F, 1), Type.LOCAL_BOT));
		players.add(new Player(new Color(0F, 1F, 0F, 1), Type.LOCAL_BOT));
		GameState result = new GameState();
		GameStateHelper.initializeMap(result, players, landMass, density, 0.2F, seed);
		return result;
	}

	static Stream<Arguments> provideMapParameters() {
		return Stream.of(Arguments.of(Intelligence.DUMB, 12F, 0F, 1L), Arguments.of(Intelligence.DUMB, 100F, -3F, 2L),
				Arguments.of(Intelligence.DUMB, 200F, 3F, 3L), Arguments.of(Intelligence.DUMB, 250F, 1F, 4L),
				Arguments.of(Intelligence.DUMB, 250F, -3F, 5L), Arguments.of(Intelligence.MEDIUM, 12F, 0F, 6L),
				Arguments.of(Intelligence.MEDIUM, 100F, -3F, 7L), Arguments.of(Intelligence.MEDIUM, 200F, 3F, 8L),
				Arguments.of(Intelligence.MEDIUM, 250F, 1F, 9L), Arguments.of(Intelligence.MEDIUM, 250F, -3F, 10L),
				Arguments.of(Intelligence.SMART, 12F, 0F, 11L), Arguments.of(Intelligence.SMART, 100F, -3F, 12L),
				Arguments.of(Intelligence.SMART, 200F, 3F, 13L), Arguments.of(Intelligence.SMART, 250F, 1F, 14L),
				Arguments.of(Intelligence.SMART, 250F, -3F, 15L));
	}

}

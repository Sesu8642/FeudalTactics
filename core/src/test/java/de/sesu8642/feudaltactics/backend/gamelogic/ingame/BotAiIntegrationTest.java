// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.backend.gamelogic.ingame;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.ApplicationStub;
import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.Capital;
import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.Castle;
import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.GameState;
import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.GameStateSerializer;
import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.Kingdom;
import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.Player;
import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.Unit;
import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.Player.Type;
import de.sesu8642.feudaltactics.backend.gamelogic.ingame.BotAi;
import de.sesu8642.feudaltactics.backend.gamelogic.ingame.BotAi.Intelligence;
import de.sesu8642.feudaltactics.backend.gamelogic.ingame.BotAi.Speed;
import de.sesu8642.feudaltactics.events.BotTurnFinishedEvent;

/** Tests for BotAi class. */
@ExtendWith(MockitoExtension.class)
class BotAiIntegrationTest {

	@Mock
	EventBus eventBusStub;

	@InjectMocks
	private BotAi systemUnderTest;

	@BeforeAll
	static void initAll() {
		Gdx.app = new ApplicationStub();
	}

	private GameState resultingGameState;

	@BeforeEach
	void init() {
		// set tick delay to 0 have it run as fast as possible
		systemUnderTest.setCurrentSpeed(Speed.INSTANT);
		doAnswer(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) {
				BotTurnFinishedEvent event = invocation.getArgument(0);
				resultingGameState = event.getGameState();
				return null;
			}
		}).when(eventBusStub).post(any(BotTurnFinishedEvent.class));
	}

	@ParameterizedTest
	@MethodSource("provideMapParameters")
	void botsDoNotGainOrLoseValueDuringTurn(BotAi.Intelligence botIntelligence, Float landMass, Float density,
			Long seed) throws Exception {
		GameState gameState = createGameState(landMass, density, seed);

		for (int i = 1; i <= 1000; i++) {
			if (gameState.getKingdoms().size() == 1) {
				return;
			}
			int activePlayerCapitalBeforeTurn = calculateActivePlayerCapital(gameState);
			String beforeJson = gameStateToJson(gameState);
			systemUnderTest.doTurn(gameState, botIntelligence);
			gameState = resultingGameState;
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
	void botsActConsistentWithTheSameSeed(BotAi.Intelligence botIntelligence, Float landMass, Float density, Long seed)
			throws Exception {
		GameState gameState1 = createGameState(landMass, density, seed);
		GameState gameState2 = createGameState(landMass, density, seed);

		for (int i = 1; i <= 1000; i++) {
			if (gameState1.getKingdoms().size() == 1) {
				return;
			}
			systemUnderTest.doTurn(gameState1, botIntelligence);
			gameState1 = resultingGameState;
			systemUnderTest.doTurn(gameState2, botIntelligence);
			gameState2 = resultingGameState;
			assertEquals(gameState1, gameState2);

			GameStateHelper.endTurn(gameState1);
			GameStateHelper.endTurn(gameState2);
		}
	}

	@ParameterizedTest
	@MethodSource("provideMapParameters")
	void gameStateStaysConsistent(BotAi.Intelligence botIntelligence, Float landMass, Float density, Long seed)
			throws Exception {
		GameState gameState = createGameState(landMass, density, seed);

		for (int i = 1; i <= 1000; i++) {
			if (gameState.getKingdoms().size() == 1) {
				return;
			}
			systemUnderTest.doTurn(gameState, botIntelligence);
			if (gameState.getKingdoms().size() > 1) {
				assertIntegreKingdomTileLinks(gameState);
				assertEveryKingdomHasExactlyOneCapital(gameState);
			}
			GameStateHelper.endTurn(gameState);
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
		int result = gameState.getMap().values().stream()
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
		players.add(new Player(new Color(1F, 1F, 0F, 1), Type.LOCAL_BOT));
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

	static void assertIntegreKingdomTileLinks(GameState gameState) {
		// the kingdom of each tile contains the tile
		gameState.getMap().values().stream().filter(tile -> tile.getKingdom() != null)
				.forEach(tile -> assertTrue(tile.getKingdom().getTiles().contains(tile)));
		// every tile in a kingdom knows that it is part of that kingdom
		for (Kingdom kingdom : gameState.getKingdoms()) {
			kingdom.getTiles().forEach(tile -> {
				assertSame(tile.getKingdom(), kingdom);
			});
		}
	}

	static void assertEveryKingdomHasExactlyOneCapital(GameState gameState) {
		for (Kingdom kingdom : gameState.getKingdoms()) {
			long amountCapitals = kingdom.getTiles().stream().filter(
					tile -> tile.getContent() != null && Capital.class.isAssignableFrom(tile.getContent().getClass()))
					.count();
			assertEquals(1, amountCapitals);
		}
	}

}

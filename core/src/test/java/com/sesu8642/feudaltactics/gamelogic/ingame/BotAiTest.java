package com.sesu8642.feudaltactics.gamelogic.ingame;

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
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameState;
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameStateHelper;
import com.sesu8642.feudaltactics.gamelogic.gamestate.GameStateSerializer;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Player;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Player.Type;
import com.sesu8642.feudaltactics.gamelogic.ingame.BotAi.Intelligence;

/** Tests for BotAi class. */
public class BotAiTest {

	private BotAi systemUnderTest = new BotAi();

	@BeforeAll
	static void init() {
		Gdx.app = new ApplicationStub();
	}

	@ParameterizedTest
	@MethodSource("provideMapParameters")
	void gamesWithBotsTerminate(BotAi.Intelligence botIntelligence, Float landMass, Float density, Long seed) {
		GameState gameState = createGameState(landMass, density, seed);

		for (int i = 1; i <= 1000; i++) {
			if (gameState.getKingdoms().size() == 1) {
				return;
			}
			gameState = systemUnderTest.doTurn(gameState, botIntelligence);
			GameStateHelper.endTurn(gameState);
		}
		// log game state as json for debugging
		String jsonString = null;
		Json json = new Json(OutputType.json);
		json.setSerializer(GameState.class, new GameStateSerializer());
		jsonString = json.toJson(gameState, GameState.class);
		System.out.println("GameState as JSON is " + jsonString);
		throw new AssertionError("Game did not terminate.");
	}

	private GameState createGameState(Float landMass, Float density, Long seed) {
		List<Player> players = new ArrayList<>();
		players.add(new Player(new Color(0, 0, 0, 1), Type.LOCAL_BOT));
		players.add(new Player(new Color(0, 0, 1, 0), Type.LOCAL_BOT));
		players.add(new Player(new Color(0, 0, 1, 1), Type.LOCAL_BOT));
		players.add(new Player(new Color(0, 1, 0, 0), Type.LOCAL_PLAYER));
		players.add(new Player(new Color(0, 1, 0, 1), Type.LOCAL_BOT));
		players.add(new Player(new Color(0, 1, 1, 0), Type.LOCAL_BOT));
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

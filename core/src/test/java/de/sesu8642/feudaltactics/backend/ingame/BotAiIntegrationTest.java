// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.backend.ingame;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.ApplicationStub;
import de.sesu8642.feudaltactics.events.BotTurnFinishedEvent;
import de.sesu8642.feudaltactics.lib.gamestate.*;
import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;
import de.sesu8642.feudaltactics.lib.ingame.botai.BotAi;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.preferences.MainGamePreferences;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.when;

/**
 * Tests for BotAi class.
 */
@ExtendWith(MockitoExtension.class)
class BotAiIntegrationTest {

    @Mock
    EventBus eventBusStub;

    @Mock
    MainPreferencesDao prefsDaoStub;

    // do not wait in tests
    MainGamePreferences stubPreferences = new MainGamePreferences(false, false, "en");

    @InjectMocks
    private BotAi systemUnderTest;
    private GameState resultingGameState;

    @BeforeAll
    static void initAll() {
        Gdx.app = new ApplicationStub();
    }

    static Stream<Arguments> provideMapParameters() {
        return Stream.of(Arguments.of(Intelligence.LEVEL_1, 12F, 0F, 1L),
            Arguments.of(Intelligence.LEVEL_1, 100F, -3F, 2L), Arguments.of(Intelligence.LEVEL_1, 200F, 3F, 3L),
            Arguments.of(Intelligence.LEVEL_1, 250F, 1F, 4L), Arguments.of(Intelligence.LEVEL_1, 250F, -3F, 5L),
            Arguments.of(Intelligence.LEVEL_2, 12F, 0F, 6L), Arguments.of(Intelligence.LEVEL_2, 100F, -3F, 7L),
            Arguments.of(Intelligence.LEVEL_2, 200F, 3F, 8L), Arguments.of(Intelligence.LEVEL_2, 250F, 1F, 9L),
            Arguments.of(Intelligence.LEVEL_2, 250F, -3F, 10L), Arguments.of(Intelligence.LEVEL_2, 12F, 0F, 6L),
            Arguments.of(Intelligence.LEVEL_2, 100F, -3F, 7L), Arguments.of(Intelligence.LEVEL_2, 200F, 3F, 8L),
            Arguments.of(Intelligence.LEVEL_2, 250F, 1F, 9L), Arguments.of(Intelligence.LEVEL_2, 250F, -3F, 10L),
            Arguments.of(Intelligence.LEVEL_4, 12F, 0F, 11L), Arguments.of(Intelligence.LEVEL_4, 100F, -3F, 12L),
            Arguments.of(Intelligence.LEVEL_4, 200F, 3F, 13L), Arguments.of(Intelligence.LEVEL_4, 250F, 1F, 14L),
            Arguments.of(Intelligence.LEVEL_4, 250F, -3F, 15L));
    }

    static List<Arguments> provideMapParametersAndHashes() {
        final List<List<Integer>> gameProgessHashes = GameStateHashes.get();

        // add the hashes to the existing arguments
        final List<Arguments> existingMapParameterArgs = provideMapParameters().collect(Collectors.toList());
        final List<Arguments> result = new ArrayList<>();
        for (int mapParameterArgsIndex = 0; mapParameterArgsIndex < existingMapParameterArgs.size(); mapParameterArgsIndex++) {
            final List<Object> newArguments =
                Arrays.stream(existingMapParameterArgs.get(mapParameterArgsIndex).get()).collect(Collectors.toList());
            newArguments.add(gameProgessHashes.get(mapParameterArgsIndex));
            result.add(Arguments.of(newArguments.toArray()));
        }
        return result;
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
            final long amountCapitals = kingdom.getTiles().stream().filter(
                    tile -> tile.getContent() != null && Capital.class.isAssignableFrom(tile.getContent().getClass()))
                .count();
            assertEquals(1, amountCapitals);
        }
    }

    @BeforeEach
    void init() {
        when(prefsDaoStub.getMainPreferences()).thenReturn(stubPreferences);
        doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) {
                final BotTurnFinishedEvent event = invocation.getArgument(0);
                resultingGameState = event.getGameState();
                return null;
            }
        }).when(eventBusStub).post(any(BotTurnFinishedEvent.class));
    }

    @ParameterizedTest
    @MethodSource("provideMapParameters")
    void botsDoNotGainOrLoseValueDuringTurn(Intelligence botIntelligence, Float landMass, Float density, Long seed)
        throws Exception {
        GameState gameState = createGameState(landMass, density, seed);

        for (int i = 1; i <= 1000; i++) {
            if (gameState.getKingdoms().size() == 1) {
                return;
            }
            final int activePlayerCapitalBeforeTurn = calculateActivePlayerCapital(gameState);
            final String beforeJson = gameStateToJson(gameState);
            systemUnderTest.doTurn(gameState, botIntelligence);
            gameState = resultingGameState;
            final String afterJson = gameStateToJson(gameState);
            final int activePlayerCapitalAfterTurn = calculateActivePlayerCapital(gameState);
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
    @MethodSource("provideMapParametersAndHashes")
    void botsActConsistentWithTheSameGame(Intelligence botIntelligence, Float landMass, Float density, Long seed,
                                          List<Integer> expectedHashes)
        throws Exception {
        final GameState gameState = createGameState(landMass, density, seed);
        final List<Integer> actualHashes = new ArrayList<>();
        boolean mismatchDetected = false;

        for (int i = 0; i <= 1000; i++) {
            if (gameState.getKingdoms().size() == 1) {
                break;
            }
            systemUnderTest.doTurn(gameState, botIntelligence);
            actualHashes.add(gameState.hashCode());
            if (!mismatchDetected && (expectedHashes.size() <= i || !expectedHashes.get(i).equals(gameState.hashCode()))) {
                mismatchDetected = true;
                System.out.println("Mismatching gameState as JSON is " + gameStateToJson(gameState));
            }
            GameStateHelper.endTurn(gameState);
        }
        assertEquals(expectedHashes, actualHashes, "GameState hashes are not as expected. This means that either " +
            "there was a code change and the hashes need to be updated or there is some inconsistency across devices.");
    }

    @ParameterizedTest
    @MethodSource("provideMapParameters")
    void gameStateStaysIntegre(Intelligence botIntelligence, Float landMass, Float density, Long seed)
        throws Exception {
        final GameState gameState = createGameState(landMass, density, seed);

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
        final Json json = new Json(OutputType.json);
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
                    return tile.getContent().getStrength() * Unit.COST;
                }
                if (Castle.class.isAssignableFrom(tile.getContent().getClass())) {
                    return Castle.COST;
                }
                return 0;
            }).sum();
        // add cash savings
        result += gameState.getKingdoms().stream()
            .filter(kingdom -> kingdom.getPlayer().equals(gameState.getActivePlayer()))
            .mapToInt(Kingdom::getSavings).sum();
        return result;
    }

    private GameState createGameState(Float landMass, Float density, Long seed) {
        final List<Player> players = new ArrayList<>();
        players.add(new Player(0, Type.LOCAL_BOT));
        players.add(new Player(1, Type.LOCAL_BOT));
        players.add(new Player(2, Type.LOCAL_BOT));
        players.add(new Player(3, Type.LOCAL_BOT));
        players.add(new Player(4, Type.LOCAL_BOT));
        players.add(new Player(5, Type.LOCAL_BOT));
        final GameState result = new GameState();
        GameStateHelper.initializeMap(result, players, landMass, density, 0.2F, seed);
        return result;
    }

}

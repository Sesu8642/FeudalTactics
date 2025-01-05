// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.editor;

import com.badlogic.gdx.math.Vector2;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.events.GameStateChangeEvent;
import de.sesu8642.feudaltactics.ingame.AutoSaveRepository;
import de.sesu8642.feudaltactics.lib.gamestate.*;
import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

/**
 * Controller for the map editor.
 */
@Singleton
public class EditorController {

    private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

    private final EventBus eventBus;
    private final AutoSaveRepository autoSaveRepo;
    private final ScenarioGameStateLoader scenarioGameStateLoader;
    private GameState gameState;

    private TileContent heldTileContent;
    private Integer heldTilePlayerIndex;

    /**
     * Constructor.
     *
     * @param eventBus event bus
     */
    @Inject
    public EditorController(EventBus eventBus, AutoSaveRepository autoSaveRepo,
                            ScenarioGameStateLoader scenarioGameStateLoader) {
        this.eventBus = eventBus;
        this.autoSaveRepo = autoSaveRepo;
        this.scenarioGameStateLoader = scenarioGameStateLoader;
        gameState = new GameState();
    }

    /**
     * Generates an empty map.
     */
    public void generateEmptyGameState() {
        gameState = new GameState();
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player(0, Type.LOCAL_PLAYER));
        for (int i = 1; i < 6; i++) {
            players.add(new Player(i, Type.LOCAL_BOT));
        }
        GameStateHelper.initializeMap(gameState, players, 0, 0, 0F, null);
        eventBus.post(new GameStateChangeEvent(gameState));
    }

    public void placeHeldObject(Vector2 hexCoords) {
        if (heldTilePlayerIndex != null) {
            Player newPlayer =
                    gameState.getPlayers().stream()
                            .filter(player -> player.getPlayerIndex() == heldTilePlayerIndex).findAny().get();
            GameStateHelper.placeTile(gameState, hexCoords, newPlayer);
        } else if (heldTileContent != null) {
            HexTile tile = gameState.getMap().get(hexCoords);
            if (tile != null) {
                logger.debug("placing object {} on tile at position {}", heldTileContent.getClass().getName(),
                        hexCoords);
                GameStateHelper.placeTileContent(gameState, tile, heldTileContent);
            }
        } else {
            HexTile tile = gameState.getMap().get(hexCoords);
            if (tile != null) {
                logger.debug("deleting tile at position {}", hexCoords);
                GameStateHelper.deleteTile(gameState, tile);
            }
        }
        eventBus.post(new GameStateChangeEvent(gameState));
        autoSaveRepo.autoSaveFullGameState(gameState);
    }

    public void updateHandContent(TileContent heldTileContent, Integer heldTilePlayerIndex) {
        this.heldTileContent = heldTileContent;
        this.heldTilePlayerIndex = heldTilePlayerIndex;
    }

    public void initializeScenario(Intelligence botIntelligence, ScenarioMap scenarioMap) {
        gameState = scenarioGameStateLoader.loadScenarioGameState(scenarioMap);

        gameState.setScenarioMap(scenarioMap);
        eventBus.post(new GameStateChangeEvent(gameState));
    }

    public GameState getGameState() {
        return gameState;
    }

}
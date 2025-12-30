// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import com.badlogic.gdx.math.Vector2;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Contains all information about a running game.
 **/
@NoArgsConstructor
public class GameState {

    @Getter
    @Setter
    private List<Player> players = new ArrayList<>();
    @Getter
    @Setter
    private Player winner;
    @Getter
    @Setter
    private Integer winningRound;
    @Getter
    @Setter
    private int playerTurn = 0;
    // need a map with fix iteration order to avoid randomness
    @Getter
    @Setter
    private LinkedHashMap<Vector2, HexTile> map = new LinkedHashMap<>();
    @Getter
    @Setter
    private List<Kingdom> kingdoms;
    @Getter
    @Setter
    private Kingdom activeKingdom;
    @Getter
    @Setter
    private TileContent heldObject;
    @Getter
    @Setter
    private Intelligence botIntelligence = Intelligence.LEVEL_1;
    @Getter
    @Setter
    private Long seed;
    @Getter
    @Setter
    private int objectiveProgress = 0;
    @Getter
    @Setter
    private ScenarioMap scenarioMap = ScenarioMap.NONE;

    /**
     * A round consists of one turn per player. Not 0-based as it might be displayed to the player.
     */
    @Getter
    @Setter
    private int round = 1;

    public Player getActivePlayer() {
        return players.get(playerTurn);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final GameState gameState = (GameState) o;
        return playerTurn == gameState.playerTurn && objectiveProgress == gameState.objectiveProgress && round == gameState.round && Objects.equals(players, gameState.players) && Objects.equals(winner, gameState.winner) && Objects.equals(winningRound, gameState.winningRound) && Objects.equals(map, gameState.map) && Objects.equals(kingdoms, gameState.kingdoms) && Objects.equals(activeKingdom, gameState.activeKingdom) && Objects.equals(heldObject, gameState.heldObject) && botIntelligence == gameState.botIntelligence && Objects.equals(seed, gameState.seed) && scenarioMap == gameState.scenarioMap;
    }

    @Override
    public int hashCode() {
        // calculating with enum strings because the hashcode must be consistent across runs
        return Objects.hash(players, winner, winningRound, playerTurn, map, kingdoms, activeKingdom, heldObject,
            botIntelligence.toString(), seed, objectiveProgress, scenarioMap.toString(), round);
    }

}

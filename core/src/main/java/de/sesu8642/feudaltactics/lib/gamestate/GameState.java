// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import com.badlogic.gdx.math.Vector2;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

/**
 * Contains all information about a running game.
 **/
public class GameState {

    private List<Player> players = new ArrayList<>();
    private Player winner = null;
    private Integer winningRound = null;
    private int playerTurn = 0;
    // need a map with fix iteration order to avoid randomness
    private LinkedHashMap<Vector2, HexTile> map = new LinkedHashMap<>();
    private List<Kingdom> kingdoms;
    private Kingdom activeKingdom = null;
    private TileContent heldObject = null;
    private Intelligence botIntelligence = Intelligence.LEVEL_1;
    private Long seed;
    private int objectiveProgress = 0;
    private ScenarioMap scenarioMap = ScenarioMap.NONE;

    /**
     * A round consists of one turn per player. Not 0-based as it might be displayed to the player.
     */
    private int round = 1;

    public GameState() {
        // no fields must be set on construction
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public Player getWinner() {
        return winner;
    }

    public void setWinner(Player winner) {
        this.winner = winner;
    }

    public Integer getWinningRound() {
        return winningRound;
    }

    public void setWinningRound(Integer winningRound) {
        this.winningRound = winningRound;
    }

    public int getPlayerTurn() {
        return playerTurn;
    }

    public void setPlayerTurn(int playerTurn) {
        this.playerTurn = playerTurn;
    }

    public LinkedHashMap<Vector2, HexTile> getMap() {
        return map;
    }

    public void setMap(LinkedHashMap<Vector2, HexTile> map) {
        this.map = map;
    }

    public List<Kingdom> getKingdoms() {
        return kingdoms;
    }

    public void setKingdoms(List<Kingdom> kingdoms) {
        this.kingdoms = kingdoms;
    }

    public Kingdom getActiveKingdom() {
        return activeKingdom;
    }

    public void setActiveKingdom(Kingdom activeKingdom) {
        this.activeKingdom = activeKingdom;
    }

    public TileContent getHeldObject() {
        return heldObject;
    }

    public void setHeldObject(TileContent heldObject) {
        this.heldObject = heldObject;
    }

    public Player getActivePlayer() {
        return players.get(playerTurn);
    }

    public Intelligence getBotIntelligence() {
        return botIntelligence;
    }

    public void setBotIntelligence(Intelligence botIntelligence) {
        this.botIntelligence = botIntelligence;
    }

    public Long getSeed() {
        return seed;
    }

    public void setSeed(Long seed) {
        this.seed = seed;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public int getObjectiveProgress() {
        return objectiveProgress;
    }

    public void setObjectiveProgress(int objectiveProgress) {
        this.objectiveProgress = objectiveProgress;
    }

    public ScenarioMap getScenarioMap() {
        return scenarioMap;
    }

    public void setScenarioMap(ScenarioMap scenarioMap) {
        this.scenarioMap = scenarioMap;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        GameState gameState = (GameState) o;
        return playerTurn == gameState.playerTurn && objectiveProgress == gameState.objectiveProgress && round == gameState.round && Objects.equals(players, gameState.players) && Objects.equals(winner, gameState.winner) && Objects.equals(winningRound, gameState.winningRound) && Objects.equals(map, gameState.map) && Objects.equals(kingdoms, gameState.kingdoms) && Objects.equals(activeKingdom, gameState.activeKingdom) && Objects.equals(heldObject, gameState.heldObject) && botIntelligence == gameState.botIntelligence && Objects.equals(seed, gameState.seed) && scenarioMap == gameState.scenarioMap;
    }

    @Override
    public int hashCode() {
        return Objects.hash(players, winner, winningRound, playerTurn, map, kingdoms, activeKingdom, heldObject,
                botIntelligence, seed, objectiveProgress, scenarioMap, round);
    }

}

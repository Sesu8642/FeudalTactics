// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import java.util.Objects;

/**
 * A human or bot player participating in a game.
 **/
public class Player {

    private int playerIndex;
    private Type type;
    private boolean defeated = false;

    public Player() {
    }

    public Player(int playerIndex, Type type) {
        this.playerIndex = playerIndex;
        this.type = type;
    }

    /**
     * Constructor.
     *
     * @param playerIndex index to differentiate players, e.g. to assign colors
     * @param defeated    whether this player is defeated
     * @param type        player type
     */
    public Player(int playerIndex, Type type, boolean defeated) {
        this.playerIndex = playerIndex;
        this.type = type;
        this.defeated = defeated;
    }

    /**
     * Returns a deep copy of the original.
     *
     * @return copy
     */
    public static Player copyOf(Player original) {
        return new Player(original.getPlayerIndex(), original.getType(), original.isDefeated());
    }

    public int getPlayerIndex() {
        return playerIndex;
    }

    public void setPlayerIndex(int playerIndex) {
        this.playerIndex = playerIndex;
    }

    public boolean isDefeated() {
        return defeated;
    }

    public void setDefeated(boolean defeated) {
        this.defeated = defeated;
    }

    public Type getType() {
        return type;
    }

    @Override
    public int hashCode() {
        // calculating with enum strings because the hashcode must be consistent across
        // runs
        return Objects.hash(defeated, playerIndex, type.toString());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Player other = (Player) obj;
        return defeated == other.defeated && playerIndex == other.playerIndex && type == other.type;
    }

    @Override
    public String toString() {
        return "Player [playerIndex=" + playerIndex + ", type=" + type + ", defeated=" + defeated + "]";
    }

    /**
     * Type of a player.
     **/
    public enum Type {
        LOCAL_PLAYER, LOCAL_BOT, REMOTE
    }

}

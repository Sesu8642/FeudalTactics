// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

/**
 * A human or bot player participating in a game.
 **/
@NoArgsConstructor
public class Player {

    @Getter
    @Setter
    private int playerIndex;
    @Getter
    private Type type;
    @Getter
    @Setter
    private Integer roundOfDefeat;

    /**
     * Constructor.
     *
     * @param playerIndex index to differentiate players, e.g. to assign colors
     * @param type        player type
     */
    public Player(int playerIndex, Type type) {
        this.playerIndex = playerIndex;
        this.type = type;
    }

    /**
     * Constructor.
     *
     * @param playerIndex index to differentiate players, e.g. to assign colors
     * @param type        player type
     */
    public Player(int playerIndex, Type type, Integer roundOfDefeat) {
        this.playerIndex = playerIndex;
        this.type = type;
        this.roundOfDefeat = roundOfDefeat;
    }

    /**
     * Returns a deep copy of the original.
     *
     * @return copy
     */
    public static Player copyOf(Player original) {
        return new Player(original.getPlayerIndex(), original.getType(), original.getRoundOfDefeat());
    }

    public boolean isDefeated() {
        return roundOfDefeat != null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Player player = (Player) o;
        return playerIndex == player.playerIndex && type == player.type && Objects.equals(roundOfDefeat,
            player.roundOfDefeat);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerIndex, type, roundOfDefeat);
    }

    @Override
    public String toString() {
        return "Player{" +
            "playerIndex=" + playerIndex +
            ", type=" + type +
            ", roundOfDefeat=" + roundOfDefeat +
            '}';
    }

    /**
     * Type of a player.
     **/
    public enum Type {
        LOCAL_PLAYER, LOCAL_BOT, REMOTE
    }

}

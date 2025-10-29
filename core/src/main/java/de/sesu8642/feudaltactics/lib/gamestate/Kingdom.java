// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * Group of connected tiles that belong to the same player.
 **/
@NoArgsConstructor
public class Kingdom {

    // need a list to have consistent iteration order; LinkedHashSet doesn't work
    // because the tiles can change
    @Getter
    @Setter
    private List<HexTile> tiles = new ArrayList<>();
    @Getter
    @Setter
    private Player player;
    @Getter
    @Setter
    private int savings = 0;
    // only used by ai
    @Getter
    @Setter
    private boolean doneMoving = false;
    // for displaying a hint when the player forgets the kingdom
    @Getter
    @Setter
    private boolean wasActiveInCurrentTurn = false;

    public Kingdom(Player player) {
        this.player = player;
    }

    @Override
    public int hashCode() {
        return Objects.hash(doneMoving, player, savings, tiles, wasActiveInCurrentTurn);
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
        Kingdom other = (Kingdom) obj;
        // ignore order of the tiles; not a perfect method but good enough since there
        // shouldnt be duplicates
        return doneMoving == other.doneMoving && Objects.equals(player, other.player) && savings == other.savings
            && tiles.size() == other.tiles.size() && tiles.containsAll(other.tiles)
            && wasActiveInCurrentTurn == other.wasActiveInCurrentTurn;
    }

    @Override
    public String toString() {
        return String.format("Kingdom [tiles=%s, player=%s, savings=%s, doneMoving=%s, wasActiveInCurrentTurn=%s]",
            tiles.stream().map(HexTile::getPosition), player, savings, doneMoving, wasActiveInCurrentTurn);
    }

}

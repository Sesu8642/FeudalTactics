// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Objects;

/**
 * A tile of land on the map.
 **/
@NoArgsConstructor
public class HexTile implements Comparable<HexTile> {

    public static String SPRITE_NAME = "tile_bw";

    @Getter
    @Setter
    private Player player;
    @Getter
    @Setter
    private TileContent content;
    @Getter
    private Kingdom kingdom;
    @Getter
    private Vector2 position;
    @Getter
    @Setter
    private List<HexTile> cachedNeighborTiles;

    public HexTile(Player player, Vector2 position) {
        this.player = player;
        this.position = position;
    }

    /**
     * Setter for kingdom. Also sets the player to the kingdom's owner.
     */
    public void setKingdom(Kingdom kingdom) {
        this.kingdom = kingdom;
        if (kingdom != null) {
            this.player = kingdom.getPlayer();
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(content, player, position);
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
        HexTile other = (HexTile) obj;
        return Objects.equals(content, other.content) && Objects.equals(player, other.player)
            && Objects.equals(position, other.position);
    }

    // compare using the coordinates; a fix order is needed in some places to avoid
    // unwanted randomness
    @Override
    public int compareTo(HexTile o) {
        int result = Float.compare(this.getPosition().x, o.getPosition().x);
        if (result == 0) {
            // both x and y cannot be the same for different tiles
            result = Float.compare(this.getPosition().y, o.getPosition().y);
        }
        return result;
    }

    @Override
    public String toString() {
        return String.format("HexTile [player=%s, content=%s, position=%s]", player, content, position);
    }

}

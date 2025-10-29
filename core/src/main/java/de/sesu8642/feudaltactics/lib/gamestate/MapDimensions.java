// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import com.badlogic.gdx.math.Vector2;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Class containing metadata about the map size and its center.
 **/
public class MapDimensions {
    @Getter
    @Setter
    private Vector2 center;
    @Getter
    @Setter
    private float width;
    @Getter
    @Setter
    private float height;

    @Override
    public int hashCode() {
        return Objects.hash(center, height, width);
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
        MapDimensions other = (MapDimensions) obj;
        return Objects.equals(center, other.center)
            && Float.floatToIntBits(height) == Float.floatToIntBits(other.height)
            && Float.floatToIntBits(width) == Float.floatToIntBits(other.width);
    }

}

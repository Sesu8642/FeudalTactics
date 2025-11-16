// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.renderer;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Representation of the game state for the map renderer. Keeping everything in separate, flat collections is more
 * efficient when rendering.
 */
@Data
public class ItemsToBeRendered {

    final Map<Vector2, DrawTile> tiles = new HashMap<>();

    final Map<Vector2, TextureRegion> nonAnimatedContents = new HashMap<>();

    final Map<Vector2, TextureRegion> darkenedNonAnimatedContents = new HashMap<>();

    final Map<Vector2, Animation<TextureRegion>> animatedContents = new HashMap<>();

    final Map<Vector2, Animation<TextureRegion>> darkenedAnimatedContents = new HashMap<>();

    /**
     * Value: whether the shield should be darkened or not.
     */
    final Map<Vector2, Boolean> shields = new HashMap<>();
    
    final List<Vector2> semitransparentGraveStones = new ArrayList<>();

    final List<Vector2> whiteLineStartPoints = new ArrayList<>();

    final List<Vector2> whiteLineEndPoints = new ArrayList<>();

    final List<Vector2> redLineStartPoints = new ArrayList<>();

    final List<Vector2> redLineEndPoints = new ArrayList<>();

    boolean darkenBeaches;

    static class DrawTile {
        Vector2 mapCoords;
        Color color;
        boolean darken = false;
        boolean topLeftBeach = false;
        boolean topBeach = false;
        boolean topRightBeach = false;
        boolean bottomRightBeach = false;
        boolean bottomBeach = false;
        boolean bottomLeftBeach = false;
    }

}

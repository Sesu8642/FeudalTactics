// SPDX-License-Identifier: GPL-3.0-or-later

/**
 * This file uses some code derived from Amits guide (https://www.redblobgames.com/grids/hexagons/implementation.html).
 * His code is licensed under CC0 as specified in one of his comments on the page as well as the header in the
 * generated java code linked on the page.
 */

package de.sesu8642.feudaltactics.lib.gamestate;

import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Contains functions to get information about tiles. Uses two kinds of
 * coordinates: hex coordinates (Cube coordinates) - every tile has an integer
 * x, y and z coordinate. The z coordinate can and is calculated from the other
 * ones; world coordinates - x and y coordinates on the world map, e.g. where a
 * player clicked.
 **/
public class HexMapHelper {

    public static final float HEX_OUTER_RADIUS = 5;

    // prevent instantiation
    private HexMapHelper() {
        throw new AssertionError();
    }

    /**
     * Converts hex coordinates to world coordinates.
     *
     * @param hexCoords x and y coordinates of the tile
     * @return x and y coordinates of the center of the tile
     */
    public static Vector2 hexCoordsToWorldCoords(Vector2 hexCoords) {
        // https://www.redblobgames.com/grids/hexagons/#hex-to-pixel
        // get third coordinate
        float cubeZ = -hexCoords.x - hexCoords.y;
        // calculate world coordinates
        float worldX = hexCoords.x * HEX_OUTER_RADIUS * 1.5F;
        float worldY = (float) (HEX_OUTER_RADIUS * (Math.sqrt(3) / 2 * hexCoords.x + Math.sqrt(3) * cubeZ));
        return new Vector2(worldX, worldY);
    }

    /**
     * Converts world coordinates to hex coordinates.
     *
     * @param worldCoords x and y coordinates of a point in the world
     * @return x and y coordinates of the tile the point belongs to (or would if
     * there was one)
     */
    public static Vector2 worldCoordsToHexCoords(Vector2 worldCoords) {
        float hexX = (2F / 3 * worldCoords.x) / HEX_OUTER_RADIUS;
        float hexY = (float) ((-1F / 3 * worldCoords.x + Math.sqrt(3) / 3 * worldCoords.y) / HEX_OUTER_RADIUS);
        return roundToHexCoords(new Vector2(hexX, hexY));
    }

    /**
     * Takes fractional hex coordinates and returns the hex coordinates of the tile
     * those are in.
     *
     * @param coords fractional hex coordinates
     * @return rounded hex coordinates
     */
    public static Vector2 roundToHexCoords(Vector2 coords) {
        // https://www.redblobgames.com/grids/hexagons/#rounding
        // get third coordinate
        float cubeZ = -coords.x - coords.y;
        // round
        float x = Math.round(coords.x);
        float y = Math.round(coords.y);
        float z = Math.round(cubeZ);
        // find greatest difference from rounding and re-calculate it from the others
        float diffX = Math.abs(coords.x - x);
        float diffY = Math.abs(coords.y - y);
        float diffZ = Math.abs(cubeZ - z);

        if (diffX > diffY && diffX > diffZ) {
            x = -y - z;
        } else if (diffY < diffZ) {
            z = -x - y;
        }
        return new Vector2(x + 0.0F, z + 0.0F);
    }

    /**
     * Returns the coordinates of all 6 neighbor tiles for the given tile
     * coordinates. Does not check if there are actually tiles on those positions.
     *
     * @param tileCoords coordinates of the center tile
     * @return neighbors' coordinates
     */
    public static List<Vector2> getNeighborCoords(Vector2 tileCoords) {
        ArrayList<Vector2> neighbors = new ArrayList<>();
        neighbors.add(new Vector2(tileCoords.x - 1, tileCoords.y));
        neighbors.add(new Vector2(tileCoords.x, tileCoords.y - 1));
        neighbors.add(new Vector2(tileCoords.x + 1, tileCoords.y - 1));
        neighbors.add(new Vector2(tileCoords.x + 1, tileCoords.y));
        neighbors.add(new Vector2(tileCoords.x, tileCoords.y + 1));
        neighbors.add(new Vector2(tileCoords.x - 1, tileCoords.y + 1));
        return neighbors;
    }

    /**
     * Returns the coordinates of all 12 tiles that are 2 tiles away from the given
     * tile coordinates (neighbors' neighbors). Does not check if there are actually
     * tiles on those positions.
     *
     * @param tileCoords coordinates of the center tile
     * @return neighbors' neighbors neighbor coordinates
     */
    public static List<Vector2> getNeighborsNeighborCoords(Vector2 tileCoords) {
        ArrayList<Vector2> neighborsNeighbors = new ArrayList<>();
        neighborsNeighbors.add(new Vector2(tileCoords.x, tileCoords.y - 2));
        neighborsNeighbors.add(new Vector2(tileCoords.x + 1, tileCoords.y - 2));
        neighborsNeighbors.add(new Vector2(tileCoords.x + 2, tileCoords.y - 2));
        neighborsNeighbors.add(new Vector2(tileCoords.x + 2, tileCoords.y - 1));
        neighborsNeighbors.add(new Vector2(tileCoords.x + 2, tileCoords.y));
        neighborsNeighbors.add(new Vector2(tileCoords.x + 1, tileCoords.y + 1));
        neighborsNeighbors.add(new Vector2(tileCoords.x, tileCoords.y + 2));
        neighborsNeighbors.add(new Vector2(tileCoords.x - 1, tileCoords.y + 2));
        neighborsNeighbors.add(new Vector2(tileCoords.x - 2, tileCoords.y + 2));
        neighborsNeighbors.add(new Vector2(tileCoords.x - 2, tileCoords.y + 1));
        neighborsNeighbors.add(new Vector2(tileCoords.x - 2, tileCoords.y));
        neighborsNeighbors.add(new Vector2(tileCoords.x - 1, tileCoords.y - 1));
        return neighborsNeighbors;
    }

    /**
     * Returns all neighbor tiles for the given tile. May contain null if there are
     * empty neighbor positions.
     *
     * @param tile center tile
     * @return neighbor tiles
     */
    public static List<HexTile> getNeighborTiles(Map<Vector2, HexTile> map, HexTile tile) {
        List<HexTile> cachedNeighbors = tile.getCachedNeighborTiles();
        if (cachedNeighbors == null) {
            cachedNeighbors = getNeighborTiles(map, tile.getPosition());
            tile.setCachedNeighborTiles(cachedNeighbors);
        }
        return cachedNeighbors;
    }

    /**
     * Clears neighbor tile cache for the whole map.
     */
    public static void clearNeighborTileCache(Map<Vector2, HexTile> map) {
        for (HexTile tile : map.values()) {
            tile.setCachedNeighborTiles(null);
        }
    }

    /**
     * Returns all neighbor tiles for the given tile coordinates. May contain null
     * if there are empty neighbor positions.
     *
     * @param tileCoords coordinates of the center tile
     * @return neighbor tiles
     */
    private static List<HexTile> getNeighborTiles(Map<Vector2, HexTile> map, Vector2 tileCoords) {
        List<Vector2> neighborCoords = getNeighborCoords(tileCoords);
        List<HexTile> neighborTiles = new ArrayList<>();
        for (Vector2 coord : neighborCoords) {
            neighborTiles.add(map.get(coord));
        }
        return neighborTiles;
    }

    public static List<HexTile> getNeighborsNeighborTiles(Map<Vector2, HexTile> map, HexTile tile) {
        return getNeighborsNeighborTiles(map, tile.getPosition());
    }

    private static List<HexTile> getNeighborsNeighborTiles(Map<Vector2, HexTile> map, Vector2 tileCoords) {
        List<Vector2> neighborsNeighborCoords = getNeighborsNeighborCoords(tileCoords);
        List<HexTile> neighborsNeighborTiles = new ArrayList<>();
        for (Vector2 coord : neighborsNeighborCoords) {
            neighborsNeighborTiles.add(map.get(coord));
        }
        return neighborsNeighborTiles;
    }

    /**
     * Returns the coordinates of all neighbor tile positions that are empty for the
     * given tile coordinates.
     *
     * @param tileCoords coordinates of the center tile
     * @return empty neighbors' coordinates
     */
    public static List<Vector2> getUnusedNeighborCoords(Map<Vector2, HexTile> map, Vector2 tileCoords) {
        List<Vector2> neighbors = getNeighborCoords(tileCoords);
        List<Vector2> unusedNeighbors = new ArrayList<>();
        for (Vector2 neighbor : neighbors) {
            if (!map.containsKey(neighbor)) {
                unusedNeighbors.add(neighbor);
            }
        }
        return unusedNeighbors;
    }

    /**
     * Returns the dimensions of the current map.
     *
     * @return map dimensions
     */
    public static MapDimensions getMapDimensionsInWorldCoords(Map<Vector2, HexTile> map) {
        // get most extreme map coordinates
        float minWorldX = 0;
        float maxWorldX = 0;
        float minWorldY = 0;
        float maxWorldY = 0;
        for (Vector2 hexCoords : map.keySet()) {
            Vector2 mapCoords = hexCoordsToWorldCoords(hexCoords);
            if (mapCoords.x < minWorldX) {
                minWorldX = mapCoords.x;
            } else if (mapCoords.x > maxWorldX) {
                maxWorldX = mapCoords.x;
            }
            if (mapCoords.y < minWorldY) {
                minWorldY = mapCoords.y;
            } else if (mapCoords.y > maxWorldY) {
                maxWorldY = mapCoords.y;
            }
        }
        // calculate dimensions
        // the world coordinates of the tiles are always the center so this must be
        // adjusted here
        minWorldX -= HEX_OUTER_RADIUS;
        maxWorldX += HEX_OUTER_RADIUS;
        minWorldY -= HEX_OUTER_RADIUS;
        maxWorldY += HEX_OUTER_RADIUS;
        MapDimensions dims = new MapDimensions();
        dims.setWidth(maxWorldX - minWorldX);
        dims.setHeight(maxWorldY - minWorldY);
        dims.setCenter(new Vector2());
        dims.getCenter().x = minWorldX + (dims.getWidth() / 2);
        dims.getCenter().y = minWorldY + (dims.getHeight() / 2);
        return dims;
    }
}

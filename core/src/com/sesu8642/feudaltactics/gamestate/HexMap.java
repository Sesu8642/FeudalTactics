package com.sesu8642.feudaltactics.gamestate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

public class HexMap {

	public static float HEX_OUTER_RADIUS = 5;

	private Map<Vector2, HexTile> tiles;

	public HexMap() {
		this.tiles = new LinkedHashMap<Vector2, HexTile>();
	}

	public Vector2 worldCoordsToHexCoords(Vector2 worldCoords) {
		float hexX = (2F / 3 * worldCoords.x) / HEX_OUTER_RADIUS;
		float hexY = (float) ((-1F / 3 * worldCoords.x + Math.sqrt(3) / 3 * worldCoords.y) / HEX_OUTER_RADIUS);
		return roundToHexCoords(new Vector2(hexX, hexY));
	}

	public Vector2 roundToHexCoords(Vector2 coords) {
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

	public ArrayList<Vector2> getNeighborCoords(Vector2 tileCoords) {
		ArrayList<Vector2> neighbors = new ArrayList<Vector2>();
		neighbors.add(new Vector2(tileCoords.x - 1, tileCoords.y));
		neighbors.add(new Vector2(tileCoords.x, tileCoords.y - 1));
		neighbors.add(new Vector2(tileCoords.x + 1, tileCoords.y - 1));
		neighbors.add(new Vector2(tileCoords.x + 1, tileCoords.y));
		neighbors.add(new Vector2(tileCoords.x, tileCoords.y + 1));
		neighbors.add(new Vector2(tileCoords.x - 1, tileCoords.y + 1));
		return neighbors;
	}

	public ArrayList<Vector2> getNeighborsNeighborCoords(Vector2 tileCoords) {
		ArrayList<Vector2> neighborsNeighbors = new ArrayList<Vector2>();
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

	public ArrayList<HexTile> getNeighborTiles(HexTile tile) {
		ArrayList<HexTile> cachedNeighbors = tile.getCachedNeighborTiles();
		if (cachedNeighbors == null) {
			cachedNeighbors = getNeighborTiles(tile.getPosition());
			tile.setCachedNeighborTiles(cachedNeighbors);
		}
		return cachedNeighbors;
	}

	private ArrayList<HexTile> getNeighborTiles(Vector2 tileCoords) {
		ArrayList<Vector2> neighborCoords = getNeighborCoords(tileCoords);
		ArrayList<HexTile> neighborTiles = new ArrayList<HexTile>();
		for (Vector2 coord : neighborCoords) {
			neighborTiles.add(tiles.get(coord));
		}
		return neighborTiles;
	}

	public ArrayList<HexTile> getNeighborsNeighborTiles(HexTile tile) {
		return getNeighborsNeighborTiles(tile.getPosition());
	}

	private ArrayList<HexTile> getNeighborsNeighborTiles(Vector2 tileCoords) {
		ArrayList<Vector2> neighborsNeighborCoords = getNeighborsNeighborCoords(tileCoords);
		ArrayList<HexTile> neighborsNeighborTiles = new ArrayList<HexTile>();
		for (Vector2 coord : neighborsNeighborCoords) {
			neighborsNeighborTiles.add(tiles.get(coord));
		}
		return neighborsNeighborTiles;
	}

	public ArrayList<Vector2> getUnusedNeighborCoords(Vector2 tileCoords) {
		ArrayList<Vector2> neighbors = getNeighborCoords(tileCoords);
		ArrayList<Vector2> unusedNeighbors = new ArrayList<Vector2>();
		for (Vector2 neighbor : neighbors) {
			if (!tiles.containsKey(neighbor)) {
				unusedNeighbors.add(neighbor);
			}
		}
		return unusedNeighbors;
	}

	public Map<Vector2, HexTile> getTiles() {
		return tiles;
	}
}

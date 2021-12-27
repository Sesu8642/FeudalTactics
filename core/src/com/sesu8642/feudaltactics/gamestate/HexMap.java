package com.sesu8642.feudaltactics.gamestate;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.Vector2;

public class HexMap {

	public static final float HEX_OUTER_RADIUS = 5;

	private Map<Vector2, HexTile> tiles;

	public HexMap() {
		this.tiles = new LinkedHashMap<>();
	}

	public Vector2 hexCoordsToWorldCoords(Vector2 hexCoords) {
		// https://www.redblobgames.com/grids/hexagons/#hex-to-pixel
		// get third coordinate
		float cubeZ = -hexCoords.x - hexCoords.y;
		// calculate world coordinates
		float worldX = hexCoords.x * HEX_OUTER_RADIUS * 1.5F;
		float worldY = (float) (HEX_OUTER_RADIUS * (Math.sqrt(3) / 2 * hexCoords.x + Math.sqrt(3) * cubeZ));
		return new Vector2(worldX, worldY);
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

	public List<Vector2> getNeighborCoords(Vector2 tileCoords) {
		ArrayList<Vector2> neighbors = new ArrayList<>();
		neighbors.add(new Vector2(tileCoords.x - 1, tileCoords.y));
		neighbors.add(new Vector2(tileCoords.x, tileCoords.y - 1));
		neighbors.add(new Vector2(tileCoords.x + 1, tileCoords.y - 1));
		neighbors.add(new Vector2(tileCoords.x + 1, tileCoords.y));
		neighbors.add(new Vector2(tileCoords.x, tileCoords.y + 1));
		neighbors.add(new Vector2(tileCoords.x - 1, tileCoords.y + 1));
		return neighbors;
	}

	public List<Vector2> getNeighborsNeighborCoords(Vector2 tileCoords) {
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

	public List<HexTile> getNeighborTiles(HexTile tile) {
		List<HexTile> cachedNeighbors = tile.getCachedNeighborTiles();
		if (cachedNeighbors == null) {
			cachedNeighbors = getNeighborTiles(tile.getPosition());
			tile.setCachedNeighborTiles(cachedNeighbors);
		}
		return cachedNeighbors;
	}

	private List<HexTile> getNeighborTiles(Vector2 tileCoords) {
		List<Vector2> neighborCoords = getNeighborCoords(tileCoords);
		List<HexTile> neighborTiles = new ArrayList<>();
		for (Vector2 coord : neighborCoords) {
			neighborTiles.add(tiles.get(coord));
		}
		return neighborTiles;
	}

	public List<HexTile> getNeighborsNeighborTiles(HexTile tile) {
		return getNeighborsNeighborTiles(tile.getPosition());
	}

	private List<HexTile> getNeighborsNeighborTiles(Vector2 tileCoords) {
		List<Vector2> neighborsNeighborCoords = getNeighborsNeighborCoords(tileCoords);
		List<HexTile> neighborsNeighborTiles = new ArrayList<>();
		for (Vector2 coord : neighborsNeighborCoords) {
			neighborsNeighborTiles.add(tiles.get(coord));
		}
		return neighborsNeighborTiles;
	}

	public List<Vector2> getUnusedNeighborCoords(Vector2 tileCoords) {
		List<Vector2> neighbors = getNeighborCoords(tileCoords);
		List<Vector2> unusedNeighbors = new ArrayList<>();
		for (Vector2 neighbor : neighbors) {
			if (!tiles.containsKey(neighbor)) {
				unusedNeighbors.add(neighbor);
			}
		}
		return unusedNeighbors;
	}

	public MapDimensions getMapDimensionsInWorldCoords() {
		// get most extreme map coordinates
		float minWorldX = 0;
		float maxWorldX = 0;
		float minWorldY = 0;
		float maxWorldY = 0;
		for (Vector2 hexCoords : tiles.keySet()) {
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
		MapDimensions dims = new MapDimensions();
		// the world coordinates of the tiles are always the center so this must be
		// adjusted here
		minWorldX -= HEX_OUTER_RADIUS;
		maxWorldX += HEX_OUTER_RADIUS;
		minWorldY -= HEX_OUTER_RADIUS;
		maxWorldY += HEX_OUTER_RADIUS;
		dims.setWidth(maxWorldX - minWorldX);
		dims.setHeight(maxWorldY - minWorldY);
		dims.setCenter(new Vector2());
		dims.getCenter().x = minWorldX + (dims.getWidth() / 2);
		dims.getCenter().y = minWorldY + (dims.getHeight() / 2);
		return dims;
	}

	public Map<Vector2, HexTile> getTiles() {
		return tiles;
	}
}

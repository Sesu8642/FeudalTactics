package com.sesu8642.feudaltactics.engine;

import java.util.ArrayList;
import java.util.HashMap;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class HexMap {

	private HashMap<Vector2, HexTile> tiles;

	public HexMap() {
		this.tiles = new HashMap<Vector2, HexTile>();
	}
	
	public int getDistance(Vector3 coordinate1, Vector3 coordinate2) {
		return (int) ((Math.abs(coordinate1.x - coordinate2.x) + Math.abs(coordinate1.y - coordinate2.y)
				+ Math.abs(coordinate1.z - coordinate2.z)) / 2);
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

	public ArrayList<Vector2> getUnusedNeighborCoords(Vector2 tileCoords) {
		ArrayList<Vector2> neighbors = getNeighborCoords(tileCoords);
		ArrayList<Vector2> unusedNeighbors = new ArrayList();
		for (Vector2 neighbor : neighbors) {
			if (!tiles.containsKey(neighbor)) {
				unusedNeighbors.add(neighbor);
			}
		}
		return unusedNeighbors;
	}
	
	public HexTile getTileFromCoords(Vector2 coords) {
		return tiles.get(coords);
	}

	public HashMap<Vector2, HexTile> getTiles() {
		return tiles;
	}
}

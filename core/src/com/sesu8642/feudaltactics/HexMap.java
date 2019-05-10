package com.sesu8642.feudaltactics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

public class HexMap {

	private HashMap<Vector2, HexTile> tiles;

	private int getDistance(Vector3 coordinate1, Vector3 coordinate2) {
		return (int) ((Math.abs(coordinate1.x - coordinate2.x) + Math.abs(coordinate1.y - coordinate2.y)
				+ Math.abs(coordinate1.z - coordinate2.z)) / 2);
	}

	private ArrayList<Vector2> getNeighborCoords(Vector2 tileCoords) {
		ArrayList<Vector2> neighbors = new ArrayList<Vector2>();
		neighbors.add(new Vector2(tileCoords.x - 1, tileCoords.y));
		neighbors.add(new Vector2(tileCoords.x, tileCoords.y - 1));
		neighbors.add(new Vector2(tileCoords.x + 1, tileCoords.y - 1));
		neighbors.add(new Vector2(tileCoords.x + 1, tileCoords.y));
		neighbors.add(new Vector2(tileCoords.x, tileCoords.y + 1));
		neighbors.add(new Vector2(tileCoords.x - 1, tileCoords.y + 1));
		return neighbors;
	}

	private ArrayList<Vector2> getUnusedNeighborCoords(Vector2 tileCoords) {
		ArrayList<Vector2> neighbors = getNeighborCoords(tileCoords);
		ArrayList<Vector2> unusedNeighbors = new ArrayList();
		for (Vector2 neighbor : neighbors) {
			if (!tiles.containsKey(neighbor)) {
				unusedNeighbors.add(neighbor);
			}
		}
		return unusedNeighbors;
	}

	public void generate(ArrayList<Player> players, float landMass, float density, Long mapSeed) {
		tiles = new HashMap<Vector2, HexTile>();
		if (mapSeed == null) {
			mapSeed = System.currentTimeMillis();
		}
		Random random = new Random(mapSeed);
		// could be done recursively but stack size is uncertain
		Vector2 nextTilePos = new Vector2(0, 0);
		ArrayList<Vector2> positionHistory = new ArrayList<Vector2>(); // for backtracking
		while (landMass > 0) {
			Vector2 currentTilePos = nextTilePos;
			// place tile
			Player player = players.get(random.nextInt(players.size()));
			HexTile tile = new HexTile(player);
			// REMOVE NEXT LINE LATER
			tile.setContent(new MapObject());
			tiles.put(currentTilePos, tile);
			landMass--;
			// add to history
			positionHistory.add(currentTilePos);
			// get next tile with usable neighboring tiles
			ArrayList<Vector2> usableCoords = getUnusedNeighborCoords(currentTilePos);
			while (usableCoords.isEmpty()) {
				// backtrack until able to place a tile again
				positionHistory.remove(positionHistory.size()-1);
				currentTilePos = positionHistory.get(positionHistory.size()-1);
				usableCoords = getUnusedNeighborCoords(currentTilePos);
			}
			// calculate a score for each neighboring tile for choosing the next one
			ArrayList<Float> scores = new ArrayList<Float>();
			float scoreSum = 0;
			for (Vector2 candidate : usableCoords) {
				// factor in density
				int usableCoordsCountFromCandidate = getUnusedNeighborCoords(candidate).size();
				float score = (float) Math.pow(usableCoordsCountFromCandidate, density * density);
				scores.add(score);
				scoreSum += score;
			}
			// select tile based on score and random
			float randomScore = random.nextFloat() * scoreSum;
			int index = 0;
			float countedScore = scores.get(0);
			while (countedScore < randomScore) {
				index++;
				countedScore += scores.get(index);
			}
			nextTilePos = usableCoords.get(index);
		}
	}

	public HashMap<Vector2, HexTile> getTiles() {
		return tiles;
	}
}

package com.sesu8642.feudaltactics.engine;

import java.util.ArrayList;
import java.util.Random;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.gamelogic.Capital;
import com.sesu8642.feudaltactics.gamelogic.Kingdom;
import com.sesu8642.feudaltactics.gamelogic.Unit;
import com.sesu8642.feudaltactics.scenes.Hud;

public class GameController {
	// only class supposed to modify the game state

	private HexMap map;
	private MapRenderer mapRenderer;
	private GameState gameState;
	private Random random;
	private Hud hud;

	public GameController(HexMap map, MapRenderer mapRenderer) {
		this.map = map;
		this.mapRenderer = mapRenderer;
		this.gameState = new GameState();
		generateDummyMap();
	}

	public void generateDummyMap() {
		ArrayList<Player> players = new ArrayList<Player>();
		Player p1 = new Player(new Color(0, 0.5f, 0.8f, 1), Player.Type.LOCAL_PLAYER);
		Player p2 = new Player(new Color(1F, 0F, 0F, 1), Player.Type.LOCAL_PLAYER);
		Player p3 = new Player(new Color(0F, 1F, 0F, 1), Player.Type.LOCAL_PLAYER);
		Player p4 = new Player(new Color(1F, 1F, 0F, 1), Player.Type.LOCAL_PLAYER);
		Player p5 = new Player(new Color(1F, 1F, 1F, 1), Player.Type.LOCAL_PLAYER);
		Player p6 = new Player(new Color(0F, 1F, 1F, 1), Player.Type.LOCAL_PLAYER);
		players.add(p1);
		players.add(p2);
		players.add(p3);
		players.add(p4);
		players.add(p5);
		players.add(p6);
		gameState.setPlayers(players);
		gameState.setMap(map);
		gameState.setKingdoms(new ArrayList<Kingdom>());
		generateMap(players, 500, 0, null);
	}

	public void generateMap(ArrayList<Player> players, float landMass, float density, Long mapSeed) {
		generateTiles(players, landMass, density, mapSeed);
		createKingdoms();
		createCapitals();
		mapRenderer.updateMap();
	}

	private void generateTiles(ArrayList<Player> players, float landMass, float density, Long mapSeed) {
		// density between -3 and 3 produces good results
		map.getTiles().clear();
		if (mapSeed == null) {
			mapSeed = System.currentTimeMillis();
		}
		random = new Random(mapSeed);
		// could be done recursively but stack size is uncertain
		Vector2 nextTilePos = new Vector2(0, 0);
		ArrayList<Vector2> positionHistory = new ArrayList<Vector2>(); // for backtracking
		while (landMass > 0) {
			Vector2 currentTilePos = nextTilePos;
			// place tile
			Player player = players.get(random.nextInt(players.size()));
			HexTile tile = new HexTile(player);
			map.getTiles().put(currentTilePos, tile);
			landMass--;
			// add to history
			positionHistory.add(currentTilePos);
			// get next tile with usable neighboring tiles
			ArrayList<Vector2> usableCoords = map.getUnusedNeighborCoords(currentTilePos);
			while (usableCoords.isEmpty()) {
				// backtrack until able to place a tile again
				positionHistory.remove(positionHistory.size() - 1);
				currentTilePos = positionHistory.get(positionHistory.size() - 1);
				usableCoords = map.getUnusedNeighborCoords(currentTilePos);
			}
			// calculate a score for each neighboring tile for choosing the next one
			ArrayList<Float> scores = new ArrayList<Float>();
			float scoreSum = 0;
			for (Vector2 candidate : usableCoords) {
				// factor in density
				int usableCoordsCountFromCandidate = map.getUnusedNeighborCoords(candidate).size();
				float score = (float) Math.pow(usableCoordsCountFromCandidate, density);
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

	private void createKingdoms() {
		gameState.getKingdoms().clear();
		for (Entry<Vector2, HexTile> tileEntry : map.getTiles().entrySet()) {
			HexTile tile = tileEntry.getValue();
			Vector2 coords = tileEntry.getKey();
			tile.setKingdom(null);
			ArrayList<Vector2> neighbors = map.getNeighborCoords(coords);
			for (Vector2 neighborCoords : neighbors) {
				HexTile neighborTile = map.getTiles().get((neighborCoords));
				if (neighborTile == null) {
					// water
					continue;
				}
				if (neighborTile.getPlayer() != tile.getPlayer()) {
					continue;
				}
				// two neighboring tiles belong to the same player
				if (tile.getKingdom() == null && neighborTile.getKingdom() == null) {
					// none of the tiles already belong to a kingdom --> create a new one
					Kingdom newKingdom = new Kingdom(tile.getPlayer());
					gameState.getKingdoms().add(newKingdom);
					newKingdom.getTiles().add(tile);
					newKingdom.getTiles().add(neighborTile);
					tile.setKingdom(newKingdom);
					neighborTile.setKingdom(newKingdom);
				} else if (tile.getKingdom() != null && neighborTile.getKingdom() == null) {
					// tile belongs to a kingdom but neighbor does not -> add neighbor to existing
					// kingdom
					tile.getKingdom().getTiles().add(neighborTile);
					neighborTile.setKingdom(tile.getKingdom());
				} else if (tile.getKingdom() == null && neighborTile.getKingdom() != null) {
					// neighbor belongs to a kingdom but tile does not -> add tile to existing
					// kingdom
					neighborTile.getKingdom().getTiles().add(tile);
					tile.setKingdom(neighborTile.getKingdom());
				} else if (tile.getKingdom() != null && neighborTile.getKingdom() != null
						&& tile.getKingdom() != neighborTile.getKingdom()) {
					// tile and neighbor belong to different kingdoms --> merge kingdoms
					gameState.getKingdoms().remove(neighborTile.getKingdom());
					for (HexTile neighborKingdomTile : neighborTile.getKingdom().getTiles()) {
						neighborKingdomTile.setKingdom(tile.getKingdom());
						tile.getKingdom().getTiles().add(neighborKingdomTile);
					}
				}
			}
		}
	}

	private void createCapitals() {
		for (Kingdom kingdom : gameState.getKingdoms()) {
			ArrayList<HexTile> tiles = kingdom.getTiles();
			Capital capital = new Capital();
			kingdom.setSavings(kingdom.getIncome() * 5);
			tiles.get(random.nextInt(tiles.size())).setContent(new Capital());
		}
	}

	public void printTileInfo(Vector2 worldCoords) {
		Vector2 hexCoords = map.worldCoordsToHexCoords(worldCoords);
		System.out.println("clicked tile position " + hexCoords);
		System.out.println(map.getTiles().get(hexCoords));
		map.getTiles().get(hexCoords).setContent(new Unit(1));
		mapRenderer.updateMap();
	}

	public void updateInfoText(Vector2 worldCoords) {
		if (hud == null) {
			return;
		}
		HexTile tile = map.getTiles().get(map.worldCoordsToHexCoords(worldCoords));
		if (tile == null) {
			return;
		}
		Kingdom kingdom = tile.getKingdom();
		if (kingdom == null) {
			return;
		}
		int income = kingdom.getIncome();
		int salaries = kingdom.getSalaries();
		int result = income - salaries;
		int savings = kingdom.getSavings();
		String infoText = "";
		infoText += "Income: " + income;
		infoText += "\nSalaries: " + salaries;
		infoText += "\nResult: " + result;
		infoText += "\nSavings: " + savings;
		hud.setInfoText(infoText);
	}

	public void setHud(Hud hud) {
		this.hud = hud;
	}

}
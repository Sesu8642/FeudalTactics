package com.sesu8642.feudaltactics.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.HexMap;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamestate.Player;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Capital;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Castle;
import com.sesu8642.feudaltactics.gamestate.mapobjects.MapObject;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit.UnitTypes;
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
		createInitialKingdoms();
		createCapitalsAndMoney();
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
			HexTile tile = new HexTile(player, currentTilePos);
			map.getTiles().put(currentTilePos, tile);
			landMass--;
			// add to history
			positionHistory.add(currentTilePos);
			// get next tile with usable neighboring tiles
			ArrayList<Vector2> usableCoords = new ArrayList<Vector2>(map.getUnusedNeighborCoords(currentTilePos));
			while (usableCoords.isEmpty()) {
				// backtrack until able to place a tile again
				positionHistory.remove(positionHistory.size() - 1);
				currentTilePos = positionHistory.get(positionHistory.size() - 1);
				usableCoords = new ArrayList<Vector2>(map.getUnusedNeighborCoords(currentTilePos));
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

	private void createInitialKingdoms() {
		gameState.getKingdoms().clear();
		for (Entry<Vector2, HexTile> tileEntry : map.getTiles().entrySet()) {
			HexTile tile = tileEntry.getValue();
			Vector2 coords = tileEntry.getKey();
			tile.setKingdom(null);
			for (HexTile neighborTile : map.getNeighborTiles(coords)) {
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

	private void createCapitalsAndMoney() {
		for (Kingdom kingdom : gameState.getKingdoms()) {
			createCapital(kingdom);
			createInitialSavings(kingdom);
		}
	}

	private void createCapital(Kingdom kingdom) {
		ArrayList<HexTile> tiles = new ArrayList<HexTile>(kingdom.getTiles());
		tiles.get(random.nextInt(tiles.size())).setContent(new Capital(kingdom));
	}

	private void createInitialSavings(Kingdom kingdom) {
		kingdom.setSavings(kingdom.getIncome() * 5);
	}

	public void printTileInfo(Vector2 hexCoords) {
		System.out.println("clicked tile position " + hexCoords);
		System.out.println(map.getTiles().get(hexCoords));
	}

	public void updateInfoText() {
		if (hud == null) {
			return;
		}
		Kingdom kingdom = gameState.getActiveKingdom();
		if (kingdom == null) {
			hud.setInfoText("");
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

	public void activateKingdom(Kingdom kingdom) {
		gameState.setActiveKingdom(kingdom);
		updateInfoText();
	}

	public void pickupObject(HexTile tile) {
		gameState.setHeldObject(tile.getContent());
		tile.setContent(null);
		mapRenderer.updateMap();
	}

	public void placeObject(HexTile tile) {
		if (tile.getContent() != null) {
			// if combining units, place resulting unit as held object
			if (tile.getPlayer() == gameState.getActivePlayer()
					&& tile.getContent().getClass().isAssignableFrom(Unit.class)
					&& gameState.getHeldObject().getClass().isAssignableFrom(Unit.class)) {
				// the unit that is not the peasant will be upgraded
				Unit oldUnit;
				if (((Unit) tile.getContent()).getUnitType() == UnitTypes.PEASANT) {
					oldUnit = (Unit) gameState.getHeldObject();
				} else {
					oldUnit = (Unit) tile.getContent();
				}
				UnitTypes newUnitType = null;
				switch (oldUnit.getUnitType()) {
				case PEASANT:
					newUnitType = UnitTypes.SPEARMAN;
					System.out.println("creating spearman");
					break;
				case SPEARMAN:
					newUnitType = UnitTypes.KNIGHT;
					break;
				case KNIGHT:
					newUnitType = UnitTypes.BARON;
					break;
				default:
					break;
				}
				gameState.setHeldObject(new Unit(tile.getKingdom(), newUnitType));
			}

			// place new capital if old one is going to be destroyed
			if (tile.getContent().getClass().isAssignableFrom(Capital.class)) {
				tile.getKingdom().setSavings(0);
				HashSet<HexTile> neighborTiles = map.getNeighborTiles(tile.getPosition());
				ArrayList<HexTile> capitalCandidates = new ArrayList<HexTile>();
				// find potential tiles for new capital
				for (HexTile neighborTile : neighborTiles) {
					if (neighborTile != null && neighborTile.getKingdom() == tile.getKingdom()) {
						capitalCandidates.add(neighborTile);
					}
				}
				// place new capital on random tile next to the old one
				capitalCandidates.get(random.nextInt(capitalCandidates.size()))
						.setContent(new Capital(tile.getKingdom()));
			}
		}
		// update kingdoms
		if (tile.getPlayer() != gameState.getActivePlayer()) {
			// tile is conquered
			if (tile.getKingdom() != null) {
				tile.getKingdom().getTiles().remove(tile);
			}
			tile.setKingdom(gameState.getHeldObject().getKingdom());
			tile.getKingdom().getTiles().add(tile);
			HashSet<Kingdom> affectedKingdoms = new HashSet<Kingdom>();
			for (HexTile neighborTile : map.getNeighborTiles(tile.getPosition())) {
				if (neighborTile == null) {
					// water
					continue;
				}
				if (neighborTile.getKingdom() == null) {
					if (neighborTile.getPlayer() == tile.getPlayer()) {
						// connect tile without kingdom to kingdom
						neighborTile.setKingdom(tile.getKingdom());
						tile.getKingdom().getTiles().add(neighborTile);
					}
				} else {
					// handle kingdom
					if (neighborTile.getPlayer() == tile.getPlayer()
							&& !(neighborTile.getKingdom() == tile.getKingdom())) {
						// combine kingdoms if owned by the same player
						combineKingdoms(tile.getKingdom(), neighborTile.getKingdom());

					} else {
						// find other affected kingdoms (that might have been split apart)
						affectedKingdoms.add(neighborTile.getKingdom());
					}
				}
			}
			// handle potentially split kingdoms
			for (Kingdom kingdom : affectedKingdoms) {
				updateSplitKingdom(kingdom.getTiles());
			}
		}
		// finally place new object
		tile.setContent(gameState.getHeldObject());
		gameState.setHeldObject(null);
		mapRenderer.updateMap();
		updateInfoText();
	}

	private void combineKingdoms(Kingdom masterKingdom, Kingdom slaveKingdom) {
		// master kingdom will determine the new capital and money
		masterKingdom.getTiles().addAll(slaveKingdom.getTiles());
		for (HexTile slaveKingdomTile : slaveKingdom.getTiles()) {
			slaveKingdomTile.setKingdom(masterKingdom);
			MapObject content = slaveKingdomTile.getContent();
			if (content != null) {
				content.setKingdom(masterKingdom);
				if (content.getClass().isAssignableFrom(Capital.class)) {
					// deltete slave capital
					slaveKingdomTile.setContent(null);
				}
			}
		}
		gameState.getKingdoms().remove(slaveKingdom);
	}

	private void updateSplitKingdom(HashSet<HexTile> tiles) {
		if (tiles.size() == 0) {
			return;
		}
		// try to find a capital
		HexTile capitalTile = null;
		for (HexTile kingdomTile : tiles) {
			if (kingdomTile.getContent() != null
					&& kingdomTile.getContent().getClass().isAssignableFrom(Capital.class)) {
				capitalTile = kingdomTile;
				break;
			}
		}
		HexTile startTile;
		Kingdom newKingdom = null;
		if (capitalTile != null) {
			// capital exists --> keep it's kingdom
			startTile = capitalTile;
			newKingdom = startTile.getKingdom();
			newKingdom.setTiles(new HashSet<HexTile>());
		} else {
			// no capital exists --> create new kingdom
			// start from some other tile
			startTile = (HexTile) tiles.toArray()[0];
			newKingdom = new Kingdom(startTile.getPlayer());
			gameState.getKingdoms().add(newKingdom);
		}
		// expand outwards from startTile to find connected tiles
		LinkedList<HexTile> todoTiles = new LinkedList<HexTile>();
		HashSet<HexTile> doneTiles = new HashSet<HexTile>();
		todoTiles.add(startTile);
		while (todoTiles.size() > 0) {
			HexTile currentTile = todoTiles.removeFirst();
			newKingdom.getTiles().add(currentTile);
			currentTile.setKingdom(newKingdom);
			doneTiles.add(currentTile);
			for (HexTile expandTile : map.getNeighborTiles(currentTile.getPosition())) {
				if (!doneTiles.contains(expandTile) && tiles.contains(expandTile)) {
					todoTiles.add(expandTile);
				}
			}
		}
		tiles.removeAll(newKingdom.getTiles());

		if (newKingdom.getTiles().size() < 2) {
			// delete capital and kingdom if too small
			if (capitalTile != null) {
				capitalTile.setContent(null);
			}
			startTile.setKingdom(null);
			gameState.getKingdoms().remove(newKingdom);
		} else if (capitalTile == null) {
			// create capital if necessary
			createCapital(newKingdom);
		}
		// recursive call with the tiles that are not connected
		updateSplitKingdom(tiles);
		return;
	}

	public void endTurn() {
		// update active player
		gameState.setPlayerTurn(gameState.getPlayerTurn() + 1);
		if (gameState.getPlayerTurn() >= gameState.getPlayers().size()) {
			gameState.setPlayerTurn(0);
		}
		// reset active kingdom
		gameState.setActiveKingdom(null);
		// reset info text
		updateInfoText();
		// update savings
		for (Kingdom kingdom : gameState.getKingdoms()) {
			if (kingdom.getPlayer() == gameState.getActivePlayer()) {
				kingdom.setSavings(kingdom.getSavings() + kingdom.getIncome());
				if (kingdom.getSavings() < kingdom.getSalaries()) {
					// destroy all units if they cannot get paid
					for (HexTile tile : kingdom.getTiles()) {
						if (tile.getContent() != null && tile.getContent().getClass().isAssignableFrom(Unit.class)) {
							tile.setContent(null);
						}
						mapRenderer.updateMap();
					}
				} else {
					kingdom.setSavings(kingdom.getSavings() - kingdom.getSalaries());
				}
			}
		}
	}

	public void buyPeasant() {
		gameState.getActiveKingdom().setSavings(gameState.getActiveKingdom().getSavings() - Unit.COST);
		gameState.setHeldObject(new Unit(gameState.getActiveKingdom(), UnitTypes.PEASANT));
		updateInfoText();
	}

	public void buyCastle() {
		gameState.getActiveKingdom().setSavings(gameState.getActiveKingdom().getSavings() - Castle.COST);
		gameState.setHeldObject(new Castle(gameState.getActiveKingdom()));
		updateInfoText();
	}

	public void setHud(Hud hud) {
		this.hud = hud;
	}

	public GameState getGameState() {
		return gameState;
	}

}
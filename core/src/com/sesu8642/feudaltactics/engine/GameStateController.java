package com.sesu8642.feudaltactics.engine;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Map.Entry;

import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.HexMap;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamestate.Player;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Capital;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Castle;
import com.sesu8642.feudaltactics.gamestate.mapobjects.MapObject;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Tree;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit.UnitTypes;

public class GameStateController {
	// only class supposed to modify the game state

	private final static float TREE_SPREAD_RATE = 0.5F;
	
	public static void initializeMap(GameState gameState, ArrayList<Player> players, float landMass, float density,
			float vegetationDensity, Long mapSeed) {
		gameState.setPlayers(players);
		gameState.setMap(new HexMap());
		gameState.setKingdoms(new ArrayList<Kingdom>());
		generateMap(gameState, players, landMass, density, vegetationDensity, mapSeed);
	}

	public static void generateMap(GameState gameState, ArrayList<Player> players, float landMass, float density,
			float vegetationDensity, Long mapSeed) {
		generateTiles(gameState, players, landMass, density, mapSeed);
		createInitialKingdoms(gameState);
		createTrees(gameState, vegetationDensity);
		createCapitalsAndMoney(gameState);
	}

	private static void generateTiles(GameState gameState, ArrayList<Player> players, float landMass, float density,
			Long mapSeed) {
		// density between -3 and 3 produces good results
		gameState.getMap().getTiles().clear();
		if (mapSeed == null) {
			mapSeed = System.currentTimeMillis();
		}
		gameState.setRandom(new Random(mapSeed));
		// could be done recursively but stack size is uncertain
		Vector2 nextTilePos = new Vector2(0, 0);
		ArrayList<Vector2> positionHistory = new ArrayList<Vector2>(); // for backtracking
		while (landMass > 0) {
			Vector2 currentTilePos = nextTilePos;
			// place tile
			Player player = players.get(gameState.getRandom().nextInt(players.size()));
			HexTile tile = new HexTile(player, currentTilePos);
			gameState.getMap().getTiles().put(currentTilePos, tile);
			landMass--;
			// add to history
			positionHistory.add(currentTilePos);
			// get next tile with usable neighboring tiles
			ArrayList<Vector2> usableCoords = gameState.getMap().getUnusedNeighborCoords(currentTilePos);
			while (usableCoords.isEmpty()) {
				// backtrack until able to place a tile again
				positionHistory.remove(positionHistory.size() - 1);
				currentTilePos = positionHistory.get(positionHistory.size() - 1);
				usableCoords = new ArrayList<Vector2>(gameState.getMap().getUnusedNeighborCoords(currentTilePos));
			}
			// calculate a score for each neighboring tile for choosing the next one
			ArrayList<Float> scores = new ArrayList<Float>();
			float scoreSum = 0;
			for (Vector2 candidate : usableCoords) {
				// factor in density
				int usableCoordsCountFromCandidate = gameState.getMap().getUnusedNeighborCoords(candidate).size();
				float score = (float) Math.pow(usableCoordsCountFromCandidate, density);
				scores.add(score);
				scoreSum += score;
			}
			// select tile based on score and random
			float randomScore = gameState.getRandom().nextFloat() * scoreSum;
			int index = 0;
			float countedScore = scores.get(0);
			while (countedScore < randomScore) {
				index++;
				countedScore += scores.get(index);
			}
			nextTilePos = usableCoords.get(index);
		}
	}

	private static void createInitialKingdoms(GameState gameState) {
		gameState.getKingdoms().clear();
		for (Entry<Vector2, HexTile> tileEntry : gameState.getMap().getTiles().entrySet()) {
			HexTile tile = tileEntry.getValue();
			Vector2 coords = tileEntry.getKey();
			tile.setKingdom(null);
			for (HexTile neighborTile : gameState.getMap().getNeighborTiles(coords)) {
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

	private static void createCapitalsAndMoney(GameState gameState) {
		for (Kingdom kingdom : gameState.getKingdoms()) {
			createCapital(gameState, kingdom);
			// first player gets more money as he wont earn money for his first turn
			int multiplier = 4;
			if (gameState.getActivePlayer() == kingdom.getPlayer()) {
				multiplier = 5;
			}
			createInitialSavings(kingdom, multiplier);
		}
	}

	private static void createTrees(GameState gameState, float vegetationDensity) {
		for (HexTile tile : gameState.getMap().getTiles().values()) {
			if (gameState.getRandom().nextFloat() <= vegetationDensity) {
				tile.setContent(new Tree(tile.getKingdom()));
			}
		}
	}

	private static void createCapital(GameState gameState, Kingdom kingdom) {
		ArrayList<HexTile> tiles = new ArrayList<HexTile>(kingdom.getTiles());
		tiles.get(gameState.getRandom().nextInt(tiles.size())).setContent(new Capital(kingdom));
	}

	private static void createInitialSavings(Kingdom kingdom, int multiplier) {
		kingdom.setSavings(kingdom.getIncome() * multiplier);
	}

	public static void activateKingdom(GameState gameState, Kingdom kingdom) {
		gameState.setActiveKingdom(kingdom);
	}

	public static void pickupObject(GameState gameState, HexTile tile) {
		gameState.setHeldObject(tile.getContent());
		tile.setContent(null);
	}

	public static void placeOwn(GameState gameState, HexTile tile) {
		// units can't act after removing trees
		if (tile.getContent() != null && tile.getContent().getClass().isAssignableFrom(Tree.class)) {
			((Unit) gameState.getHeldObject()).setCanAct(false);
		}
		placeObject(gameState, tile);
	}

	public static void combineUnits(GameState gameState, HexTile tile) {
		// place resulting unit as held object
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
		Unit newUnit = new Unit(tile.getKingdom(), newUnitType);
		newUnit.setCanAct(((Unit) tile.getContent()).isCanAct());
		gameState.setHeldObject(newUnit);
		placeObject(gameState, tile);
	}

	public static void conquer(GameState gameState, HexTile tile) {
		// units can't act after conquering
		((Unit) gameState.getHeldObject()).setCanAct(false);
		// place new capital if old one is going to be destroyed
		if (tile.getContent() != null && tile.getContent().getClass().isAssignableFrom(Capital.class)) {
			tile.getKingdom().setSavings(0);
			ArrayList<HexTile> neighborTiles = gameState.getMap().getNeighborTiles(tile.getPosition());
			ArrayList<HexTile> capitalCandidates = new ArrayList<HexTile>();
			// find potential tiles for new capital
			for (HexTile neighborTile : neighborTiles) {
				if (neighborTile != null && neighborTile.getKingdom() == tile.getKingdom()) {
					capitalCandidates.add(neighborTile);
				}
			}
			// place new capital on random tile next to the old one
			capitalCandidates.get(gameState.getRandom().nextInt(capitalCandidates.size())).setContent(new Capital(tile.getKingdom()));
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
			for (HexTile neighborTile : gameState.getMap().getNeighborTiles(tile.getPosition())) {
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
						combineKingdoms(gameState, neighborTile.getKingdom(), tile.getKingdom());
						gameState.getHeldObject().setKingdom(neighborTile.getKingdom());
						gameState.setActiveKingdom(neighborTile.getKingdom());

					} else {
						// find other affected kingdoms (that might have been split apart)
						if (neighborTile.getKingdom() != tile.getKingdom()) {
							affectedKingdoms.add(neighborTile.getKingdom());
						}
					}
				}
			}
			// handle potentially split kingdoms
			for (Kingdom kingdom : affectedKingdoms) {
				updateSplitKingdom(gameState, kingdom.getTiles());
			}
		}
		placeObject(gameState, tile);
	}

	private static void placeObject(GameState gameState, HexTile tile) {
		tile.setContent(gameState.getHeldObject());
		gameState.setHeldObject(null);
	}

	private static void combineKingdoms(GameState gameState, Kingdom masterKingdom, Kingdom slaveKingdom) {
		// master kingdom will determine the new capital
		masterKingdom.getTiles().addAll(slaveKingdom.getTiles());
		masterKingdom.setSavings(masterKingdom.getSavings() + slaveKingdom.getSavings());
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

	private static void updateSplitKingdom(GameState gameState, HashSet<HexTile> tiles) {
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
			for (HexTile expandTile : gameState.getMap().getNeighborTiles(currentTile.getPosition())) {
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
			createCapital(gameState, newKingdom);
		}
		// recursive call with the tiles that are not connected
		updateSplitKingdom(gameState, tiles);
		return;
	}

	public static void endTurn(GameState gameState) {
		// update active player
		gameState.setPlayerTurn(gameState.getPlayerTurn() + 1);
		if (gameState.getPlayerTurn() >= gameState.getPlayers().size()) {
			gameState.setPlayerTurn(0);
			spreadTrees(gameState);
		}
		// reset active kingdom
		gameState.setActiveKingdom(null);
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
					}
				} else {
					kingdom.setSavings(kingdom.getSavings() - kingdom.getSalaries());
					// all units can act again
					for (HexTile tile : kingdom.getTiles()) {
						if (tile.getContent() != null && tile.getContent().getClass().isAssignableFrom(Unit.class)) {
							((Unit) tile.getContent()).setCanAct(true);
						}
					}
				}
			}
		}
	}

	private static void spreadTrees(GameState gameState) {
		// spread trees
		HashSet<HexTile> newTreeTiles = new HashSet<HexTile>();
		for (HexTile tile : gameState.getMap().getTiles().values()) {
			if (tile.getContent() != null && tile.getContent().getClass().isAssignableFrom(Tree.class)) {
				if (gameState.getRandom().nextFloat() <= TREE_SPREAD_RATE) {
					ArrayList<HexTile> candidates = new ArrayList<HexTile>();
					for (HexTile neighbor : gameState.getMap().getNeighborTiles(tile.getPosition())) {
						if (neighbor != null && neighbor.getContent() == null) {
							candidates.add(neighbor);
						}
					}
					if (candidates.size() > 0) {
						newTreeTiles.add(candidates.get(gameState.getRandom().nextInt(candidates.size())));
						candidates.clear();
					}
				}
			}
		}
		for (HexTile tile : newTreeTiles) {
			tile.setContent(new Tree(tile.getKingdom()));
		}
	}

	public static void buyPeasant(GameState gameState) {
		gameState.getActiveKingdom().setSavings(gameState.getActiveKingdom().getSavings() - Unit.COST);
		gameState.setHeldObject(new Unit(gameState.getActiveKingdom(), UnitTypes.PEASANT));
	}

	public static void buyCastle(GameState gameState) {
		gameState.getActiveKingdom().setSavings(gameState.getActiveKingdom().getSavings() - Castle.COST);
		gameState.setHeldObject(new Castle(gameState.getActiveKingdom()));
	}
}

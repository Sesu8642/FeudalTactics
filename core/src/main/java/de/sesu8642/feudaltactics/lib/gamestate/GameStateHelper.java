// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.reflect.ClassReflection;

import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;
import de.sesu8642.feudaltactics.lib.gamestate.Unit.UnitTypes;

/**
 * Helper class that is used to modify a {@link GameState} in a way that
 * respects the game rules and guarantees integrity.
 **/
public class GameStateHelper {

	private static final Logger LOGGER = LoggerFactory.getLogger(GameStateHelper.class);

	public static final float DEAFULT_INITIAL_TREE_DENSITY = 0.1F;
	public static final float WIN_LANDMASS_PERCENTAGE = 0.8F;

	// prevent instantiation
	private GameStateHelper() {
		throw new AssertionError();
	}

	/**
	 * Creates a mostly deep copy of the original. Exception: The random uses a seed
	 * that is derived from the old randoms output.
	 * 
	 * @param original Original to copy
	 * @return copy Copy of the original
	 */
	public static GameState getCopy(GameState original) {
		GameState result = new GameState();

		List<Player> copiedPlayers = new ArrayList<>();
		for (Player originalPlayer : original.getPlayers()) {
			Player newPlayer = Player.copyOf(originalPlayer);
			copiedPlayers.add(newPlayer);
		}
		result.setPlayers(copiedPlayers);
		if (original.getWinner() != null) {
			result.setWinner(copiedPlayers.get(original.getPlayers().indexOf(original.getWinner())));
		}

		List<Kingdom> copiedKingdoms = new ArrayList<>();
		for (Kingdom originalKingdom : original.getKingdoms()) {
			Kingdom newKingdom = new Kingdom(
					copiedPlayers.get(original.getPlayers().indexOf(originalKingdom.getPlayer())));
			newKingdom.setSavings(originalKingdom.getSavings());
			newKingdom.setDoneMoving(originalKingdom.isDoneMoving());
			newKingdom.setWasActiveInCurrentTurn(originalKingdom.isWasActiveInCurrentTurn());
			copiedKingdoms.add(newKingdom);
		}
		result.setKingdoms(copiedKingdoms);

		LinkedHashMap<Vector2, HexTile> copiedMap = new LinkedHashMap<>();
		// note: this potentially results in a different tile order in the new kingdom
		// vs the other
		for (Entry<Vector2, HexTile> originalTileEntry : original.getMap().entrySet()) {
			HexTile originalTile = originalTileEntry.getValue();
			HexTile newTile = new HexTile(copiedPlayers.get(original.getPlayers().indexOf(originalTile.getPlayer())),
					new Vector2(originalTileEntry.getKey()));
			if (originalTile.getKingdom() != null) {
				newTile.setKingdom(copiedKingdoms.get(original.getKingdoms().indexOf(originalTile.getKingdom())));
				newTile.getKingdom().getTiles().add(newTile);
			}
			if (originalTile.getContent() != null) {
				newTile.setContent(originalTile.getContent().getCopy());
			}
			copiedMap.put(newTile.getPosition(), newTile);
		}
		result.setMap(copiedMap);

		if (original.getActiveKingdom() != null) {
			result.setActiveKingdom(copiedKingdoms.get(original.getKingdoms().indexOf(original.getActiveKingdom())));
		}

		if (original.getHeldObject() != null) {
			result.setHeldObject(original.getHeldObject().getCopy());
		}

		if (original.getSeed() != null) {
			result.setSeed(original.getSeed());
		}

		result.setRound(original.getRound());

		return result;
	}

	/**
	 * Generates a map on a {@link GameState}.
	 * 
	 * @param gameState         GameState to generate the map in
	 * @param players           players that own tiles on the map
	 * @param landMass          number of tiles to generate
	 * @param density           Higher density means the map will be more clumpy and
	 *                          lower means it will be more stringy. Values between
	 *                          -3 and 3 produce good results.
	 * @param vegetationDensity determines how many trees will be generated. 0.5 =
	 *                          50% of empty tiles will have trees
	 * @param mapSeed           map seed to use for generating the map
	 */
	public static void initializeMap(GameState gameState, List<Player> players, float landMass, float density,
			Float vegetationDensity, Long mapSeed) {
		if (mapSeed == null) {
			mapSeed = System.currentTimeMillis();
		}
		if (vegetationDensity == null) {
			vegetationDensity = DEAFULT_INITIAL_TREE_DENSITY;
		}
		gameState.setSeed(mapSeed);
		gameState.setPlayers(players);
		gameState.setMap(new LinkedHashMap<>());
		gameState.setKingdoms(new ArrayList<>());
		if (landMass == 0) {
			return;
		}
		Random random = new Random(mapSeed);
		generateMap(gameState, players, landMass, density, vegetationDensity, random);
	}

	private static void generateMap(GameState gameState, List<Player> players, float landMass, float density,
			float vegetationDensity, Random random) {
		// if not every player has at least one kingdom, try again
		do {
			generateTiles(gameState, players, landMass, density, random);
			createInitialKingdoms(gameState);
		} while (!doesEveryPlayerHaveKingdom(gameState));
		createTrees(gameState, vegetationDensity, random);
		createCapitals(gameState);
		sortPlayersByIncome(gameState);
		createMoney(gameState);
	}

	private static boolean doesEveryPlayerHaveKingdom(GameState gameState) {
		List<Player> playersWithoutKingdoms = new ArrayList<>(gameState.getPlayers());
		for (Kingdom kingdom : gameState.getKingdoms()) {
			if (playersWithoutKingdoms.contains(kingdom.getPlayer())) {
				playersWithoutKingdoms.remove(kingdom.getPlayer());
			}
		}
		return playersWithoutKingdoms.isEmpty();
	}

	private static void sortPlayersByIncome(GameState gameState) {
		gameState.getPlayers().sort((a, b) -> {
			// if they are the same, it doesn't matter
			int incomeA = gameState.getKingdoms().stream().filter(kingdom -> kingdom.getPlayer() == a)
					.mapToInt(GameStateHelper::getKingdomIncome).sum();
			int incomeB = gameState.getKingdoms().stream().filter(kingdom -> kingdom.getPlayer() == b)
					.mapToInt(GameStateHelper::getKingdomIncome).sum();
			return incomeA > incomeB ? 1 : -1;
		});
	}

	private static void generateTiles(GameState gameState, List<Player> players, float landMass, float density,
			Random random) {
		// distribute the land mass evenly to all players
		Map<Player, Integer> tileAmountsToGenerate = new HashMap<>();
		// if there are tiles left, distribute them to random players
		Collections.shuffle(players, random);
		int remainingLandMass = (int) (landMass % players.size());
		for (Player player : players) {
			int additionalTiles = 0;
			if (remainingLandMass > 0) {
				additionalTiles = 1;
				remainingLandMass--;
			}
			tileAmountsToGenerate.put(player, (int) (landMass / players.size() + additionalTiles));
		}
		// keep track of the players that still have tiles left to generate in a list
		// (because a random one can be selected)
		ArrayList<Player> remainingPlayers = new ArrayList<>(players);
		gameState.getMap().clear();
		// could be done recursively but stack size is uncertain
		Vector2 nextTilePos = new Vector2(0, 0);
		ArrayList<Vector2> positionHistory = new ArrayList<>(); // for backtracking
		while (!remainingPlayers.isEmpty()) {
			Vector2 currentTilePos = nextTilePos;
			// place tile
			Player player = remainingPlayers.get(random.nextInt(remainingPlayers.size()));
			HexTile tile = new HexTile(player, currentTilePos);
			gameState.getMap().put(currentTilePos, tile);
			// remove player if no tiles are left
			if (tileAmountsToGenerate.get(player) == 1) {
				remainingPlayers.remove(player);
			} else {
				tileAmountsToGenerate.put(player, tileAmountsToGenerate.get(player) - 1);
			}
			// add to history
			positionHistory.add(currentTilePos);
			// get next tile position with empty neighboring tiles
			List<Vector2> usableCoords = HexMapHelper.getUnusedNeighborCoords(gameState.getMap(), currentTilePos);
			while (usableCoords.isEmpty()) {
				// backtrack until able to place a tile again
				positionHistory.remove(positionHistory.size() - 1);
				currentTilePos = positionHistory.get(positionHistory.size() - 1);
				usableCoords = new ArrayList<>(
						HexMapHelper.getUnusedNeighborCoords(gameState.getMap(), currentTilePos));
			}
			// calculate a score for each neighboring tile for choosing the next one
			ArrayList<Float> scores = new ArrayList<>();
			float scoreSum = 0;
			for (Vector2 candidate : usableCoords) {
				// factor in density
				int usableCoordsCountFromCandidate = HexMapHelper.getUnusedNeighborCoords(gameState.getMap(), candidate)
						.size();
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

	private static void createInitialKingdoms(GameState gameState) {
		gameState.getKingdoms().clear();
		for (Entry<Vector2, HexTile> tileEntry : gameState.getMap().entrySet()) {
			HexTile tile = tileEntry.getValue();
			for (HexTile neighborTile : HexMapHelper.getNeighborTiles(gameState.getMap(), tile)) {
				if (neighborTile == null || neighborTile.getPlayer() != tile.getPlayer()) {
					// water or tile of a different player
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

	private static void createCapitals(GameState gameState) {
		for (Kingdom kingdom : gameState.getKingdoms()) {
			createCapital(kingdom);
		}
	}

	private static void createMoney(GameState gameState) {
		for (Kingdom kingdom : gameState.getKingdoms()) {
			int savings = Math.min(kingdom.getTiles().size() * 5, 20);
			// players other than the first one will earn some money once their turn starts
			if (gameState.getActivePlayer() != kingdom.getPlayer()) {
				savings -= getKingdomIncome(kingdom);
			}
			kingdom.setSavings(savings);
		}
	}

	private static void createTrees(GameState gameState, float vegetationDensity, Random random) {
		for (HexTile tile : gameState.getMap().values()) {
			if (random.nextFloat() <= vegetationDensity) {
				spawnTree(gameState, tile);
			}
		}
	}

	/**
	 * Creates a new capital in a kingdom after the old one was destroyed.
	 * 
	 * @param gameState      game state
	 * @param oldCapitalTile tile that previously contained the capital
	 */
	private static void createCapital(GameState gameState, HexTile oldCapitalTile) {
		HexTile newCapitalTile;
		// try to find empty neighbor tile
		List<HexTile> neighborTiles = HexMapHelper.getNeighborTiles(gameState.getMap(), oldCapitalTile);
		Optional<HexTile> emptyTileOptional = neighborTiles.stream()
				.filter((HexTile neighborTile) -> neighborTile != null
						&& neighborTile.getKingdom() == oldCapitalTile.getKingdom() && neighborTile.getContent() == null
						&& !isCapitalCandidateDisconnected(gameState, oldCapitalTile, neighborTile))
				.findFirst();
		for (HexTile neighborTile : neighborTiles) {
			if (neighborTile == null) {
				continue;
			}
			if (neighborTile.getKingdom() != oldCapitalTile.getKingdom()) {
				continue;
			}
			if (neighborTile.getContent() != null) {
				continue;
			}
			if (isCapitalCandidateDisconnected(gameState, oldCapitalTile, neighborTile)) {
				continue;
			}
			emptyTileOptional = Optional.of(neighborTile);
		}
		if (emptyTileOptional.isPresent()) {
			newCapitalTile = emptyTileOptional.get();
		} else {
			// no empty neighbor tile -> select any empty tile in the kingdom
			emptyTileOptional = oldCapitalTile.getKingdom().getTiles().stream()
					.filter((HexTile kingdomTile) -> kingdomTile.getContent() == null
							&& !isCapitalCandidateDisconnected(gameState, oldCapitalTile, kingdomTile))
					.findFirst();
			if (emptyTileOptional.isPresent()) {
				newCapitalTile = emptyTileOptional.get();
			} else {
				// no empty tile -> select any neighbor tile
				emptyTileOptional = neighborTiles.stream()
						.filter((HexTile neighborTile) -> (neighborTile != null
								&& neighborTile.getKingdom() == oldCapitalTile.getKingdom()
								&& !isCapitalCandidateDisconnected(gameState, oldCapitalTile, neighborTile)))
						.findFirst();
				if (emptyTileOptional.isPresent()) {
					newCapitalTile = emptyTileOptional.get();
				} else {
					// no suitable candidate found, this is ok
					LOGGER.debug("no suitable capital tile found after the old one was destroyed");
					return;
				}
			}
		}
		newCapitalTile.setContent(new Capital());
	}

	/**
	 * Creates a new capital in a kingdom that doesn't have one.
	 * 
	 * @param kingdom kingdom without capital
	 */
	private static void createCapital(Kingdom kingdom) {
		HexTile newCapitalTile;
		// try to find any empty kingdom tile
		Optional<HexTile> emptyTileOptional = kingdom.getTiles().stream()
				.filter((HexTile kingdomTile) -> kingdomTile.getContent() == null).findFirst();
		if (emptyTileOptional.isPresent()) {
			newCapitalTile = emptyTileOptional.get();
		} else {
			// no empty tile -> just select any
			Optional<HexTile> newCapitalTileOptional = kingdom.getTiles().stream().findFirst();
			if (newCapitalTileOptional.isPresent()) {
				newCapitalTile = newCapitalTileOptional.get();
			} else {
				throw new AssertionError(
						String.format("The kingdom %s has no tiles which a capital could be placed on.", kingdom));
			}
		}
		newCapitalTile.setContent(new Capital());
	}

	/**
	 * Returns whether a candidate tile is suitable for placing a new capital in the
	 * regard that it is not a single, unconnected tile.
	 */
	private static boolean isCapitalCandidateDisconnected(GameState gameState, HexTile oldCapitalTile,
			HexTile candidate) {
		return HexMapHelper.getNeighborTiles(gameState.getMap(), candidate).stream().noneMatch(
				(HexTile neighborsNeighbor) -> neighborsNeighbor != null && neighborsNeighbor != oldCapitalTile
						&& neighborsNeighbor.getKingdom() == oldCapitalTile.getKingdom());
	}

	/**
	 * Activates a kingdom.
	 * 
	 * @param gameState GameState to act on
	 * @param kingdom   kingdom to be activated
	 */
	public static void activateKingdom(GameState gameState, Kingdom kingdom) {
		kingdom.setWasActiveInCurrentTurn(true);
		gameState.setActiveKingdom(kingdom);
	}


	/**
	 * Picks up an object.
	 * 
	 * @param gameState GameState to act on
	 * @param tile      tile that contains the object
	 */
	public static void pickupObject(GameState gameState, HexTile tile) {
		gameState.setHeldObject(tile.getContent());
		tile.setContent(null);
	}

	/**
	 * Places a held object on a tile in the own kingdom.
	 * 
	 * @param gameState GameState to act on
	 * @param tile      tile to place to object on
	 */
	public static void placeOwn(GameState gameState, HexTile tile) {
		// units can't act after removing blocking stuff
		if (tile.getContent() != null
				&& ClassReflection.isAssignableFrom(Blocking.class, tile.getContent().getClass())) {
			((Unit) gameState.getHeldObject()).setCanAct(false);
		}
		placeObject(gameState, tile);
	}

	/**
	 * Combines the held unit with a unit on the map.
	 * 
	 * @param gameState GameState to act on
	 * @param tile      tile that contains the unit on the map
	 */
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
		Unit newUnit = new Unit(newUnitType);
		newUnit.setCanAct(((Unit) tile.getContent()).isCanAct());
		gameState.setHeldObject(newUnit);
		placeObject(gameState, tile);
	}


	public static Kingdom getPlayerKingdom(GameState gameState, Player player) {
        for (Kingdom kingdom : gameState.getKingdoms()) {
            if (kingdom.getPlayer().equals(player)) {
                return kingdom;
            }
        }
        return null; // Or handle the case when no kingdom is found for the player
    }


	/**
	 * Conquers an enemy tile.
	 * 
	 * @param gameState GameState to act on
	 * @param tile      tile to conquer
	 */
	public static void conquer(GameState gameState, HexTile tile) {
		Kingdom oldTileKingdom = tile.getKingdom();
		// units can't act after conquering
		((Unit) gameState.getHeldObject()).setCanAct(false);

		// update kingdoms
		if (tile.getKingdom() != null) {
			// place new capital if old one is going to be destroyed
			if (tile.getContent() != null
					&& ClassReflection.isAssignableFrom(Capital.class, tile.getContent().getClass())
					&& tile.getKingdom().getTiles().size() > 2) {
				tile.getKingdom().setSavings(0);
				createCapital(gameState, tile);
			}
			boolean removeResult = tile.getKingdom().getTiles().remove(tile);
			if (!removeResult) {
				throw new AssertionError(String.format("tile could not be removed from its kingdom: '%s'", tile));
			}
		}
		tile.setKingdom(gameState.getActiveKingdom());
		tile.getKingdom().getTiles().add(tile);
		ArrayList<HexTile> oldKingdomNeighborTiles = new ArrayList<>();
		List<HexTile> neighborTiles = HexMapHelper.getNeighborTiles(gameState.getMap(), tile);
		for (HexTile neighborTile : neighborTiles) {
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
				if (neighborTile.getPlayer() == tile.getPlayer() && neighborTile.getKingdom() != tile.getKingdom()) {
					// combine kingdoms if owned by the same player
					combineKingdoms(gameState, tile.getKingdom(), neighborTile.getKingdom());
					gameState.setActiveKingdom(neighborTile.getKingdom());
					neighborTile.getKingdom().setWasActiveInCurrentTurn(true);
				} else if (neighborTile.getKingdom() == oldTileKingdom) {
					// remember neighbor tiles of the same kingdom as the old tile
					oldKingdomNeighborTiles.add(neighborTile);
				}
			}
		}
		// find out whether kingdom was potentially split
		boolean potentiallySplit = true;
		switch (oldKingdomNeighborTiles.size()) {
		case 2:
			// both tiles next to to each other --> no split possible
			if (HexMapHelper.getNeighborTiles(gameState.getMap(), oldKingdomNeighborTiles.get(0))
					.contains(oldKingdomNeighborTiles.get(1))) {
				potentiallySplit = false;
			}
			break;
		case 3:
			// if the first or the second tile is next to both of the other ones, they are
			// all next to each other --> no split possible
			if (((HexMapHelper.getNeighborTiles(gameState.getMap(), oldKingdomNeighborTiles.get(0))
					.contains(oldKingdomNeighborTiles.get(1)))
					&& (HexMapHelper.getNeighborTiles(gameState.getMap(), oldKingdomNeighborTiles.get(0))
							.contains(oldKingdomNeighborTiles.get(2))))
					|| ((HexMapHelper.getNeighborTiles(gameState.getMap(), oldKingdomNeighborTiles.get(1))
							.contains(oldKingdomNeighborTiles.get(0)))
							&& (HexMapHelper.getNeighborTiles(gameState.getMap(), oldKingdomNeighborTiles.get(1))
									.contains(oldKingdomNeighborTiles.get(2))))) {
				potentiallySplit = false;
			}
			break;
		case 4:
			// if the other tiles are next to each other, the 4 oldKingdomNeighborTiles must
			// also be next to each other --> no split possible
			ArrayList<HexTile> notOldKingdomNeighborTiles = new ArrayList<>(
					HexMapHelper.getNeighborTiles(gameState.getMap(), tile));
			notOldKingdomNeighborTiles.removeAll(oldKingdomNeighborTiles);
			if (notOldKingdomNeighborTiles.get(0) != null && notOldKingdomNeighborTiles.get(1) != null
					&& HexMapHelper.getNeighborTiles(gameState.getMap(), notOldKingdomNeighborTiles.get(0))
							.contains(notOldKingdomNeighborTiles.get(1))) {
				potentiallySplit = false;
			}
			break;
		default:
			// 1 or 5 means no split possible
			potentiallySplit = false;
			break;
		}
		if (potentiallySplit || (oldTileKingdom != null && oldTileKingdom.getTiles().size() < 2)) {
			updateSplitKingdom(gameState, oldTileKingdom.getTiles());
		}
		placeObject(gameState, tile);
	}

	private static void placeObject(GameState gameState, HexTile tile) {
		tile.setContent(gameState.getHeldObject());
		gameState.setHeldObject(null);
	}

	
	private static void combineKingdoms(GameState gameState, Kingdom masterKingdom, Kingdom slaveKingdom) {
		// master kingdom will determine the new capital
		masterKingdom.setSavings(masterKingdom.getSavings() + slaveKingdom.getSavings());
		if (!slaveKingdom.isDoneMoving()) {
			masterKingdom.setDoneMoving(false);
		}
		for (HexTile slaveKingdomTile : slaveKingdom.getTiles()) {
			// add all the absent tiles of the slave kingdom to the master one
			if (!masterKingdom.getTiles().contains(slaveKingdomTile)) {
				masterKingdom.getTiles().add(slaveKingdomTile);
			}
			slaveKingdomTile.setKingdom(masterKingdom);
			MapObject content = slaveKingdomTile.getContent();
			if (content != null && ClassReflection.isAssignableFrom(Capital.class, content.getClass())) {
				// delete slave capital
				slaveKingdomTile.setContent(null);
			}
		}
		gameState.getKingdoms().remove(slaveKingdom);
	}

	private static void updateSplitKingdom(GameState gameState, List<HexTile> tiles) {
		if (tiles.isEmpty()) {
			return;
		}
		Kingdom oldKingdom = tiles.get(0).getKingdom();
		// try to find a capital
		HexTile capitalTile = null;
		for (HexTile kingdomTile : tiles) {
			if (kingdomTile.getContent() != null
					&& ClassReflection.isAssignableFrom(Capital.class, kingdomTile.getContent().getClass())) {
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
			newKingdom.setTiles(new ArrayList<>());
		} else {
			// no capital exists --> create new kingdom
			// start from some other tile
			startTile = tiles.get(0);
			newKingdom = new Kingdom(startTile.getPlayer());
			gameState.getKingdoms().add(newKingdom);
		}
		// expand outwards from startTile to find connected tiles
		LinkedList<HexTile> todoTiles = new LinkedList<>();
		HashSet<HexTile> doneTiles = new HashSet<>();
		todoTiles.add(startTile);
		while (!todoTiles.isEmpty()) {
			HexTile currentTile = todoTiles.removeFirst();
			newKingdom.getTiles().add(currentTile);
			currentTile.setKingdom(newKingdom);
			doneTiles.add(currentTile);
			for (HexTile expandTile : HexMapHelper.getNeighborTiles(gameState.getMap(), currentTile)) {
				if (expandTile != null && !doneTiles.contains(expandTile) && !todoTiles.contains(expandTile)
						&& expandTile.getKingdom() == oldKingdom) {
					todoTiles.add(expandTile);
				}
			}
		}
		tiles.removeAll(newKingdom.getTiles());

		if (newKingdom.getTiles().size() < 2) {
			// remove contents if kingdom is a single tile
			for (HexTile tile : newKingdom.getTiles()) {
				if (tile.getContent() != null) {
					if (Capital.class.isAssignableFrom(tile.getContent().getClass())) {
						spawnTree(gameState, tile);
					} else if (Castle.class.isAssignableFrom(tile.getContent().getClass())) {
						tile.setContent(null);
					}
				}
			}
			startTile.setKingdom(null);
			gameState.getKingdoms().remove(newKingdom);
		} else if (capitalTile == null) {
			// create capital if necessary
			createCapital(newKingdom);
		}
		// recursive call with the tiles that are not connected
		updateSplitKingdom(gameState, tiles);
		// remove old empty kingdom
		if (oldKingdom.getTiles().isEmpty()) {
			gameState.getKingdoms().remove(oldKingdom);
		}
	}

	/**
	 * Ends the turn.
	 * 
	 * @param gameState GameState to act on
	 */
	public static GameState endTurn(GameState gameState) {
		// check win condition; the winner can change if a player recovers from
		// a really bad situation
		// tiles that belong to no kingdom are irrelevant as they help no player
		int noOfKingdomTiles = gameState.getKingdoms().stream()
				.collect(Collectors.summingInt(kingdom -> kingdom.getTiles().size()));
		for (Kingdom kingdom : gameState.getKingdoms()) {
			if (kingdom.getPlayer() == gameState.getActivePlayer()
					&& kingdom.getTiles().size() >= noOfKingdomTiles * WIN_LANDMASS_PERCENTAGE) {
				gameState.setWinner(kingdom.getPlayer());
			}
		}
		// update active player
		gameState.setPlayerTurn(gameState.getPlayerTurn() + 1);
		if (gameState.getPlayerTurn() >= gameState.getPlayers().size()) {
			// round ended
			gameState.setRound(gameState.getRound() + 1);
			gameState.setPlayerTurn(0);
			spreadTrees(gameState);
		}
		progressBlockingObjects(gameState, gameState.getActivePlayer());
		// check defeat condition
		playerLoop: for (Player player : gameState.getPlayers()) {
			if (player.isDefeated()) {
				continue;
			}
			for (Kingdom kingdom : gameState.getKingdoms()) {
				if (kingdom.getPlayer() == player) {
					continue playerLoop;
				}
			}
			// player has no kingdoms --> is defeated
			player.setDefeated(true);
		}
		// reset active kingdom
		gameState.setActiveKingdom(null);
		for (Kingdom kingdom : gameState.getKingdoms()) {
			// update savings
			if (kingdom.getPlayer() == gameState.getActivePlayer()) {
				kingdom.setSavings(kingdom.getSavings() + getKingdomIncome(kingdom));
				if (kingdom.getSavings() < getKingdomSalaries(gameState, kingdom)) {
					// kill all units if they cannot get paid
					for (HexTile tile : kingdom.getTiles()) {
						if (tile.getContent() != null
								&& ClassReflection.isAssignableFrom(Unit.class, tile.getContent().getClass())) {
							// spawn a gravestone
							tile.setContent(new Gravestone());
						}
					}
				} else {
					kingdom.setSavings(kingdom.getSavings() - getKingdomSalaries(gameState, kingdom));
					// reset canAct and hasActed state
					for (HexTile tile : kingdom.getTiles()) {
						if (tile.getContent() != null
								&& ClassReflection.isAssignableFrom(Unit.class, tile.getContent().getClass())) {
							((Unit) tile.getContent()).setCanAct(true);
						}
					}
				}
			}
			// reset wasActiveInCurrentTurn
			kingdom.setWasActiveInCurrentTurn(false);
		}
		return gameState;
	}

	private static void spreadTrees(GameState gameState) {
		Random random = new Random(gameState.hashCode());
		// keep track of the tiles with trees that are new or have already participated
		// in spreading; those shouldn't spread again in that turn
		HashSet<HexTile> tileBlackList = new HashSet<>();
		for (HexTile tile : gameState.getMap().values()) {
			if (tileBlackList.contains(tile)) {
				continue;
			}
			if (tile.getContent() != null
					&& ClassReflection.isAssignableFrom(Tree.class, tile.getContent().getClass())) {
				// regular trees spread if they have another regular tree next to them
				ArrayList<HexTile> candidates = new ArrayList<>();
				HexTile neighborTreeTile = null;
				for (HexTile neighbor : HexMapHelper.getNeighborTiles(gameState.getMap(), tile)) {
					if (neighbor == null) {
						continue;
					}
					if (neighbor.getContent() == null && !isCoastTile(gameState, neighbor)) {
						candidates.add(neighbor);
					} else if (neighbor.getContent() != null
							&& ClassReflection.isAssignableFrom(Tree.class, neighbor.getContent().getClass())
							&& !tileBlackList.contains(neighbor)) {
						neighborTreeTile = neighbor;
					}
				}
				if (neighborTreeTile != null && !candidates.isEmpty()) {
					HexTile newTreeTile = candidates.get(random.nextInt(candidates.size()));
					spawnTree(gameState, newTreeTile);
					tileBlackList.add(tile);
					tileBlackList.add(newTreeTile);
					tileBlackList.add(neighborTreeTile);
					candidates.clear();
				}
			} else if (tile.getContent() != null
					&& ClassReflection.isAssignableFrom(PalmTree.class, tile.getContent().getClass())) {
				// palm trees always spread to a neighboring coast tile, if any
				HexMapHelper
						.getNeighborTiles(gameState.getMap(), tile).stream().filter(neighbor -> neighbor != null
								&& neighbor.getContent() == null && isCoastTile(gameState, neighbor))
						.limit(1).forEach(newTreeTile -> {
							spawnTree(gameState, newTreeTile);
							tileBlackList.add(newTreeTile);
						});
			}
		}
	}

	private static void progressBlockingObjects(GameState gameState, Player player) {
		for (HexTile tile : gameState.getMap().values()) {
			// gravestones become trees/palms at the start of the player turn
			if (tile.getPlayer() == player && tile.getContent() != null) {
				if (ClassReflection.isAssignableFrom(Gravestone.class, tile.getContent().getClass())) {
					spawnTree(gameState, tile);
				} else if (tile.getKingdom() == null
						&& ClassReflection.isAssignableFrom(Unit.class, tile.getContent().getClass())) {
					// cut off units become gravestones at the start of the player turn
					tile.setContent(new Gravestone());
				}
			}
		}
	}

	/** Spawns a regular or palm tree depending on the position. */
	private static void spawnTree(GameState gameState, HexTile tile) {
		if (isCoastTile(gameState, tile)) {
			tile.setContent(new PalmTree());
		} else {
			tile.setContent(new Tree());
		}
	}

	/** Returns whether a tile is a coast tile (= next to water). */
	private static boolean isCoastTile(GameState gameState, HexTile tile) {
		return HexMapHelper.getNeighborTiles(gameState.getMap(), tile).contains(null);
	}

	/**
	 * Buys a peasant.
	 * 
	 * @param gameState GameState to act on
	 */
	public static void buyPeasant(GameState gameState) {
		gameState.getActiveKingdom().setSavings(gameState.getActiveKingdom().getSavings() - Unit.COST);
		if (gameState.getHeldObject() == null) {
			gameState.setHeldObject(new Unit(UnitTypes.PEASANT));
		} else {
			// held object is guaranteed to be a non-baron unit by the validation
			gameState.setHeldObject(
					new Unit(UnitTypes.ofStrength(((Unit) gameState.getHeldObject()).getStrength() + 1)));
		}

	}

	/**
	 * Buys a castle.
	 * 
	 * @param gameState GameState to act on
	 */
	public static void buyCastle(GameState gameState) {
		gameState.getActiveKingdom().setSavings(gameState.getActiveKingdom().getSavings() - Castle.COST);
		gameState.setHeldObject(new Castle());
	}

	/**
	 * Places a new tile.
	 * 
	 * @param gameState GameState to act on
	 * @param hexCoords coords for the tile
	 * @param player    player that should own the tile
	 */
	public static void placeTile(GameState gameState, Vector2 hexCoords, Player player) {
		HexTile newTile = new HexTile(player, hexCoords);
		gameState.getMap().put(hexCoords, newTile);
	}

	/**
	 * Deletes a tile.
	 * 
	 * @param gameState GameState to act on
	 * @param hexCoords coords of the tile
	 */
	public static void deleteTile(GameState gameState, Vector2 hexCoords) {
		gameState.getMap().remove(hexCoords);
	}

	/**
	 * Determines the protection level of a tile (strength of the strongest object
	 * protecting it).
	 * 
	 * @param gameState GameState to analyze
	 * @param tile      tile to determine the protection level of
	 * @return protection level
	 */
	public static int getProtectionLevel(GameState gameState, HexTile tile) {
		int protectionLevel = 0;
		if (tile.getContent() != null) {
			protectionLevel = tile.getContent().getStrength();
		}
		for (HexTile neighbor : HexMapHelper.getNeighborTiles(gameState.getMap(), tile)) {
			if (neighbor != null && neighbor.getKingdom() != null && tile.getKingdom() == neighbor.getKingdom()
					&& neighbor.getContent() != null && neighbor.getContent().getStrength() > protectionLevel) {
				protectionLevel = neighbor.getContent().getStrength();
			}
		}
		return protectionLevel;
	}

	/**
	 * Calculates the income of a kingdom.
	 * 
	 * @param kingdom relevant kingdom
	 * @return income
	 */
	public static int getKingdomIncome(Kingdom kingdom) {
		// number of tiles - trees & palms
		return kingdom.getTiles().size() - (int) kingdom.getTiles().stream()
				.filter(tile -> tile.getContent() != null
						&& (ClassReflection.isAssignableFrom(Tree.class, tile.getContent().getClass())
								|| ClassReflection.isAssignableFrom(PalmTree.class, tile.getContent().getClass())))
				.count();
	}

	/**
	 * Calculates the salaries a kingdom has to pay every turn.
	 * 
	 * @param kingdom relevant kingdom
	 * @return salaries
	 */
	public static int getKingdomSalaries(GameState gameState, Kingdom kingdom) {
		// sum of the salaries of all the units
		int result = kingdom.getTiles().stream()
				.filter(tile -> tile.getContent() != null
						&& ClassReflection.isAssignableFrom(Unit.class, tile.getContent().getClass()))
				.mapToInt(tile -> ((Unit) tile.getContent()).getUnitType().salary()).sum();
		// if there is a held unit, subtract their salary
		if (gameState.getHeldObject() != null
				&& ClassReflection.isAssignableFrom(Unit.class, gameState.getHeldObject().getClass())) {
			result += ((Unit) gameState.getHeldObject()).getUnitType().salary();
		}
		return result;
	}

	/**
	 * Determines whether a player has likely forgotten to do actions for one of
	 * their kingdoms in the current turn.
	 * 
	 * @param gameState GameState to analyze
	 * @return whether it is the case
	 */
	public static boolean hasActivePlayerlikelyForgottenKingom(GameState gameState) {
		for (Kingdom kingdom : gameState.getKingdoms()) {
			if (kingdom.getPlayer() == gameState.getActivePlayer() && !kingdom.isWasActiveInCurrentTurn()) {
				// can buy castle or any unit that is more expensive
				if (InputValidationHelper.checkBuyObject(gameState, gameState.getActivePlayer(), Castle.class)) {
					return true;
				}
				// has unit stronger than peasant
				boolean hasPeasant = false;
				boolean hasTree = false;
				for (HexTile tile : kingdom.getTiles()) {
					if (tile.getContent() != null
							&& ClassReflection.isAssignableFrom(Unit.class, tile.getContent().getClass())) {
						if (tile.getContent().getStrength() > 1) {
							return true;
						} else if (((Unit) tile.getContent()).getUnitType() == UnitTypes.PEASANT) {
							hasPeasant = true;
						}
					} else if (tile.getContent() != null
							&& ClassReflection.isAssignableFrom(Tree.class, tile.getContent().getClass())) {
						hasTree = true;
					}
				}
				boolean canBuyPeasant = kingdom.getSavings() >= Unit.COST;
				// has or can get peasant that can conquer something or destroy tree
				if (hasPeasant || canBuyPeasant) {
					if (hasTree) {
						return true;
					}
					// there is a neighbor tile which can be conquered by the peasant
					for (HexTile tile : kingdom.getTiles()) {
						for (HexTile neighborTile : HexMapHelper.getNeighborTiles(gameState.getMap(), tile)) {
							if (neighborTile != null && neighborTile.getKingdom() != tile.getKingdom()
									&& getProtectionLevel(gameState, neighborTile) == 0) {
								return true;
							}
						}
					}
				}
			}
		}
		return false;
	}

    /**
     * Determines whether a player's units are at risk due to negative salary.
     * 
     * @param gameState game state of the current game
     * @param player    player attempting the action
     * @return true if player's units are at risk, false otherwise
     */
    public static boolean arePlayerUnitsAtRisk(GameState gameState, Player player) {
        Kingdom activeKingdom = gameState.getActiveKingdom();
		int income = GameStateHelper.getKingdomIncome(activeKingdom);
        int salaries = GameStateHelper.getKingdomSalaries(gameState, activeKingdom);
        int result = income - salaries;
        if (result < 0) {
            return true;
        }
        return false;
	}

	/**
	 * Determines which local player is responsible for any inputs.
	 * 
	 * @return optional containing acting player or empty optional if there is no
	 *         human player
	 */
	public static Optional<Player> determineActingLocalPlayer(GameState gameState) {
		if (gameState.getActivePlayer().getType() == Type.LOCAL_PLAYER) {
			return Optional.of(gameState.getActivePlayer());
		}
		// if it is a bot's turn, assume the first human player attemted the action
		return gameState.getPlayers().stream().filter(player -> player.getType() == Player.Type.LOCAL_PLAYER)
				.findFirst();
	}
}

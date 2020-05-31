package com.sesu8642.feudaltactics.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Capital;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Castle;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Tree;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit.UnitTypes;

public class BotAI {

	final int MUST_PROTECT_SCORE_THRESHOLD = 12;
	final int SHOULD_PROTECT_WITH_UNIT_SCORE_THRESHOLD = 3;

	public enum Intelligence {
		DUMB, MEDIUM, SMART
	}

	private class TileScoreInfo {

		public HexTile tile;
		public int score;

		public TileScoreInfo(HexTile tile, int score) {
			super();
			this.tile = tile;
			this.score = score;
		}
	}

	private class OffenseTileScoreInfo extends TileScoreInfo {

		public int requiredStrength;

		public OffenseTileScoreInfo(HexTile tile, int score, int requiredStrength) {
			super(tile, score);
			this.requiredStrength = requiredStrength;
		}

	}

	private class PickedUpUnits {
		Integer availablePeasants = 0;
		Integer availableSpearmen = 0;
		Integer availableKnights = 0;
		Integer availableBarons = 0;
	}

	public GameState doTurn(GameState gameState, Intelligence intelligence) {
		Kingdom nextKingdom = getNextKingdom(gameState);
		while (nextKingdom != null) {
			nextKingdom.setDoneMoving(true);
			gameState = doKingdomMove(gameState, nextKingdom, intelligence);
			nextKingdom = getNextKingdom(gameState);
		}
		// reset kingdom done moving state
		for (Kingdom kingdom : gameState.getKingdoms()) {
			if (kingdom.isDoneMoving()) {
				kingdom.setDoneMoving(false);
			}
		}
		return gameState;
	}

	private Kingdom getNextKingdom(GameState gameState) {
		for (Kingdom kingdom : gameState.getKingdoms()) {
			if (kingdom.getTiles().size() == 0) {
				// TODO: why is it even there?!
				// continue;
			}
			if (!kingdom.isDoneMoving() && kingdom.getPlayer() == gameState.getActivePlayer()) {
				return kingdom;
			}
		}
		return null;
	}

	private GameState doKingdomMove(GameState gameState, Kingdom kingdom, Intelligence intelligence) {
		gameState.setActiveKingdom(kingdom);
		// pick up all units
		PickedUpUnits pickedUpUnits = new PickedUpUnits();
		pickUpAllAvailableUnits(kingdom, pickedUpUnits);
		// remember the tiles where a castle was placed to reverse the decision later after conquering
		Set<HexTile> placedCastleTiles = new HashSet<HexTile>();
		switch (intelligence) {
		case DUMB:
			chopTrees(gameState, pickedUpUnits, 0.5F);
			conquerAsMuchAsPossible(gameState, pickedUpUnits);
			protectWithLeftoverUnits(gameState, pickedUpUnits);
			break;
		case MEDIUM:
			chopTrees(gameState, pickedUpUnits, 0.7F);
			defendMostImportantTiles(gameState, pickedUpUnits, placedCastleTiles);
			conquerAsMuchAsPossible(gameState, pickedUpUnits);
			protectWithLeftoverUnits(gameState, pickedUpUnits);
			break;
		case SMART:
			chopTrees(gameState, pickedUpUnits, 1F);
			defendMostImportantTiles(gameState, pickedUpUnits, placedCastleTiles);
			conquerAsMuchAsPossible(gameState, pickedUpUnits);
			sellCastles(kingdom, placedCastleTiles);
			pickUpAllAvailableUnits(kingdom, pickedUpUnits);
			defendMostImportantTiles(gameState, pickedUpUnits, placedCastleTiles);
			protectWithLeftoverUnits(gameState, pickedUpUnits);
			break;
		}
		return gameState;
	}

	private void pickUpAllAvailableUnits(Kingdom kingdom, PickedUpUnits pickedUpUnits) {
		for (HexTile tile : kingdom.getTiles()) {
			if (tile.getContent() != null && tile.getContent().getClass().isAssignableFrom(Unit.class)
					&& ((Unit) tile.getContent()).isCanAct()) {
				switch (((Unit) tile.getContent()).getStrength()) {
				case 1:
					pickedUpUnits.availablePeasants++;
					break;
				case 2:
					pickedUpUnits.availableSpearmen++;
					break;
				case 3:
					pickedUpUnits.availableKnights++;
					break;
				case 4:
					pickedUpUnits.availableBarons++;
					break;
				}
				tile.setContent(null);
			}
		}
	}

	private void chopTrees(GameState gameState, PickedUpUnits pickedUpUnits, float chance) {
		for (HexTile tile : gameState.getActiveKingdom().getTiles()) {
			if (tile.getContent() != null && tile.getContent().getClass().isAssignableFrom(Tree.class)) {
				if (gameState.getRandom().nextFloat() <= chance) {
					if (pickedUpUnits.availablePeasants >= 1
							|| acquireUnit(gameState.getActiveKingdom(), pickedUpUnits, 1)) {
						pickedUpUnits.availablePeasants--;
						gameState.setHeldObject(new Unit(gameState.getActiveKingdom(), UnitTypes.PEASANT));
						GameStateController.placeOwn(gameState, tile);
					} else {
						return;
					}
				}
			}
		}
	}

	private void defendMostImportantTiles(GameState gameState, PickedUpUnits pickedUpUnits,
			Set<HexTile> placedCastleTiles) {
		Set<HexTile> interestingProtectionTiles = getInterestingProtectionTiles(gameState);
		TileScoreInfo bestProtectionCandidate = getBestDefenseTileScore(gameState, interestingProtectionTiles);
		while (bestProtectionCandidate.score >= MUST_PROTECT_SCORE_THRESHOLD) {
			// if enough money buy castle
			if (InputValidator.checkBuyObject(gameState, Castle.COST)) {
				GameStateController.buyCastle(gameState);
				GameStateController.placeOwn(gameState, bestProtectionCandidate.tile);
				placedCastleTiles.add(bestProtectionCandidate.tile);
			} else if (pickedUpUnits.availablePeasants > 0) {
				// protect with existing peasant
				pickedUpUnits.availablePeasants--;
				gameState.setHeldObject(new Unit(gameState.getActiveKingdom(), UnitTypes.PEASANT));
				GameStateController.placeOwn(gameState, bestProtectionCandidate.tile);
			} else if (InputValidator.checkBuyObject(gameState, Unit.COST)) {
				// protect with new peasant
				GameStateController.buyPeasant(gameState);
				GameStateController.placeOwn(gameState, bestProtectionCandidate.tile);
			} else {
				break;
			}
			bestProtectionCandidate = getBestDefenseTileScore(gameState, interestingProtectionTiles);
		}
		while (bestProtectionCandidate.score >= SHOULD_PROTECT_WITH_UNIT_SCORE_THRESHOLD) {
			if (pickedUpUnits.availablePeasants > 0 || acquireUnit(gameState.getActiveKingdom(), pickedUpUnits, 1)) {
				// protect with existing peasant
				pickedUpUnits.availablePeasants--;
				gameState.setHeldObject(new Unit(gameState.getActiveKingdom(), UnitTypes.PEASANT));
				GameStateController.placeOwn(gameState, bestProtectionCandidate.tile);
			} else {
				break;
			}
			bestProtectionCandidate = getBestDefenseTileScore(gameState, interestingProtectionTiles);
		}
		return;
	}

	private void conquerAsMuchAsPossible(GameState gameState, PickedUpUnits pickedUpUnits) {
		Kingdom kingdom = gameState.getActiveKingdom();
		boolean unableToConquerAnyMore = false;
		whileloop: while (!unableToConquerAnyMore) {
			Set<HexTile> possibleConquerTiles = new HashSet<HexTile>();
			for (HexTile tile : kingdom.getTiles()) {
				ArrayList<HexTile> neighborTiles = gameState.getMap().getNeighborTiles(tile);
				for (HexTile neighborTile : neighborTiles) {
					if (neighborTile != null && neighborTile.getKingdom() != tile.getKingdom()) {
						possibleConquerTiles.add(neighborTile);
					}
				}
			}
			if (possibleConquerTiles.isEmpty()) {
				// the bot actually won the game
				break;
			}

			Set<OffenseTileScoreInfo> offenseTileScoreInfoSet = Collections
					.newSetFromMap(new ConcurrentHashMap<BotAI.OffenseTileScoreInfo, Boolean>());
			possibleConquerTiles.parallelStream().forEach((conquerTile) -> {
				offenseTileScoreInfoSet.add(getOffenseTileScoreInfo(gameState, conquerTile));
			});
			List<OffenseTileScoreInfo> offenseTileScoreInfos = new ArrayList<OffenseTileScoreInfo>(
					offenseTileScoreInfoSet);
			offenseTileScoreInfos
					.sort((OffenseTileScoreInfo o1, OffenseTileScoreInfo o2) -> Integer.compare(o2.score, o1.score));
			for (OffenseTileScoreInfo offenseTileScoreInfo : offenseTileScoreInfos) {

				switch (offenseTileScoreInfo.requiredStrength) {
				// fall-through intentional
				case 1:
					if (conquerTileWithStoredUnit(gameState, offenseTileScoreInfo.tile, UnitTypes.PEASANT,
							pickedUpUnits.availablePeasants)) {
						pickedUpUnits.availablePeasants--;
						continue whileloop;
					}

				case 2:
					if (conquerTileWithStoredUnit(gameState, offenseTileScoreInfo.tile, UnitTypes.SPEARMAN,
							pickedUpUnits.availableSpearmen)) {
						pickedUpUnits.availableSpearmen--;
						continue whileloop;
					}

				case 3:
					if (conquerTileWithStoredUnit(gameState, offenseTileScoreInfo.tile, UnitTypes.KNIGHT,
							pickedUpUnits.availableKnights)) {
						pickedUpUnits.availableKnights--;
						continue whileloop;
					}

				case 4:
					if (conquerTileWithStoredUnit(gameState, offenseTileScoreInfo.tile, UnitTypes.BARON,
							pickedUpUnits.availableBarons)) {
						pickedUpUnits.availableBarons--;
						continue whileloop;
					}

				default:
					// can not be conquered
					break;
				}

			}
			// at this point no more tiles can be conquered with the existing units --> buy
			// some more or combine
			int minimumRequiredStrengthForConquering = offenseTileScoreInfos
					.stream().min((OffenseTileScoreInfo t1, OffenseTileScoreInfo t2) -> Integer
							.compare(t1.requiredStrength, t2.requiredStrength))
					.orElse(new OffenseTileScoreInfo(null, -1, -1)).requiredStrength;
			if (!acquireUnit(kingdom, pickedUpUnits, minimumRequiredStrengthForConquering)) {
				unableToConquerAnyMore = true;
			}
		}
	}

	private boolean acquireUnit(Kingdom kingdom, PickedUpUnits pickedUpUnits, int strength) {
		switch (strength) {
		case 1:
			// buy peasant
			if (kingdom.getIncome() - getActualKingdomSalaries(kingdom, pickedUpUnits) - UnitTypes.PEASANT.salary() >= 0
					&& kingdom.getSavings() >= Unit.COST) {
				kingdom.setSavings(kingdom.getSavings() - Unit.COST);
				pickedUpUnits.availablePeasants++;
				return true;
			}
			break;
		case 2:
			if (pickedUpUnits.availablePeasants >= 2) {
				// combine 2 existing peasants
				pickedUpUnits.availablePeasants -= 2;
				pickedUpUnits.availableSpearmen++;
				return true;
			} else if (pickedUpUnits.availablePeasants >= 1
					&& kingdom.getIncome() - getActualKingdomSalaries(kingdom, pickedUpUnits)
							- UnitTypes.SPEARMAN.salary() + UnitTypes.PEASANT.salary() >= 0
					&& kingdom.getSavings() >= Unit.COST) { //
				// buy 1 peasant and combine with an existing one
				kingdom.setSavings(kingdom.getSavings() - Unit.COST);
				pickedUpUnits.availableSpearmen++;
				pickedUpUnits.availablePeasants--;
				return true;
			} else if (kingdom.getIncome() - getActualKingdomSalaries(kingdom, pickedUpUnits)
					- UnitTypes.SPEARMAN.salary() >= 0 && kingdom.getSavings() >= Unit.COST * 2) {
				// buy 2 peasants = 1 spearman
				kingdom.setSavings(kingdom.getSavings() - 2 * Unit.COST);
				pickedUpUnits.availableSpearmen++;
				return true;
			}
			break;
		case 3:
			if (pickedUpUnits.availablePeasants >= 1 && pickedUpUnits.availableSpearmen >= 1) {
				// combine spearman and peasant
				pickedUpUnits.availablePeasants--;
				pickedUpUnits.availableSpearmen--;
				pickedUpUnits.availableKnights++;
				return true;
			} else if (pickedUpUnits.availableSpearmen >= 1
					&& kingdom.getIncome() - getActualKingdomSalaries(kingdom, pickedUpUnits)
							- UnitTypes.KNIGHT.salary() + UnitTypes.SPEARMAN.salary() >= 0
					&& kingdom.getSavings() > Unit.COST) { //
				// buy 1 peasant and combine with an existing spearman
				kingdom.setSavings(kingdom.getSavings() - Unit.COST);
				pickedUpUnits.availableSpearmen--;
				pickedUpUnits.availableKnights++;
				return true;
			} else if (kingdom.getIncome() - getActualKingdomSalaries(kingdom, pickedUpUnits)
					- UnitTypes.KNIGHT.salary() >= 0 && kingdom.getSavings() >= Unit.COST * 3) {
				// buy 3 peasants = 1 knight
				kingdom.setSavings(kingdom.getSavings() - 3 * Unit.COST);
				pickedUpUnits.availableKnights++;
				return true;
			}
			break;
		case 4:
			if (pickedUpUnits.availablePeasants >= 1 && pickedUpUnits.availableKnights >= 1) {
				// combine knight and peasant
				pickedUpUnits.availablePeasants--;
				pickedUpUnits.availableKnights--;
				pickedUpUnits.availableBarons++;
				return true;
			} else if (pickedUpUnits.availableKnights >= 1
					&& kingdom.getIncome() - getActualKingdomSalaries(kingdom, pickedUpUnits) - UnitTypes.BARON.salary()
							+ UnitTypes.KNIGHT.salary() >= 0
					&& kingdom.getSavings() >= Unit.COST) {
				// buy 1 peasant and combine with an existing knight
				kingdom.setSavings(kingdom.getSavings() - Unit.COST);
				pickedUpUnits.availableKnights--;
				pickedUpUnits.availableBarons++;
				return true;
			} else if (kingdom.getIncome() - getActualKingdomSalaries(kingdom, pickedUpUnits)
					- UnitTypes.BARON.salary() >= 0 && kingdom.getSavings() >= Unit.COST * 4) {
				// buy 4 peasants = 1 baron
				kingdom.setSavings(kingdom.getSavings() - 4 * Unit.COST);
				pickedUpUnits.availableBarons++;
				return true;
			}
			break;
		}
		return false;
	}

	private void protectWithLeftoverUnits(GameState gameState, PickedUpUnits pickedUpUnits) {
		Set<HexTile> interestingProtectionTiles = getInterestingProtectionTiles(gameState);
		TileScoreInfo bestDefenseTileScore = getBestDefenseTileScore(gameState, interestingProtectionTiles);
		while (bestDefenseTileScore.score >= 0) {
			if (pickedUpUnits.availableBarons > 0) {
				gameState.setHeldObject(new Unit(gameState.getActiveKingdom(), UnitTypes.BARON));
				GameStateController.placeOwn(gameState, bestDefenseTileScore.tile);
				pickedUpUnits.availableBarons--;
			} else if (pickedUpUnits.availableKnights > 0) {
				gameState.setHeldObject(new Unit(gameState.getActiveKingdom(), UnitTypes.KNIGHT));
				GameStateController.placeOwn(gameState, bestDefenseTileScore.tile);
				pickedUpUnits.availableKnights--;
			} else if (pickedUpUnits.availableSpearmen > 0) {
				gameState.setHeldObject(new Unit(gameState.getActiveKingdom(), UnitTypes.SPEARMAN));
				GameStateController.placeOwn(gameState, bestDefenseTileScore.tile);
				pickedUpUnits.availableSpearmen--;
			} else if (pickedUpUnits.availablePeasants > 0) {
				gameState.setHeldObject(new Unit(gameState.getActiveKingdom(), UnitTypes.PEASANT));
				GameStateController.placeOwn(gameState, bestDefenseTileScore.tile);
				pickedUpUnits.availablePeasants--;
			} else {
				break;
			}
			bestDefenseTileScore = getBestDefenseTileScore(gameState, interestingProtectionTiles);
		}
	}

	private void sellCastles(Kingdom kingdom, Set<HexTile> placedCastleTiles) {
		// sell the castles bought earlier to re-assess the situation after conquering
		for (HexTile tile : placedCastleTiles) {
			tile.setContent(null);
			kingdom.setSavings(kingdom.getSavings() + Castle.COST);
		}
	}

	private int getActualKingdomSalaries(Kingdom kingdom, PickedUpUnits pickedUpUnits) {
		return kingdom.getSalaries() + pickedUpUnits.availablePeasants * UnitTypes.PEASANT.salary()
				+ pickedUpUnits.availableSpearmen * UnitTypes.SPEARMAN.salary()
				+ pickedUpUnits.availableKnights * UnitTypes.KNIGHT.salary()
				+ pickedUpUnits.availableBarons * UnitTypes.BARON.salary();
	}

	private Set<HexTile> getInterestingProtectionTiles(GameState gameState) {
		HashSet<HexTile> interestingPlacementTiles = new HashSet<HexTile>();
		for (HexTile tile : gameState.getActiveKingdom().getTiles()) {
			// interestingPlacementTilePositions
			// tile is interesting for placement if it is close to another kingdom
			Collection<HexTile> neighborsNeighbors = gameState.getMap().getNeighborsNeighborTiles(tile);
			for (HexTile neighborsNeighbor : neighborsNeighbors) {
				if (neighborsNeighbor == null) {
					continue;
				}
				if (neighborsNeighbor.getKingdom() != gameState.getActiveKingdom()) {
					interestingPlacementTiles.add(tile);
					break;
				}
			}
		}
		return interestingPlacementTiles;
	}

	private TileScoreInfo getBestDefenseTileScore(GameState gameState, Set<HexTile> interestingProtectionTiles) {
		Set<TileScoreInfo> results = Collections.newSetFromMap(new ConcurrentHashMap<BotAI.TileScoreInfo, Boolean>());
		interestingProtectionTiles.parallelStream().forEach((tile) -> {
			results.add(new TileScoreInfo(tile, getTileDefenseScore(gameState, tile)));
		});
		return results.stream().max((TileScoreInfo t1, TileScoreInfo t2) -> Integer.compare(t1.score, t2.score))
				.orElse(new TileScoreInfo(null, -1));
	}

	private int getTileDefenseScore(GameState gameState, HexTile tile) {
		if (tile.getContent() != null) {
			// already occupied
			return -1;
		}
		// count the enemy tiles next to the tile and 2 tiles away
		int score = 0;
		ArrayList<HexTile> neighborTiles = gameState.getMap().getNeighborTiles(tile);
		for (HexTile neighborTile : neighborTiles) {
			if (neighborTile != null && neighborTile.getKingdom() != null) {
				if (neighborTile.getKingdom() != tile.getKingdom()) {
					score++;
				} else {
					if (neighborTile.getContent() != null && neighborTile.getContent().getStrength() > 0) {
						// already (somewhat) protected
						return 0;
					}
					Collection<HexTile> neighborsNeighbors = gameState.getMap().getNeighborsNeighborTiles(tile);
					int neighborBonus = 0;
					for (HexTile neighborsNeighbor : neighborsNeighbors) {
						if (neighborsNeighbor != null) {
							if (neighborsNeighbor.getKingdom() != tile.getKingdom()) {
								neighborBonus++;
							} else if (neighborsNeighbor.getContent() != null
									&& neighborsNeighbor.getContent().getStrength() > 0) {
								// neighbor tile is already protected
								score = 0;
								neighborBonus = 0;
								break;
							}
						}
					}
					score += neighborBonus;
				}
			}
		}
		return score;
	}

	private OffenseTileScoreInfo getOffenseTileScoreInfo(GameState gameState, HexTile tile) {
		int score;
		int requiredStrength = 1;
		if (tile.getKingdom() == null) {
			if (tile.getContent() == null) {
				// conquering single tiles is not as good as stealing from enemy kingdoms
				score = 1;
			} else {
				// nearby trees might spread to the own kingdom
				score = 5;
			}
		} else {
			if (tile.getContent() != null) {
				if (!tile.getContent().getClass().isAssignableFrom(Capital.class)) {
					// destroying units or castles is better than conquering empty tiles
					score = tile.getContent().getStrength() + 2;
				} else {
					// destroying the capital is very good
					score = 50;
				}
			} else {
				score = 2;
			}
			// find out required strength and add some bonus for tiles next to multiple
			// tiles of the own kingdom
			ArrayList<HexTile> neighborTiles = new ArrayList<HexTile>(gameState.getMap().getNeighborTiles(tile));
			neighborTiles.add(tile);
			for (HexTile neighborTile : neighborTiles) {
				if (neighborTile != null && neighborTile.getKingdom() == tile.getKingdom()
						&& neighborTile.getContent() != null
						&& neighborTile.getContent().getStrength() >= requiredStrength) {
					requiredStrength = neighborTile.getContent().getStrength() + 1;
				} else if (neighborTile != null && neighborTile.getKingdom() == gameState.getActiveKingdom()) {
					score++;
				}
			}
		}
		return new OffenseTileScoreInfo(tile, score, requiredStrength);
	}

	private boolean conquerTileWithStoredUnit(GameState gameState, HexTile tile, Unit.UnitTypes unitType,
			Integer nrAvailableUnits) {
		if (nrAvailableUnits > 0) {
			gameState.setHeldObject(new Unit(gameState.getActiveKingdom(), unitType));
			GameStateController.conquer(gameState, tile);
			return true;
		}
		return false;
	}
}
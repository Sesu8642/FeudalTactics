// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.gamelogic.ingame;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.events.BotTurnFinishedEvent;
import de.sesu8642.feudaltactics.events.GameStateChangeEvent;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Capital;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Castle;
import de.sesu8642.feudaltactics.gamelogic.gamestate.GameState;
import de.sesu8642.feudaltactics.gamelogic.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.gamelogic.gamestate.HexMapHelper;
import de.sesu8642.feudaltactics.gamelogic.gamestate.HexTile;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Kingdom;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Tree;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Unit;
import de.sesu8642.feudaltactics.gamelogic.gamestate.Unit.UnitTypes;
import de.sesu8642.feudaltactics.input.InputValidationHelper;

/** Class that does the turns for bot players. */
@Singleton
public class BotAi {

	private static final String TAG = BotAi.class.getName();
	static final int MUST_PROTECT_SCORE_THRESHOLD = 25;
	static final int SHOULD_PROTECT_WITH_UNIT_SCORE_THRESHOLD = 20;

	/** Possible intelligence levels for the AI. */
	public enum Intelligence {
		DUMB, MEDIUM, SMART
	}

	/** Possible speeds for the preview. */
	public enum Speed {
		HALF(600), NORMAL(300), TIMES_TWO(150), INSTANT(0);

		/**
		 * Time to wait after activating each kingdom as well as after doing the moves
		 * for each one. For the player to see what is happening.
		 */
		public final int tickDelayMs;

		private Speed(int tickDelayMs) {
			this.tickDelayMs = tickDelayMs;
		}
	}

	private EventBus eventBus;

	/** Current speed. */
	private Speed currentSpeed = Speed.NORMAL;

	@Inject
	public BotAi(EventBus eventBus) {
		this.eventBus = eventBus;
	}

	/**
	 * Does the current players turn.
	 * 
	 * @param gameState    game state to do the turn in
	 * @param intelligence intelligence level to use for the turn
	 * @throws InterruptedException if interrupted
	 */
	public void doTurn(GameState gameState, Intelligence intelligence) throws InterruptedException {
		Gdx.app.debug(TAG, String.format("doing the turn for bot player '%s' with intelligence level '%s'",
				gameState.getActivePlayer(), intelligence));
		Random random = new Random(gameState.hashCode());
		Optional<Kingdom> nextKingdomOptional = getNextKingdom(gameState);
		while (nextKingdomOptional.isPresent()) {
			Kingdom nextKingdom = nextKingdomOptional.get();
			nextKingdom.setDoneMoving(true);
			doKingdomMove(gameState, nextKingdom, intelligence, random);
			nextKingdomOptional = getNextKingdom(gameState);
		}
		// reset kingdom done moving state
		for (Kingdom kingdom : gameState.getKingdoms()) {
			if (kingdom.isDoneMoving()) {
				kingdom.setDoneMoving(false);
			}
		}
		eventBus.post(new BotTurnFinishedEvent(gameState));
	}

	private Optional<Kingdom> getNextKingdom(GameState gameState) {
		for (Kingdom kingdom : gameState.getKingdoms()) {
			if (!kingdom.isDoneMoving() && kingdom.getPlayer() == gameState.getActivePlayer()) {
				return Optional.of(kingdom);
			}
		}
		return Optional.empty();
	}

	private GameState doKingdomMove(GameState gameState, Kingdom kingdom, Intelligence intelligence, Random random)
			throws InterruptedException {
		Gdx.app.debug(TAG, String.format("doing moves in kingdom '%s'", kingdom));
		gameState.setActiveKingdom(kingdom);
		delayForPreview(gameState);
		// pick up all units
		PickedUpUnits pickedUpUnits = new PickedUpUnits();
		pickUpAllAvailableUnits(kingdom, pickedUpUnits);
		// remember the tiles where a castle was placed to reverse the decision later
		// after conquering
		Set<HexTile> placedCastleTiles = new HashSet<>();
		switch (intelligence) {
		case DUMB:
			chopTrees(gameState, pickedUpUnits, 0.3F, random);
			// only 50% chance to conquer anything
			if (random.nextFloat() <= 0.5F) {
				conquerAsMuchAsPossible(gameState, pickedUpUnits);
			}
			protectWithLeftoverUnits(gameState, pickedUpUnits);
			break;
		case MEDIUM:
			chopTrees(gameState, pickedUpUnits, 0.7F, random);
			conquerAsMuchAsPossible(gameState, pickedUpUnits);
			protectWithLeftoverUnits(gameState, pickedUpUnits);
			break;
		case SMART:
			chopTrees(gameState, pickedUpUnits, 1F, random);
			defendMostImportantTiles(gameState, pickedUpUnits, placedCastleTiles);
			conquerAsMuchAsPossible(gameState, pickedUpUnits);
			sellCastles(gameState.getActiveKingdom(), placedCastleTiles);
			pickUpAllAvailableUnits(gameState.getActiveKingdom(), pickedUpUnits);
			defendMostImportantTiles(gameState, pickedUpUnits, placedCastleTiles);
			protectWithLeftoverUnits(gameState, pickedUpUnits);
			break;
		default:
			throw new AssertionError("Unknown bot intelligence " + intelligence);
		}
		delayForPreview(gameState);
		return gameState;
	}

	/**
	 * Delays a little for the user to see what is happening.
	 * 
	 * @param gameState intermediate gameState to display as a preview
	 * @throws InterruptedException if interrupted
	 */
	private void delayForPreview(GameState gameState) throws InterruptedException {
		// no need to update the game state if there is no delay to see it anyway
		if (currentSpeed == Speed.INSTANT) {
			return;
		}
		eventBus.post(new GameStateChangeEvent(gameState));
		Thread.sleep(currentSpeed.tickDelayMs);
	}

	private void pickUpAllAvailableUnits(Kingdom kingdom, PickedUpUnits pickedUpUnits) {
		Gdx.app.debug(TAG, "picking up all available units");
		for (HexTile tile : kingdom.getTiles()) {
			if (tile.getContent() != null && ClassReflection.isAssignableFrom(Unit.class, tile.getContent().getClass())
					&& ((Unit) tile.getContent()).isCanAct()) {
				int strength = ((Unit) tile.getContent()).getStrength();
				pickedUpUnits.addUnitOfStrength(strength);
				tile.setContent(null);
			}
		}
	}

	private void chopTrees(GameState gameState, PickedUpUnits pickedUpUnits, float chance, Random random) {
		Gdx.app.debug(TAG, "chopping trees");
		for (HexTile tile : gameState.getActiveKingdom().getTiles()) {
			if (tile.getContent() != null && ClassReflection.isAssignableFrom(Tree.class, tile.getContent().getClass())
					&& random.nextFloat() <= chance) {
				if (pickedUpUnits.ofType(UnitTypes.PEASANT) >= 1 || acquireUnit(gameState, gameState.getActiveKingdom(),
						pickedUpUnits, UnitTypes.PEASANT.strength())) {
					pickedUpUnits.removeUnit(UnitTypes.PEASANT);
					gameState.setHeldObject(new Unit(UnitTypes.PEASANT));
					GameStateHelper.placeOwn(gameState, tile);
				} else {
					return;
				}
			}
		}
	}

	private void defendMostImportantTiles(GameState gameState, PickedUpUnits pickedUpUnits,
			Set<HexTile> placedCastleTiles) {
		Gdx.app.debug(TAG, "defending most important tiles");
		Set<HexTile> interestingProtectionTiles = getInterestingProtectionTiles(gameState);
		TileScoreInfo bestProtectionCandidate = getBestDefenseTileScore(gameState, interestingProtectionTiles);
		while (bestProtectionCandidate.score >= MUST_PROTECT_SCORE_THRESHOLD) {
			// if enough money buy castle
			if (InputValidationHelper.checkBuyObject(gameState, gameState.getActivePlayer(), Castle.COST)) {
				GameStateHelper.buyCastle(gameState);
				GameStateHelper.placeOwn(gameState, bestProtectionCandidate.tile);
				placedCastleTiles.add(bestProtectionCandidate.tile);
			} else if (pickedUpUnits.ofType(UnitTypes.PEASANT) > 0) {
				// protect with existing peasant
				pickedUpUnits.removeUnit(UnitTypes.PEASANT);
				gameState.setHeldObject(new Unit(UnitTypes.PEASANT));
				GameStateHelper.placeOwn(gameState, bestProtectionCandidate.tile);
			} else if (InputValidationHelper.checkBuyObject(gameState, gameState.getActivePlayer(), Unit.COST)) {
				// protect with new peasant
				GameStateHelper.buyPeasant(gameState);
				GameStateHelper.placeOwn(gameState, bestProtectionCandidate.tile);
			} else {
				break;
			}
			bestProtectionCandidate = getBestDefenseTileScore(gameState, interestingProtectionTiles);
		}
		while (bestProtectionCandidate.score >= SHOULD_PROTECT_WITH_UNIT_SCORE_THRESHOLD) {
			if (pickedUpUnits.ofType(UnitTypes.PEASANT) > 0 || acquireUnit(gameState, gameState.getActiveKingdom(),
					pickedUpUnits, UnitTypes.PEASANT.strength())) {
				// protect with existing peasant
				pickedUpUnits.removeUnit(UnitTypes.PEASANT);
				gameState.setHeldObject(new Unit(UnitTypes.PEASANT));
				GameStateHelper.placeOwn(gameState, bestProtectionCandidate.tile);
			} else {
				break;
			}
			bestProtectionCandidate = getBestDefenseTileScore(gameState, interestingProtectionTiles);
		}
	}

	private void conquerAsMuchAsPossible(GameState gameState, PickedUpUnits pickedUpUnits) {
		Gdx.app.debug(TAG, "conquering as much as possible");
		boolean unableToConquerAnyMore = false;
		whileloop: while (!unableToConquerAnyMore) {
			// need a list here to be deterministic
			List<HexTile> possibleConquerTiles = determineNeighboringEnemyTiles(gameState);
			if (possibleConquerTiles.isEmpty()) {
				// the bot actually won the game
				break;
			}

			// determine how "valuable" the tiles are for conquering
			Set<OffenseTileScoreInfo> offenseTileScoreInfoSet = Collections
					.newSetFromMap(new ConcurrentHashMap<BotAi.OffenseTileScoreInfo, Boolean>());
			possibleConquerTiles.parallelStream().forEach(
					conquerTile -> offenseTileScoreInfoSet.add(getOffenseTileScoreInfo(gameState, conquerTile)));
			List<OffenseTileScoreInfo> offenseTileScoreInfos = new ArrayList<>(offenseTileScoreInfoSet);
			offenseTileScoreInfos.sort((OffenseTileScoreInfo o1, OffenseTileScoreInfo o2) -> {
				int result = Integer.compare(o2.score, o1.score);
				// if the score is the same, use the coordinates to eliminate randomness
				if (result == 0) {
					result = o1.tile.compareTo(o2.tile);
				}
				return result;
			});

			for (OffenseTileScoreInfo offenseTileScoreInfo : offenseTileScoreInfos) {
				if (pickedUpUnits.getTotalNoOfUnits() == 0) {
					break;
				}
				for (int i = offenseTileScoreInfo.requiredStrength; i <= UnitTypes.strongest().strength(); i++) {
					if (conquerTileWithStoredUnit(gameState, offenseTileScoreInfo.tile, UnitTypes.ofStrength(i),
							pickedUpUnits.ofStrength(i))) {
						pickedUpUnits.removeUnitOfStrength(i);
						continue whileloop;
					}
				}
			}
			// at this point no more tiles can be conquered with the existing units --> buy
			// some more or combine
			int minimumRequiredStrengthForConquering = offenseTileScoreInfos
					.stream().min((OffenseTileScoreInfo t1, OffenseTileScoreInfo t2) -> Integer
							.compare(t1.requiredStrength, t2.requiredStrength))
					.orElse(new OffenseTileScoreInfo(null, -1, -1)).requiredStrength;
			if (!acquireUnit(gameState, gameState.getActiveKingdom(), pickedUpUnits,
					minimumRequiredStrengthForConquering)) {
				unableToConquerAnyMore = true;
			}
		}
	}

	private List<HexTile> determineNeighboringEnemyTiles(GameState gameState) {
		List<HexTile> result = new ArrayList<>();
		for (HexTile tile : gameState.getActiveKingdom().getTiles()) {
			List<HexTile> neighborTiles = HexMapHelper.getNeighborTiles(gameState.getMap(), tile);
			for (HexTile neighborTile : neighborTiles) {
				if (neighborTile != null && neighborTile.getKingdom() != tile.getKingdom()) {
					result.add(neighborTile);
				}
			}
		}
		return result;
	}

	private boolean acquireUnit(GameState gameState, Kingdom kingdom, PickedUpUnits pickedUpUnits, int strength) {
		Gdx.app.debug(TAG, "acquiring a new unit");
		// this could probably be done in much less lines but be 5x less readable
		// could try with recursion: acquire the next weaker unit first
		switch (strength) {
		case 1:
			return acquirePeasant(gameState, kingdom, pickedUpUnits);
		case 2:
			return acquireSpearman(gameState, kingdom, pickedUpUnits);
		case 3:
			return acquireKnight(gameState, kingdom, pickedUpUnits);
		case 4:
			return acquireBaron(gameState, kingdom, pickedUpUnits);
		default:
			// the requested strength is greater than the strongest unit --> not possible
			return false;
		}
	}

	private boolean acquirePeasant(GameState gameState, Kingdom kingdom, PickedUpUnits pickedUpUnits) {
		if (canKingdomSustainNewUnit(gameState, kingdom, pickedUpUnits, UnitTypes.PEASANT)) {
			buyUnitDirectly(kingdom, pickedUpUnits, UnitTypes.PEASANT);
			return true;
		}
		return false;
	}

	private boolean acquireSpearman(GameState gameState, Kingdom kingdom, PickedUpUnits pickedUpUnits) {
		if (pickedUpUnits.ofType(UnitTypes.PEASANT) >= 2) {
			// combine 2 existing peasants
			pickedUpUnits.removeUnit(UnitTypes.PEASANT, 2);
			pickedUpUnits.addUnit(UnitTypes.SPEARMAN);
			return true;
		} else if (pickedUpUnits.ofType(UnitTypes.PEASANT) >= 1
				&& (GameStateHelper.getKingdomIncome(kingdom)
						- getActualKingdomSalaries(gameState, kingdom, pickedUpUnits) - UnitTypes.SPEARMAN.salary()
						+ UnitTypes.PEASANT.salary() >= 0 || kingdom.getSavings() > UnitTypes.SPEARMAN.salary() * 3)
				&& kingdom.getSavings() >= Unit.COST) {
			// buy 1 peasant and combine with an existing one
			kingdom.setSavings(kingdom.getSavings() - Unit.COST);
			pickedUpUnits.addUnit(UnitTypes.SPEARMAN);
			pickedUpUnits.removeUnit(UnitTypes.PEASANT);
			return true;
		} else if (canKingdomSustainNewUnit(gameState, kingdom, pickedUpUnits, UnitTypes.SPEARMAN)) {
			// buy 2 peasants = 1 spearman
			buyUnitDirectly(kingdom, pickedUpUnits, UnitTypes.SPEARMAN);
			return true;
		}
		return false;
	}

	private boolean acquireKnight(GameState gameState, Kingdom kingdom, PickedUpUnits pickedUpUnits) {
		if (pickedUpUnits.ofType(UnitTypes.PEASANT) >= 1 && pickedUpUnits.ofType(UnitTypes.SPEARMAN) >= 1) {
			// combine spearman and peasant
			pickedUpUnits.removeUnit(UnitTypes.PEASANT);
			pickedUpUnits.removeUnit(UnitTypes.SPEARMAN);
			pickedUpUnits.addUnit(UnitTypes.KNIGHT);
			return true;
		} else if (pickedUpUnits.ofType(UnitTypes.SPEARMAN) >= 1
				&& (GameStateHelper.getKingdomIncome(kingdom)
						- getActualKingdomSalaries(gameState, kingdom, pickedUpUnits) - UnitTypes.KNIGHT.salary()
						+ UnitTypes.SPEARMAN.salary() >= 0 || kingdom.getSavings() > UnitTypes.KNIGHT.salary() * 3)
				&& kingdom.getSavings() > Unit.COST) {
			// buy 1 peasant and combine with an existing spearman
			kingdom.setSavings(kingdom.getSavings() - Unit.COST);
			pickedUpUnits.removeUnit(UnitTypes.SPEARMAN);
			pickedUpUnits.addUnit(UnitTypes.KNIGHT);
			return true;
		} else if (canKingdomSustainNewUnit(gameState, kingdom, pickedUpUnits, UnitTypes.KNIGHT)) {
			// buy 3 peasants = 1 knight
			buyUnitDirectly(kingdom, pickedUpUnits, UnitTypes.KNIGHT);
			return true;
		}
		return false;
	}

	private boolean acquireBaron(GameState gameState, Kingdom kingdom, PickedUpUnits pickedUpUnits) {
		// this does not have all possible combination options to get a baron
		if (pickedUpUnits.ofType(UnitTypes.PEASANT) >= 1 && pickedUpUnits.ofType(UnitTypes.KNIGHT) >= 1) {
			// combine knight and peasant
			pickedUpUnits.removeUnit(UnitTypes.PEASANT);
			pickedUpUnits.removeUnit(UnitTypes.KNIGHT);
			pickedUpUnits.addUnit(UnitTypes.BARON);
			return true;
		} else if (pickedUpUnits.ofType(UnitTypes.KNIGHT) >= 1
				&& (GameStateHelper.getKingdomIncome(kingdom)
						- getActualKingdomSalaries(gameState, kingdom, pickedUpUnits) - UnitTypes.BARON.salary()
						+ UnitTypes.KNIGHT.salary() >= 0 || kingdom.getSavings() > UnitTypes.BARON.salary() * 3)
				&& kingdom.getSavings() >= Unit.COST) {
			// buy 1 peasant and combine with an existing knight
			kingdom.setSavings(kingdom.getSavings() - Unit.COST);
			pickedUpUnits.removeUnit(UnitTypes.KNIGHT);
			pickedUpUnits.addUnit(UnitTypes.BARON);
			return true;
		} else if (canKingdomSustainNewUnit(gameState, kingdom, pickedUpUnits, UnitTypes.BARON)) {
			// buy 4 peasants = 1 baron
			buyUnitDirectly(kingdom, pickedUpUnits, UnitTypes.BARON);
			return true;
		}
		return false;
	}

	private boolean canKingdomSustainNewUnit(GameState gameState, Kingdom kingdom, PickedUpUnits pickedUpUnits,
			UnitTypes unitType) {
		// this does not account for units that will no longer be there after combining
		// them to get the new one
		return ((GameStateHelper.getKingdomIncome(kingdom) - getActualKingdomSalaries(gameState, kingdom, pickedUpUnits)
				- unitType.salary() >= 0 || kingdom.getSavings() > unitType.salary() * 3)
				&& kingdom.getSavings() >= Unit.COST * unitType.strength());
	}

	private void buyUnitDirectly(Kingdom kingdom, PickedUpUnits pickedUpUnits, UnitTypes unitType) {
		kingdom.setSavings(kingdom.getSavings() - Unit.COST * unitType.strength());
		pickedUpUnits.addUnit(unitType);
	}

	private void protectWithLeftoverUnits(GameState gameState, PickedUpUnits pickedUpUnits) {
		Gdx.app.debug(TAG, "protecting the kingdom with leftover units");
		Set<HexTile> interestingProtectionTiles = getInterestingProtectionTiles(gameState);
		TileScoreInfo bestDefenseTileScore = getBestDefenseTileScore(gameState, interestingProtectionTiles);
		while (bestDefenseTileScore.score >= 0) {
			if (pickedUpUnits.getTotalNoOfUnits() == 0) {
				break;
			}
			// use the strongest units to protect the most important tiles --> use negative
			// strength to get strongest units first
			List<UnitTypes> orderedUnitTypes = Arrays.stream(UnitTypes.values())
					.sorted(Comparator.comparingInt(type -> type.strength() * -1)).collect(Collectors.toList());
			for (UnitTypes type : orderedUnitTypes) {
				if (pickedUpUnits.ofType(type) > 0) {
					gameState.setHeldObject(new Unit(type));
					GameStateHelper.placeOwn(gameState, bestDefenseTileScore.tile);
					pickedUpUnits.removeUnit(type);
					break;
				}
			}
			bestDefenseTileScore = getBestDefenseTileScore(gameState, interestingProtectionTiles);
		}
		placeLeftOverUnitsSomeWhere(gameState, pickedUpUnits);
	}

	private void placeLeftOverUnitsSomeWhere(GameState gameState, PickedUpUnits pickedUpUnits) {
		for (UnitTypes type : UnitTypes.values()) {
			for (int i = 0; i < pickedUpUnits.ofType(type); i++) {
				Optional<HexTile> emptyOrTreeTileOptional = findEmptyOrTreeTileInActiveKingdom(gameState);
				if (emptyOrTreeTileOptional.isPresent()) {
					gameState.setHeldObject(new Unit(type));
					GameStateHelper.placeOwn(gameState, emptyOrTreeTileOptional.get());
				} else {
					Gdx.app.error(TAG, "Unable to place leftover unit because there are no available spaces.");
				}
			}
		}
	}

	private Optional<HexTile> findEmptyOrTreeTileInActiveKingdom(GameState gameState) {
		return gameState.getActiveKingdom().getTiles().stream().filter(tile -> tile.getContent() == null
				|| ClassReflection.isAssignableFrom(Tree.class, tile.getContent().getClass())).findFirst();
	}

	private void sellCastles(Kingdom kingdom, Set<HexTile> placedCastleTiles) {
		Gdx.app.debug(TAG, "selling previously bought castles again");
		// sell the castles bought earlier to re-assess the situation after conquering
		for (HexTile tile : placedCastleTiles) {
			tile.setContent(null);
			kingdom.setSavings(kingdom.getSavings() + Castle.COST);
		}
	}

	private int getActualKingdomSalaries(GameState gameState, Kingdom kingdom, PickedUpUnits pickedUpUnits) {
		int result = GameStateHelper.getKingdomSalaries(gameState, kingdom);
		for (UnitTypes type : UnitTypes.values()) {
			result += pickedUpUnits.ofType(type) * type.salary();
		}
		return result;
	}

	private Set<HexTile> getInterestingProtectionTiles(GameState gameState) {
		HashSet<HexTile> interestingPlacementTiles = new HashSet<>();
		for (HexTile tile : gameState.getActiveKingdom().getTiles()) {
			// tile is interesting for placement if it is close to another kingdom
			List<HexTile> neighborsNeighbors = HexMapHelper.getNeighborsNeighborTiles(gameState.getMap(), tile);
			for (HexTile neighborsNeighbor : neighborsNeighbors) {
				if (neighborsNeighbor != null && neighborsNeighbor.getKingdom() != gameState.getActiveKingdom()) {
					interestingPlacementTiles.add(tile);
					break;
				}
			}
		}
		return interestingPlacementTiles;
	}

	private TileScoreInfo getBestDefenseTileScore(GameState gameState, Set<HexTile> interestingProtectionTiles) {
		Set<TileScoreInfo> results = Collections.newSetFromMap(new ConcurrentHashMap<BotAi.TileScoreInfo, Boolean>());
		interestingProtectionTiles.parallelStream()
				.forEach(tile -> results.add(new TileScoreInfo(tile, getTileDefenseScore(gameState, tile))));
		return results.stream().max((TileScoreInfo t1, TileScoreInfo t2) -> {
			int result = Integer.compare(t1.score, t2.score);
			// if the score is the same, use the coordinates to eliminate randomness
			if (result == 0) {
				result = t1.tile.compareTo(t2.tile);
			}
			return result;
		}).orElse(new TileScoreInfo(null, -1));
	}

	private int getTileDefenseScore(GameState gameState, HexTile tile) {
		if (tile.getContent() != null) {
			// already occupied
			return -1;
		}
		// count the tiles that will be protected
		boolean tileIsBorder = false;
		boolean tileIsProtected = false;
		int score = 0;
		List<HexTile> neighborTiles = HexMapHelper.getNeighborTiles(gameState.getMap(), tile);
		for (HexTile neighborTile : neighborTiles) {
			boolean neighborIsBorder = false;
			boolean neighborIsProtected = false;
			if (neighborTile != null && neighborTile.getKingdom() != null) {
				if (neighborTile.getKingdom() != tile.getKingdom()) {
					// the tile itself is worth protecting
					tileIsBorder = true;
				} else {
					if (neighborTile.getContent() != null && neighborTile.getContent().getStrength() > 0) {
						// the tile is already (somewhat) protected
						tileIsProtected = true;
						neighborIsProtected = true;
					}
					List<HexTile> neighborsNeighbors = HexMapHelper.getNeighborTiles(gameState.getMap(), neighborTile);
					for (HexTile neighborsNeighbor : neighborsNeighbors) {
						if (neighborsNeighbor != null) {
							if (neighborsNeighbor.getKingdom() != null
									&& neighborsNeighbor.getKingdom() != tile.getKingdom()) {
								neighborIsBorder = true;
							} else if (neighborsNeighbor.getKingdom() == tile.getKingdom()
									&& neighborsNeighbor.getContent() != null
									&& neighborsNeighbor.getContent().getStrength() > 0) {
								neighborIsProtected = true;
							}
						}
					}
				}
			}
			if (neighborIsBorder) {
				// the 1 is there because it is better to protect a tile twice than to place
				// the unit somewhere useless
				score += neighborIsProtected ? 1 : 10;
			}
		}
		if (tileIsBorder) {
			// only 5 because it should be preferred to place the unit not directly at the
			// border
			score += tileIsProtected ? 1 : 5;
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
				if (!ClassReflection.isAssignableFrom(Capital.class, tile.getContent().getClass())) {
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
			ArrayList<HexTile> neighborTiles = new ArrayList<>(HexMapHelper.getNeighborTiles(gameState.getMap(), tile));
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
		Gdx.app.debug(TAG, String.format("conquering tile '%s' with stored unit '%s'", tile, unitType));
		if (nrAvailableUnits > 0) {
			gameState.setHeldObject(new Unit(unitType));
			GameStateHelper.conquer(gameState, tile);
			return true;
		}
		return false;
	}

	public Speed getCurrentSpeed() {
		return currentSpeed;
	}

	public void setCurrentSpeed(Speed currentSpeed) {
		this.currentSpeed = currentSpeed;
		Gdx.app.debug(TAG, "Bot turn speed set to " + currentSpeed);
	}

	private class TileScoreInfo {

		HexTile tile;
		int score;

		public TileScoreInfo(HexTile tile, int score) {
			this.tile = tile;
			this.score = score;
		}
	}

	private class OffenseTileScoreInfo extends TileScoreInfo {

		int requiredStrength;

		public OffenseTileScoreInfo(HexTile tile, int score, int requiredStrength) {
			super(tile, score);
			this.requiredStrength = requiredStrength;
		}

	}

	private class PickedUpUnits {

		/** key = strength of the unit; value: number of picked up units. */
		private Map<Integer, Integer> internalPickedUpUnits = new HashMap<>();

		public PickedUpUnits() {
			for (UnitTypes type : UnitTypes.values()) {
				internalPickedUpUnits.put(type.strength(), 0);
			}
		}

		public Integer ofType(UnitTypes type) {
			return ofStrength(type.strength());
		}

		public Integer ofStrength(int strength) {
			return internalPickedUpUnits.get(strength);
		}

		public void addUnit(UnitTypes type) {
			addUnitOfStrength(type.strength());
		}

		public void addUnitOfStrength(int strength) {
			internalPickedUpUnits.put(strength, internalPickedUpUnits.get(strength) + 1);
		}

		public void removeUnit(UnitTypes type) {
			removeUnit(type, 1);
		}

		public void removeUnit(UnitTypes type, int amount) {
			removeUnitOfStrength(type.strength(), amount);
		}

		public void removeUnitOfStrength(int strength) {
			removeUnitOfStrength(strength, 1);
		}

		public void removeUnitOfStrength(int strength, int amount) {
			internalPickedUpUnits.put(strength, internalPickedUpUnits.get(strength) - amount);
		}

		public int getTotalNoOfUnits() {
			return internalPickedUpUnits.values().stream().mapToInt(Integer::intValue).sum();
		}
	}
}
package com.sesu8642.feudaltactics.engine.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit.UnitTypes;

class PossibleGameState {

	public enum Action {
		CONQUER, PLACEOWN, COMBINE
	}

	private GameState gameState;
	private PossibleGameState parent;
	private Float overallScore;
	private HashMap<Vector2, Float> tileScores = new HashMap<Vector2, Float>();
	private Vector2 targetTilePosition; // where a unit was placed
	private Vector2 sourceTilePosition; // where a unit was taken from
	private Action actionTaken;
	// all the tiles in the active kingdom where it might make sense to place an
	// object at
	private HashSet<Vector2> interestingPlacementTilePositions;
	// all the tiles next to the active kingdom which it might make sense to conquer
	private HashSet<Vector2> interestingConquerTilePositions;
	// like interestingConquerTilePositions but only the ones next to the tile
	// conquered before
	private HashSet<Vector2> interestingConquerChainTilePositions;
	// all the tiles with units that are not barons
	private HashSet<Vector2> interestingCombineTilePositions;

	public PossibleGameState(GameState gameState) {
		// this is the root PossibleGamestate
		this.gameState = gameState;
		initializeInterestingTilePositions();
		calculateOverallScore();
	}

	public PossibleGameState(PossibleGameState parent) {
		this.parent = parent;
		this.gameState = new GameState(parent.getGameState());
		updateInterestingTilePositions();
	}

	private void initializeInterestingTilePositions() {
		Collection<HexTile> tilesToUpdate = gameState.getActiveKingdom().getTiles();
		interestingPlacementTilePositions = new HashSet<Vector2>();
		interestingConquerTilePositions = new HashSet<Vector2>();
		interestingCombineTilePositions = new HashSet<Vector2>();
		for (HexTile tileToUpdate : tilesToUpdate) {
			// interestingCombineTilePositions
			if (tileToUpdate.getContent() != null && tileToUpdate.getContent().getClass().isAssignableFrom(Unit.class)
					&& !(((Unit) tileToUpdate.getContent()).getUnitType() == UnitTypes.BARON)) {
				interestingCombineTilePositions.add(tileToUpdate.getPosition());
			}
			// interestingPlacementTilePositions
			boolean isInteresting = false;
			if (tileToUpdate.getContent() != null
					&& (tileToUpdate.getContent().getClass().isAssignableFrom(Tree.class))) {
				// tile is interesting for placement if it contains a tree
				isInteresting = true;
			} else {
				// tile is interesting for placement if it is close to another kingdom
				// check the tiles in a radius of 1 for other kingdoms
				Collection<Vector2> neighborTilePositions = gameState.getMap()
						.getNeighborCoords(tileToUpdate.getPosition());
				for (Vector2 neighborTilePostion : neighborTilePositions) {
					HexTile neighborTile = gameState.getMap().getTiles().get(neighborTilePostion);
					if (neighborTile != null && neighborTile.getKingdom() != gameState.getActiveKingdom()) {
						isInteresting = true;
						// this also means that neighbor tile might be conquered
						interestingConquerTilePositions.add(neighborTilePostion);
					} else { // check the tiles in a radius of 2 for other kingdoms
						Collection<Vector2> neighborsNeighborTilePositions = gameState.getMap()
								.getNeighborCoords(neighborTilePostion);
						neighborsNeighborTilePositions.removeAll(neighborTilePositions);
						for (Vector2 neighborsNeighborTilePostion : neighborsNeighborTilePositions) {
							HexTile neighborsNeighborTile = gameState.getMap().getTiles()
									.get(neighborsNeighborTilePostion);
							if (neighborsNeighborTile != null
									&& neighborsNeighborTile.getKingdom() != gameState.getActiveKingdom()) {
								isInteresting = true;
							}
						}
					}
				}
			}
			if (isInteresting) {
				interestingPlacementTilePositions.add(tileToUpdate.getPosition());
			}
		}
	}

	public void updateInterestingTilePositions() {
		interestingPlacementTilePositions = new HashSet<Vector2>(parent.getInterestingPlacementTilePositions());
		interestingConquerTilePositions = new HashSet<Vector2>(parent.getInterestingConquerTilePositions());
		interestingCombineTilePositions = new HashSet<Vector2>(parent.getInterestingCombineTilePositions());
		if (parent.getTargetTilePosition() == null) {
			return;
		}
		Collection<Vector2> tilePositionsToUpdate = new ArrayList<Vector2>();
		tilePositionsToUpdate.addAll(gameState.getMap().getNeighborCoords(parent.getTargetTilePosition()));
		tilePositionsToUpdate.add(parent.targetTilePosition);
		if (parent.getSourceTilePosition() != null) {
			tilePositionsToUpdate.addAll(gameState.getMap().getNeighborCoords(parent.getSourceTilePosition()));
			tilePositionsToUpdate.add(parent.sourceTilePosition);
		}
		if (parent.getActionTaken() == Action.CONQUER) {
			interestingConquerChainTilePositions = new HashSet<Vector2>();
		}
		for (Vector2 tilePostionToUpdate : tilePositionsToUpdate) {
			HexTile tileToUpdate = gameState.getMap().getTiles().get(tilePostionToUpdate);
			if (tileToUpdate != null && tileToUpdate.getKingdom() == gameState.getActiveKingdom()) {
				// interestingCombineTilePositions
				if (tileToUpdate.getContent() != null
						&& tileToUpdate.getContent().getClass().isAssignableFrom(Unit.class)
						&& !(((Unit) tileToUpdate.getContent()).getUnitType() == UnitTypes.BARON)) {
					interestingCombineTilePositions.add(tilePostionToUpdate);
				} else {
					interestingCombineTilePositions.remove(tilePostionToUpdate);
				}
				// interestingPlacementTilePositions
				boolean isInteresting = false;
				if (tileToUpdate.getContent() != null
						&& (tileToUpdate.getContent().getClass().isAssignableFrom(Tree.class))) {
					// tile is interesting for placement if it contains a tree
					isInteresting = true;
				} else {
					// tile is interesting for placement if it is close to another kingdom
					// check the tiles in a radius of 1 for other kingdoms
					Collection<Vector2> neighborTilePositions = gameState.getMap()
							.getNeighborCoords(tilePostionToUpdate);
					for (Vector2 neighborTilePostion : neighborTilePositions) {
						HexTile neighborTile = gameState.getMap().getTiles().get(neighborTilePostion);
						if (neighborTile != null && neighborTile.getKingdom() != gameState.getActiveKingdom()) {
							isInteresting = true;
							// this also means that neighbor tile might be conquered
							interestingConquerTilePositions.add(neighborTilePostion);
							if (interestingConquerChainTilePositions != null) {
								interestingConquerChainTilePositions.add(neighborTilePostion);
							}
						} else {
							// this also means that the neighbor tile might NOT be conquered
							interestingConquerTilePositions.remove(neighborTilePostion);
							// check the tiles in a radius of 2 for other kingdoms
							Collection<Vector2> neighborsNeighborTilePositions = gameState.getMap()
									.getNeighborCoords(neighborTilePostion);
							neighborsNeighborTilePositions.removeAll(neighborTilePositions);
							for (Vector2 neighborsNeighborTilePostion : neighborsNeighborTilePositions) {
								HexTile neighborsNeighborTile = gameState.getMap().getTiles()
										.get(neighborsNeighborTilePostion);
								if (neighborsNeighborTile != null
										&& neighborsNeighborTile.getKingdom() != gameState.getActiveKingdom()) {
									isInteresting = true;
								}
							}
						}
					}
				}
				if (isInteresting) {
					interestingPlacementTilePositions.add(tilePostionToUpdate);
				} else {
					interestingPlacementTilePositions.remove(tilePostionToUpdate);
				}
			}
		}
	}

	private float calculateOverallScore() {
		// tileScores = new HashMap<Vector2, Float>();
		overallScore = 0F;
		for (Kingdom kingdom : gameState.getKingdoms()) {
			overallScore += calculateKingdomScore(kingdom);
		}
		return overallScore;
	}

	private float calculateKingdomScore(Kingdom kingdom) {
		float kingdomScore = 0F;
		if (kingdom.getPlayer() == gameState.getActivePlayer()) {
			boolean ignoreUnits = false;
			if (kingdom.getSavings() / 2 + kingdom.getIncome() - kingdom.getSalaries() < 0) {
				ignoreUnits = true;
			}
			for (HexTile tile : kingdom.getTiles()) {
				if (tile.getPlayer() == gameState.getActivePlayer()) {
					Vector2 tilePosition = tile.getPosition();
					Float tileScore = getTileScore(tilePosition);
					if (tileScore == null) {
						tileScore = calculateTileScore(tile, ignoreUnits);
						tileScores.put(tilePosition, tileScore);
					}
					kingdomScore += tileScore;
				}
			}
		}
		kingdomScore -= kingdom.getSalaries();
		return kingdomScore;
	}

	private float updateOverallScore() {
		ArrayList<HexTile> tilesToUpdate = new ArrayList<HexTile>();
		tilesToUpdate.addAll(gameState.getMap().getNeighborTiles(targetTilePosition));
		tilesToUpdate.add(gameState.getMap().getTiles().get(targetTilePosition));
		if (sourceTilePosition != null) {
			tilesToUpdate.addAll(gameState.getMap().getNeighborTiles(sourceTilePosition));
			tilesToUpdate.add(gameState.getMap().getTiles().get(sourceTilePosition));
		}
		for (HexTile tileToUpdate : tilesToUpdate) {
			if (tileToUpdate == null) {
				continue;
			}
			Kingdom kingdom = tileToUpdate.getKingdom();
			if (kingdom == gameState.getActiveKingdom()) {
				boolean ignoreUnits = false;
				if (kingdom.getSavings() / 2 + kingdom.getIncome() - kingdom.getSalaries() < 0) {
					ignoreUnits = true;
				}
				float tileScore = calculateTileScore(tileToUpdate, ignoreUnits);
				tileScores.put(tileToUpdate.getPosition(), tileScore);
			}
		}
		return calculateOverallScore();
	}

	private float calculateTileScore(HexTile tile, boolean ignoreUnits) {
		float score = 50;
		int protectedLevel = 0;
		int enemyNeighborTilesCount = 0;
		ArrayList<HexTile> neighborTiles = gameState.getMap().getNeighborTiles(tile.getPosition());
		neighborTiles.add(tile);
		for (HexTile neighborTile : neighborTiles) {
			if (neighborTile != null) {
				if (neighborTile.getPlayer() != gameState.getActivePlayer() && neighborTile.getKingdom() != null) {
					enemyNeighborTilesCount++;
				} else if (neighborTile.getContent() != null && neighborTile.getKingdom() == tile.getKingdom()
						&& protectedLevel < neighborTile.getContent().getStrength()) {
					protectedLevel = neighborTile.getContent().getStrength();
				}
			}
		}
		if (ignoreUnits) {
			protectedLevel = 0;
		}
		score -= enemyNeighborTilesCount * (10 - protectedLevel * 2);

		if (tile.getContent() != null) {
			if (tile.getContent().getClass().isAssignableFrom(Tree.class)) {
				score = -10000; // trees are bad TODO tweak
			}
		}
		if (tile.getContent() != null && tile.getContent().getClass().isAssignableFrom(Unit.class)) {
			score += ((Unit) tile.getContent()).getStrength() ^ 5 * 20;
		}
		return score;
	}

	private Float getTileScore(Vector2 position) {
		Float score = tileScores.get(position);
		if (score == null) {
			if (parent == null) {
				return null;
			}
			score = parent.getTileScore(position);
		}
		return score;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	public float getOverallScore() {
		if (overallScore != null) {
			return overallScore;
		}
		calculateOverallScore();
		return overallScore;
	}

	public void setOverallScore(float overallScore) {
		this.overallScore = overallScore;
	}

	public HashMap<Vector2, Float> getTileScores() {
		return tileScores;
	}

	public void setTileScores(HashMap<Vector2, Float> tileScores) {
		this.tileScores = tileScores;
	}

	public Vector2 getTargetTilePosition() {
		return targetTilePosition;
	}

	public void setTargetTilePosition(Vector2 changedTilePosition) {
		this.targetTilePosition = changedTilePosition;
	}

	public HashSet<Vector2> getInterestingPlacementTilePositions() {
		return interestingPlacementTilePositions;
	}

	public void setInterestingPlacementTilePositions(HashSet<Vector2> interestingPlacementTilePositions) {
		this.interestingPlacementTilePositions = interestingPlacementTilePositions;
	}

	public HashSet<Vector2> getInterestingConquerTilePositions() {
		return interestingConquerChainTilePositions != null ? interestingConquerChainTilePositions
				: interestingConquerTilePositions;
	}

	public void setInterestingConquerTilePositions(HashSet<Vector2> interestingChainingTilePositions) {
		this.interestingConquerTilePositions = interestingChainingTilePositions;
	}

	public Action getActionTaken() {
		return actionTaken;
	}

	public void setActionTaken(Action actionTaken) {
		this.actionTaken = actionTaken;
		updateOverallScore();
	}

	public HashSet<Vector2> getInterestingCombineTilePositions() {
		return interestingCombineTilePositions;
	}

	public void setInterestingCombineTilePositions(HashSet<Vector2> interestingCombineTilePositions) {
		this.interestingCombineTilePositions = interestingCombineTilePositions;
	}

	public Vector2 getSourceTilePosition() {
		return sourceTilePosition;
	}

	public void setSourceTilePosition(Vector2 sourceTilePosition) {
		this.sourceTilePosition = sourceTilePosition;
	}

	public PossibleGameState getParent() {
		return parent;
	}

	public void setParent(PossibleGameState parent) {
		this.parent = parent;
	}

}

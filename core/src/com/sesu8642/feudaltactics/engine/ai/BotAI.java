package com.sesu8642.feudaltactics.engine.ai;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.ui.Tree;
import com.sesu8642.feudaltactics.engine.GameStateController;
import com.sesu8642.feudaltactics.engine.InputValidator;
import com.sesu8642.feudaltactics.engine.ai.PossibleGameState.Action;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Castle;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit.UnitTypes;

// TODO PROBLEM: units get placed for protecting but then tiles next to them get conquered
// TODO problem?!: combining units is probably worse than the last gamestate

public class BotAI {

	private final int maxActionsPerTypeLastGeneration = 5;
	private int maxGenerations;
	private PossibleGameState highScoreState;

	public BotAI(int maxGenerations) {
		this.maxGenerations = maxGenerations;
	}

	public GameState doTurn(GameState gameState) {
		Kingdom nextKingdom;
		nextKingdom = getNextKingdom(gameState);
		while (nextKingdom != null) {
			nextKingdom.setDoneMoving(true);
			gameState = doKingdomMove(gameState, nextKingdom);
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
			if (!kingdom.isDoneMoving() && kingdom.getPlayer() == gameState.getActivePlayer()) {
				return kingdom;
			}
		}
		return null;
	}

	private GameState doKingdomMove(GameState gameState, Kingdom kingdom) {
		// for matching the same kingdom in different game states
		Vector2 kingdomTilePos = ((HexTile) kingdom.getTiles().toArray()[0]).getPosition();
		ArrayList<HashSet<PossibleGameState>> gameStateGenerations;
		gameState.setActiveKingdom(kingdom);
		highScoreState = new PossibleGameState(gameState);
		boolean done = false;
		while (!done) {
			gameStateGenerations = new ArrayList<HashSet<PossibleGameState>>();
			// get all the possible game states
			gameStateGenerations.add(new HashSet<PossibleGameState>());
			gameStateGenerations.get(0).add(highScoreState);
			for (int i = 1; i <= maxGenerations; i++) {
				gameStateGenerations.add(i, new HashSet<PossibleGameState>());
				for (PossibleGameState previousGenerationGameState : gameStateGenerations.get(i - 1)) {
					Kingdom previousGameStateKingdom = previousGenerationGameState.getGameState().getMap().getTiles()
							.get(kingdomTilePos).getKingdom();
					previousGenerationGameState.getGameState().setActiveKingdom(previousGameStateKingdom);
					HashSet<PossibleGameState> possibleGameStatesAfterAction = getPossibleGameStatesAfterAction(
							previousGenerationGameState, previousGenerationGameState.getGameState().getMap().getTiles()
									.get(kingdomTilePos).getKingdom(),
							i);
					gameStateGenerations.get(i).addAll(possibleGameStatesAfterAction);
					if (i == 1 && possibleGameStatesAfterAction.isEmpty()) {
						done = true;
						for (HexTile tile : highScoreState.getGameState().getActiveKingdom().getTiles()) {
							if (tile.getContent() != null && tile.getContent().getClass().isAssignableFrom(Unit.class)
									&& !((Unit) tile.getContent()).isHasActed()) {
							}
						}
					}
				}
			}
		}
		return highScoreState.getGameState();
	}

	private HashSet<PossibleGameState> getPossibleGameStatesAfterAction(PossibleGameState initialGameState,
			Kingdom kingdom, int generation) {
		// for every game state remember the tile that changed
		HashSet<PossibleGameState> possibleGameStates = new HashSet<PossibleGameState>();
		HashSet<UnitTypes> unitTypesTried = new HashSet<UnitTypes>(); // optimization: only try to place one unit of
																		// each type
		GameState gameState = initialGameState.getGameState();
		PossibleGameState possibleGameState;
		if (InputValidator.checkBuyObject(gameState, Unit.COST)) {
			unitTypesTried.add(UnitTypes.PEASANT);
			possibleGameState = new PossibleGameState(initialGameState);
			GameStateController.buyPeasant(possibleGameState.getGameState());
			possibleGameStates.addAll(getPossiblePlaceOwnGameStates(possibleGameState, generation));
			possibleGameStates.addAll(getPossibleCombineGameStates(possibleGameState, generation));
			possibleGameStates.addAll(getPossibleConquerGameStates(possibleGameState, generation));
		}
		if (InputValidator.checkBuyObject(gameState, Castle.COST)) {
			possibleGameState = new PossibleGameState(initialGameState);
			GameStateController.buyCastle(possibleGameState.getGameState());
			possibleGameStates.addAll(getPossibleCastlePlacementGameStates(possibleGameState, generation));

		}
		for (HexTile kingdomTile : kingdom.getTiles()) {
			if (kingdomTile.getContent() != null && kingdomTile.getContent().getClass().isAssignableFrom(Unit.class)
					&& !unitTypesTried.contains(((Unit) kingdomTile.getContent()).getUnitType())
					&& ((Unit) kingdomTile.getContent()).isCanAct()
					&& !((Unit) kingdomTile.getContent()).isHasActed()) { // optimization: only move each unit once
				unitTypesTried.add(((Unit) kingdomTile.getContent()).getUnitType());
				possibleGameState = new PossibleGameState(initialGameState);
				GameStateController.pickupObject(possibleGameState.getGameState(),
						possibleGameState.getGameState().getMap().getTiles().get(kingdomTile.getPosition()));
				possibleGameState.setSourceTilePosition(kingdomTile.getPosition());
				possibleGameStates.addAll(getPossiblePlaceOwnGameStates(possibleGameState, generation));
				possibleGameStates.addAll(getPossibleCombineGameStates(possibleGameState, generation));
				possibleGameStates.addAll(getPossibleConquerGameStates(possibleGameState, generation));
			}
		}
		return possibleGameStates;
	}

	private HashSet<PossibleGameState> getPossiblePlaceOwnGameStates(PossibleGameState initialPossibleGameState,
			int generation) {
		HashSet<PossibleGameState> possibleGameStates = new HashSet<PossibleGameState>();
		if (initialPossibleGameState.getActionTaken() == null) {
			GameState initialGameState = initialPossibleGameState.getGameState();
			// optimization: when having multiple trees, it doesn't matter much which one to
			// destroy
			boolean placedOnTree = false;
			float previousGenerationScore = initialPossibleGameState.getOverallScore();
			for (Vector2 targetTilePosition : initialPossibleGameState.getInterestingPlacementTilePositions()) {
				HexTile targetTile = initialGameState.getMap().getTiles().get(targetTilePosition);
				if (!targetTilePosition.equals(initialPossibleGameState.getSourceTilePosition()) && InputValidator
						.checkPlaceOwn(initialGameState, initialGameState.getActivePlayer(), targetTile)) {
					if (targetTile.getContent() != null
							&& targetTile.getContent().getClass().isAssignableFrom(Tree.class)) {
						if (placedOnTree) {
							continue;
						} else {
							placedOnTree = true;
						}
					}
					PossibleGameState newPossibleGameState = new PossibleGameState(initialPossibleGameState);
					newPossibleGameState.setSourceTilePosition(initialPossibleGameState.getSourceTilePosition());
					GameState possibleGameState = newPossibleGameState.getGameState();
					GameStateController.placeOwn(possibleGameState,
							possibleGameState.getMap().getTiles().get(targetTile.getPosition()));
					newPossibleGameState.setTargetTilePosition(targetTile.getPosition());
					if (isGameStateGood(previousGenerationScore, newPossibleGameState)) {
						newPossibleGameState.setActionTaken(Action.PLACEOWN);
						possibleGameStates.add(newPossibleGameState);
						if (generation == maxGenerations
								&& possibleGameStates.size() >= maxActionsPerTypeLastGeneration) {
							return possibleGameStates;
						}
					}
				}
			}
		}
		return possibleGameStates;
	}

	private HashSet<PossibleGameState> getPossibleCombineGameStates(PossibleGameState initialPossibleGameState,
			int generation) {
		HashSet<PossibleGameState> possibleGameStates = new HashSet<PossibleGameState>();
		if ((initialPossibleGameState.getActionTaken() == null
				|| initialPossibleGameState.getActionTaken() == Action.COMBINE)) {
			GameState initialGameState = initialPossibleGameState.getGameState();
			float previousGenerationScore = initialPossibleGameState.getOverallScore();
			// optimization: only try each unit type as combining target once
			HashSet<UnitTypes> doneTargetUnittypes = new HashSet<UnitTypes>();
			for (Vector2 targetTilePosition : initialPossibleGameState.getInterestingCombineTilePositions()) {
				HexTile targetTile = initialGameState.getMap().getTiles().get(targetTilePosition);
				if (!targetTilePosition.equals(initialPossibleGameState.getSourceTilePosition())
						&& !doneTargetUnittypes.contains(((Unit) targetTile.getContent()).getUnitType())
						&& InputValidator.checkCombineUnits(initialGameState, initialGameState.getActivePlayer(),
								targetTile)) {
					PossibleGameState newPossibleGameState = new PossibleGameState(initialPossibleGameState);
					newPossibleGameState.setSourceTilePosition(initialPossibleGameState.getSourceTilePosition());
					GameState possibleGameState = newPossibleGameState.getGameState();
					GameStateController.combineUnits(possibleGameState,
							possibleGameState.getMap().getTiles().get(targetTile.getPosition()));
					newPossibleGameState.setTargetTilePosition(targetTile.getPosition());
					newPossibleGameState.setActionTaken(Action.COMBINE);
					doneTargetUnittypes.add(((Unit) targetTile.getContent()).getUnitType());
					if (isGameStateGood(previousGenerationScore, newPossibleGameState)) {
						possibleGameStates.add(newPossibleGameState);
						if (generation == maxGenerations
								&& possibleGameStates.size() >= maxActionsPerTypeLastGeneration) {
							return possibleGameStates;
						}
					}
				}
			}
		}
		return possibleGameStates;
	}

	private HashSet<PossibleGameState> getPossibleConquerGameStates(PossibleGameState initialPossibleGameState,
			int generation) {
		HashSet<PossibleGameState> possibleGameStates = new HashSet<PossibleGameState>();
		if (initialPossibleGameState.getActionTaken() != Action.PLACEOWN) {
			GameState initialGameState = initialPossibleGameState.getGameState();
			float previousGenerationScore = initialPossibleGameState.getOverallScore();
			for (Vector2 targetTilePosition : initialPossibleGameState.getInterestingConquerTilePositions()) {
				HexTile targetTile = initialGameState.getMap().getTiles().get(targetTilePosition);
				if (InputValidator.checkConquer(initialGameState, initialGameState.getActivePlayer(), targetTile)) {
					PossibleGameState newPossibleGameState = new PossibleGameState(initialPossibleGameState);
					newPossibleGameState.setSourceTilePosition(initialPossibleGameState.getSourceTilePosition());
					GameState possibleGameState = newPossibleGameState.getGameState();
					GameStateController.conquer(possibleGameState,
							possibleGameState.getMap().getTiles().get(targetTile.getPosition()));
					newPossibleGameState.setTargetTilePosition(targetTile.getPosition());
					newPossibleGameState.setActionTaken(Action.CONQUER);
					if (isGameStateGood(previousGenerationScore, newPossibleGameState)) {
						possibleGameStates.add(newPossibleGameState);
						if (generation == maxGenerations
								&& possibleGameStates.size() >= maxActionsPerTypeLastGeneration) {
							return possibleGameStates;
						}
					}
				}
			}
		}
		return possibleGameStates;
	}

	private HashSet<PossibleGameState> getPossibleCastlePlacementGameStates(PossibleGameState initialPossibleGameState,
			int generation) {
		GameState initialGameState = initialPossibleGameState.getGameState();
		HashSet<PossibleGameState> possibleGameStates = new HashSet<PossibleGameState>();
		if (initialPossibleGameState.getActionTaken() != null) {
			return possibleGameStates;
		}
		float previousGenerationScore = initialPossibleGameState.getOverallScore();
		for (HexTile tile : initialGameState.getActiveKingdom().getTiles()) {
			if (initialPossibleGameState.getInterestingPlacementTilePositions().contains(tile.getPosition())) {
				if (InputValidator.checkPlaceOwn(initialGameState, initialGameState.getActivePlayer(), tile)) {
					PossibleGameState newPossibleGameState = new PossibleGameState(initialPossibleGameState);
					newPossibleGameState.setSourceTilePosition(initialPossibleGameState.getSourceTilePosition());
					GameState possibleGameState = newPossibleGameState.getGameState();
					GameStateController.placeOwn(possibleGameState,
							possibleGameState.getMap().getTiles().get(tile.getPosition()));
					newPossibleGameState.setGameState(possibleGameState);
					newPossibleGameState.setTargetTilePosition(tile.getPosition());
					newPossibleGameState.setActionTaken(Action.PLACEOWN);
					if (isGameStateGood(previousGenerationScore, newPossibleGameState)) {
						possibleGameStates.add(newPossibleGameState);
						if (generation == maxGenerations
								&& possibleGameStates.size() >= maxActionsPerTypeLastGeneration) {
							return possibleGameStates;
						}
					}
				}
			}
		}
		return possibleGameStates;
	}

	private boolean isGameStateGood(float previousGenerationScore, PossibleGameState possibleGameState) {
		float score = possibleGameState.getOverallScore();
		// optimization: only keep the game states that are better than the previous one
		if (score > previousGenerationScore) {
			if (score > highScoreState.getOverallScore()) {
				highScoreState = possibleGameState;
			}
			return true;
		} else {
			return false;
		}
	}
	
}
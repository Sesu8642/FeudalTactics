package com.sesu8642.feudaltactics.engine;

import java.util.ArrayList;
import java.util.LinkedList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamestate.Player;
import com.sesu8642.feudaltactics.gamestate.Player.Type;
import com.sesu8642.feudaltactics.screens.IngameScreen;
import com.sesu8642.feudaltactics.screens.IngameScreen.IngameStages;

public class GameController {

	public final static Color[] PLAYER_COLORS = { new Color(0F, 1F, 1F, 1), new Color(0.75F, 0.5F, 0F, 1),
			new Color(1F, 0.67F, 0.67F, 1), new Color(1F, 1F, 0F, 1), new Color(1F, 1F, 1F, 1),
			new Color(0F, 1F, 0F, 1) };

	private MapRenderer mapRenderer;
	private GameState gameState;
	private IngameScreen ingameScreen;
	private LinkedList<GameState> undoStates;
	BotAI botAI = new BotAI();

	public GameController() {
		this.gameState = new GameState();
		this.undoStates = new LinkedList<GameState>();
	}

	public void generateMap(int humanPlayerNo, int botPlayerNo, BotAI.Intelligence botIntelligence, Long seed,
			float landMass, float density) {
		gameState.setBotIntelligence(botIntelligence);
		ArrayList<Player> players = new ArrayList<Player>();
		int remainingHumanPlayers = humanPlayerNo;
		int remainingBotPlayers = botPlayerNo;
		for (Color color : PLAYER_COLORS) {
			if (remainingHumanPlayers > 0) {
				remainingHumanPlayers--;
				players.add(new Player(color, Type.LOCAL_PLAYER));
			} else if (remainingBotPlayers > 0) {
				players.add(new Player(color, Type.LOCAL_BOT));
			} else {
				break;
			}
		}
		Long actualSeed = GameStateController.initializeMap(gameState, players, landMass, density, null, seed);
		updateSeedText(actualSeed.toString());
		mapRenderer.updateMap(gameState);
	}

	public void printTileInfo(Vector2 hexCoords) {
		System.out.println("clicked tile position " + hexCoords);
		System.out.println(gameState.getMap().getTiles().get(hexCoords));
	}

	public void updateSeedText(String seedText) {
		ingameScreen.getMenuStage().setBottomLabelText("Seed: " + seedText);
	}

	public void updateInfoText() {
		if (ingameScreen == null) {
			return;
		}
		Kingdom kingdom = gameState.getActiveKingdom();
		if (kingdom == null) {
			ingameScreen.getHudStage().setInfoText("");
			return;
		}
		int income = kingdom.getIncome();
		int salaries = kingdom.getSalaries();
		int result = income - salaries;
		int savings = kingdom.getSavings();
		String resultText = result < 0 ? String.valueOf(result) : "+" + result;
		String infoText = "Savings: " + savings + " (" + resultText + ")";
		ingameScreen.getHudStage().setInfoText(infoText);
	}

	public void activateKingdom(Kingdom kingdom) {
		GameStateController.activateKingdom(gameState, kingdom);
		updateInfoText();
		mapRenderer.updateMap(gameState);
	}

	public void pickupObject(HexTile tile) {
		undoStates.add(new GameState(this.gameState));
		GameStateController.pickupObject(gameState, tile);
		mapRenderer.updateMap(gameState);
		ingameScreen.getHudStage().updateHandContent(gameState.getHeldObject().getSpriteName());
	}

	public void placeOwn(HexTile tile) {
		undoStates.add(new GameState(this.gameState));
		GameStateController.placeOwn(gameState, tile);
		mapRenderer.updateMap(gameState);
		ingameScreen.getHudStage().updateHandContent(null);
	}

	public void combineUnits(HexTile tile) {
		undoStates.add(new GameState(this.gameState));
		GameStateController.combineUnits(gameState, tile);
		mapRenderer.updateMap(gameState);
		updateInfoText();
		ingameScreen.getHudStage().updateHandContent(null);
	}

	public void conquer(HexTile tile) {
		undoStates.add(new GameState(this.gameState));
		GameStateController.conquer(gameState, tile);
		mapRenderer.updateMap(gameState);
		updateInfoText();
		ingameScreen.getHudStage().updateHandContent(null);
	}

	public void endTurn() {
		gameState = GameStateController.endTurn(gameState);
		// clear undo states
		undoStates.clear();
		// reset info text
		updateInfoText();
		mapRenderer.updateMap(gameState);
		// make bots act
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			gameState = botAI.doTurn(gameState, gameState.getBotIntelligence());
			endTurn();
		}
	}

	public void buyPeasant() {
		undoStates.add(new GameState(this.gameState));
		GameStateController.buyPeasant(gameState);
		updateInfoText();
		mapRenderer.updateMap(gameState);
		ingameScreen.getHudStage().updateHandContent(gameState.getHeldObject().getSpriteName());
	}

	public void buyCastle() {
		undoStates.add(new GameState(this.gameState));
		GameStateController.buyCastle(gameState);
		mapRenderer.updateMap(gameState);
		updateInfoText();
		ingameScreen.getHudStage().updateHandContent(gameState.getHeldObject().getSpriteName());
	}

	public void undoLastAction() {
		this.gameState = undoStates.removeLast();
		mapRenderer.updateMap(gameState);
		updateInfoText();
		if (gameState.getHeldObject() != null) {
			ingameScreen.getHudStage().updateHandContent(gameState.getHeldObject().getSpriteName());
		} else {
			ingameScreen.getHudStage().updateHandContent(null);
		}
	}

	public void toggleMenu() {
		ingameScreen.activateStage(IngameStages.MENU);
	}

	public void setHud(IngameScreen gameUIOverlay) {
		this.ingameScreen = gameUIOverlay;
	}

	public MapRenderer getMapRenderer() {
		return mapRenderer;
	}

	public void setMapRenderer(MapRenderer mapRenderer) {
		this.mapRenderer = mapRenderer;
	}

	public GameState getGameState() {
		return gameState;
	}

	public LinkedList<GameState> getUndoStates() {
		return undoStates;
	}

}
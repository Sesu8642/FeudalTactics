package com.sesu8642.feudaltactics.engine;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamestate.Player;
import com.sesu8642.feudaltactics.gamestate.Player.Type;
import com.sesu8642.feudaltactics.screens.IngameScreen;

public class GameController {

	public final static Color[] PLAYER_COLORS = { new Color(0F, 1F, 1F, 1), new Color(0.75F, 0.5F, 0F, 1),
			new Color(1F, 0.67F, 0.67F, 1), new Color(1F, 1F, 0F, 1), new Color(1F, 1F, 1F, 1),
			new Color(0F, 1F, 0F, 1) };

	private MapRenderer mapRenderer;
	private GameState gameState;
	private IngameScreen ingameScreen;
	BotAI botAI = new BotAI();

	public GameController() {
		this.gameState = new GameState();
	}

	public void startGame() {
		// if a bot begins, make it act
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			gameState = botAI.doTurn(gameState, gameState.getBotIntelligence());
			endTurn();
		}
	}
	
	private void autosave() {
		PreferencesHelper.autoSaveGameState(gameState);
	}

	public void loadLatestAutosave() {
		gameState = PreferencesHelper.getLatestAutoSave();
		mapRenderer.updateMap(gameState);
		if (gameState.getHeldObject() != null) {
			ingameScreen.getHudStage().updateHandContent(gameState.getHeldObject().getSpriteName());
		}
		updateInfoText();
		updateSeedText(gameState.getSeed().toString());
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
		GameStateHelper.initializeMap(gameState, players, landMass, density, null, seed);
		updateSeedText(gameState.getSeed().toString());
		mapRenderer.updateMap(gameState);
		PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
		autosave();
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
		GameStateHelper.activateKingdom(gameState, kingdom);
		updateInfoText();
		mapRenderer.updateMap(gameState);
		autosave();
	}

	public void pickupObject(HexTile tile) {
		GameStateHelper.pickupObject(gameState, tile);
		mapRenderer.updateMap(gameState);
		ingameScreen.getHudStage().updateHandContent(gameState.getHeldObject().getSpriteName());
		autosave();
	}

	public void placeOwn(HexTile tile) {
		GameStateHelper.placeOwn(gameState, tile);
		mapRenderer.updateMap(gameState);
		ingameScreen.getHudStage().updateHandContent(null);
		autosave();
	}

	public void combineUnits(HexTile tile) {
		GameStateHelper.combineUnits(gameState, tile);
		mapRenderer.updateMap(gameState);
		updateInfoText();
		ingameScreen.getHudStage().updateHandContent(null);
		autosave();
	}

	public void conquer(HexTile tile) {
		GameStateHelper.conquer(gameState, tile);
		mapRenderer.updateMap(gameState);
		updateInfoText();
		ingameScreen.getHudStage().updateHandContent(null);
		autosave();
	}

	public void endTurn() {
		// remember old winner
		Player oldWinner = gameState.getWinner();
		// update gameState
		gameState = GameStateHelper.endTurn(gameState);
		// check if player lost
		if (gameState.getActivePlayer().getType() == Type.LOCAL_PLAYER && gameState.getActivePlayer().isDefeated()) {
			ingameScreen.showLostMessage();
		} else {
			// check if winner changed
			if (oldWinner != gameState.getWinner()) {
				ingameScreen.showGiveUpGameMessage(gameState.getWinner().getType() == Type.LOCAL_PLAYER,
						gameState.getWinner().getColor());
			}
		}
		// reset info text
		updateInfoText();
		mapRenderer.updateMap(gameState);
		// make bots act
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			gameState = botAI.doTurn(gameState, gameState.getBotIntelligence());
			endTurn();
		} else {
			// autosave when a player turn begins
			autosave();
			// clear autosaves from previous turn
			PreferencesHelper.deleteAllAutoSaveExceptLatestN(1);
		}
	}

	public void buyPeasant() {
		GameStateHelper.buyPeasant(gameState);
		updateInfoText();
		mapRenderer.updateMap(gameState);
		ingameScreen.getHudStage().updateHandContent(gameState.getHeldObject().getSpriteName());
		autosave();
	}

	public void buyCastle() {
		GameStateHelper.buyCastle(gameState);
		mapRenderer.updateMap(gameState);
		updateInfoText();
		ingameScreen.getHudStage().updateHandContent(gameState.getHeldObject().getSpriteName());
		autosave();
	}

	public void undoLastAction() {
		if (PreferencesHelper.getNoOfAutoSaves() > 1) {
			// 1 means the current state is the only one saved
			// remove the current state from autosaves
			PreferencesHelper.deleteLatestAutoSave();
			// load the previous state
			GameState loaded = PreferencesHelper.getLatestAutoSave();
			gameState = loaded;
			mapRenderer.updateMap(gameState);
			updateInfoText();
			if (gameState.getHeldObject() != null) {
				ingameScreen.getHudStage().updateHandContent(gameState.getHeldObject().getSpriteName());
			} else {
				ingameScreen.getHudStage().updateHandContent(null);
			}
		}
	}

	public void toggleMenu() {
		ingameScreen.tooglePause();
	}
	
	public void placeCameraForFullMapView(long marginLeftPx, long marginBottomPx, long marginRightPx, long marginTopPx) {
		mapRenderer.placeCameraForFullMapView(gameState, marginLeftPx, marginBottomPx, marginRightPx, marginTopPx);
	}

	public void setIngameScreen(IngameScreen gameUIOverlay) {
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
}
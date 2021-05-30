package com.sesu8642.feudaltactics;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.dagger.IngameRenderer;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.GameStateHelper;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.Kingdom;
import com.sesu8642.feudaltactics.gamestate.Player;
import com.sesu8642.feudaltactics.gamestate.Player.Type;
import com.sesu8642.feudaltactics.preferences.PreferencesHelper;

@Singleton
public class GameController {

	public final static Color[] PLAYER_COLORS = { new Color(0.2F, 0.45F, 0.8F, 1), new Color(0.75F, 0.5F, 0F, 1),
			new Color(1F, 0.67F, 0.67F, 1), new Color(1F, 1F, 0F, 1), new Color(1F, 1F, 1F, 1),
			new Color(0F, 1F, 0F, 1) };

	public final static String GAME_STATE_OBSERVABLE_PROPERTY_NAME = "gameState";
	
	private MapRenderer mapRenderer;
	private BotAI botAI;
	private GameState gameState;
	// for observing the GameState
	private PropertyChangeSupport propertyChangeSupport;

	@Inject
	public GameController(@IngameRenderer MapRenderer mapRenderer, BotAI botAI) {
		this.mapRenderer = mapRenderer;
		this.botAI = botAI;
		this.propertyChangeSupport = new PropertyChangeSupport(this);
		gameState = new GameState();
	}

	public void startGame() {
		// if a bot begins, make it act
		if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT) {
			gameState = botAI.doTurn(gameState, gameState.getBotIntelligence());
			endTurn();
		}
		PreferencesHelper.deleteAllAutoSaveExceptLatestN(0);
		autosave();
		propertyChangeSupport.firePropertyChange(GAME_STATE_OBSERVABLE_PROPERTY_NAME, null, gameState);
	}
	
	private void autosave() {
		PreferencesHelper.autoSaveGameState(gameState);
	}

	public void loadLatestAutosave() {
		gameState = PreferencesHelper.getLatestAutoSave();
		mapRenderer.updateMap(gameState);
		propertyChangeSupport.firePropertyChange(GAME_STATE_OBSERVABLE_PROPERTY_NAME, null, gameState);
	}

	public void generateMap(int humanPlayerNo, int botPlayerNo, BotAI.Intelligence botIntelligence, Long seed,
			float landMass, float density) {
		gameState = new GameState();
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
		mapRenderer.updateMap(gameState);
		propertyChangeSupport.firePropertyChange(GAME_STATE_OBSERVABLE_PROPERTY_NAME, null, gameState);
	}

	public void printTileInfo(Vector2 hexCoords) {
		System.out.println("clicked tile position " + hexCoords);
		System.out.println(gameState.getMap().getTiles().get(hexCoords));
	}

	public void activateKingdom(Kingdom kingdom) {
		GameStateHelper.activateKingdom(gameState, kingdom);
		mapRenderer.updateMap(gameState);
		autosave();
		// save first because is is relevant for the undo button status
		propertyChangeSupport.firePropertyChange(GAME_STATE_OBSERVABLE_PROPERTY_NAME, null, gameState);
	}

	public void pickupObject(HexTile tile) {
		GameStateHelper.pickupObject(gameState, tile);
		mapRenderer.updateMap(gameState);
		autosave();
		propertyChangeSupport.firePropertyChange(GAME_STATE_OBSERVABLE_PROPERTY_NAME, null, gameState);
	}

	public void placeOwn(HexTile tile) {
		GameStateHelper.placeOwn(gameState, tile);
		mapRenderer.updateMap(gameState);
		autosave();
		propertyChangeSupport.firePropertyChange(GAME_STATE_OBSERVABLE_PROPERTY_NAME, null, gameState);
	}

	public void combineUnits(HexTile tile) {
		GameStateHelper.combineUnits(gameState, tile);
		mapRenderer.updateMap(gameState);
		autosave();
		propertyChangeSupport.firePropertyChange(GAME_STATE_OBSERVABLE_PROPERTY_NAME, null, gameState);
	}

	public void conquer(HexTile tile) {
		GameStateHelper.conquer(gameState, tile);
		mapRenderer.updateMap(gameState);
		autosave();
		propertyChangeSupport.firePropertyChange(GAME_STATE_OBSERVABLE_PROPERTY_NAME, null, gameState);
	}

	public void endTurn() {
		// remember old state
		GameState oldState = new GameState(gameState);
		// update gameState
		gameState = GameStateHelper.endTurn(gameState);
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
			propertyChangeSupport.firePropertyChange(GAME_STATE_OBSERVABLE_PROPERTY_NAME, oldState, gameState);
		}
	}

	public void buyPeasant() {
		GameStateHelper.buyPeasant(gameState);
		mapRenderer.updateMap(gameState);
		autosave();
		propertyChangeSupport.firePropertyChange(GAME_STATE_OBSERVABLE_PROPERTY_NAME, null, gameState);
	}

	public void buyCastle() {
		GameStateHelper.buyCastle(gameState);
		mapRenderer.updateMap(gameState);
		autosave();
		propertyChangeSupport.firePropertyChange(GAME_STATE_OBSERVABLE_PROPERTY_NAME, null, gameState);
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
		}
		propertyChangeSupport.firePropertyChange(GAME_STATE_OBSERVABLE_PROPERTY_NAME, null, gameState);
	}

	public void placeCameraForFullMapView(long marginLeftPx, long marginBottomPx, long marginRightPx, long marginTopPx) {
		mapRenderer.placeCameraForFullMapView(gameState, marginLeftPx, marginBottomPx, marginRightPx, marginTopPx);
	}

    public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
    }
	
	public MapRenderer getMapRenderer() {
		return mapRenderer;
	}

	public GameState getGameState() {
		return gameState;
	}
}
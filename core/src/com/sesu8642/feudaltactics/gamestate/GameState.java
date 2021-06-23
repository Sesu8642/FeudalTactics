package com.sesu8642.feudaltactics.gamestate;

import java.util.ArrayList;
import java.util.Random;
import java.util.Map.Entry;

import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.BotAI;
import com.sesu8642.feudaltactics.gamestate.mapobjects.MapObject;

public class GameState {

	private ArrayList<Player> players;
	private Player winner = null;
	private int playerTurn = 0;
	private HexMap map;
	private ArrayList<Kingdom> kingdoms;
	private Kingdom activeKingdom;
	private MapObject heldObject;
	private Random random = new Random();
	private BotAI.Intelligence botIntelligence;
	private Long seed;

	public GameState() {
	}

	public static GameState copyOf(GameState original) {
		// create a deep copy of the original
		// random is not actually copied but that should do
		GameState result = new GameState();
		result.playerTurn = original.playerTurn;
		result.players = new ArrayList<Player>();
		result.map = new HexMap();
		result.botIntelligence = original.botIntelligence;
		result.kingdoms = new ArrayList<Kingdom>();
		for (Player originalPlayer : original.getPlayers()) {
			Player newPlayer = originalPlayer.clone();
			result.players.add(newPlayer);
		}
		for (Kingdom originalKingdom : original.getKingdoms()) {
			Kingdom newKingdom = new Kingdom(
					result.players.get(original.getPlayers().indexOf(originalKingdom.getPlayer())));
			newKingdom.setSavings(originalKingdom.getSavings());
			newKingdom.setDoneMoving(originalKingdom.isDoneMoving());
			newKingdom.setWasActiveInCurrentTurn(originalKingdom.isWasActiveInCurrentTurn());
			result.kingdoms.add(newKingdom);
		}
		if (original.getActiveKingdom() != null) {
			result.activeKingdom = result.kingdoms.get(original.getKingdoms().indexOf(original.getActiveKingdom()));
		}
		for (Entry<Vector2, HexTile> originalTileEntry : original.getMap().getTiles().entrySet()) {
			HexTile originalTile = originalTileEntry.getValue();
			HexTile newTile = new HexTile(result.players.get(original.getPlayers().indexOf(originalTile.getPlayer())),
					new Vector2(originalTileEntry.getKey()));
			if (originalTile.getKingdom() != null) {
				newTile.setKingdom(result.kingdoms.get(original.getKingdoms().indexOf(originalTile.getKingdom())));
				newTile.getKingdom().getTiles().add(newTile);
			}
			if (originalTile.getContent() != null) {
				newTile.setContent(originalTile.getContent().getCopy());
			}
			result.map.getTiles().put(newTile.getPosition(), newTile);
		}
		if (original.getHeldObject() != null) {
			result.setHeldObject(original.getHeldObject().getCopy());
		}
		return result;
	}
	
	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
	}

	public Player getWinner() {
		return winner;
	}

	public void setWinner(Player winner) {
		this.winner = winner;
	}

	public int getPlayerTurn() {
		return playerTurn;
	}

	public void setPlayerTurn(int playerTurn) {
		this.playerTurn = playerTurn;
	}

	public HexMap getMap() {
		return map;
	}

	public void setMap(HexMap map) {
		this.map = map;
	}

	public ArrayList<Kingdom> getKingdoms() {
		return kingdoms;
	}

	public void setKingdoms(ArrayList<Kingdom> kingdoms) {
		this.kingdoms = kingdoms;
	}

	public Kingdom getActiveKingdom() {
		return activeKingdom;
	}

	public void setActiveKingdom(Kingdom activeKingdom) {
		this.activeKingdom = activeKingdom;
	}

	public MapObject getHeldObject() {
		return heldObject;
	}

	public void setHeldObject(MapObject heldObject) {
		this.heldObject = heldObject;
	}

	public Player getActivePlayer() {
		return players.get(playerTurn);
	}

	public Random getRandom() {
		return random;
	}

	public BotAI.Intelligence getBotIntelligence() {
		return botIntelligence;
	}

	public void setBotIntelligence(BotAI.Intelligence botIntelligence) {
		this.botIntelligence = botIntelligence;
	}

	public Long getSeed() {
		return seed;
	}

	public void setSeed(Long seed) {
		this.seed = seed;
	}

}

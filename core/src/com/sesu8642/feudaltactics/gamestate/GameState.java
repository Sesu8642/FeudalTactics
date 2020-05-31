package com.sesu8642.feudaltactics.gamestate;

import java.util.ArrayList;
import java.util.Random;
import java.util.Map.Entry;

import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.engine.BotAI;
import com.sesu8642.feudaltactics.gamestate.mapobjects.MapObject;

public class GameState {

	private ArrayList<Player> players;
	private int playerTurn = 0;
	private HexMap map;
	private ArrayList<Kingdom> kingdoms;
	private Kingdom activeKingdom;
	private MapObject heldObject;
	private Random random;
	private BotAI.Intelligence botIntelligence;

	public GameState() {
	}

	public GameState(GameState original) {
		// create a deep copy of the original
		// random is not actually copied but that should do
		this.random = new Random(original.getRandom().nextLong());
		this.playerTurn = original.playerTurn;
		this.players = new ArrayList<Player>();
		this.map = new HexMap();
		this.botIntelligence = original.botIntelligence;
		this.kingdoms = new ArrayList<Kingdom>();
		for (Player originalPlayer : original.getPlayers()) {
			Player newPlayer = originalPlayer.clone();
			this.players.add(newPlayer);
		}
		for (Kingdom originalKingdom : original.getKingdoms()) {
			Kingdom newKingdom = new Kingdom(
					this.players.get(original.getPlayers().indexOf(originalKingdom.getPlayer())));
			newKingdom.setSavings(originalKingdom.getSavings());
			newKingdom.setDoneMoving(originalKingdom.isDoneMoving());
			this.kingdoms.add(newKingdom);
		}
		this.activeKingdom = this.kingdoms.get(original.getKingdoms().indexOf(original.getActiveKingdom()));
		for (Entry<Vector2, HexTile> originalTileEntry : original.getMap().getTiles().entrySet()) {
			HexTile originalTile = originalTileEntry.getValue();
			HexTile newTile = new HexTile(this.players.get(original.getPlayers().indexOf(originalTile.getPlayer())),
					new Vector2(originalTileEntry.getKey()));
			if (originalTile.getKingdom() != null) {
				newTile.setKingdom(this.kingdoms.get(original.getKingdoms().indexOf(originalTile.getKingdom())));
				newTile.getKingdom().getTiles().add(newTile);
			}
			if (originalTile.getContent() != null) {
				newTile.setContent(originalTile.getContent().getCopy(newTile.getKingdom()));
			}
			this.map.getTiles().put(newTile.getPosition(), newTile);
		}
		if (original.getHeldObject() != null) {
			this.setHeldObject(original.getHeldObject()
					.getCopy(this.kingdoms.get(original.getKingdoms().indexOf(original.getHeldObject().getKingdom()))));
		}
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public void setPlayers(ArrayList<Player> players) {
		this.players = players;
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

	public void setRandom(Random random) {
		this.random = random;
	}

	public BotAI.Intelligence getBotIntelligence() {
		return botIntelligence;
	}

	public void setBotIntelligence(BotAI.Intelligence botIntelligence) {
		this.botIntelligence = botIntelligence;
	}

}

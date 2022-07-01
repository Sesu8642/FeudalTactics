package com.sesu8642.feudaltactics.gamelogic.gamestate;

import java.util.List;
import java.util.Objects;

import com.badlogic.gdx.math.Vector2;

/** A tile of land on the map. **/
public class HexTile {

	private Player player;
	private MapObject content;
	private Kingdom kingdom;
	private Vector2 position;
	private List<HexTile> cachedNeighborTiles;

	public HexTile() {
	}

	public HexTile(Player player, Vector2 position) {
		this.player = player;
		this.position = position;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public MapObject getContent() {
		return content;
	}

	public void setContent(MapObject content) {
		this.content = content;
	}

	public Kingdom getKingdom() {
		return kingdom;
	}

	/**
	 * Setter for kingdom. Also sets the player to the kingdom's owner.
	 */
	public void setKingdom(Kingdom kingdom) {
		this.kingdom = kingdom;
		if (kingdom != null) {
			this.player = kingdom.getPlayer();
		}
	}

	public Vector2 getPosition() {
		return position;
	}

	public List<HexTile> getCachedNeighborTiles() {
		return cachedNeighborTiles;
	}

	public void setCachedNeighborTiles(List<HexTile> neighborTiles) {
		this.cachedNeighborTiles = neighborTiles;
	}

	@Override
	public String toString() {
		String kingdomStr = kingdom == null ? "null" : kingdom.toString();
		String contentStr = content == null ? "null" : content.toString();
		return "Position: " + position.toString() + " Color: " + player.getColor().toString() + ", Kingdom: "
				+ kingdomStr + ", Content: " + contentStr;
	}

	@Override
	public int hashCode() {
		return Objects.hash(content, player, position);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		HexTile other = (HexTile) obj;
		return Objects.equals(content, other.content) && Objects.equals(player, other.player)
				&& Objects.equals(position, other.position);
	}

}

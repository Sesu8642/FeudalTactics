package com.sesu8642.feudaltactics.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.sesu8642.feudaltactics.engine.BotAI.Intelligence;
import com.sesu8642.feudaltactics.gamestate.mapobjects.MapObject;
import com.badlogic.gdx.utils.JsonValue;

public class GameStateSerializer implements Serializer<GameState> {

	private static final String KINGDOM_FIELD_NAME = "kingdom";
	private static final String MAPOBJECTS_CLASS_BASE_NAME = "com.sesu8642.feudaltactics.gamestate.mapobjects.";
	private static final String ACTIVE_KINGDOM_ID_NAME = "active_kingdom_id";
	private static final String WINNER_ID_NAME = "winner_id";
	private static final String BOT_INTELLIGENCE_NAME = "bot_intelligence";
	private static final String BOT_INTELLIGENCE_FIELD_NAME = "botIntelligence";
	private static final String PLAYER_TURN_FIELD_NAME = "playerTurn";
	private static final String PLAYER_TURN_NAME = "player_turn";
	private static final String KINGDOM_ID_NAME = "kingdom_id";
	private static final String HELD_OBJ_NAME = "held_obj";
	private static final String TILE_IDS_NAME = "tile_ids";
	private static final String SAVINGS_NAME = "savings";
	private static final String KINGDOMS_NAME = "kingdoms";
	private static final String CLASS_NAME = "class";
	private static final String CONTENT_NAME = "content";
	private static final String POSITION_NAME = "position";
	private static final String PLAYER_ID_NAME = "player_id";
	private static final String TILES_NAME = "tiles";
	private static final String ID_NAME = "id";
	private static final String PLAYERS_NAME = "players";

	Integer lastId = 0;

	private int getId(Map<Object, Integer> idMap, Object obj) {
		Integer exisingId = idMap.get(obj);
		if (exisingId == null) {
			int newId = ++lastId;
			idMap.put(obj, newId);
			return newId;
		} else {
			return exisingId;
		}
	}

	@Override
	public void write(Json json, GameState object, @SuppressWarnings("rawtypes") Class knownType) {
		lastId = 0;
		Map<Object, Integer> idMap = new HashMap<Object, Integer>();
		json.writeObjectStart();
		json.writeArrayStart(PLAYERS_NAME);
		for (Player player : object.getPlayers()) {
			json.writeObjectStart();
			json.writeValue(ID_NAME, getId(idMap, player));
			json.writeFields(player);
			json.writeObjectEnd();
		}
		json.writeArrayEnd();
		json.writeArrayStart(TILES_NAME);
		for (HexTile tile : object.getMap().getTiles().values()) {
			json.writeObjectStart();
			json.writeValue(ID_NAME, getId(idMap, tile));
			json.writeValue(PLAYER_ID_NAME, getId(idMap, tile.getPlayer()));
			json.writeField(tile, POSITION_NAME);
			if (tile.getContent() != null) {
				json.writeObjectStart(CONTENT_NAME);
				json.writeValue(ID_NAME, getId(idMap, tile.getContent()));
				json.writeValue(CLASS_NAME, tile.getContent().getClass().getSimpleName());
				// make copy to remove kingdom reference
				MapObject contentDuplicate = tile.getContent().getCopy(null);
				json.writeFields(contentDuplicate);
				json.writeObjectEnd();
			}
			json.writeObjectEnd();
		}
		json.writeArrayEnd();
		json.writeArrayStart(KINGDOMS_NAME);
		for (Kingdom kingdom : object.getKingdoms()) {
			json.writeObjectStart();
			json.writeValue(ID_NAME, getId(idMap, kingdom));
			json.writeValue(PLAYER_ID_NAME, getId(idMap, kingdom.getPlayer()));
			json.writeField(kingdom, SAVINGS_NAME);
			json.writeArrayStart(TILE_IDS_NAME);
			for (HexTile tile : kingdom.getTiles()) {
				json.writeValue(getId(idMap, tile));
			}
			json.writeArrayEnd();
			json.writeObjectEnd();
		}
		json.writeArrayEnd();
		if (object.getHeldObject() != null) {
			json.writeObjectStart(HELD_OBJ_NAME);
			json.writeValue(CLASS_NAME, object.getHeldObject().getClass().getSimpleName());
			// make copy to remove kingdom reference
			MapObject objectDuplicate = object.getHeldObject().getCopy(null);
			json.writeFields(objectDuplicate);
			json.writeValue(KINGDOM_ID_NAME, getId(idMap, object.getHeldObject().getKingdom()));
			json.writeObjectEnd();
		}
		json.writeField(object, PLAYER_TURN_FIELD_NAME, PLAYER_TURN_NAME);
		json.writeField(object, BOT_INTELLIGENCE_FIELD_NAME, BOT_INTELLIGENCE_NAME);
		if (object.getWinner() != null) {
			json.writeValue(WINNER_ID_NAME, getId(idMap, object.getWinner()));
		}
		if (object.getActiveKingdom() != null) {
			json.writeValue(ACTIVE_KINGDOM_ID_NAME, getId(idMap, object.getActiveKingdom()));
		}
		json.writeObjectEnd();
	}

	@Override
	public GameState read(Json json, JsonValue jsonData, @SuppressWarnings("rawtypes") Class type) {
		Map<Integer, Object> reverseIdMap = new HashMap<Integer, Object>();

		GameState result = new GameState();
		result.setPlayers(new ArrayList<Player>());
		JsonValue playersJson = jsonData.get(PLAYERS_NAME);
		playersJson.forEach(playerJson -> {
			int id = playerJson.getInt(ID_NAME);
			playerJson.remove(ID_NAME);
			Player player = json.fromJson(Player.class, playerJson.toString());
			reverseIdMap.put(id, player);
			result.getPlayers().add(player);
		});
		result.setMap(new HexMap());
		JsonValue tilesJson = jsonData.get(TILES_NAME);
		tilesJson.forEach(tileJson -> {
			int id = tileJson.getInt(ID_NAME);
			int playerId = tileJson.getInt(PLAYER_ID_NAME);
			tileJson.remove(ID_NAME);
			tileJson.remove(PLAYER_ID_NAME);
			JsonValue contentJson = tileJson.get(CONTENT_NAME);
			int contentId = -1;
			if (contentJson != null) {
				contentId = contentJson.getInt(ID_NAME);
				contentJson.remove(ID_NAME);
				// replace short class name with full one
				String shortClassName = contentJson.getString(CLASS_NAME);
				contentJson.remove(CLASS_NAME);
				contentJson.addChild(CLASS_NAME, new JsonValue(MAPOBJECTS_CLASS_BASE_NAME + shortClassName));
			}
			HexTile tile = json.fromJson(HexTile.class, tileJson.toString());
			if (tile.getContent() != null) {
				reverseIdMap.put(contentId, tile.getContent());
			}
			tile.setPlayer((Player) reverseIdMap.get(playerId));
			reverseIdMap.put(id, tile);
			result.getMap().getTiles().put(tile.getPosition(), tile);
		});
		result.setKingdoms(new ArrayList<Kingdom>());
		JsonValue kingdomsJson = jsonData.get(KINGDOMS_NAME);
		kingdomsJson.forEach(kingdomJson -> {
			int id = kingdomJson.getInt(ID_NAME);
			int playerId = kingdomJson.getInt(PLAYER_ID_NAME);
			JsonValue tileIdsJson = kingdomJson.get(TILE_IDS_NAME);
			LinkedHashSet<HexTile> kingdomTiles = new LinkedHashSet<HexTile>();
			tileIdsJson.forEach(tileIdJson -> {
				int tileId = tileIdJson.asInt();
				kingdomTiles.add((HexTile) reverseIdMap.get(tileId));
			});
			kingdomJson.remove(TILE_IDS_NAME);
			kingdomJson.remove(ID_NAME);
			kingdomJson.remove(PLAYER_ID_NAME);
			Kingdom kingdom = json.fromJson(Kingdom.class, kingdomJson.toString());
			kingdom.setPlayer((Player) reverseIdMap.get(playerId));
			kingdom.setTiles(kingdomTiles);
			reverseIdMap.put(id, kingdom);
			result.getKingdoms().add(kingdom);
		});
		if (jsonData.has(HELD_OBJ_NAME)) {
			JsonValue heldObjJson = jsonData.get(HELD_OBJ_NAME);
			int heldObjectKingdomId = heldObjJson.getInt(KINGDOM_ID_NAME);
			heldObjJson.remove(KINGDOM_ID_NAME);
			// replace short class name with full one
			String shortClassName = heldObjJson.getString(CLASS_NAME);
			heldObjJson.remove(CLASS_NAME);
			heldObjJson.addChild(CLASS_NAME, new JsonValue(MAPOBJECTS_CLASS_BASE_NAME + shortClassName));
			heldObjJson.remove(KINGDOM_FIELD_NAME);
			// toString causes an error here... maybe because of the enum?
			MapObject heldObject = json.fromJson(MapObject.class, heldObjJson.prettyPrint(OutputType.json, 1));
			heldObject.setKingdom((Kingdom) reverseIdMap.get(heldObjectKingdomId));
			result.setHeldObject(heldObject);
		}
		result.setPlayerTurn(jsonData.getInt(PLAYER_TURN_NAME));
		JsonValue botIntelligenceJson = jsonData.get(BOT_INTELLIGENCE_NAME);
		result.setBotIntelligence(Intelligence.valueOf(botIntelligenceJson.asString()));
		if (jsonData.has(WINNER_ID_NAME)) {
			Integer winnerId = jsonData.getInt(WINNER_ID_NAME);
			Player winner = (Player) reverseIdMap.get(winnerId);
			result.setWinner(winner);
		}
		if (jsonData.has(ACTIVE_KINGDOM_ID_NAME)) {
			Integer activeKingdomId = jsonData.getInt(ACTIVE_KINGDOM_ID_NAME);
			Kingdom activeKingdom = (Kingdom) reverseIdMap.get(activeKingdomId);
			result.setActiveKingdom(activeKingdom);
		}
		// add missing references
		for (Kingdom kingdom : result.getKingdoms()) {
			for (HexTile tile : kingdom.getTiles()) {
				tile.setKingdom(kingdom);
				if (tile.getContent() != null) {
					tile.getContent().setKingdom(kingdom);
				}
			}
		}
		return result;
	}

}

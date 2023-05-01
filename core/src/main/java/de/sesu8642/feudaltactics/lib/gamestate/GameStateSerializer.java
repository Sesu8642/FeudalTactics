// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.Json.Serializer;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;

/** JSON serializer for the {@link GameState} class. **/
public class GameStateSerializer implements Serializer<GameState> {

	private static final String KINGDOM_FIELD_NAME = "kingdom";
	private static final String MAPOBJECTS_CLASS_BASE_NAME = "de.sesu8642.feudaltactics.lib.gamestate.";
	private static final String ACTIVE_KINGDOM_ID_NAME = "active_kingdom_id";
	private static final String WINNER_ID_NAME = "winner_id";
	private static final String BOT_INTELLIGENCE_NAME = "bot_intelligence";
	private static final String BOT_INTELLIGENCE_FIELD_NAME = "botIntelligence";
	private static final String PLAYER_TURN_FIELD_NAME = "playerTurn";
	private static final String PLAYER_TURN_NAME = "player_turn";
	private static final String HELD_OBJ_NAME = "held_obj";
	private static final String TILE_IDS_NAME = "tile_ids";
	private static final String SAVINGS_NAME = "savings";
	private static final String WAS_ACTIVE_IN_CURRENT_TURN_NAME = "wasActiveInCurrentTurn";
	private static final String KINGDOMS_NAME = "kingdoms";
	private static final String CLASS_NAME = "class";
	private static final String CONTENT_NAME = "content";
	private static final String POSITION_NAME = "position";
	private static final String PLAYER_ID_NAME = "player_id";
	private static final String TILES_NAME = "tiles";
	private static final String ID_NAME = "id";
	private static final String PLAYERS_NAME = "players";
	private static final String SEED_FIELD_NAME = "seed";
	private static final String SEED_NAME = "seed";
	private static final String ROUND_FIELD_NAME = "round";
	private static final String ROUND_NAME = "round";

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
		Map<Object, Integer> idMap = new HashMap<>();
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
		for (HexTile tile : object.getMap().values()) {
			json.writeObjectStart();
			json.writeValue(ID_NAME, getId(idMap, tile));
			json.writeValue(PLAYER_ID_NAME, getId(idMap, tile.getPlayer()));
			json.writeField(tile, POSITION_NAME);
			if (tile.getContent() != null) {
				json.writeObjectStart(CONTENT_NAME);
				json.writeValue(ID_NAME, getId(idMap, tile.getContent()));
				json.writeValue(CLASS_NAME, tile.getContent().getClass().getSimpleName());
				json.writeFields(tile.getContent());
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
			json.writeField(kingdom, WAS_ACTIVE_IN_CURRENT_TURN_NAME);
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
			json.writeFields(object.getHeldObject());
			json.writeObjectEnd();
		}
		json.writeField(object, PLAYER_TURN_FIELD_NAME, PLAYER_TURN_NAME);
		json.writeField(object, BOT_INTELLIGENCE_FIELD_NAME, BOT_INTELLIGENCE_NAME);
		json.writeField(object, SEED_FIELD_NAME, SEED_NAME);
		if (object.getWinner() != null) {
			json.writeValue(WINNER_ID_NAME, getId(idMap, object.getWinner()));
		}
		if (object.getActiveKingdom() != null) {
			json.writeValue(ACTIVE_KINGDOM_ID_NAME, getId(idMap, object.getActiveKingdom()));
		}
		json.writeField(object, ROUND_FIELD_NAME, ROUND_NAME);
		json.writeObjectEnd();
	}

	@Override
	public GameState read(Json json, JsonValue jsonData, @SuppressWarnings("rawtypes") Class type) {
		Map<Integer, Object> reverseIdMap = new HashMap<>();

		GameState result = new GameState();
		result.setPlayers(new ArrayList<>());
		JsonValue playersJson = jsonData.get(PLAYERS_NAME);
		playersJson.forEach(playerJson -> {
			int id = playerJson.getInt(ID_NAME);
			playerJson.remove(ID_NAME);
			Player player = json.fromJson(Player.class, playerJson.toString());
			reverseIdMap.put(id, player);
			result.getPlayers().add(player);
		});
		result.setMap(new LinkedHashMap<>());
		JsonValue tilesJson = jsonData.get(TILES_NAME);
		tilesJson.forEach(tileJson -> {
			final int id = tileJson.getInt(ID_NAME);
			final int playerId = tileJson.getInt(PLAYER_ID_NAME);
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
			result.getMap().put(tile.getPosition(), tile);
		});
		result.setKingdoms(new ArrayList<>());
		JsonValue kingdomsJson = jsonData.get(KINGDOMS_NAME);
		kingdomsJson.forEach(kingdomJson -> {
			final int id = kingdomJson.getInt(ID_NAME);
			final int playerId = kingdomJson.getInt(PLAYER_ID_NAME);
			JsonValue tileIdsJson = kingdomJson.get(TILE_IDS_NAME);
			ArrayList<HexTile> kingdomTiles = new ArrayList<>();
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
			// replace short class name with full one
			String shortClassName = heldObjJson.getString(CLASS_NAME);
			heldObjJson.remove(CLASS_NAME);
			heldObjJson.addChild(CLASS_NAME, new JsonValue(MAPOBJECTS_CLASS_BASE_NAME + shortClassName));
			heldObjJson.remove(KINGDOM_FIELD_NAME);
			// toString causes an error here... maybe because of the enum?
			MapObject heldObject = json.fromJson(MapObject.class, heldObjJson.prettyPrint(OutputType.json, 1));
			result.setHeldObject(heldObject);
		}
		result.setPlayerTurn(jsonData.getInt(PLAYER_TURN_NAME));
		JsonValue botIntelligenceJson = jsonData.get(BOT_INTELLIGENCE_NAME);
		result.setBotIntelligence(Intelligence.valueOf(botIntelligenceJson.asString()));
		JsonValue seedJson = jsonData.get(SEED_NAME);
		result.setSeed(seedJson.asLong());
		JsonValue roundJson = jsonData.get(ROUND_NAME);
		result.setRound(roundJson.asInt());
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
			}
		}
		return result;
	}

}

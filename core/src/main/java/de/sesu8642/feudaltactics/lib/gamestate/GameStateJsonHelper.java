// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import javax.inject.Inject;

/**
 * Helper class to convert a GameState to JSON and vice versa with a simple API.
 */
public class GameStateJsonHelper {

    private final Json json = new Json(JsonWriter.OutputType.json);
    private final JsonReader jsonReader = new JsonReader();

    public GameStateJsonHelper() {
        json.setSerializer(GameState.class, new GameStateSerializer());
        json.setIgnoreUnknownFields(true);
    }

    /**
     * Takes a JSON string representing a game state and returns the parsed game state.
     */
    public GameState fromJson(String jsonString) {
        final JsonValue jsonValue = jsonReader.parse(jsonString);
        return json.readValue(GameState.class, jsonValue);
    }

    /**
     * Takes a game state object and returns its JSON representation.
     */
    public String toJsonString(GameState gameState) {
      return json.toJson(gameState, GameState.class);
    };

}

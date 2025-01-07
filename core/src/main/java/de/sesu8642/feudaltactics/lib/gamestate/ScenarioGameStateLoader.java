package de.sesu8642.feudaltactics.lib.gamestate;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

import java.nio.charset.StandardCharsets;

/**
 * Can load scenario maps from assets.
 */
public class ScenarioGameStateLoader {

    private final Json json = new Json(OutputType.json);
    private final JsonReader jsonReader = new JsonReader();

    public ScenarioGameStateLoader() {
        json.setSerializer(GameState.class, new GameStateSerializer());
    }

    /**
     * Loads a gamestate from a scenario file.
     *
     * @param scenarioMap map to load into the gamestate
     * @return loaded gameState
     */
    public GameState loadScenarioGameState(ScenarioMap scenarioMap) {
        FileHandle assetsFileHandle = Gdx.files.internal(scenarioMap.mapPath);
        String loadedString = assetsFileHandle.readString(StandardCharsets.UTF_8.name());

        JsonValue loadedStateJsonValue = jsonReader.parse(loadedString);
        return json.readValue(GameState.class, loadedStateJsonValue);
    }


}

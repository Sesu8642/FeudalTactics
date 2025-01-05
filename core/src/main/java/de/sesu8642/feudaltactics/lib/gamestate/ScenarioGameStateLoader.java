package de.sesu8642.feudaltactics.lib.gamestate;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;
import com.google.common.io.Resources;
import de.sesu8642.feudaltactics.exceptions.FatalErrorException;

import java.io.IOException;
import java.net.URL;
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
        URL url = Resources.getResource(scenarioMap.mapPath);
        String loadedString = null;
        try {
            loadedString = Resources.toString(url, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new FatalErrorException("Unable to load scenario map from " + scenarioMap.mapPath);
        }
        JsonValue loadedStateJsonValue = jsonReader.parse(loadedString);
        return json.readValue(GameState.class, loadedStateJsonValue);
    }


}

package com.sesu8642.feudaltactics.gamelogic.gamestate;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

import com.badlogic.gdx.graphics.Color;
import com.sesu8642.feudaltactics.gamelogic.gamestate.Player.Type;

public class GameStateHelperTest {

	@Test
	void copyEqualsOriginal() {

		ArrayList<Player> players = new ArrayList<>();
		players.add(new Player(new Color(), Type.LOCAL_BOT));
		players.add(new Player(new Color(), Type.LOCAL_BOT));
		players.add(new Player(new Color(), Type.LOCAL_PLAYER));

		GameState original = new GameState();
		GameStateHelper.initializeMap(original, players, 500, 2, 0.2F, 12345L);

		original.setActiveKingdom(original.getKingdoms().get(0));

		GameState copy = GameStateHelper.getCopy(original);

		assertEquals(original, copy);
	}

}
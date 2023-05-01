// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.Color;

import de.sesu8642.feudaltactics.dagger.DaggerFeudalTacticsComponent;
import de.sesu8642.feudaltactics.dagger.FeudalTacticsComponent;

/** The game's entry point. */
public class FeudalTactics extends Game {

	// this needs to be accessed somehow by the other classes and cannot be provided
	// by DI because it is created by the launcher
	public static FeudalTactics game;

	// TODO: put those in a custom skin
	public static final Color buttonIconColor = new Color(1, 0.7F, 0.15F, 1);
	public static final Color disabledButtonIconColor = new Color(0.75F, 0.75F, 0.75F, 1);
	public static final Color backgroundColor = new Color(0, 0.2f, 0.8f, 1);

	private FeudalTacticsComponent component;

	@Override
	public void create() {
		game = this;

		// Eclipse cannot resolve this. See https://stackoverflow.com/a/31669111 for
		// more information.
		component = DaggerFeudalTacticsComponent.create();

		GameInitializer gameInitializer = component.getGameInitializer();
		gameInitializer.initializeGame();
	}

	@Override
	public void dispose() {
		// shutdown executor services to kill all background threads
		component.getBotAiExecutor().shutdownNow();
		component.getCopyButtonExecutor().shutdownNow();
		super.dispose();
	}

	/**
	 * For accessing dependencies from non-DI-capable classes like the custom JUL
	 * handler.
	 */
	public FeudalTacticsComponent getComponent() {
		return component;
	}

}
// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import de.sesu8642.feudaltactics.events.GameStateChangeEvent;
import de.sesu8642.feudaltactics.ingame.dagger.IngameRenderer;
import de.sesu8642.feudaltactics.ingame.ui.IngameScreen;
import de.sesu8642.feudaltactics.menu.common.ui.Margin;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

/** Handles events that affect rendering. */
public class IngameRendererEventHandler {

	MapRenderer mapRenderer;
	IngameScreen ingameSceen;

	/**
	 * Constructor.
	 * 
	 * @param mapRenderer map renderer
	 * @param ingameSceen ingame screen
	 */
	@Inject
	public IngameRendererEventHandler(@IngameRenderer MapRenderer mapRenderer, IngameScreen ingameSceen) {
		this.mapRenderer = mapRenderer;
		this.ingameSceen = ingameSceen;
	}

	/**
	 * Event handler for gameState change.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleGameStateChange(GameStateChangeEvent event) {
		mapRenderer.updateMap(event.getGameState());
		if (event.isMapDimensionsChanged()) {
			Margin margin = ingameSceen.calculateMapScreenArea();
			mapRenderer.placeCameraForFullMapView(event.getGameState(), margin.marginLeft, margin.marginBottom, 0, 0);
		}
	}

}

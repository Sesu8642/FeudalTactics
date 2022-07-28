// SPDX-License-Identifier: GPL-3.0-or-later

package com.sesu8642.feudaltactics.renderer;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.IngameRenderer;
import com.sesu8642.feudaltactics.events.GameStateChangeEvent;
import com.sesu8642.feudaltactics.ui.Margin;
import com.sesu8642.feudaltactics.ui.screens.IngameScreen;

/** Handles events that affect rendering. */
public class EventHandler {

	MapRenderer mapRenderer;
	IngameScreen ingameSceen;

	/**
	 * Constructor.
	 * 
	 * @param mapRenderer map renderer
	 * @param ingameSceen ingame screen
	 */
	@Inject
	public EventHandler(@IngameRenderer MapRenderer mapRenderer, IngameScreen ingameSceen) {
		super();
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

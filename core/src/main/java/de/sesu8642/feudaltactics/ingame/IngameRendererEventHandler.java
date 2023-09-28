// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import javax.inject.Inject;

import com.google.common.eventbus.Subscribe;

import de.sesu8642.feudaltactics.events.CenterMapEvent;
import de.sesu8642.feudaltactics.events.GameStateChangeEvent;
import de.sesu8642.feudaltactics.ingame.dagger.IngameRenderer;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

/** Handles events that affect rendering. */
public class IngameRendererEventHandler {

	MapRenderer mapRenderer;

	/**
	 * Constructor.
	 * 
	 * @param mapRenderer map renderer
	 */
	@Inject
	public IngameRendererEventHandler(@IngameRenderer MapRenderer mapRenderer) {
		this.mapRenderer = mapRenderer;
	}

	/**
	 * Event handler for gameState change.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleGameStateChange(GameStateChangeEvent event) {
		mapRenderer.updateMap(event.getGameState());
	}

	/**
	 * Event handler for map centering.
	 * 
	 * @param event event to handle
	 */
	@Subscribe
	public void handleMapCentering(CenterMapEvent event) {
		mapRenderer.placeCameraForFullMapView(event.getGameState(), event.getMarginLeftPx(), event.getMarginBottomPx(),
				event.getMarginRightPx(), event.getMarginTopPx());
	}

}

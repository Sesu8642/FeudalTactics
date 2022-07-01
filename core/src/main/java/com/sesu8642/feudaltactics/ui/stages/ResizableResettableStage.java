package com.sesu8642.feudaltactics.ui.stages;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.ui.NeedsUpdateOnResize;

/**
 * Parent {@link Stage} for the game's stages. Implements functionality for
 * resetting and resizing.
 */
public abstract class ResizableResettableStage extends Stage implements NeedsUpdateOnResize {

	protected ResizableResettableStage() {
		super();
	}

	protected ResizableResettableStage(Viewport viewport, Batch batch) {
		super(viewport, batch);
	}

	protected ResizableResettableStage(Viewport viewport) {
		super(viewport);
	}

	public abstract void reset();

}

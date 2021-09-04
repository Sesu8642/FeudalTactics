package com.sesu8642.feudaltactics.ui.stages;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.ui.NeedsUpdateOnResize;

public abstract class ResizableResettableStage extends Stage implements NeedsUpdateOnResize {

	public ResizableResettableStage() {
		super();
	}

	public ResizableResettableStage(Viewport viewport, Batch batch) {
		super(viewport, batch);
	}

	public ResizableResettableStage(Viewport viewport) {
		super(viewport);
	}
	
	public abstract void reset();

}

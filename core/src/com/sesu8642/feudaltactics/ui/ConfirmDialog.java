package com.sesu8642.feudaltactics.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class ConfirmDialog extends FeudalTacticsDialog {

	private Runnable action;
	
	public ConfirmDialog(String message, Runnable action, Skin skin) {
		super(skin);
		this.action = action;
		text(message);
		button("OK", true);
		button("Cancel", false);
	}

	@Override
	public void result(Object result) {
		if ((boolean) result) {
			action.run();
		}
		this.remove();
	}
}

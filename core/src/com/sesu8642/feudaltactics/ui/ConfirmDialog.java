package com.sesu8642.feudaltactics.ui;

public class ConfirmDialog extends FeudalTacticsDialog {

	private Runnable action;
	
	public ConfirmDialog(String message, Runnable action) {
		super();
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

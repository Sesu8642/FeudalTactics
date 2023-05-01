// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/** {@link Dialog} for having the user confirm something or cancel. */
public class ConfirmDialog extends FeudalTacticsDialog {

	private Runnable action;

	/**
	 * Constructor.
	 * 
	 * @param message message to display to the user
	 * @param action  action to execute when the user confirms
	 * @param skin    game skin
	 */
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

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.ui;

import java.util.function.Consumer;

import javax.inject.Inject;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/** Factory for creating dialogs. */
public class DialogFactory {

	private Skin skin;

	@Inject
	public DialogFactory(Skin skin) {
		this.skin = skin;
	}

	/**
	 * Creates a new dialog.
	 * 
	 * @param action action to execute once the dialog is confirmed
	 * @return the created dialog
	 */
	public FeudalTacticsDialog createDialog(Consumer<Object> action) {
		return new FeudalTacticsDialog(skin) {

			@Override
			public void result(Object result) {
				action.accept(result);
				this.remove();
			}

		};
	}

	public ConfirmDialog createConfirmDialog(String message, Runnable action) {
		return new ConfirmDialog(message, action, skin);
	}

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Parent class for all {@link Dialog}s in the game. Applies sane settings and
 * handles responsiveness.
 */
public class FeudalTacticsDialog extends Dialog {

	public static final float DIALOG_PADDING = 20;
	public static final float DIALOG_LABEL_MAX_WIDTH = 600;

	private Skin skin;

	/**
	 * Constructor.
	 * 
	 * @param skin game skin
	 */
	public FeudalTacticsDialog(Skin skin) {
		super("", skin);
		this.skin = skin;
		getColor().a = 0; // fixes pop-in; see https://github.com/libgdx/libgdx/issues/3920
		setMovable(false);
		setKeepWithinStage(false);
		pad(DIALOG_PADDING);
	}

	@Override
	public Dialog text(String text) {
		Label responsiveLabel = new Label(text, skin);
		responsiveLabel.setWrap(true);
		this.getContentTable().add(responsiveLabel)
				.width(Math.min(DIALOG_LABEL_MAX_WIDTH, Gdx.graphics.getWidth() - 2 * DIALOG_PADDING));
		return this;
	}

	@Override
	public Dialog button(String text, Object param) {
		TextButton button = new TextButton(text, skin);
		// pack the table so its width is calculated
		getButtonTable().pack();
		if (getButtonTable().getWidth() + button.getWidth() > Gdx.graphics.getWidth() - 2 * DIALOG_PADDING) {
			// put button in second row
			getButtonTable().row();
		}
		return super.button(button, param);
	}

}

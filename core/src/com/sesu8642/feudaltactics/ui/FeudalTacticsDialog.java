package com.sesu8642.feudaltactics.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.sesu8642.feudaltactics.FeudalTactics;

public class FeudalTacticsDialog extends Dialog {

	public FeudalTacticsDialog() {
		super("", FeudalTactics.skin);
		getColor().a = 0; // fixes pop-in; see https://github.com/libgdx/libgdx/issues/3920
		setMovable(false);
		pad(20);
	}
		
}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.input;

import com.badlogic.gdx.math.Vector2;

/**
 * An input handler accepts player input (e.g. tap), determines what the input
 * is supposed to do and triggers the appropriate reaction if any.
 **/
interface InputHandler {

	public void inputTap(Vector2 worldCoords);

	public void inputEsc();

	public void inputBack();

}

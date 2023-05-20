// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.input;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.input.GestureDetector;

/**
 * Custom gesture detector to reduce the default long press duration.
 */
@Singleton
public class FeudalTacticsGestureDetector extends GestureDetector {

	@Inject
	public FeudalTacticsGestureDetector(CombinedInputProcessor inputProcessor) {
		super(20, 0.4f, 0.4f, Integer.MAX_VALUE, inputProcessor);
	}

}

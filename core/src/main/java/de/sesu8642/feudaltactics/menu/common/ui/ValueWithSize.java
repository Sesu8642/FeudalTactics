// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import java.util.Objects;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Value;

/**
 * A {@link Value} with the option to be a percentage of the size of an actor.
 */
public class ValueWithSize extends Value {

	@Override
	public float get(Actor context) {
		return 0;
	}

	/**
	 * Returns a value that is a percentage of the specified actor's size. The
	 * context actor is ignored.
	 */
	public static Value percentSize(final float percent, final Actor actor) {
		Objects.requireNonNull(actor);
		return new Value() {
			@Override
			public float get(Actor context) {
				return (float) Math.sqrt(actor.getHeight() * actor.getWidth()) * percent;
			}
		};
	}

}

package com.sesu8642.feudaltactics.libgdx;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Value;

public class ValueWithSize extends Value {

	@Override
	public float get(Actor context) {
		return 0;
	}

	/**
	 * Returns a value that is a percentage of the specified actor's size. The
	 * context actor is ignored.
	 */
	static public Value percentSize(final float percent, final Actor actor) {
		if (actor == null)
			throw new IllegalArgumentException("actor cannot be null.");
		return new Value() {
			public float get(Actor context) {
				return (float) Math.sqrt(((float) actor.getHeight() * actor.getWidth())) * percent;
			}
		};
	}

}

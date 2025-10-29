// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Value;

import java.util.Objects;

/**
 * A {@link Value} with the option to be a percentage of the size of an actor.
 */
public class ValueWithSize extends Value {

    /**
     * Returns a value that is a percentage of the specified actor's size. The
     * context actor is ignored.
     */
    public static Value percentSize(float percent, Actor actor) {
        Objects.requireNonNull(actor);
        return new Value() {
            @Override
            public float get(Actor context) {
                return (float) Math.sqrt(actor.getHeight() * actor.getWidth()) * percent;
            }
        };
    }

    /**
     * Returns a value that is the smaller of 1. a percentage of the specified actor's size. 2. the pixel density
     * multiplied by a given multiplier. The context actor is ignored.
     */
    public static Value percentSizeDensityMin(float percent, Actor actor,
                                              int pixelDensityMultiplier) {
        Objects.requireNonNull(actor);
        return new Value() {
            @Override
            public float get(Actor context) {
                return (float) Math.min(Math.sqrt(actor.getHeight() * actor.getWidth()) * percent,
                    Gdx.graphics.getDensity() * pixelDensityMultiplier);
            }
        };
    }

    @Override
    public float get(Actor context) {
        return 0;
    }

}

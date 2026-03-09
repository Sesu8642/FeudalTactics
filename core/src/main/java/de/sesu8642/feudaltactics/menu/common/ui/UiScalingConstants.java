// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.Gdx;

/**
 * Constants scaling UI elements.
 */
public final class UiScalingConstants {

    /**
     * Whether the game is running on a small device. Fullscreen devices that have a window width < 15 cm are
     * probably handhelds which are held closer to the eyes and should have smaller UI.
     */
    private static final boolean IS_SMALL_DEVICE =
        Gdx.graphics.isFullscreen() && Gdx.graphics.getWidth() / Gdx.graphics.getPpcX() < 15;

    /**
     * General scaling factor for UI elements.
     */
    public static final float UI_SCALING_FACTOR = IS_SMALL_DEVICE ? 0.5F : 1F;

    /**
     * Scaling factor for general texts.
     */
    public static final float TEXT_SCALING_FACTOR = IS_SMALL_DEVICE ? 0.5F : 1F;

    /**
     * Scaling factor for button texts. Those should be even smaller on handheld/portrait devices because they cannot
     * wrap.
     */
    public static final float BUTTON_TEXT_SCALING_FACTOR = IS_SMALL_DEVICE ? 0.4F : 1F;

    /**
     * Text size of button texts.
     */
    public static final int BUTTON_TEXT_SIZE = (int) (Gdx.graphics.getDensity() * BUTTON_TEXT_SCALING_FACTOR * 64);

    /**
     * Scaling factor for the hexagon font. For some reason applying it to the style in the SkinFactory doesn't work.
     * That's why the size needs to be set explicitly.
     */
    public static final float HEXAGON_FONT_SCALING_FACTOR = IS_SMALL_DEVICE ? 0.9F * Gdx.graphics.getDensity() :
        1.8F * Gdx.graphics.getDensity();

    private UiScalingConstants() {
        // prevent instantiation
        throw new AssertionError();
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Factory that returns customized buttons for the game.
 */
public class ButtonFactory {

    private static final Logger buttonLogger = LoggerFactory.getLogger(ButtonFactory.class.getName());

    private ButtonFactory() {
        // prevent instantiation
        throw new AssertionError();
    }

    /**
     * Creates a text button.
     */
    public static TextButton createTextButton(String text, Skin skin) {
        TextButton result = new TextButton(text, skin);
        result.addListener(new ExceptionLoggingChangeListener(() -> buttonLogger.debug("clicked text button: {}",
            result.getText())));
        return result;
    }

    /**
     * Creates a text button.
     */
    public static CopyButton createCopyButton(String text, Skin skin, boolean renderButtonBackground) {
        CopyButton result = new CopyButton(text, skin, renderButtonBackground);
        result.addListener(new ExceptionLoggingChangeListener(() -> buttonLogger.debug("clicked copy button")));
        return result;
    }

    /**
     * Creates an image button.
     */
    public static ImageButton createImageButton(String styleName, Skin skin) {
        ImageButton result = new ImageButton(skin.get(styleName, ImageButton.ImageButtonStyle.class));
        result.addListener(new ExceptionLoggingChangeListener(() -> buttonLogger.debug("clicked image button: {}",
            styleName)));
        return result;
    }

}

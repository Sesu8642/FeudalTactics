// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.ray3k.stripe.FreeTypeSkin;

import java.util.HashMap;
import java.util.Map;

/**
 * Factoring for creating the skin for the game. Most stuff in here is a big hack, but it's the only way I found to
 * scale the fonts dynamically based on the device screen parameters.
 */
public final class SkinFactory {

    private static final String SUPPORTED_CHARACTERS =
        "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789()[]{}%&$§!?=+-.,:;_/\\*~#\"'↗–ÄÖÜäöüẞß";

    // prevent instantiation
    private SkinFactory() {
        throw new AssertionError();
    }

    /**
     * Creates the game skin.
     */
    public static Skin createSkin() {

        // fullscreen devices that have a window width < 15 cm are probably handhelds which are held closer to the
        // eyes and should have smaller fonts
        final boolean isSmallDevice =
            Gdx.graphics.isFullscreen() && Gdx.graphics.getWidth() / Gdx.graphics.getPpcX() < 15;
        final float textScalingFactor = isSmallDevice ? 0.5F : 1F;
        // buttons texts should be even smaller because they cannot wrap
        final float buttonTextScalingFactor = isSmallDevice ? 0.4F : 1F;

        final Skin skin = new FreeTypeSkin(Gdx.files.internal("skin/pixthulhu-ui.json"));

        // fonts cannot have dynamic size if included in the skin file using FreeTypeSkin
        final Map<String, BitmapFont> fonts = SkinFactory.createFonts(textScalingFactor, buttonTextScalingFactor);

        // headline label
        final Label.LabelStyle headlineLabelStyle = skin.get(SkinConstants.FONT_HEADLINE, Label.LabelStyle.class);
        headlineLabelStyle.font = fonts.get(SkinConstants.FONT_HEADLINE);

        // button texts
        final TextButton.TextButtonStyle buttonStyle = skin.get(SkinConstants.DEFAULT_NAME,
            TextButton.TextButtonStyle.class);
        buttonStyle.font = fonts.get(SkinConstants.FONT_BUTTON);

        // overlay texts
        final Label.LabelStyle overlayLabelStyle = skin.get(SkinConstants.FONT_OVERLAY, Label.LabelStyle.class);
        overlayLabelStyle.font = fonts.get(SkinConstants.FONT_OVERLAY);
        final Label.LabelStyle overlayLabelWithBackgroundStyle = skin.get(SkinConstants.FONT_OVERLAY_WITH_BACKGROUND,
            Label.LabelStyle.class);
        overlayLabelWithBackgroundStyle.font = fonts.get(SkinConstants.FONT_OVERLAY);

        // default texts
        final Label.LabelStyle textLabelStyle = skin.get(SkinConstants.DEFAULT_NAME,
            Label.LabelStyle.class);
        textLabelStyle.font = fonts.get(SkinConstants.FONT_DEFAULT_TEXT);

        // select boxes
        final SelectBox.SelectBoxStyle selectBoxStyle = skin.get(SkinConstants.DEFAULT_NAME,
            SelectBox.SelectBoxStyle.class);
        selectBoxStyle.font = fonts.get(SkinConstants.FONT_BUTTON);

        // color select box
        final SelectBox.SelectBoxStyle colorSelectBoxStyle = skin.get(SkinConstants.SELECT_BOX_STYLE_COLOR_SELECT,
            SelectBox.SelectBoxStyle.class);
        // this is a bitmap font from the skin, it can only be scaled
        colorSelectBoxStyle.font.getData().setScale((Gdx.graphics.getDensity() * buttonTextScalingFactor * 1.8F));

        // lists
        final List.ListStyle listStyle = skin.get(SkinConstants.DEFAULT_NAME, List.ListStyle.class);
        listStyle.font = fonts.get(SkinConstants.FONT_BUTTON);

        // text fields
        final TextField.TextFieldStyle textFieldStyle = skin.get(SkinConstants.DEFAULT_NAME,
            TextField.TextFieldStyle.class);
        textFieldStyle.font = fonts.get(SkinConstants.FONT_SMALLER_TEXT);

        // set some additional things that are not configurable in the skin composer directly
        final Label.LabelStyle style = skin.get(SkinConstants.FONT_OVERLAY_WITH_BACKGROUND, Label.LabelStyle.class);
        style.font.getData().markupEnabled = true;

        return skin;
    }

    private static Map<String, BitmapFont> createFonts(float scalingFactor, float buttonScalingFactor) {
        // creating all fonts at once to be able to re-use the generators

        final FreeTypeFontGenerator regularFontGenerator = new FreeTypeFontGenerator(
            Gdx.files.internal("skin/FreeSans.ttf"));
        final FreeTypeFontGenerator boldFontGenerator = new FreeTypeFontGenerator(
            Gdx.files.internal("skin/FreeSansBold.ttf"));

        final Map<String, BitmapFont> result = new HashMap<>();
        result.put(SkinConstants.FONT_HEADLINE, createHeadlineFont(boldFontGenerator, scalingFactor));
        result.put(SkinConstants.FONT_BUTTON, createButtonFont(boldFontGenerator, buttonScalingFactor));
        result.put(SkinConstants.FONT_OVERLAY, createOverlayFont(regularFontGenerator, scalingFactor));
        result.put(SkinConstants.FONT_DEFAULT_TEXT, createTextFont(regularFontGenerator, scalingFactor));
        result.put(SkinConstants.FONT_SMALLER_TEXT, createSmallerTextFont(regularFontGenerator, scalingFactor));

        regularFontGenerator.dispose();
        boldFontGenerator.dispose();
        return result;
    }

    private static BitmapFont createHeadlineFont(FreeTypeFontGenerator fontGenerator, float scalingFactor) {
        final FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();
        fontParameters.size = (int) (Gdx.graphics.getDensity() * scalingFactor * 70);
        fontParameters.characters = SUPPORTED_CHARACTERS;
        return fontGenerator.generateFont(fontParameters);
    }

    private static BitmapFont createButtonFont(FreeTypeFontGenerator fontGenerator, float scalingFactor) {
        final FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();
        fontParameters.size = (int) (Gdx.graphics.getDensity() * scalingFactor * 64);
        fontParameters.characters = SUPPORTED_CHARACTERS;
        fontParameters.borderWidth = 3;
        return fontGenerator.generateFont(fontParameters);
    }

    private static BitmapFont createOverlayFont(FreeTypeFontGenerator fontGenerator, float scalingFactor) {
        final FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();
        fontParameters.size = (int) (Gdx.graphics.getDensity() * scalingFactor * 40);
        fontParameters.characters = SUPPORTED_CHARACTERS;
        fontParameters.borderWidth = 1;
        return fontGenerator.generateFont(fontParameters);
    }

    private static BitmapFont createTextFont(FreeTypeFontGenerator fontGenerator, float scalingFactor) {
        final FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();
        fontParameters.size = (int) (Gdx.graphics.getDensity() * scalingFactor * 48);
        fontParameters.characters = SUPPORTED_CHARACTERS;
        fontParameters.color = Color.BLACK;
        return fontGenerator.generateFont(fontParameters);
    }

    private static BitmapFont createSmallerTextFont(FreeTypeFontGenerator fontGenerator, float scalingFactor) {
        final FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();
        fontParameters.size = (int) (Gdx.graphics.getDensity() * scalingFactor * 32);
        fontParameters.characters = SUPPORTED_CHARACTERS;
        return fontGenerator.generateFont(fontParameters);
    }

}

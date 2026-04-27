// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator.FreeTypeFontParameter;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.ray3k.stripe.FreeTypeSkin;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static de.sesu8642.feudaltactics.menu.common.ui.UiScalingConstants.*;

/**
 * Factoring for creating the skin for the game. Most stuff in here is a big hack, but it's the only way I found to
 * scale the fonts dynamically based on the device screen parameters.
 */
@Slf4j
public final class SkinFactory {

    // prevent instantiation
    private SkinFactory() {
        throw new AssertionError();
    }

    /**
     * Creates the game skin.
     */
    public static Skin createSkin(java.util.List<String> languageFiles) {
        final Skin skin = new FreeTypeSkin(Gdx.files.internal("skin/pixthulhu-ui.json"));

        final String neededCharacters = getNeededCharactersString(languageFiles);
        log.info("Generating the following characters for fonts: {}", neededCharacters);

        // fonts cannot have dynamic size if included in the skin file using FreeTypeSkin
        final Map<String, BitmapFont> fonts = SkinFactory.createFonts(neededCharacters);

        // headline label
        final Label.LabelStyle headlineLabelStyle = skin.get(SkinConstants.FONT_HEADLINE, Label.LabelStyle.class);
        headlineLabelStyle.font = fonts.get(SkinConstants.FONT_HEADLINE);

        // button texts
        final TextButton.TextButtonStyle buttonStyle = skin.get(SkinConstants.DEFAULT_NAME,
            TextButton.TextButtonStyle.class);
        buttonStyle.font = fonts.get(SkinConstants.FONT_BUTTON);

        // generic image text button texts
        final ImageTextButton.ImageTextButtonStyle imageTextButtonStyle = skin.get(SkinConstants.DEFAULT_NAME,
            ImageTextButton.ImageTextButtonStyle.class);
        imageTextButtonStyle.font = fonts.get(SkinConstants.FONT_BUTTON);

        // copy icon text button texts
        final ImageTextButton.ImageTextButtonStyle copyImageTextButtonStyle = skin.get(SkinConstants.BUTTON_COPY,
            ImageTextButton.ImageTextButtonStyle.class);
        copyImageTextButtonStyle.font = fonts.get(SkinConstants.FONT_BUTTON);

        // tick icon text button texts
        final ImageTextButton.ImageTextButtonStyle tickImageTextButtonStyle = skin.get(SkinConstants.BUTTON_COPY_TICK,
            ImageTextButton.ImageTextButtonStyle.class);
        tickImageTextButtonStyle.font = fonts.get(SkinConstants.FONT_BUTTON);

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
        colorSelectBoxStyle.font.getData().setScale((Gdx.graphics.getDensity() * BUTTON_TEXT_SCALING_FACTOR * 1.8F));

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

    private static String getNeededCharactersString(java.util.List<String> languageFiles) {
        final Set<Character> neededCharacters =
            languageFiles.stream().map(filePath -> Gdx.files.internal(filePath).readString(StandardCharsets.UTF_8.name()))
                .flatMapToInt(String::chars).mapToObj(c -> (char) c)
                // collect to set to remove duplicates
                .collect(Collectors.toSet());
        // add numbers that are dynamically inserted
        final java.util.List<Character> numbers =
            "0123456789".chars().mapToObj(c -> (char) c).collect(Collectors.toList());
        neededCharacters.addAll(numbers);
        return neededCharacters
            // sort after (just nicer for debugging)
            .stream().sorted().collect(Collectors.toList()).toString();
    }

    private static Map<String, BitmapFont> createFonts(String neededCharacters) {
        // creating all fonts at once to be able to re-use the generators

        final FreeTypeFontGenerator regularFontGenerator = new FreeTypeFontGenerator(
            Gdx.files.internal("skin/FreeSans.ttf"));
        final FreeTypeFontGenerator boldFontGenerator = new FreeTypeFontGenerator(
            Gdx.files.internal("skin/FreeSansBold.ttf"));

        final Map<String, BitmapFont> result = new HashMap<>();
        result.put(SkinConstants.FONT_HEADLINE, createHeadlineFont(boldFontGenerator, neededCharacters));
        result.put(SkinConstants.FONT_BUTTON, createButtonFont(boldFontGenerator, neededCharacters));
        result.put(SkinConstants.FONT_OVERLAY, createOverlayFont(regularFontGenerator, neededCharacters));
        result.put(SkinConstants.FONT_DEFAULT_TEXT, createTextFont(regularFontGenerator, neededCharacters));
        result.put(SkinConstants.FONT_SMALLER_TEXT, createSmallerTextFont(regularFontGenerator, neededCharacters));

        regularFontGenerator.dispose();
        boldFontGenerator.dispose();
        return result;
    }

    private static BitmapFont createHeadlineFont(FreeTypeFontGenerator fontGenerator, String neededCharacters) {
        final FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();
        fontParameters.size = (int) (Gdx.graphics.getDensity() * TEXT_SCALING_FACTOR * 70);
        fontParameters.characters = neededCharacters;
        return fontGenerator.generateFont(fontParameters);
    }

    private static BitmapFont createButtonFont(FreeTypeFontGenerator fontGenerator, String neededCharacters) {
        final FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();
        fontParameters.size = BUTTON_TEXT_SIZE;
        fontParameters.characters = neededCharacters;
        fontParameters.borderWidth = Gdx.graphics.getDensity() * 5 * BUTTON_TEXT_SCALING_FACTOR;
        return fontGenerator.generateFont(fontParameters);
    }

    private static BitmapFont createOverlayFont(FreeTypeFontGenerator fontGenerator, String neededCharacters) {
        final FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();
        fontParameters.size = (int) (Gdx.graphics.getDensity() * TEXT_SCALING_FACTOR * 40);
        fontParameters.characters = neededCharacters;
        fontParameters.borderWidth = Gdx.graphics.getDensity() * 2 * TEXT_SCALING_FACTOR;
        return fontGenerator.generateFont(fontParameters);
    }

    private static BitmapFont createTextFont(FreeTypeFontGenerator fontGenerator, String neededCharacters) {
        final FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();
        fontParameters.size = (int) (Gdx.graphics.getDensity() * TEXT_SCALING_FACTOR * 48);
        fontParameters.characters = neededCharacters;
        fontParameters.color = Color.BLACK;
        return fontGenerator.generateFont(fontParameters);
    }

    private static BitmapFont createSmallerTextFont(FreeTypeFontGenerator fontGenerator, String neededCharacters) {
        final FreeTypeFontParameter fontParameters = new FreeTypeFontParameter();
        fontParameters.size = (int) (Gdx.graphics.getDensity() * TEXT_SCALING_FACTOR * 32);
        fontParameters.characters = neededCharacters;
        return fontGenerator.generateFont(fontParameters);
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.about.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.dagger.VersionProperty;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Factory for the slide displayed in the About Menu Option.
 */
@Singleton
public class AboutSlideFactory {

    private final Skin skin;
    private final String version;
    private final LocalizationManager localizationManager;

    /**
     * Constructor.
     *
     * @param version game version
     * @param skin    game skin
     */
    @Inject
    public AboutSlideFactory(@VersionProperty String version, Skin skin, LocalizationManager localizationManager) {
        this.version = version;
        this.skin = skin;
        this.localizationManager = localizationManager;
    }

    /**
     * Creates the about slide.
     *
     * @return about slides
     */
    public Slide createAboutSlide() {
        final String text1 = localizationManager.localizeText("version-text",
            localizationManager.localizeText("by"), "Sesu8642",
            localizationManager.localizeText("version"), version);
        final String text2 = localizationManager.localizeText("gpl-text");
        final String text3 = "\n" + localizationManager.localizeText("acknowledgements") +
            "\n" + localizationManager.localizeText("acknowledgements-text");
        final String imagePath = "square_logo_64.png";
        final Slide slide = new Slide(skin, localizationManager.localizeText("about-feudal-tactics"));
        slide.getTable().add(new Image(new Texture(imagePath))).row();
        slide.getTable().add(new Label(text1, skin)).center().row();
        slide.addLabel(text2);
        slide.addLabel(text3);
        return slide;
    }

}

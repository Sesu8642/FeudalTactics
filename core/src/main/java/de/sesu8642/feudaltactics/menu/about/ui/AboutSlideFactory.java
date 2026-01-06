// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.about.ui;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
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

    /**
     * Constructor.
     *
     * @param version game version
     * @param skin    game skin
     */
    @Inject
    public AboutSlideFactory(@VersionProperty String version, Skin skin) {
        this.version = version;
        this.skin = skin;
    }

    /**
     * Creates the about slide.
     *
     * @return about slides
     */
    public Slide createAboutSlide() {
        final String text1 = "by Sesu8642\nVersion " + version;
        final String text2 = "This program is free software: you can redistribute it and/or modify it under the terms" +
            " of " +
            "the GNU General Public License as published by the Free Software Foundation, either version 3 of the " +
            "License, or (at your option) any later version.\n\nThis program is distributed in the hope that it will " +
            "be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR" +
            " A PARTICULAR PURPOSE.  See the GNU General Public License for more details.\n\nYou should have received" +
            " a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu" +
            ".org/licenses/>.";
        final String text3 = "\nAcknowledgements:" +
            "\nMany thanks to Sean O'Connor for his game Slay which this game is heavily inspired " +
            "by!\n" +
            "\n" +
            "Many thanks to Amit Patel for his guide to hexagonal grids on redblobgames.com!";
        final String imagePath = "square_logo_64.png";
        final Slide slide = new Slide(skin, "About FeudalTactics");
        slide.getTable().add(new Image(new Texture(imagePath))).row();
        slide.getTable().add(new Label(text1, skin)).center().row();
        slide.addLabel(text2);
        slide.addLabel(text3);
        return slide;
    }

}

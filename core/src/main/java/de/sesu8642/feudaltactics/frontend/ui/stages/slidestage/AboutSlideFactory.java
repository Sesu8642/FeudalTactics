// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.ui.stages.slidestage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.DependencyLicenses;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.VersionProperty;

/** Factory for the slides displayed in the About Menu Option. */
@Singleton
public class AboutSlideFactory {

	private Skin skin;
	private String version;
	private String dependencyLicensesText;

	/**
	 * Constructor.
	 * 
	 * @param version                game version
	 * @param dependencyLicensesText dependency license texts
	 * @param skin                   game skin
	 */
	@Inject
	public AboutSlideFactory(@VersionProperty String version, @DependencyLicenses String dependencyLicensesText,
			Skin skin) {
		this.version = version;
		this.dependencyLicensesText = dependencyLicensesText;
		this.skin = skin;
	}

	/**
	 * Creates all about slides.
	 * 
	 * @return about slides
	 */
	public List<Slide> createAllSlides() {
		List<Slide> slides = new ArrayList<>();
		slides.add(createAboutSlide1());
		slides.add(createAboutSlide2());
		return slides;
	}

	private Slide createAboutSlide1() {
		String text1 = "by Sesu8642\nVersion " + version;
		String text2 = "This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.\n\nThis program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.\n\nYou should have received a copy of the GNU General Public License along with this program.  If not, see <https://www.gnu.org/licenses/>.";
		String imagePath = "square_logo_64.png";
		Slide slide = new Slide(skin, "About FeudalTactics");
		slide.getTable().add(new Image(new Texture(imagePath))).row();
		slide.getTable().add(slide.newNiceLabel(text1)).center().row();
		slide.addLabel(text2);
		return slide;
	}

	private Slide createAboutSlide2() {
		return new Slide(skin, "Dependency Licenses").addLabel(dependencyLicensesText);
	}
}

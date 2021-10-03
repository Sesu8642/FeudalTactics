package com.sesu8642.feudaltactics.ui.stages.slidestage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.sesu8642.feudaltactics.dagger.VersionProperty;

@Singleton
public class AboutSlideFactory {

	private Skin skin;
	private String version;

	@Inject
	public AboutSlideFactory(@VersionProperty String version, Skin skin) {
		this.skin = skin;
		this.version = version;
	}

	public List<Slide> createAllSlides() {
		List<Slide> slides = new ArrayList<Slide>();
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
		String text = "Two or more connected tiles of the same color will form a kingdom. A kingdom will gain one coin per tile per turn. A player can have multiple kingdoms that are financially independent from each other. Select a kingdom by clicking any of its tiles. Its finances will be displayed in the top left corner of the screen. Money is stored in the capital. If a kingdom's capital is destroyed, all of its money is lost.";
		String imagePath = "tutorial_kingdoms_money.png";
		return new Slide(skin, "Dependency Licenses").addLabel(text).addImage(imagePath);
	}
}

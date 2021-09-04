package com.sesu8642.feudaltactics.ui.stages.slidestage;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

@Singleton
public class AboutSlideFactory {

	private Skin skin;

	@Inject
	public AboutSlideFactory(Skin skin) {
		this.skin = skin;
	}

	public List<Slide> createAllSlides() {
		List<Slide> slides = new ArrayList<Slide>();
		slides.add(createABoutSlide1());
		slides.add(createABoutSlide2());
		return slides;
	}

	private Slide createABoutSlide1() {
		String text = "This game is a turn based strategy game. You play on an island composed of hexagonal tiles. The color of the tile indicates which player it is owned by. The blue tiles are owned by you. Your goal is to conquer the whole island.";
		String imagePath = "tutorial_island.png";
		return new Slide(skin, "About FeudalTactics").addLabel(text).addImage(imagePath);
	}

	private Slide createABoutSlide2() {
		String text = "Two or more connected tiles of the same color will form a kingdom. A kingdom will gain one coin per tile per turn. A player can have multiple kingdoms that are financially independent from each other. Select a kingdom by clicking any of its tiles. Its finances will be displayed in the top left corner of the screen. Money is stored in the capital. If a kingdom's capital is destroyed, all of its money is lost.";
		String imagePath = "tutorial_kingdoms_money.png";
		return new Slide(skin, "Dependency Licenses").addLabel(text).addImage(imagePath);
	}
}

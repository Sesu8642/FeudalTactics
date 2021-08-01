package com.sesu8642.feudaltactics.ui.stages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit.UnitTypes;

@Singleton
public class TutorialSlideFactory {

	private Skin skin;

	@Inject
	public TutorialSlideFactory(Skin skin) {
		this.skin = skin;
	}

	public List<Slide> createAllSlides() {
		List<Slide> slides = new ArrayList<Slide>();
		slides.add(createTutorialSlide1());
		slides.add(createTutorialSlide2());
		slides.add(createTutorialSlide3());
		slides.add(createTutorialSlide4());
		return slides;
	}

	private Slide createTutorialSlide1() {
		String text = "There is an island composed of hexagonal tiles. The color of the tile indicates which player it is owned by. The blue tiles are owned by you. Your goal is to conquer the whole island.";
		String imagePath = "tutorial_island.png";
		float aspectRatio = 1;
		return new Slide(skin).addLabel(text).addImage(imagePath, aspectRatio);
	}

	private Slide createTutorialSlide2() {
		String text = "Two or more connected tiles of the same color will form a kingdom. A kingdom will gain one money unit per tile per turn. A player can have multiple kingdoms that are financially independent from each other. Select a kingdom by clicking any of its tiles. Its finances will be displayed in the top left corner of the screen. Money is stored in the capital. If a kingdom's capital is destroyed, all of its money is lost.";
		String imagePath = "tutorial_kingdoms_money.png";
		float aspectRatio = 1;
		return new Slide(skin).addLabel(text).addImage(imagePath, aspectRatio);
	}

	private Slide createTutorialSlide3() {
		String text = "To conquer enemy tiles, you need to get units. You can buy peasants for 10 money units. To get stronger units, combine a unit with a peasant by placing them on top of each other. Every unit must be paid a salary at the start of your turn. If you cannot pay all of your units, they will vanish.";
		String imagePath = "tutorial_units.png";
		float aspectRatio = 1;
		List<List<String>> tableData = new ArrayList<List<String>>();
		tableData.add(Arrays.asList("Unit", "Strength", "Salary"));
		for (UnitTypes unitType : UnitTypes.values()) {
			tableData.add(Arrays.asList(unitType.name(), String.valueOf(unitType.strength()),
					String.valueOf(unitType.salary())));
		}

		return new Slide(skin).addLabel(text).addTable(tableData).addImage(imagePath, aspectRatio);
	}
	
	private Slide createTutorialSlide4() {
		String text = "Units can be picked up and placed in their own kingdom or on neighboring enemy tiles to conquer them. Click on a unit to pick it up. The hand icon will indicate that you have something picked up. Click on any of the highlighted tiles to place it down. After conquering, a unit can no longer be moved for the rest of the turn.";
		String imagePath = "tutorial_unit_movement.png";
		float aspectRatio = 23/34;
		return new Slide(skin).addLabel(text).addImage(imagePath, aspectRatio);
	}

}

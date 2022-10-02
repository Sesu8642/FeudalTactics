// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.ui.stages.slidestage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.sesu8642.feudaltactics.backend.gamelogic.gamestate.Unit.UnitTypes;

/** Factory for the slides displayed in the tutorial. */
@Singleton
public class TutorialSlideFactory {

	private Skin skin;

	private static final String HEADLINE = "Tutorial";

	/**
	 * Constructor.
	 * 
	 * @param skin game skin
	 */
	@Inject
	public TutorialSlideFactory(Skin skin) {
		this.skin = skin;
	}

	/**
	 * Creates all about slides.
	 * 
	 * @return about slides
	 */
	public List<Slide> createAllSlides() {
		List<Slide> slides = new ArrayList<>();
		slides.add(createTutorialSlide1());
		slides.add(createTutorialSlide2());
		slides.add(createTutorialSlide3());
		slides.add(createTutorialSlide4());
		slides.add(createTutorialSlide5());
		slides.add(createTutorialSlide6());
		slides.add(createTutorialSlide7());
		return slides;
	}

	private Slide createTutorialSlide1() {
		String text = "This game is a turn based strategy game. You play on an island composed of hexagonal tiles. The color of the tile indicates which player it is owned by. The blue tiles are owned by you. Your goal is to conquer the whole island.";
		String imagePath = "tutorial_island.png";
		return new Slide(skin, HEADLINE).addLabel(text).addImage(imagePath);
	}

	private Slide createTutorialSlide2() {
		String text = "Two or more connected tiles of the same color will form a kingdom. A kingdom will gain one coin per tile per turn. A player can have multiple kingdoms that are financially independent from each other. Select a kingdom by clicking any of its tiles. Its finances will be displayed in the top left corner of the screen. Money is stored in the capital. If a kingdom's capital is destroyed, all of its money is lost.";
		String imagePath = "tutorial_kingdoms_money.png";
		return new Slide(skin, HEADLINE).addLabel(text).addImage(imagePath);
	}

	private Slide createTutorialSlide3() {
		String text = "To conquer enemy tiles, you need to get units. You can buy peasants for 10 coins. To get stronger units, combine a unit with a peasant by placing them on top of each other. Every unit must be paid a salary at the start of your turn. If you cannot pay all of your units, they will vanish.";
		String imagePath = "tutorial_units.png";
		List<List<String>> tableData = new ArrayList<>();
		tableData.add(Arrays.asList("Unit", "Strength", "Salary"));
		for (UnitTypes unitType : UnitTypes.values()) {
			tableData.add(Arrays.asList(unitType.name(), String.valueOf(unitType.strength()),
					String.valueOf(unitType.salary())));
		}
		return new Slide(skin, HEADLINE).addLabel(text).addTable(tableData).addImage(imagePath);
	}

	private Slide createTutorialSlide4() {
		String text = "Units can be picked up and placed in their own kingdom or on neighboring enemy tiles to conquer them. Click on a unit to pick it up. The hand icon will indicate that you have something picked up. Click on any of the highlighted tiles to place it down. After conquering, a unit can no longer be moved for the rest of the turn.";
		String imagePath = "tutorial_unit_movement.png";
		return new Slide(skin, HEADLINE).addLabel(text).addImage(imagePath);
	}

	private Slide createTutorialSlide5() {
		String text = "Units will protect the tile they stand on as well as the tiles next to them from being conquered. To conquer protected tiles anyway, you need a unit stronger that the one protecting. Capitals will also protect the neighboring tiles with a strength of 1. Additionaly, you can buy a castle for 15 coins which will protect neighboring tiles with a strength of 2. When you have something picked up, shield icons indicate the protection level of each tile.";
		String imagePath = "tutorial_protection.png";
		return new Slide(skin, HEADLINE).addLabel(text).addImage(imagePath);
	}

	private Slide createTutorialSlide6() {
		String text = "Trees may randomly grow on the map. They will prevent the tiles they stand on from generating income. Even worse, there is a TODO% chance they will spread to a neighboring tile. They can be removed by placing a unit on them but the unit will be unable to be moved again for the rest of that turn.";
		String imagePath = "tutorial_trees.png";
		return new Slide(skin, HEADLINE).addLabel(text).addImage(imagePath);
	}

	private Slide createTutorialSlide7() {
		String text = "The user interface contains the following elements:\n1. Finaces of the selected kingdom. (income in brackets)\n2. Selected kingdom: outlined in white; tiles that can be conquered outlined in red\n3. Picked up object\n4. Pause\n5. Undo action\n6. Buy Peasant for 10 coins\n7. Buy castle for 15 coins\n8. End turn";
		String imagePath = "tutorial_ui.png";
		return new Slide(skin, HEADLINE).addLabel(text).addImage(imagePath);
	}

}

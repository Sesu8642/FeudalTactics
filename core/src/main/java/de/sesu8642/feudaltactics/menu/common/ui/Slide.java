// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;

/** A single slide / page that can be displayed in the {@link SlideStage}. */
public class Slide {

	private static final float MAX_RESPONSIVE_IMAGE_WIDTH = Gdx.graphics.getDensity() * 1500F;

	private Skin skin;
	private Table table = new Table();

	/**
	 * Constructor. Creates an empty slide. Content can be added with fluent add*
	 * functions.
	 * 
	 * @param skin     game skin
	 * @param headline headline for this slide
	 */
	public Slide(Skin skin, String headline) {
		this.skin = skin;
		// cannot use fillParent because it then the content will be placed a little too
		// high
		table.defaults().pad(10);
		// adding the headline is a hack needed because the slide would get a width of 0
		// if the the label does not need to wrap (bug?)
		Table hackTable = new Table();
		Label headlineLabel = newNiceLabel(headline);
		headlineLabel.setFontScale(2F);
		hackTable.add(headlineLabel);
		table.add(hackTable);
		table.row();
	}

	/**
	 * Adds a label to the slide fluent style.
	 * 
	 * @param text text for the label
	 * @return this slide
	 */
	public Slide addLabel(String text) {
		Label label = newNiceLabel(text);
		label.setWrap(true);
		table.add(label).fill().expand();
		table.row();
		return this;
	}

	/**
	 * Adds an image to the slide fluent style.
	 * 
	 * @param imagePath internal path of the image
	 * @return this slide
	 */
	public Slide addImage(String imagePath) {
		Texture imageTexture = new Texture(Gdx.files.internal(imagePath));
		Image image = new Image(imageTexture);
		float aspectRatio = ((float) imageTexture.getHeight()) / ((float) imageTexture.getWidth());
		table.add(image).prefWidth(0).maxWidth(MAX_RESPONSIVE_IMAGE_WIDTH).height(Value.percentWidth(aspectRatio))
				.expand().fill();
		table.row();
		return this;
	}

	/**
	 * Adds a table to the slide fluent style.
	 * 
	 * @param data data to be displayed in the table; outer list = rows, inner list
	 *             = cols
	 * @return this slide
	 */
	public Slide addTable(List<List<String>> data) {
		Table dataTable = new Table();
		dataTable.defaults().pad(5);
		for (List<String> rowContent : data) {
			for (String cellContent : rowContent) {
				dataTable.add(newNiceLabel(cellContent));
			}
			dataTable.row();
		}
		table.add(dataTable);
		table.row();
		return this;
	}

	/**
	 * Creates a nicely formatted label.
	 * 
	 * @param content text for the label
	 * @return label
	 */
	public Label newNiceLabel(String content) {
		Label result = new Label(content, skin);
		result.setColor(skin.getColor("black"));
		result.setFontScale(1.5F);
		return result;
	}

	/**
	 * Creates a nicely formatted label.
	 * 
	 * @param content text for the label
	 * @param skin    game skin
	 * @return label
	 */
	public static Label newNiceLabel(String content, Skin skin) {
		Label result = new Label(content, skin);
		result.setColor(skin.getColor("black"));
		result.setFontScale(1.5F);
		return result;
	}

	public Table getTable() {
		return table;
	}

}

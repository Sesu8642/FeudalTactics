package com.sesu8642.feudaltactics.ui.stages;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;

public class Slide {

	public static float MAX_RESPONSIVE_IMAGE_SIZE = 1000;

	private Skin skin;
	private Table table = new Table();

	public Slide(Skin skin) {
		this.skin = skin;
		table.defaults().pad(10);
	}

	public Slide addLabel(String text) {
		Label label = newNiceLabel(text);
		label.setWrap(true);

		table.add(label).fill();
		table.row();
		return this;
	}

	public Slide addImage(String imagePath, float aspectRatio) {
		Texture imageTexture = new Texture(Gdx.files.internal(imagePath));
		Image image = new Image(imageTexture);

		table.add(image).fill().expand().prefWidth(0).maxWidth(MAX_RESPONSIVE_IMAGE_SIZE)
				.height(Value.percentWidth(aspectRatio));
		table.row();
		return this;
	}

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
	
	private Label newNiceLabel(String content) {
		Label result = new Label(content, skin);
		result.setColor(skin.getColor("black"));
		return result;
	}
	
	public Table getTable() {
		return table;
	}

}

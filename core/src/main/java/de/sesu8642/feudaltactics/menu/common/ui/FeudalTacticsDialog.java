// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;

/**
 * Parent class for all {@link Dialog}s in the game. Applies sane settings and
 * handles responsiveness.
 */
public class FeudalTacticsDialog extends Dialog {

	public static final float DIALOG_PADDING = 20;
	public static final float DIALOG_LABEL_MAX_WIDTH = 600;
	
	private static final float MAX_RESPONSIVE_IMAGE_WIDTH = Gdx.graphics.getDensity() * 1500F;

	private Skin skin;

	/**
	 * Constructor.
	 * 
	 * @param skin game skin
	 */
	public FeudalTacticsDialog(Skin skin) {
		super("", skin);
		this.skin = skin;
		getColor().a = 0; // fixes pop-in; see https://github.com/libgdx/libgdx/issues/3920
		setMovable(false);
		setKeepWithinStage(false);
		pad(DIALOG_PADDING);
	}

	@Override
	public FeudalTacticsDialog text(String text) {
		Label responsiveLabel = new Label(text, skin.get(SkinConstants.FONT_OVERLAY, LabelStyle.class));
		LabelStyle style = new LabelStyle(responsiveLabel.getStyle());
		responsiveLabel.setStyle(style);
		
		responsiveLabel.setWrap(true);
		this.getContentTable().add(responsiveLabel)
				.width(Math.min(DIALOG_LABEL_MAX_WIDTH, Gdx.graphics.getWidth() - 2 * DIALOG_PADDING));
		this.getContentTable().row();
		return this;
	}

	@Override
	public FeudalTacticsDialog button(String text, Object param) {
		TextButton button = new TextButton(text, skin);
		return button(button, param);
	}
	
	@Override
	public FeudalTacticsDialog button(Button button, Object param) {
		// pack the table so its width is calculated
		getButtonTable().pack();
		if (getButtonTable().getWidth() + button.getWidth() > Gdx.graphics.getWidth() - 2 * DIALOG_PADDING) {
			// put button in second row
			getButtonTable().row();
		}
		super.button(button, param);
		return this;
	}
	
	/**
	 * Adds a table to the dialog fluent style.
	 * 
	 * @param data data to be displayed in the table; outer list = rows, inner list
	 *             = cols
	 * @return this dialog
	 */
	public FeudalTacticsDialog addTable(List<List<String>> data) {
		Table dataTable = new Table();
		dataTable.defaults().pad(5);
		for (List<String> rowContent : data) {
			for (String cellContent : rowContent) {
				Label responsiveLabel = new Label(cellContent, skin.get(SkinConstants.FONT_OVERLAY, LabelStyle.class));
				dataTable.add(responsiveLabel);
			}
			dataTable.row();
		}
		this.getContentTable().add(dataTable);
		this.getContentTable().row();
		return this;
	}
	
	/**
	 * Adds an image to the dialog fluent style.
	 * 
	 * @param imagePath internal path of the image
	 * @return this dialog
	 */
	public FeudalTacticsDialog addImage(String imagePath) {
		Texture imageTexture = new Texture(Gdx.files.internal(imagePath));
		Image image = new Image(imageTexture);
		float aspectRatio = ((float) imageTexture.getHeight()) / ((float) imageTexture.getWidth());
		this.getContentTable().add(image).prefWidth(0).maxWidth(MAX_RESPONSIVE_IMAGE_WIDTH).height(Value.percentWidth(aspectRatio))
				.expand().fill();
		this.getContentTable().row();
		return this;
	}
	
}

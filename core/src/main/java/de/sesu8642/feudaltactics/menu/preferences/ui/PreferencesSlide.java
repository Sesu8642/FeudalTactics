// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

import de.sesu8642.feudaltactics.menu.common.ui.Slide;

// this is not just a slide created by a factory because it needs the additional accessors for the preferences
// it is not created by the PreferencesStage because that could only use static methods as the slide needs to be passed to the super constructor

/** UI for the preferences. */
@Singleton
public class PreferencesSlide extends Slide {

	private final SelectBox<Boolean> forgottenKingdomSelectBox;
	private final SelectBox<Boolean> showEnemyTurnsSelectBox;

	/**
	 * Constructor.
	 * 
	 * @param skin game skin
	 */
	@Inject
	public PreferencesSlide(Skin skin) {
		super(skin, "Preferences");

		Table preferencesTable = new Table();

		forgottenKingdomSelectBox = placeBooleanSelectWithLabel(preferencesTable, "Warn about forgotten kingdoms",
				skin);
		showEnemyTurnsSelectBox = placeBooleanSelectWithLabel(preferencesTable, "Show enemy turns", skin);

		// add a row to fill the rest of the space in order for the other options to be
		// at the top of the page
		preferencesTable.row();
		preferencesTable.add().fill().expand();

		getTable().add(preferencesTable).fill().expand();
	}

	private SelectBox<Boolean> placeBooleanSelectWithLabel(Table preferencesTable, String labelText, Skin skin) {
		Label newLabel = Slide.newNiceLabel(labelText, skin);
		newLabel.setWrap(true);
		preferencesTable.add(newLabel).left().fill().expandX().prefWidth(200);
		SelectBox<Boolean> newSelectBox = new SelectBox<>(skin);
		newSelectBox.setItems(true, false);
		preferencesTable.add(newSelectBox).center().fillX().expandX();
		preferencesTable.row();
		preferencesTable.add().height(20);
		preferencesTable.row();
		return newSelectBox;
	}

	public SelectBox<Boolean> getForgottenKingdomSelectBox() {
		return forgottenKingdomSelectBox;
	}

	public SelectBox<Boolean> getShowEnemyTurnsSelectBox() {
		return showEnemyTurnsSelectBox;
	}

}

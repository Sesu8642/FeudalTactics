// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.information.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

import de.sesu8642.feudaltactics.menu.common.ui.Slide;

// this is not created by the DependencyLicensesStage because that could only use static methods as the slide needs to be passed to the super constructor

/** UI for the dependency details. */
@Singleton
public class DependencyDetailsSlide extends Slide {

	final Label label;

	/**
	 * Constructor.
	 */
	@Inject
	public DependencyDetailsSlide(Skin skin) {
		super(skin, "Dependency Details");
		label = new Label("", skin);
		label.setWrap(true);
		super.getTable().add(label).fill().expand();
	}

}

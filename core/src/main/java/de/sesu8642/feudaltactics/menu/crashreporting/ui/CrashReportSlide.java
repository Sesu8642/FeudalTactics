// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.crashreporting.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;

import de.sesu8642.feudaltactics.menu.common.ui.CopyButton;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;

// this is not just a slide created by a factory because it needs the additional accessors for text
// it is not created by the CrashReportStageForMenu because that could only use static methods as the slide needs to be passed to the super constructor

/** UI for crash reporting. */
@Singleton
public class CrashReportSlide extends Slide {

	final Label descriptionLabel;
	final ImageTextButton copyButton;
	final TextArea textArea;
	final TextButton sendMailButton;
	final TextButton openGithubButton;
	final HorizontalGroup buttonGroup;

	/**
	 * Constructor.
	 * 
	 * @param skin game skin
	 */
	@Inject
	public CrashReportSlide(Skin skin) {
		super(skin, "Report a Crash");

		descriptionLabel = new Label(
				"Feudal Tactics previously crashed. Please report the following information via email or GitHub.",
				skin);
		descriptionLabel.setWrap(true);
		descriptionLabel.setAlignment(Align.topLeft);

		textArea = new TextArea("", skin);
		textArea.setDisabled(true);

		copyButton = new CopyButton("Copy", skin);
		sendMailButton = new TextButton("Send Email", skin);
		openGithubButton = new TextButton("Open GitHub", skin);

		buttonGroup = new HorizontalGroup();
		buttonGroup.addActor(copyButton);
		buttonGroup.addActor(sendMailButton);
		buttonGroup.addActor(openGithubButton);
		buttonGroup.wrap();
		buttonGroup.space(10);

		getTable().add(descriptionLabel).fill().expandX();
		getTable().row();
		getTable().add(textArea).fill().expand();
		getTable().row();
		getTable().add(buttonGroup).fill().expandX();
	}

}

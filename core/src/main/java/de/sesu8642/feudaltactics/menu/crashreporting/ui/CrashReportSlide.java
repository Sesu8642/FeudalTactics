// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.crashreporting.ui;

import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Align;
import de.sesu8642.feudaltactics.menu.common.ui.ButtonFactory;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;

import javax.inject.Inject;
import javax.inject.Singleton;

// this is not just a slide created by a factory because it needs the additional accessors for text
// it is not created by the CrashReportStageForMenu because that could only use static methods as the slide needs to
// be passed to the super constructor

/**
 * UI for crash reporting.
 */
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

        copyButton = ButtonFactory.createCopyButton("Copy", skin, true);
        sendMailButton = ButtonFactory.createTextButton("Send Email ↗", skin);
        openGithubButton = ButtonFactory.createTextButton("Open GitHub ↗", skin);

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

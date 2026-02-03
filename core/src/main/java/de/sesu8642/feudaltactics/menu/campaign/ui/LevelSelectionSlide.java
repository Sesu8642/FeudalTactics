// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.campaign.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.Align;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;

import javax.inject.Inject;
import javax.inject.Singleton;


// TODO: check this
// this is not just a slide created by a factory because it needs the additional accessors for text
// it is not created by the LevelSelectionStageForMenu because that could only use static methods as the slide needs to
// be passed to the super constructor

/**
 * UI for level selection.
 */
@Singleton
public class LevelSelectionSlide extends Slide {

    final Label descriptionLabel;
    final TextArea textArea;

    /**
     * Constructor.
     */
    @Inject
    public LevelSelectionSlide(Skin skin, LocalizationManager localizationManager) {
        super(skin, localizationManager.localizeText("level-selection"));

        final MapPreviewWidget mapPreviewWidget = new MapPreviewWidget();
        mapPreviewWidget.setPosition(300, 150);

        descriptionLabel = new Label(
            "",
            skin);
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.topLeft);

        textArea = new TextArea("", skin);
        textArea.setDisabled(true);

        getTable().add(descriptionLabel).fill().expandX();
        getTable().row();
        getTable().add(mapPreviewWidget).fill().expand();
    }

}

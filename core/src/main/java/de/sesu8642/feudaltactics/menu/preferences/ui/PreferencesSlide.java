// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;
import de.sesu8642.feudaltactics.menu.preferences.SupportedLanguages;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;

// this is not just a slide created by a factory because it needs the additional accessors for the preferences
// it is not created by the PreferencesStage because that could only use static methods as the slide needs to be
// passed to the super constructor

/**
 * UI for the preferences.
 */
@Singleton
public class PreferencesSlide extends Slide {

    @Getter
    private final SelectBox<Boolean> forgottenKingdomSelectBox;
    @Getter
    private final SelectBox<Boolean> showEnemyTurnsSelectBox;
    @Getter
    private final SelectBox<String> languageSelectBox;

    /**
     * Constructor.
     *
     * @param skin game skin
     */
    @Inject
    public PreferencesSlide(Skin skin, LocalizationManager localizationManager) {
        super(skin, localizationManager.localizeText("preferences"));

        final Table preferencesTable = new Table();

        forgottenKingdomSelectBox = placeBooleanSelectWithLabel(preferencesTable, localizationManager.localizeText("warn-about-forgotten-kingdoms"),
            skin);
        showEnemyTurnsSelectBox = placeBooleanSelectWithLabel(preferencesTable,
            localizationManager.localizeText("show-enemy-turns"), skin);

        languageSelectBox = placeStringSelectWithLabel(preferencesTable,
            localizationManager.localizeText("select-language"), skin, SupportedLanguages.getSupportedLanguages());

        // add a row to fill the rest of the space in order for the other options to be
        // at the top of the page
        preferencesTable.row();
        preferencesTable.add().fill().expand();

        getTable().add(preferencesTable).fill().expand();
    }

    private SelectBox<String> placeStringSelectWithLabel(Table preferencesTable, String labelText, Skin skin, String[] options) {
        final Label newLabel = new Label(labelText, skin);
        newLabel.setWrap(true);
        preferencesTable.add(newLabel).left().fill().expandX().prefWidth(200);
        final SelectBox<String> newSelectBox = new SelectBox<>(skin);
        newSelectBox.setItems(options);
        preferencesTable.add(newSelectBox).center().fillX().expandX();
        preferencesTable.row();
        preferencesTable.add().height(20);
        preferencesTable.row();
        return newSelectBox;
    }

    private SelectBox<Boolean> placeBooleanSelectWithLabel(Table preferencesTable, String labelText, Skin skin) {
        final Label newLabel = new Label(labelText, skin);
        newLabel.setWrap(true);
        preferencesTable.add(newLabel).left().fill().expandX().prefWidth(200);
        final SelectBox<Boolean> newSelectBox = new SelectBox<>(skin);
        newSelectBox.setItems(true, false);
        preferencesTable.add(newSelectBox).center().fillX().expandX();
        preferencesTable.row();
        preferencesTable.add().height(20);
        preferencesTable.row();
        return newSelectBox;
    }

}

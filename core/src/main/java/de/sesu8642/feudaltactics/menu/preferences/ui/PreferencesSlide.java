// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.localization.SupportedLanguage;
import de.sesu8642.feudaltactics.menu.common.ui.BooleanSelectBox;
import de.sesu8642.feudaltactics.menu.common.ui.InsetsRespectingSelectBox;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
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
    private final InsetsRespectingSelectBox<Boolean> forgottenKingdomSelectBox;
    @Getter
    private final InsetsRespectingSelectBox<Boolean> showEnemyTurnsSelectBox;
    @Getter
    private final InsetsRespectingSelectBox<String> languageSelectBox;

    /**
     * Constructor.
     *
     * @param skin game skin
     */
    @Inject
    public PreferencesSlide(Skin skin, LocalizationManager localizationManager, PlatformInsetsProvider platformInsetsProvider) {
        super(skin, localizationManager.localizeText(TranslationKeys.SETTINGS_PAGE_HEADLINE));

        final Table preferencesTable = new Table();

        forgottenKingdomSelectBox = placeBooleanSelectWithLabel(preferencesTable, localizationManager.localizeText(
                TranslationKeys.SETTINGS_PAGE_LABEL_WARN_ABOUT_FORGOTTEN_KINGDOMS),
            skin, localizationManager, platformInsetsProvider);
        showEnemyTurnsSelectBox = placeBooleanSelectWithLabel(preferencesTable,
            localizationManager.localizeText(TranslationKeys.SETTINGS_PAGE_LABEL_SHOW_ENEMY_TURNS), skin,
            localizationManager, platformInsetsProvider);

        String[] supportedLanguages = localizationManager.getSupportedLanguages().stream().map(SupportedLanguage::getDisplayName).toArray(String[]::new);
        languageSelectBox = placeStringSelectWithLabel(preferencesTable,
            localizationManager.localizeText(TranslationKeys.SETTINGS_PAGE_LABEL_LANGUAGE), skin,
            platformInsetsProvider, supportedLanguages);

        // add a row to fill the rest of the space in order for the other options to be
        // at the top of the page
        preferencesTable.row();
        preferencesTable.add().fill().expand();

        getTable().add(preferencesTable).fill().expand();
    }

    private InsetsRespectingSelectBox<String> placeStringSelectWithLabel(Table preferencesTable, String labelText, Skin skin,
                                                                         PlatformInsetsProvider platformInsetsProvider, String[] options) {
        final Label newLabel = new Label(labelText, skin);
        newLabel.setWrap(true);
        preferencesTable.add(newLabel).left().fill().expandX().prefWidth(200);
        final InsetsRespectingSelectBox<String> newSelectBox = new InsetsRespectingSelectBox<>(skin, platformInsetsProvider);
        newSelectBox.setItems(options);
        preferencesTable.add(newSelectBox).center().fillX().expandX();
        preferencesTable.row();
        preferencesTable.add().height(20);
        preferencesTable.row();
        return newSelectBox;
    }

    private InsetsRespectingSelectBox<Boolean> placeBooleanSelectWithLabel(Table preferencesTable, String labelText, Skin skin,
                                                           LocalizationManager localizationManager, PlatformInsetsProvider platformInsetsProvider) {
        final Label newLabel = new Label(labelText, skin);
        newLabel.setWrap(true);
        preferencesTable.add(newLabel).left().fill().expandX().prefWidth(200);
        final BooleanSelectBox newSelectBox = new BooleanSelectBox(skin,
            localizationManager.localizeText(TranslationKeys.BOOLEAN_COMBOBOX_OPTION_YES),
            localizationManager.localizeText(TranslationKeys.BOOLEAN_COMBOBOX_OPTION_NO), platformInsetsProvider);
        newSelectBox.setItems(true, false);
        preferencesTable.add(newSelectBox).center().fillX().expandX();
        preferencesTable.row();
        preferencesTable.add().height(20);
        preferencesTable.row();
        return newSelectBox;
    }

}

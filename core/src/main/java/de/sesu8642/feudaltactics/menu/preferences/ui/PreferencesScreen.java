// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.events.MainPreferencesChangeEvent;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
import de.sesu8642.feudaltactics.localization.SupportedLanguage;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.preferences.MainGamePreferences;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Stream;

/**
 * Screen for the preferences menu.
 */
@Singleton
public class PreferencesScreen extends GameScreen {

    private final MainPreferencesDao mainPrefsDao;
    private final PreferencesStage preferencesStage;
    private final EventBus eventBus;
    private final LocalizationManager localizationManager;
    private final DialogFactory dialogFactory;
    /**
     * Language used at the time of opening the preferences. Using the language string instead of the Language enum
     * because that would require handling the auto option specifically.
     */
    private String currentLanguage;


    @Inject
    public PreferencesScreen(MainPreferencesDao mainPrefsDao, @MenuCamera OrthographicCamera camera,
                             @MenuViewport Viewport viewport, PreferencesStage preferencesStage,
                             LocalizationManager localizationManager, EventBus eventBus, DialogFactory dialogFactory) {
        super(camera, viewport, preferencesStage);
        this.preferencesStage = preferencesStage;
        this.mainPrefsDao = mainPrefsDao;
        this.eventBus = eventBus;
        this.localizationManager = localizationManager;
        this.dialogFactory = dialogFactory;

        registerEventListeners();
    }

    void saveUpdatedPreferences(MainGamePreferences mainPreferences) {
        mainPrefsDao.saveMainPreferences(mainPreferences);
    }

    private void sendPreferencesChangedEvent() {
        eventBus.post(new MainPreferencesChangeEvent(
            new MainGamePreferences(
                preferencesStage.preferencesSlide.getForgottenKingdomSelectBox().getSelected(),
                preferencesStage.preferencesSlide.getShowEnemyTurnsSelectBox().getSelected(),
                localizationManager.getSupportedLanguages().get(preferencesStage.preferencesSlide.getLanguageSelectBox().getSelectedIndex()))));
    }

    private void registerEventListeners() {
        Stream.of(preferencesStage.preferencesSlide.getForgottenKingdomSelectBox(),
                preferencesStage.preferencesSlide.getShowEnemyTurnsSelectBox())
            .forEach(actor -> actor
                .addListener(new ExceptionLoggingChangeListener(this::sendPreferencesChangedEvent)));

        preferencesStage.preferencesSlide.getLanguageSelectBox().addListener(new ExceptionLoggingChangeListener(() -> {
            final SupportedLanguage selectedLanguage =
                localizationManager.getSupportedLanguages().get(preferencesStage.preferencesSlide.getLanguageSelectBox().getSelectedIndex());
            if (selectedLanguage.getLocale().getLanguage().equals(currentLanguage)) {
                sendPreferencesChangedEvent();
                return;
            }

            // Show bilingual restart prompt
            final String oldLanguageText = localizationManager.localizeText(TranslationKeys.DIALOG_TEXT_RESTART_GAME);
            final String newLanguageText = localizationManager.localizeTextInLanguage(selectedLanguage,
                TranslationKeys.DIALOG_TEXT_RESTART_GAME);

            sendPreferencesChangedEvent();

            dialogFactory.createInformationDialog(
                oldLanguageText + "\n\n" + newLanguageText, () -> {
                }).show(preferencesStage);
        }));
    }

    @Override
    public void show() {
        currentLanguage = mainPrefsDao.getMainPreferences().getLanguage().getLocale().getLanguage();
        super.show();
    }
}

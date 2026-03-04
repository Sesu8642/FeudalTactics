// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.events.MainPreferencesChangeEvent;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.preferences.MainGamePreferences;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;
import de.sesu8642.feudaltactics.menu.preferences.SupportedLanguages;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Locale;
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
     * Locale used at the time of opening the preferences. Using the locale instead of the Language enum because that
     * would require handling the auto option specifically.
     */
    private Locale currentLocale;


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
                SupportedLanguages.values()[preferencesStage.preferencesSlide.getLanguageSelectBox().getSelectedIndex()])));
    }

    private void registerEventListeners() {
        Stream.of(preferencesStage.preferencesSlide.getForgottenKingdomSelectBox(),
                preferencesStage.preferencesSlide.getShowEnemyTurnsSelectBox())
            .forEach(actor -> actor
                .addListener(new ExceptionLoggingChangeListener(this::sendPreferencesChangedEvent)));

        preferencesStage.preferencesSlide.getLanguageSelectBox().addListener(new ExceptionLoggingChangeListener(() -> {
            final SupportedLanguages selectedLanguage =
                SupportedLanguages.values()[preferencesStage.preferencesSlide.getLanguageSelectBox().getSelectedIndex()];
            if (selectedLanguage.getLocale().equals(currentLocale)) {
                sendPreferencesChangedEvent();
                return;
            }

            // Show bilingual restart prompt
            final String oldLanguageText = localizationManager.localizeText("restart-game-prompt");
            final String newLanguageText = localizationManager.localizeTextInLanguage(selectedLanguage, "restart-game" +
                "-prompt");

            sendPreferencesChangedEvent();

            dialogFactory.createInformationDialog(
                oldLanguageText + "\n\n" + newLanguageText, () -> {
                }).show(preferencesStage);
        }));
    }

    @Override
    public void show() {
        currentLocale = mainPrefsDao.getMainPreferences().getLanguage().getLocale();
        super.show();
    }
}

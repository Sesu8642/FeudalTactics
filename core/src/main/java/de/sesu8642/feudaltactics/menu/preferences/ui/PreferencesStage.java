// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.events.MainPreferencesChangeEvent;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;
import de.sesu8642.feudaltactics.menu.preferences.MainGamePreferences;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;
import java.util.stream.Stream;

/**
 * {@link Stage} that displays the global preferences menu.
 */
@Singleton
public class PreferencesStage extends SlideStage {

    private final EventBus eventBus;
    private final PreferencesSlide preferencesSlide;
    private final MainPreferencesDao mainPrefsDao;

    /**
     * Constructor.
     */
    @Inject
    public PreferencesStage(EventBus eventBus, PreferencesSlide preferencesSlide, MainPreferencesDao mainPrefsDao,
                            @MenuViewport Viewport viewport, PlatformInsetsProvider platformInsetsProvider,
                            @MenuCamera OrthographicCamera camera, Skin skin,
                            ScreenNavigationController screenNavigationController,
                            LocalizationManager localizationManager) {
        super(viewport, Collections.singletonList(preferencesSlide), platformInsetsProvider,
            screenNavigationController::transitionToMainMenuScreen, camera, skin, localizationManager);
        this.eventBus = eventBus;
        this.preferencesSlide = preferencesSlide;
        this.mainPrefsDao = mainPrefsDao;
        initUi();
    }

    private void initUi() {
        Stream.of(preferencesSlide.getForgottenKingdomSelectBox(),
                preferencesSlide.getShowEnemyTurnsSelectBox(),
                preferencesSlide.getLanguageSelectBox())
            .forEach(actor -> actor
                .addListener(new ExceptionLoggingChangeListener(this::sendPreferencesChangedEvent)));
    }

    private void sendPreferencesChangedEvent() {
        eventBus.post(new MainPreferencesChangeEvent(
            new MainGamePreferences(
                preferencesSlide.getForgottenKingdomSelectBox().getSelected(),
                preferencesSlide.getShowEnemyTurnsSelectBox().getSelected(),
                preferencesSlide.getLanguageSelectBox().getSelected())));
    }

    @Override
    public void reset() {
        super.reset();
        // sync the UI with the current preferences
        final MainGamePreferences currentPreferences = mainPrefsDao.getMainPreferences();
        preferencesSlide.getForgottenKingdomSelectBox().setSelected(currentPreferences.isWarnAboutForgottenKingdoms());
        preferencesSlide.getShowEnemyTurnsSelectBox().setSelected(currentPreferences.isShowEnemyTurns());
        preferencesSlide.getLanguageSelectBox().setSelected(currentPreferences.getLanguage());
    }
}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.play.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.dagger.EnableCampaignProperty;
import de.sesu8642.feudaltactics.events.NewGamePreferencesChangedEvent;
import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.events.SeedGeneratedEvent;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.NewGamePreferencesDao;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.common.ui.MenuStage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * {@link Screen} for displaying the play menu.
 */
@Singleton
public class PlayMenuScreen extends GameScreen {

    private final NewGamePreferencesDao newGamePreferencesDao;
    private final EventBus eventBus;
    private final ScreenNavigationController screenNavigationController;
    private final boolean campaignEnabled;

    /**
     * Constructor.
     */
    @Inject
    public PlayMenuScreen(NewGamePreferencesDao newGamePreferencesDao,
                          @MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                          PlayMenuStage playMenuStage, EventBus eventBus,
                          ScreenNavigationController screenNavigationController,
                          @EnableCampaignProperty boolean campaignEnabled) {
        super(camera, viewport, playMenuStage);
        this.newGamePreferencesDao = newGamePreferencesDao;
        this.eventBus = eventBus;
        this.screenNavigationController = screenNavigationController;
        this.campaignEnabled = campaignEnabled;
        initUi(playMenuStage);
    }

    private void initUi(MenuStage stage) {
        final List<TextButton> buttons = stage.getButtons();
        int i = 0;
        // sandbox game button
        buttons.get(i).addListener(new ExceptionLoggingChangeListener(this::initSandboxGame));
        if (campaignEnabled) {
            // campaign button
            buttons.get(++i).addListener(new ExceptionLoggingChangeListener(screenNavigationController::transitionToCampaignLevelSelectionScreen));
        }
        // back button
        buttons.get(++i).addListener(new ExceptionLoggingChangeListener(screenNavigationController::transitionToMainMenuScreen));
    }

    private void initSandboxGame() {
        final NewGamePreferences savedPrefs = newGamePreferencesDao.getNewGamePreferences();
        final long newSeed = System.currentTimeMillis();
        savedPrefs.setSeed(newSeed);
        newGamePreferencesDao.saveNewGamePreferences(savedPrefs);
        screenNavigationController.transitionToIngameScreen();
        eventBus.post(new NewGamePreferencesChangedEvent(savedPrefs));
        eventBus.post(new RegenerateMapEvent(savedPrefs.toGameParameters()));
        eventBus.post(new SeedGeneratedEvent());
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.play.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.events.NewGamePreferencesChangedEvent;
import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
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

    /**
     * Constructor.
     */
    @Inject
    public PlayMenuScreen(NewGamePreferencesDao newGamePreferencesDao,
                          @MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                          PlayMenuStage playMenuStage, EventBus eventBus) {
        super(camera, viewport, playMenuStage);
        this.newGamePreferencesDao = newGamePreferencesDao;
        this.eventBus = eventBus;
        initUi(playMenuStage);
    }

    private void initUi(MenuStage stage) {
        final List<TextButton> buttons = stage.getButtons();
        // sandbox game button
        buttons.get(0).addListener(new ExceptionLoggingChangeListener(this::initSandboxGame));
    }

    private void initSandboxGame() {
        final NewGamePreferences savedPrefs = newGamePreferencesDao.getNewGamePreferences();
        final long newSeed = System.currentTimeMillis();
        savedPrefs.setSeed(newSeed);
        newGamePreferencesDao.saveNewGamePreferences(savedPrefs);
        eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INGAME_SCREEN));
        eventBus.post(new NewGamePreferencesChangedEvent(savedPrefs));
        eventBus.post(new RegenerateMapEvent(savedPrefs.toGameParameters()));
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.mainmenu.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.dagger.EnableLevelEditorProperty;
import de.sesu8642.feudaltactics.events.InitializeScenarioEvent;
import de.sesu8642.feudaltactics.events.moves.GameStartEvent;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.common.ui.MenuStage;
import de.sesu8642.feudaltactics.menu.preferences.NagPreferencesDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * {@link Screen} for displaying the main menu.
 */
@Singleton
public class MainMenuScreen extends GameScreen {

    private final Logger logger = LoggerFactory.getLogger(getClass().getName());

    private final NagPreferencesDao nagPreferencesDao;
    private final DialogFactory dialogFactory;
    private final EventBus eventBus;
    private final ScreenNavigationController screenNavigationController;
    private final boolean levelEditorEnabled;

    /**
     * Constructor.
     */
    @Inject
    public MainMenuScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                          MainMenuStage mainMenuStage, NagPreferencesDao nagPreferencesDao,
                          DialogFactory dialogFactory, EventBus eventBus,
                          ScreenNavigationController screenNavigationController,
                          @EnableLevelEditorProperty boolean levelEditorEnabled) {
        super(camera, viewport, mainMenuStage);
        this.nagPreferencesDao = nagPreferencesDao;
        this.dialogFactory = dialogFactory;
        this.eventBus = eventBus;
        this.screenNavigationController = screenNavigationController;
        this.levelEditorEnabled = levelEditorEnabled;
        initUi(mainMenuStage);
    }

    private void initUi(MenuStage stage) {
        final List<TextButton> buttons = stage.getButtons();
        int i = 0;
        // play button
        buttons.get(i).addListener(new ExceptionLoggingChangeListener(() -> {
            if (nagPreferencesDao.getShowTutorialNag()) {
                nagPreferencesDao.setShowTutorialNag(false);
                showTutorialNag();
            } else {
                screenNavigationController.transitionToPlayMenuScreen();
            }
        }));
        if (levelEditorEnabled) {
            // level editor
            buttons.get(++i).addListener(new ExceptionLoggingChangeListener(() -> {
                screenNavigationController.transitionToEditorScreen();
                eventBus.post(new InitializeScenarioEvent(Intelligence.LEVEL_1, ScenarioMap.TUTORIAL));
            }));
        }
        // tutorial button
        buttons.get(++i).addListener(new ExceptionLoggingChangeListener(() -> {
            initTutorial();
            if (nagPreferencesDao.getShowTutorialNag()) {
                nagPreferencesDao.setShowTutorialNag(false);
            }
        }));
        // preferences button
        buttons.get(++i).addListener(new ExceptionLoggingChangeListener(
            screenNavigationController::transitionToPreferencesScreen));
        // information button
        buttons.get(++i).addListener(new ExceptionLoggingChangeListener(
            screenNavigationController::transitionToInformationMenuScreenPage1));
    }

    private void initTutorial() {
        screenNavigationController.transitionToIngameScreen();
        eventBus.post(new InitializeScenarioEvent(Intelligence.LEVEL_1, ScenarioMap.TUTORIAL));
        eventBus.post(new GameStartEvent());
    }

    @Override
    public void show() {
        super.show();
        if (nagPreferencesDao.getShowChangelog()) {
            // show the dialog only once after the update
            nagPreferencesDao.setShowChangelog(false);
            showNewVersionDialog();
        }
    }

    private void showTutorialNag() {
        logger.debug("showing tutorial nag");
        final Dialog tutorialNagDialog = dialogFactory.createDialog(result -> {
            switch ((byte) result) {
                case 0:
                    // no
                    logger.debug("the user chose to play the tutorial");
                    initTutorial();
                    break;
                case 1:
                    // yes
                    logger.debug("the user dismissed the tutorial nag dialog");
                    screenNavigationController.transitionToPlayMenuScreen();
                    break;
                default:
                    break;
            }
        });
        tutorialNagDialog.text("Do you know how to play?\n");
        tutorialNagDialog.button("No", (byte) 0);
        tutorialNagDialog.button("Yes", (byte) 1);
        tutorialNagDialog.show(getActiveStage());
    }

    private void showNewVersionDialog() {
        logger.debug("informing the user about the version upgrade");
        final Dialog newVersionDialog = dialogFactory.createDialog(result -> {
            switch ((byte) result) {
                case 0:
                    // ok button
                    logger.debug("the user dismissed the version upgrade dialog");
                    break;
                case 1:
                    // open changelog button
                    logger.debug("the user openened the changelog");
                    screenNavigationController.transitionToChangelogScreen();
                    break;
                default:
                    break;
            }
        });
        newVersionDialog.text("The game was updated. See the changelog for details.\n");
        newVersionDialog.button("OK", (byte) 0);
        newVersionDialog.button("Open changelog", (byte) 1);
        newVersionDialog.show(getActiveStage());
    }

}

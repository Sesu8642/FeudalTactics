// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.editor.EditorInputHandler;
import de.sesu8642.feudaltactics.editor.EventHandler;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.ingame.IngameRendererEventHandler;
import de.sesu8642.feudaltactics.ingame.LocalIngameInputHandler;
import de.sesu8642.feudaltactics.ingame.ui.EditorScreen;
import de.sesu8642.feudaltactics.ingame.ui.IngameScreen;
import de.sesu8642.feudaltactics.ingame.ui.IngameScreenEventHandler;
import de.sesu8642.feudaltactics.lib.ingame.GameControllerEventHandler;
import de.sesu8642.feudaltactics.menu.about.dagger.AboutScreen;
import de.sesu8642.feudaltactics.menu.changelog.dagger.ChangelogScreen;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.crashreporting.ui.CrashReportScreen;
import de.sesu8642.feudaltactics.menu.information.ui.DependencyLicensesScreen;
import de.sesu8642.feudaltactics.menu.information.ui.InformationMenuPage1Screen;
import de.sesu8642.feudaltactics.menu.information.ui.InformationMenuPage2Screen;
import de.sesu8642.feudaltactics.menu.mainmenu.ui.MainMenuScreen;
import de.sesu8642.feudaltactics.menu.preferences.ui.PreferencesScreen;
import de.sesu8642.feudaltactics.menu.preferences.ui.PreferencesScreenEventHandler;
import de.sesu8642.feudaltactics.menu.splashscreen.ui.SplashScreen;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.stream.Stream;

/**
 * Controller for navigating between screens.
 */
@Singleton
public class ScreenNavigationController {

    private final EventBus eventBus;
    private final LocalIngameInputHandler localIngameInputHandler;
    private final EditorInputHandler editorInputHandler;
    private final SplashScreen splashScreen;
    private final IngameScreen ingameScreen;
    private final GameScreen mainMenuScreen;
    private final GameScreen aboutScreen;
    private final GameScreen preferencesScreen;
    private final GameScreen informationMenuScreen;
    private final GameScreen informationMenuScreen2;
    private final GameScreen dependencyLicensesScreen;
    private final GameScreen changelogScreen;
    private final GameScreen editorScreen;
    private final CrashReportScreen crashReportScreen;
    private final GameControllerEventHandler gameLogicEventHandler;
    private final EventHandler editorEventHandler;
    private final IngameRendererEventHandler rendererEventHandler;
    private final IngameScreenEventHandler ingameScreenEventHandler;
    private final PreferencesScreenEventHandler preferencesScreenEventHandler;

    /**
     * Constructor.
     */
    @Inject
    public ScreenNavigationController(EventBus eventBus, LocalIngameInputHandler localIngameInputHandler,
                                      EditorInputHandler editorInputHandler, SplashScreen splashScreen,
                                      IngameScreen ingameScreen,
                                      MainMenuScreen mainMenuScreen, @AboutScreen GameScreen aboutScreen,
                                      PreferencesScreen preferencesScreen,
                                      InformationMenuPage1Screen informationMenuScreen,
                                      InformationMenuPage2Screen informationMenuScreen2,
                                      DependencyLicensesScreen dependencyLicensesScreen,
                                      @ChangelogScreen GameScreen changelogScreen,
                                      EditorScreen editorScreen,
                                      CrashReportScreen crashReportScreen,
                                      GameControllerEventHandler gameLogicEventHandler,
                                      EventHandler editorEventHandler, IngameRendererEventHandler rendererEventHandler,
                                      IngameScreenEventHandler ingameScreenEventHandler,
                                      PreferencesScreenEventHandler preferencesScreenEventHandler) {
        this.eventBus = eventBus;
        this.localIngameInputHandler = localIngameInputHandler;
        this.editorInputHandler = editorInputHandler;
        this.splashScreen = splashScreen;
        this.ingameScreen = ingameScreen;
        this.mainMenuScreen = mainMenuScreen;
        this.aboutScreen = aboutScreen;
        this.preferencesScreen = preferencesScreen;
        this.informationMenuScreen = informationMenuScreen;
        this.informationMenuScreen2 = informationMenuScreen2;
        this.dependencyLicensesScreen = dependencyLicensesScreen;
        this.changelogScreen = changelogScreen;
        this.editorScreen = editorScreen;
        this.crashReportScreen = crashReportScreen;
        this.gameLogicEventHandler = gameLogicEventHandler;
        this.editorEventHandler = editorEventHandler;
        this.rendererEventHandler = rendererEventHandler;
        this.ingameScreenEventHandler = ingameScreenEventHandler;
        this.preferencesScreenEventHandler = preferencesScreenEventHandler;
    }

    private void unregisterAllEventHandlers() {
        Stream.of(localIngameInputHandler, gameLogicEventHandler, ingameScreenEventHandler, rendererEventHandler,
                preferencesScreenEventHandler).forEach(object -> {
            try {
                eventBus.unregister(object);
            } catch (IllegalArgumentException e) {
                // noop: expected; not all of the objects were registered in the first place
            }
        });
    }

    /**
     * Event handler for Screen transition events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleScreenTransitionTrigger(ScreenTransitionTriggerEvent event) {
        switch (event.getTransitionTarget()) {
            case SPLASH_SCREEN:
                changeScreen(splashScreen);
                break;
            case MAIN_MENU_SCREEN:
                changeScreen(mainMenuScreen);
                break;
            case INGAME_SCREEN:
                transitionToIngameScreen();
                break;
            case EDITOR_SCREEN:
                transitionToEditorScreen();
                break;
            case ABOUT_SCREEN:
                changeScreen(aboutScreen);
                break;
            case PREFERENCES_SCREEN:
                transitionToPreferencesScreen();
                break;
            case INFORMATION_MENU_SCREEN:
                changeScreen(informationMenuScreen);
                break;
            case INFORMATION_MENU_SCREEN_2:
                changeScreen(informationMenuScreen2);
                break;
            case DEPENDENCY_LICENSES_SCREEN:
                changeScreen(dependencyLicensesScreen);
                break;
            case CHANGELOG_SCREEN:
                changeScreen(changelogScreen);
                break;
            case CRASH_REPORT_SCREEN_IN_MAIN_MENU:
                transitionToCrashReportScreenInMainMenu();
                break;
            case CRASH_REPORT_SCREEN_ON_STARTUP:
                transitionToCrashReportScreenOnStartup();
                break;
            default:
                throw new AssertionError("Unimplemented transition target: " + event.getTransitionTarget());
        }
    }

    private void transitionToIngameScreen() {
        changeScreen(ingameScreen);
        Stream.of(localIngameInputHandler, gameLogicEventHandler, ingameScreenEventHandler, rendererEventHandler)
                .forEach(eventBus::register);
    }

    private void transitionToEditorScreen() {
        changeScreen(editorScreen);
        Stream.of(editorInputHandler, editorEventHandler, editorScreen, rendererEventHandler)
                .forEach(eventBus::register);
    }

    private void transitionToCrashReportScreenInMainMenu() {
        changeScreen(crashReportScreen);
        crashReportScreen.setGameStartup(false);
    }

    private void transitionToCrashReportScreenOnStartup() {
        changeScreen(crashReportScreen);
        crashReportScreen.setGameStartup(true);
    }

    private void changeScreen(Screen screen) {
        unregisterAllEventHandlers();
        // changing the screen needs to happen in the UI thread, otherwise there can be
        // some exception in native code
        Gdx.app.postRunnable(() -> FeudalTactics.getDaggerComponent().getGameInstance().setScreen(screen));
    }

    private void transitionToPreferencesScreen() {
        changeScreen(preferencesScreen);
        eventBus.register(preferencesScreenEventHandler);
    }

}

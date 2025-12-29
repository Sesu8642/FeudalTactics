// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.editor.EditorInputHandler;
import de.sesu8642.feudaltactics.editor.EventHandler;
import de.sesu8642.feudaltactics.ingame.IngameRendererEventHandler;
import de.sesu8642.feudaltactics.ingame.LocalIngameInputHandler;
import de.sesu8642.feudaltactics.ingame.ui.EditorScreen;
import de.sesu8642.feudaltactics.ingame.ui.IngameScreen;
import de.sesu8642.feudaltactics.ingame.ui.IngameScreenEventHandler;
import de.sesu8642.feudaltactics.lib.ingame.GameControllerEventHandler;
import de.sesu8642.feudaltactics.menu.about.dagger.AboutScreen;
import de.sesu8642.feudaltactics.menu.campaign.ui.CampaignLevelSelectionScreen;
import de.sesu8642.feudaltactics.menu.changelog.dagger.ChangelogScreen;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.crashreporting.ui.CrashReportScreen;
import de.sesu8642.feudaltactics.menu.information.ui.DependencyLicensesScreen;
import de.sesu8642.feudaltactics.menu.information.ui.InformationMenuPage1Screen;
import de.sesu8642.feudaltactics.menu.information.ui.InformationMenuPage2Screen;
import de.sesu8642.feudaltactics.menu.mainmenu.ui.MainMenuScreen;
import de.sesu8642.feudaltactics.menu.play.ui.PlayMenuScreen;
import de.sesu8642.feudaltactics.menu.preferences.ui.PreferencesScreen;
import de.sesu8642.feudaltactics.menu.preferences.ui.PreferencesScreenEventHandler;
import de.sesu8642.feudaltactics.menu.splashscreen.ui.SplashScreen;

import javax.inject.Inject;
import javax.inject.Provider;
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

    // using providers here because there's a cyclic dependency
    private final Provider<SplashScreen> splashScreenProvider;
    private final Provider<IngameScreen> ingameScreenProvider;
    private final Provider<MainMenuScreen> mainMenuScreenProvider;
    private final Provider<PlayMenuScreen> playMenuScreenProvider;
    private final Provider<GameScreen> aboutScreenProvider;
    private final Provider<PreferencesScreen> preferencesScreenProvider;
    private final Provider<InformationMenuPage1Screen> informationMenuScreenProvider;
    private final Provider<InformationMenuPage2Screen> informationMenuScreen2Provider;
    private final Provider<DependencyLicensesScreen> dependencyLicensesScreenProvider;
    private final Provider<GameScreen> changelogScreenProvider;
    private final Provider<EditorScreen> editorScreenProvider;
    private final Provider<CrashReportScreen> crashReportScreenProvider;
    private final Provider<CampaignLevelSelectionScreen> campaignLevelSelectionScreenProvider;

    private final GameControllerEventHandler gameLogicEventHandler;
    private final EventHandler editorEventHandler;
    private final IngameRendererEventHandler rendererEventHandler;
    private final Provider<IngameScreenEventHandler> ingameScreenEventHandlerProvider;
    private final Provider<PreferencesScreenEventHandler> preferencesScreenEventHandlerProvider;

    /**
     * Constructor.
     */
    @Inject
    public ScreenNavigationController(
        EventBus eventBus,
        LocalIngameInputHandler localIngameInputHandler,
        EditorInputHandler editorInputHandler,
        Provider<SplashScreen> splashScreenProvider,
        Provider<IngameScreen> ingameScreenProvider,
        Provider<MainMenuScreen> mainMenuScreenProvider,
        Provider<PlayMenuScreen> playMenuScreenProvider,
        @AboutScreen Provider<GameScreen> aboutScreenProvider,
        Provider<PreferencesScreen> preferencesScreenProvider,
        Provider<InformationMenuPage1Screen> informationMenuScreenProvider,
        Provider<InformationMenuPage2Screen> informationMenuScreen2Provider,
        Provider<DependencyLicensesScreen> dependencyLicensesScreenProvider,
        @ChangelogScreen Provider<GameScreen> changelogScreenProvider,
        Provider<EditorScreen> editorScreenProvider,
        Provider<CrashReportScreen> crashReportScreenProvider,
        Provider<CampaignLevelSelectionScreen> campaignLevelSelectionScreenProvider,
        GameControllerEventHandler gameLogicEventHandler,
        EventHandler editorEventHandler,
        IngameRendererEventHandler rendererEventHandler,
        Provider<IngameScreenEventHandler> ingameScreenEventHandlerProvider,
        Provider<PreferencesScreenEventHandler> preferencesScreenEventHandlerProvider) {

        this.eventBus = eventBus;
        this.localIngameInputHandler = localIngameInputHandler;
        this.editorInputHandler = editorInputHandler;
        this.splashScreenProvider = splashScreenProvider;
        this.ingameScreenProvider = ingameScreenProvider;
        this.mainMenuScreenProvider = mainMenuScreenProvider;
        this.playMenuScreenProvider = playMenuScreenProvider;
        this.aboutScreenProvider = aboutScreenProvider;
        this.preferencesScreenProvider = preferencesScreenProvider;
        this.informationMenuScreenProvider = informationMenuScreenProvider;
        this.informationMenuScreen2Provider = informationMenuScreen2Provider;
        this.dependencyLicensesScreenProvider = dependencyLicensesScreenProvider;
        this.changelogScreenProvider = changelogScreenProvider;
        this.editorScreenProvider = editorScreenProvider;
        this.crashReportScreenProvider = crashReportScreenProvider;
        this.campaignLevelSelectionScreenProvider = campaignLevelSelectionScreenProvider;
        this.gameLogicEventHandler = gameLogicEventHandler;
        this.editorEventHandler = editorEventHandler;
        this.rendererEventHandler = rendererEventHandler;
        this.ingameScreenEventHandlerProvider = ingameScreenEventHandlerProvider;
        this.preferencesScreenEventHandlerProvider = preferencesScreenEventHandlerProvider;
    }

    /**
     * Transitions to the changelog screen.
     */
    public void transitionToChangelogScreen() {
        changeScreen(changelogScreenProvider.get());
    }

    /**
     * Transitions to the dependency licenses screen.
     */
    public void transitionToDependencyLicensesScreen() {
        changeScreen(dependencyLicensesScreenProvider.get());
    }

    /**
     * Transitions to the information menu screen.
     */
    public void transitionToInformationMenuScreenPage2() {
        changeScreen(informationMenuScreen2Provider.get());
    }

    /**
     * Transitions to the information menu screen.
     */
    public void transitionToInformationMenuScreenPage1() {
        changeScreen(informationMenuScreenProvider.get());
    }

    /**
     * Transitions to the about screen.
     */
    public void transitionToAboutScreen() {
        changeScreen(aboutScreenProvider.get());
    }

    /**
     * Transitions to the play manu screen.
     */
    public void transitionToPlayMenuScreen() {
        changeScreen(playMenuScreenProvider.get());
    }

    /**
     * Transitions to the main menu screen.
     */
    public void transitionToMainMenuScreen() {
        changeScreen(mainMenuScreenProvider.get());
    }

    /**
     * Transitions to the splash screen.
     */
    public void transitionToSplashScreen() {
        changeScreen(splashScreenProvider.get());
    }

    /**
     * Transitions to the ingame screen.
     */
    public void transitionToIngameScreen() {
        changeScreen(ingameScreenProvider.get());
        Stream.of(localIngameInputHandler, gameLogicEventHandler,
                ingameScreenEventHandlerProvider.get(), rendererEventHandler)
            .forEach(eventBus::register);
    }

    /**
     * Transitions to the editor screen.
     */
    public void transitionToEditorScreen() {
        changeScreen(editorScreenProvider.get());
        Stream.of(editorInputHandler, editorEventHandler,
                editorScreenProvider.get(), rendererEventHandler)
            .forEach(eventBus::register);
    }

    /**
     * Transitions to the crash report screen after the user explicitly opening it.
     */
    public void transitionToCrashReportScreenInMainMenu() {
        final CrashReportScreen screen = crashReportScreenProvider.get();
        changeScreen(screen);
        screen.setGameStartup(false);
    }

    /**
     * Transitions to the crash report screen on game startup.
     */
    public void transitionToCrashReportScreenOnStartup() {
        final CrashReportScreen screen = crashReportScreenProvider.get();
        changeScreen(screen);
        screen.setGameStartup(true);
    }

    /**
     * Transitions to the preferences screen.
     */
    public void transitionToPreferencesScreen() {
        changeScreen(preferencesScreenProvider.get());
        eventBus.register(preferencesScreenEventHandlerProvider.get());
    }

    /**
     * Transitions to the campaign level selection screen.
     */
    public void transitionToCampaignLevelSelectionScreen() {
        changeScreen(campaignLevelSelectionScreenProvider.get());
    }

    private void changeScreen(Screen screen) {
        unregisterAllEventHandlers();
        // changing the screen needs to happen in the UI thread, otherwise there can be
        // some exception in native code
        Gdx.app.postRunnable(() ->
            FeudalTactics.getDaggerComponent()
                .getGameInstance()
                .setScreen(screen));
    }

    private void unregisterAllEventHandlers() {
        Stream.of(localIngameInputHandler, gameLogicEventHandler, ingameScreenEventHandlerProvider.get(),
            rendererEventHandler, preferencesScreenEventHandlerProvider.get()).forEach(object -> {
            try {
                eventBus.unregister(object);
            } catch (IllegalArgumentException e) {
                // noop: expected; not all of the objects were registered in the first place
            }
        });
    }

}

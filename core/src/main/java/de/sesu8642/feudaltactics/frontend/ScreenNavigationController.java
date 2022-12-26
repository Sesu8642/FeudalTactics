// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend;

import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Screen;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.sesu8642.feudaltactics.FeudalTactics;
import de.sesu8642.feudaltactics.backend.editor.EditorInputHandler;
import de.sesu8642.feudaltactics.backend.ingame.LocalIngameInputHandler;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.AboutScreen;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.ChangelogScreen;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.DependencyLicensesScreen;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.InformationMenuScreen;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.TutorialScreen;
import de.sesu8642.feudaltactics.frontend.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.frontend.ui.screens.GameScreen;
import de.sesu8642.feudaltactics.frontend.ui.screens.IngameScreen;
import de.sesu8642.feudaltactics.frontend.ui.screens.IngameScreenEventHandler;
import de.sesu8642.feudaltactics.frontend.ui.screens.PreferencesScreen;
import de.sesu8642.feudaltactics.frontend.ui.screens.PreferencesScreenEventHandler;
import de.sesu8642.feudaltactics.frontend.ui.screens.SplashScreen;

/**
 * Controller for navigating between screens.
 */
@Singleton
public class ScreenNavigationController {

	private EventBus eventBus;
	private LocalIngameInputHandler localIngameInputHandler;
	private EditorInputHandler editorInputHandler;
	private SplashScreen splashScreen;
	private IngameScreen ingameScreen;
	private GameScreen mainMenuScreen;
	private GameScreen tutorialScreen;
	private GameScreen aboutScreen;
	private GameScreen preferencesScreen;
	private GameScreen informationMenuScreen;
	private GameScreen dependencyLicensesScreen;
	private GameScreen changelogScreen;
	private de.sesu8642.feudaltactics.backend.ingame.EventHandler gameLogicEventHandler;
	private de.sesu8642.feudaltactics.backend.editor.EventHandler editorEventHandler;
	private de.sesu8642.feudaltactics.frontend.renderer.EventHandler rendererEventHandler;
	private IngameScreenEventHandler ingameScreenEventHandler;
	private PreferencesScreenEventHandler preferencesScreenEventHandler;

	/**
	 * Constructor.
	 * 
	 * @param eventBus                      event bus to register and unregister
	 *                                      to/with
	 * @param localIngameInputHandler       local ingame input handler
	 * @param gameLogicEventHandler         game logic event handler
	 * @param splashScreen                  splash screen
	 * @param ingameScreen                  ingame screen
	 * @param mainMenuScreen                main menu screen
	 * @param rendererEventHandler          renderer event handler
	 * @param ingameScreenEventHandler      ingame screen event handler
	 * @param preferencesScreenEventHandler preferences screen event handler
	 */
	@Inject
	public ScreenNavigationController(EventBus eventBus, LocalIngameInputHandler localIngameInputHandler,
			EditorInputHandler editorInputHandler, SplashScreen splashScreen, IngameScreen ingameScreen,
			de.sesu8642.feudaltactics.frontend.ui.screens.MainMenuScreen mainMenuScreen,
			@TutorialScreen GameScreen tutorialScreen, @AboutScreen GameScreen aboutScreen,
			PreferencesScreen preferencesScreen, @InformationMenuScreen GameScreen informationMenuScreen,
			@DependencyLicensesScreen GameScreen dependencyLicensesScreen, @ChangelogScreen GameScreen changelogScreen,
			de.sesu8642.feudaltactics.backend.ingame.EventHandler gameLogicEventHandler,
			de.sesu8642.feudaltactics.backend.editor.EventHandler editorEventHandler,
			de.sesu8642.feudaltactics.frontend.renderer.EventHandler rendererEventHandler,
			IngameScreenEventHandler ingameScreenEventHandler,
			PreferencesScreenEventHandler preferencesScreenEventHandler) {
		this.eventBus = eventBus;
		this.localIngameInputHandler = localIngameInputHandler;
		this.editorInputHandler = editorInputHandler;
		this.splashScreen = splashScreen;
		this.ingameScreen = ingameScreen;
		this.mainMenuScreen = mainMenuScreen;
		this.tutorialScreen = tutorialScreen;
		this.aboutScreen = aboutScreen;
		this.preferencesScreen = preferencesScreen;
		this.informationMenuScreen = informationMenuScreen;
		this.dependencyLicensesScreen = dependencyLicensesScreen;
		this.changelogScreen = changelogScreen;
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
		case TUTORIAL_SCREEN:
			changeScreen(tutorialScreen);
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
		case DEPENDENCY_LICENSES_SCREEN:
			changeScreen(dependencyLicensesScreen);
			break;
		case CHANGELOG_SCREEN:
			changeScreen(changelogScreen);
			break;
		default:
			throw new AssertionError("Unimplemented transition target: " + event.getTransitionTarget());
		}
	}

	private void transitionToIngameScreen() {
		changeScreen(ingameScreen);
		Stream.of(localIngameInputHandler, gameLogicEventHandler, ingameScreenEventHandler, rendererEventHandler)
				.forEach(object -> eventBus.register(object));
	}

	private void transitionToEditorScreen() {
		changeScreen(ingameScreen);
		Stream.of(editorInputHandler, editorEventHandler, ingameScreen, rendererEventHandler)
				.forEach(object -> eventBus.register(object));
	}

	private void changeScreen(Screen screen) {
		unregisterAllEventHandlers();
		FeudalTactics.game.setScreen(screen);
	}

	private void transitionToPreferencesScreen() {
		changeScreen(preferencesScreen);
		eventBus.register(preferencesScreenEventHandler);
	}

}

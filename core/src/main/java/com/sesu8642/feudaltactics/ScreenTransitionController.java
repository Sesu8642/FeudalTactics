package com.sesu8642.feudaltactics;

import java.util.stream.Stream;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.AboutScreen;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.MainMenuScreen;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.TutorialScreen;
import com.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import com.sesu8642.feudaltactics.events.moves.RegenerateMapUiEvent;
import com.sesu8642.feudaltactics.gamelogic.MapParameters;
import com.sesu8642.feudaltactics.gamelogic.editor.EditorInputHandler;
import com.sesu8642.feudaltactics.gamelogic.ingame.LocalIngameInputHandler;
import com.sesu8642.feudaltactics.preferences.NewGamePreferences;
import com.sesu8642.feudaltactics.preferences.PreferencesHelper;
import com.sesu8642.feudaltactics.ui.screens.GameScreen;
import com.sesu8642.feudaltactics.ui.screens.IngameScreen;
import com.sesu8642.feudaltactics.ui.screens.SplashScreen;

/**
 * Controller switching between screens bus.
 */
@Singleton
public class ScreenTransitionController {

	private EventBus eventBus;
	private LocalIngameInputHandler localIngameInputHandler;
	private EditorInputHandler editorInputHandler;
	private SplashScreen splashScreen;
	private IngameScreen ingameScreen;
	private GameScreen mainMenuScreen;
	private GameScreen tutorialScreen;
	private GameScreen aboutScreen;
	private com.sesu8642.feudaltactics.gamelogic.ingame.EventHandler gameLogicEventHandler;
	private com.sesu8642.feudaltactics.gamelogic.editor.EventHandler editorEventHandler;
	private com.sesu8642.feudaltactics.renderer.EventHandler rendererEventHandler;
	private com.sesu8642.feudaltactics.preferences.EventHandler preferencesEventHandler;

	/**
	 * Constructor.
	 * 
	 * @param eventBus                event bus to register and unregister to/with
	 * @param localIngameInputHandler local ingame input handler
	 * @param gameLogicEventHandler   game logic event handler
	 * @param splashScreen            splash screen
	 * @param ingameScreen            ingame screen
	 * @param mainMenuScreen          main menu screen
	 * @param rendererEventHandler    renderer event handler
	 * @param preferencesEventHandler preferences event handler
	 */
	@Inject
	public ScreenTransitionController(EventBus eventBus, LocalIngameInputHandler localIngameInputHandler,
			EditorInputHandler editorInputHandler, SplashScreen splashScreen, IngameScreen ingameScreen,
			@MainMenuScreen GameScreen mainMenuScreen, @TutorialScreen GameScreen tutorialScreen,
			@AboutScreen GameScreen aboutScreen,
			com.sesu8642.feudaltactics.gamelogic.ingame.EventHandler gameLogicEventHandler,
			com.sesu8642.feudaltactics.gamelogic.editor.EventHandler editorEventHandler,
			com.sesu8642.feudaltactics.renderer.EventHandler rendererEventHandler,
			com.sesu8642.feudaltactics.preferences.EventHandler preferencesEventHandler) {
		this.eventBus = eventBus;
		this.localIngameInputHandler = localIngameInputHandler;
		this.editorInputHandler = editorInputHandler;
		this.splashScreen = splashScreen;
		this.ingameScreen = ingameScreen;
		this.mainMenuScreen = mainMenuScreen;
		this.tutorialScreen = tutorialScreen;
		this.aboutScreen = aboutScreen;
		this.gameLogicEventHandler = gameLogicEventHandler;
		this.editorEventHandler = editorEventHandler;
		this.rendererEventHandler = rendererEventHandler;
		this.preferencesEventHandler = preferencesEventHandler;
	}

	private void unregisterAllEventHandlers() {
		Stream.of(localIngameInputHandler, gameLogicEventHandler, ingameScreen, rendererEventHandler,
				preferencesEventHandler).forEach(object -> {
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
			transitionToSplashScreen();
			break;
		case MAIN_MENU_SCREEN:
			transitionToMainMenuScreen();
			break;
		case INGAME_SCREEN:
			transitionToIngameScreen();
			break;
		case EDITOR_SCREEN:
			transitionToEditorScreen();
			break;
		case TUTORIAL_SCREEN:
			transitionToTutorialScreen();
			break;
		case ABOUT_SCREEN:
			transitionToAboutScreen();
			break;
		default:
			throw new AssertionError("Unimplemented transition target: " + event.getTransitionTarget());
		}
	}

	/** Transitions to the splash screen. */
	public void transitionToSplashScreen() {
		unregisterAllEventHandlers();
		// nothing to register currently
		FeudalTactics.game.setScreen(splashScreen);
	}

	/** Transitions to the main menu screen. */
	public void transitionToMainMenuScreen() {
		unregisterAllEventHandlers();
		// nothing to register currently
		FeudalTactics.game.setScreen(mainMenuScreen);
	}

	/** Transitions to the ingame screen. */
	public void transitionToIngameScreen() {
		unregisterAllEventHandlers();
		Stream.of(localIngameInputHandler, gameLogicEventHandler, ingameScreen, rendererEventHandler,
				preferencesEventHandler).forEach(object -> eventBus.register(object));
		NewGamePreferences savedPrefs = PreferencesHelper.getNewGamePreferences();
		eventBus.post(new RegenerateMapUiEvent(savedPrefs.getBotIntelligence(),
				new MapParameters(System.currentTimeMillis(), savedPrefs.getMapSize(), savedPrefs.getDensity())));
		FeudalTactics.game.setScreen(ingameScreen);
	}

	/** Transitions to the editor screen. */
	public void transitionToEditorScreen() {
		unregisterAllEventHandlers();
		Stream.of(editorInputHandler, editorEventHandler, ingameScreen, rendererEventHandler)
				.forEach(object -> eventBus.register(object));
		eventBus.post(new RegenerateMapUiEvent(null, null));
		FeudalTactics.game.setScreen(ingameScreen);
	}

	public void transitionToTutorialScreen() {
		unregisterAllEventHandlers();
		FeudalTactics.game.setScreen(tutorialScreen);
	}

	public void transitionToAboutScreen() {
		unregisterAllEventHandlers();
		FeudalTactics.game.setScreen(aboutScreen);
	}

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.mainmenu.ui;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.ingame.MapParameters;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.NewGamePreferencesDao;
import de.sesu8642.feudaltactics.menu.changelog.GameVersionDao;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.DialogFactory;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.common.ui.MenuStage;

/** {@link Screen} for displaying the main menu. */
@Singleton
public class MainMenuScreen extends GameScreen {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private GameVersionDao gameVersionDao;
	private NewGamePreferencesDao newGamePreferencesDao;
	private DialogFactory dialogFactory;
	private EventBus eventBus;

	/**
	 * Constructor.
	 * 
	 * @param gameVersionDao dao for interacting with version persistence
	 * @param camera         camera for the menus
	 * @param viewport       viewport for the menus
	 * @param mainMenuStage  stage for the main menu
	 * @param dialogFactory  factory for creating dialogs
	 * @param eventBus       event bus
	 */
	@Inject
	public MainMenuScreen(GameVersionDao gameVersionDao, NewGamePreferencesDao newGamePreferencesDao,
			@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport, MainMenuStage mainMenuStage,
			DialogFactory dialogFactory, EventBus eventBus) {
		super(camera, viewport, mainMenuStage);
		this.gameVersionDao = gameVersionDao;
		this.newGamePreferencesDao = newGamePreferencesDao;
		this.dialogFactory = dialogFactory;
		this.eventBus = eventBus;
		initUi(mainMenuStage);
	}

	private void initUi(MenuStage stage) {
		List<TextButton> buttons = stage.getButtons();
		// play button
		buttons.get(0).addListener(new ExceptionLoggingChangeListener(() -> {
			NewGamePreferences savedPrefs = newGamePreferencesDao.getNewGamePreferences();
			eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INGAME_SCREEN));
			eventBus.post(new RegenerateMapEvent(savedPrefs.getBotIntelligence(),
					new MapParameters(System.currentTimeMillis(), savedPrefs.getMapSize().getAmountOfTiles(),
							savedPrefs.getDensity().getDensityFloat(), savedPrefs.getUserColor().getKingdomColor())));
		}));
		// tutorial button
		buttons.get(1).addListener(new ExceptionLoggingChangeListener(
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.TUTORIAL_SCREEN))));
		// preferences button
		buttons.get(2).addListener(new ExceptionLoggingChangeListener(
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.PREFERENCES_SCREEN))));
		// information button
		buttons.get(3).addListener(new ExceptionLoggingChangeListener(
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INFORMATION_MENU_SCREEN))));
	}

	@Override
	public void show() {
		super.show();
		if (gameVersionDao.getChangelogState()) {
			// show the dialog only once after the update
			gameVersionDao.saveChangelogState(false);
			showNewVersionDialog();
		}
	}

	private void showNewVersionDialog() {
		logger.debug("informing the user about the version upgrade");
		Dialog newVersionDialog = dialogFactory.createDialog(result -> {
			switch ((byte) result) {
			case 0:
				// ok button
				logger.debug("the user dismissed the version upgrade dialog");
				break;
			case 1:
				// open changelog button
				logger.debug("the user openened the changelog");
				eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.CHANGELOG_SCREEN));
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

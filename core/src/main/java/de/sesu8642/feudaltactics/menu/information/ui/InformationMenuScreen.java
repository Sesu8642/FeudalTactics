// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.information.ui;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;

@Singleton
public class InformationMenuScreen extends GameScreen {

	@Inject
	public InformationMenuScreen(EventBus eventBus, @MenuCamera OrthographicCamera camera,
			@MenuViewport Viewport viewport, InformationMenuStage menuStage) {
		super(camera, viewport, menuStage);
		List<TextButton> buttons = menuStage.getButtons();
		buttons.get(0).addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.ABOUT_SCREEN));
			}
		});
		buttons.get(1).addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.CHANGELOG_SCREEN));
			}
		});
		buttons.get(2).addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.DEPENDENCY_LICENSES_SCREEN));
			}
		});
		buttons.get(3).addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				Gdx.net.openURI("https://raw.githubusercontent.com/Sesu8642/FeudalTactics/master/privacy_policy.txt");
			}
		});
		buttons.get(4).addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				eventBus.post(
						new ScreenTransitionTriggerEvent(ScreenTransitionTarget.CRASH_REPORT_SCREEN_IN_MAIN_MENU));
			}
		});
		buttons.get(5).addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.MAIN_MENU_SCREEN));
			}
		});

	}

}

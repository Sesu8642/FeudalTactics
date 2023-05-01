// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.information.dagger;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;
import com.google.common.io.Resources;

import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.dagger.VersionProperty;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.exceptions.InitializationException;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuBackgroundRenderer;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.common.ui.MenuStage;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

/** Dagger module for the information sub-menu and its items. */
@Module
public class InformationMenuDaggerModule {

	private InformationMenuDaggerModule() {
		// prevent instantiation
		throw new AssertionError();
	}

	@Provides
	@Singleton
	@DependencyLicenses
	static String provideDependencyLicensesText() {
		try {
			URL url = Resources.getResource("licenses.txt");
			return Resources.toString(url, StandardCharsets.UTF_8);
		} catch (IOException e) {
			throw new InitializationException("Dependency licenses cannot be read!", e);
		}
	}

	@Provides
	@Singleton
	@InformationMenuStage
	static MenuStage provideInformationMenuStage(EventBus eventBus, @MenuViewport Viewport viewport,
			@MenuBackgroundCamera OrthographicCamera camera, @MenuBackgroundRenderer MapRenderer mapRenderer, Skin skin,
			@VersionProperty String gameVersion) {
		MenuStage stage = new MenuStage(viewport, camera, mapRenderer, skin);
		stage.addButton("About",
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.ABOUT_SCREEN)));
		stage.addButton("Changelog",
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.CHANGELOG_SCREEN)));
		stage.addButton("Dependency Licenses", () -> eventBus
				.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.DEPENDENCY_LICENSES_SCREEN)));
		stage.addButton("Privacy Policy", () -> Gdx.net
				.openURI("https://raw.githubusercontent.com/Sesu8642/FeudalTactics/master/privacy_policy.txt"));
		stage.addButton("Report Crash", () -> eventBus
				.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.CRASH_REPORT_SCREEN_IN_MAIN_MENU)));
		stage.addButton("Back",
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.MAIN_MENU_SCREEN)));
		stage.setBottomRightLabelText(String.format("Version %s", gameVersion));
		return stage;
	}

	@Provides
	@Singleton
	@DependencyLicensesStage
	static SlideStage provideDependencyLicensesStage(EventBus eventBus, @MenuViewport Viewport viewport,
			@DependencyLicenses String dependencyLicensesText, @MenuBackgroundCamera OrthographicCamera camera,
			Skin skin) {
		Slide licenseSlide = new Slide(skin, "Dependency Licenses").addLabel(dependencyLicensesText);
		return new SlideStage(viewport, Collections.singletonList(licenseSlide),
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INFORMATION_MENU_SCREEN)),
				camera, skin);
	}

	@Provides
	@Singleton
	@DependencyLicensesScreen
	static GameScreen provideDependencyLicensesScreen(@MenuCamera OrthographicCamera camera,
			@MenuViewport Viewport viewport, @DependencyLicensesStage SlideStage dependencyLicensesStage) {
		return new GameScreen(camera, viewport, dependencyLicensesStage);
	}

	@Provides
	@Singleton
	@InformationMenuScreen
	static GameScreen provideInformationMenuScreen(@MenuCamera OrthographicCamera camera,
			@MenuViewport Viewport viewport, @InformationMenuStage MenuStage menuStage) {
		return new GameScreen(camera, viewport, menuStage);
	}

}

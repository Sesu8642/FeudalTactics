// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.dagger;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import com.google.common.eventbus.EventBus;

import dagger.Component;
import de.sesu8642.feudaltactics.ScreenTransitionController;
import de.sesu8642.feudaltactics.dagger.qualifierannotations.VersionProperty;
import de.sesu8642.feudaltactics.ui.screens.IngameScreen;
import de.sesu8642.feudaltactics.ui.screens.SplashScreen;

/** Dagger component. **/
@Component(modules = { DaggerModule.class })
@Singleton
public interface FeudalTacticsComponent {

	IngameScreen getIngameScreen();

	SplashScreen getSplashScreen();

	EventBus getEventBus();

	ScreenTransitionController getScreenTransitionController();

	@VersionProperty
	String getGameVersion();

	ExecutorService getBotAiExecutor();

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import java.util.concurrent.ExecutorService;

import javax.inject.Singleton;

import com.google.common.eventbus.EventBus;

import dagger.Component;
import de.sesu8642.feudaltactics.backend.dagger.BackendDaggerModule;
import de.sesu8642.feudaltactics.frontend.ScreenTransitionController;
import de.sesu8642.feudaltactics.frontend.dagger.FrontendDaggerModule;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.VersionProperty;
import de.sesu8642.feudaltactics.frontend.ui.screens.IngameScreen;
import de.sesu8642.feudaltactics.frontend.ui.screens.SplashScreen;

/** Dagger component. **/
@Component(modules = { BackendDaggerModule.class, FrontendDaggerModule.class })
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

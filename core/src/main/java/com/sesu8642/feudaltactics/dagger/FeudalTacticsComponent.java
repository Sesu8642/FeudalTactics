// SPDX-License-Identifier: GPL-3.0-or-later

package com.sesu8642.feudaltactics.dagger;

import javax.inject.Singleton;

import com.google.common.eventbus.EventBus;
import com.sesu8642.feudaltactics.ScreenTransitionController;
import com.sesu8642.feudaltactics.dagger.qualifierannotations.VersionProperty;
import com.sesu8642.feudaltactics.ui.screens.IngameScreen;
import com.sesu8642.feudaltactics.ui.screens.SplashScreen;

import dagger.Component;

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

}

package com.sesu8642.feudaltactics.dagger;

import javax.inject.Singleton;

import com.sesu8642.feudaltactics.ui.screens.IngameScreen;
import com.sesu8642.feudaltactics.ui.screens.SplashScreen;

import dagger.Component;

@Component(modules = DaggerModule.class)
@Singleton
public interface FeudalTacticsComponent {
	
	IngameScreen getIngameScreen();
	
	SplashScreen getSplashScreen();
}

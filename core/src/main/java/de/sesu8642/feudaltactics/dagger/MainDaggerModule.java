// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.dagger;

import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionHandler;
import com.ray3k.stripe.FreeTypeSkin;

import dagger.Module;
import dagger.Provides;

/** Dagger module for the application. */
@Module
public class MainDaggerModule {

	private MainDaggerModule() {
		// prevent instantiation
		throw new AssertionError();
	}

	@Provides
	@Singleton
	static EventBus provideEventBus() {
		return new EventBus((exception, context) -> {
			Logger logger = LoggerFactory.getLogger(SubscriberExceptionHandler.class.getName());
			logger.error(String.format(
					"an unexpected error happened while handling the event %s in method %s of subscriber %s",
					context.getEvent(), context.getSubscriberMethod(), context.getSubscriber()), exception);
		});
	}

	@Provides
	@Singleton
	static Skin provideSkin() {
		return new FreeTypeSkin(Gdx.files.internal("skin/pixthulhu-ui.json"));
	}

	@Provides
	static InputMultiplexer provideInputMultiplexer() {
		return new InputMultiplexer();
	}

}
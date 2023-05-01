// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.dagger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.exceptions.InitializationException;

/** Dagger module for configuration properties. */
@Module
public class ConfigDaggerModule {

	private ConfigDaggerModule() {
		// prevent instantiation
		throw new AssertionError();
	}

	@Provides
	@Singleton
	static Properties provideGameConfig() {
		Properties config = new Properties();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		try (InputStream inputStream = classLoader.getResourceAsStream("gameconfig.properties")) {
			config.load(inputStream);
		} catch (IOException e) {
			// modules can only throw unchecked exceptions so this needs to be converted
			throw new InitializationException("Config cannot be read!", e);
		}
		return config;
	}

	@Provides
	@Singleton
	@VersionProperty
	static String provideVersionProperty(Properties config) {
		return config.getProperty("version");
	}

	@Provides
	@Singleton
	@PreferencesPrefixProperty
	static String providePreferencesPrefixProperty(Properties config) {
		return config.getProperty("preferences_prefix");
	}

	@Provides
	@Singleton
	@EnableDeepWaterRenderingProperty
	static Boolean provideEnableDeepWaterRenderingProperty(Properties config) {
		return Boolean.parseBoolean(config.getProperty("enable_deep_water_rendering"));
	}

}

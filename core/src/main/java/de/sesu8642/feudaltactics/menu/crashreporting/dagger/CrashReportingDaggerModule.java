// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.crashreporting.dagger;

import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Singleton;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;

import dagger.Module;
import dagger.Provides;
import de.sesu8642.feudaltactics.dagger.PreferencesPrefixProperty;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.crashreporting.CrashReportDao;
import de.sesu8642.feudaltactics.menu.crashreporting.ui.CrashReportScreen;
import de.sesu8642.feudaltactics.menu.crashreporting.ui.CrashReportStageForMenu;
import de.sesu8642.feudaltactics.menu.crashreporting.ui.CrashReportStageForStartup;

/** Dagger module for crash reporting. */
@Module
public class CrashReportingDaggerModule {

	private CrashReportingDaggerModule() {
		// prevent instantiation
		throw new AssertionError();
	}

	@Provides
	@Singleton
	@CrashReportScreenInMainMenu
	static CrashReportScreen provideCrashReportScreenInMainMenu(@MenuCamera OrthographicCamera camera,
			@MenuViewport Viewport viewport, CrashReportStageForMenu crashReportStage, CrashReportDao crashReportDao,
			ScheduledExecutorService copyButtonFeedbackExecutorService) {
		return new CrashReportScreen(camera, viewport, crashReportStage, crashReportDao,
				copyButtonFeedbackExecutorService);
	}

	/**
	 * The difference between this instance and the one accessible in the menu is
	 * that this one transitions to the splash screen afterwards while the other one
	 * transitions to the menu.
	 */
	@Provides
	@Singleton
	@CrashReportScreenOnStartup
	static CrashReportScreen provideCrashReportScreenOnStartup(@MenuCamera OrthographicCamera camera,
			@MenuViewport Viewport viewport, CrashReportStageForStartup crashReportStage, CrashReportDao crashReportDao,
			ScheduledExecutorService copyButtonFeedbackExecutorService) {
		return new CrashReportScreen(camera, viewport, crashReportStage, crashReportDao,
				copyButtonFeedbackExecutorService);
	}

	@Provides
	@Singleton
	@CrashReportPrefStore
	static Preferences provideCrashReportPrefStore(@PreferencesPrefixProperty String prefix) {
		return Gdx.app.getPreferences(prefix + CrashReportDao.CRASH_REPORT_PREFERENCES_NAME);
	}

}

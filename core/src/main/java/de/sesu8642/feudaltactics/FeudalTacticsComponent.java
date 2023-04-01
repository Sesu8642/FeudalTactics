// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;

import javax.inject.Singleton;

import dagger.Component;
import de.sesu8642.feudaltactics.backend.dagger.BackendDaggerModule;
import de.sesu8642.feudaltactics.frontend.crashreporting.GameCrasher;
import de.sesu8642.feudaltactics.frontend.dagger.FrontendDaggerModule;

/** Dagger component. **/
@Component(modules = { BackendDaggerModule.class, FrontendDaggerModule.class })
@Singleton
public interface FeudalTacticsComponent {

	GameInitializer getGameInitializer();

	ExecutorService getBotAiExecutor();

	ScheduledExecutorService getCopyButtonExecutor();

	GameCrasher getGameCrasher();

}

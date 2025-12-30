// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.dagger;

import dagger.BindsInstance;
import dagger.Component;
import de.sesu8642.feudaltactics.FeudalTactics;
import de.sesu8642.feudaltactics.GameInitializer;
import de.sesu8642.feudaltactics.editor.dagger.EditorDaggerModule;
import de.sesu8642.feudaltactics.ingame.dagger.IngameDaggerModule;
import de.sesu8642.feudaltactics.menu.about.dagger.AboutDaggerModule;
import de.sesu8642.feudaltactics.menu.changelog.dagger.ChangelogDaggerModule;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuDaggerModule;
import de.sesu8642.feudaltactics.menu.crashreporting.GameCrasher;
import de.sesu8642.feudaltactics.menu.crashreporting.dagger.CrashReportingDaggerModule;
import de.sesu8642.feudaltactics.menu.information.dagger.InformationMenuDaggerModule;
import de.sesu8642.feudaltactics.menu.preferences.dagger.PrefsDaggerModule;
import de.sesu8642.feudaltactics.menu.statistics.dagger.StatisticsDaggerModule;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
import de.sesu8642.feudaltactics.platformspecific.PlatformSharing;
import de.sesu8642.feudaltactics.renderer.dagger.RendererDaggerModule;

import javax.inject.Singleton;

/**
 * Dagger component.
 **/
@Component(modules = {MainDaggerModule.class, ConfigDaggerModule.class, CrashReportingDaggerModule.class,
    EditorDaggerModule.class, IngameDaggerModule.class, MenuDaggerModule.class, AboutDaggerModule.class,
    ChangelogDaggerModule.class, InformationMenuDaggerModule.class, PrefsDaggerModule.class,
    StatisticsDaggerModule.class, RendererDaggerModule.class})
@Singleton
public interface FeudalTacticsComponent {

    GameInitializer getGameInitializer();

    GameCrasher getGameCrasher();

    FeudalTactics getGameInstance();

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder platformSharing(PlatformSharing platformSharing);

        @BindsInstance
        Builder platformInsetProvider(PlatformInsetsProvider platformInsetsProvider);

        @BindsInstance
        Builder gameInstance(FeudalTactics gameInstance);

        FeudalTacticsComponent build();
    }

}

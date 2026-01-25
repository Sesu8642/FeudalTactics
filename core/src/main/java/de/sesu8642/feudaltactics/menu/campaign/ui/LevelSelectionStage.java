// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.campaign.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.SlideStage;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collections;

/**
 * {@link Stage} that displays a selection of levels to play.
 */
@Singleton
public class LevelSelectionStage extends SlideStage {

    final LevelSelectionSlide levelSelectionSlide;

    /**
     * Constructor.
     */
    @Inject
    public LevelSelectionStage(LevelSelectionSlide levelSelectionSlide, PlatformInsetsProvider platformInsetsProvider
        , @MenuViewport Viewport viewport,
                               @MenuCamera OrthographicCamera camera, Skin skin,
                               LocalizationManager localizationManager) {
        super(viewport, Collections.singletonList(levelSelectionSlide), platformInsetsProvider, camera, skin, localizationManager);
        this.levelSelectionSlide = levelSelectionSlide;
    }

}

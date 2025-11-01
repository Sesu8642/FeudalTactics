// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.splashscreen.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.TimeUtils;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * {@link Screen} for displaying a splash image.
 */
@Singleton
public class SplashScreen extends GameScreen {

    private final ScreenNavigationController screenNavigationController;
    private long startTime;

    /**
     * Constructor.
     */
    @Inject
    public SplashScreen(@MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                        SplashScreenStage stage, ScreenNavigationController screenNavigationController) {
        super(camera, viewport, stage);
        this.screenNavigationController = screenNavigationController;
    }

    @Override
    public void render(float delta) {
        super.render(delta);
        if (TimeUtils.timeSinceMillis(startTime) > 1000) {
            screenNavigationController.transitionToMainMenuScreen();
            hide();
        }
    }

    @Override
    public void show() {
        startTime = TimeUtils.millis();
        super.show();
    }

    @Override
    public void hide() {
        super.hide();
        // TODO: disposing here causes error "buffer not allocated with
        // newUnsafeByteBuffer or already
        // disposed"; maybe because the call is caused by the render method
    }

}

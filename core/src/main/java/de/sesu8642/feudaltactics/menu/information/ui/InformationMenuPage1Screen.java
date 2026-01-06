// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.information.ui;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

/**
 * {@link Screen} for displaying the information menu, page 1.
 */
@Singleton
public class InformationMenuPage1Screen extends GameScreen {

    /**
     * Constructor.
     */
    @Inject
    public InformationMenuPage1Screen(ScreenNavigationController screenNavigationController,
                                      @MenuCamera OrthographicCamera camera,
                                      @MenuViewport Viewport viewport, InformationMenuPage1Stage menuStage) {
        super(camera, viewport, menuStage);
        final List<TextButton> buttons = menuStage.getButtons();
        buttons.get(0).addListener(new ExceptionLoggingChangeListener(screenNavigationController::transitionToAboutScreen));
        buttons.get(1).addListener(new ExceptionLoggingChangeListener(screenNavigationController::transitionToCrashReportScreenInMainMenu));
        buttons.get(2).addListener(new ExceptionLoggingChangeListener(screenNavigationController::transitionToStatisticsScreen));
        buttons.get(3).addListener(new ExceptionLoggingChangeListener(screenNavigationController::transitionToInformationMenuScreenPage2));
        buttons.get(4).addListener(new ExceptionLoggingChangeListener(screenNavigationController::transitionToMainMenuScreen));
    }

}

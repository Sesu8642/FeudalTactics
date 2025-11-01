// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.information.ui;

import com.badlogic.gdx.Gdx;
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
 * {@link Screen} for displaying the information menu, page 2.
 */
@Singleton
public class InformationMenuPage2Screen extends GameScreen {

    /**
     * Constructor.
     */
    @Inject
    public InformationMenuPage2Screen(ScreenNavigationController screenNavigationController,
                                      @MenuCamera OrthographicCamera camera,
                                      @MenuViewport Viewport viewport, InformationMenuPage2Stage menuStage) {
        super(camera, viewport, menuStage);
        final List<TextButton> buttons = menuStage.getButtons();
        buttons.get(0).addListener(new ExceptionLoggingChangeListener(screenNavigationController::transitionToChangelogScreen));
        buttons.get(1).addListener(new ExceptionLoggingChangeListener(screenNavigationController::transitionToDependencyLicensesScreen));
        buttons.get(2).addListener(new ExceptionLoggingChangeListener(() -> Gdx.net
            .openURI("https://raw.githubusercontent.com/Sesu8642/FeudalTactics/master/privacy_policy.txt")));
        buttons.get(3).addListener(new ExceptionLoggingChangeListener(screenNavigationController::transitionToInformationMenuScreenPage1));
        buttons.get(4).addListener(new ExceptionLoggingChangeListener(screenNavigationController::transitionToMainMenuScreen));
    }

}

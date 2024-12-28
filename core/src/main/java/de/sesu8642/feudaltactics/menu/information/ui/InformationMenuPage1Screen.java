// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.information.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class InformationMenuPage1Screen extends GameScreen {

    @Inject
    public InformationMenuPage1Screen(EventBus eventBus, @MenuCamera OrthographicCamera camera,
                                      @MenuViewport Viewport viewport, InformationMenuPage1Stage menuStage) {
        super(camera, viewport, menuStage);
        List<TextButton> buttons = menuStage.getButtons();
        buttons.get(0).addListener(new ExceptionLoggingChangeListener(
                () -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.ABOUT_SCREEN))));
        buttons.get(1).addListener(new ExceptionLoggingChangeListener(() -> {
            // Java thinks that this is not a valid URI. Android disagrees. So there's a fallback that redirects to the actual site.
            boolean success = Gdx.net.openURI("https://matrix.to/#/#feudal-tactics-community:matrix.org");
            if (!success) {
                Gdx.net.openURI("https://sesu8642.github.io/FeudalTacticsCommunityRedirect/");
            }
        }));
        buttons.get(2).addListener(new ExceptionLoggingChangeListener(() -> eventBus
                .post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.CRASH_REPORT_SCREEN_IN_MAIN_MENU))));
        buttons.get(3).addListener(new ExceptionLoggingChangeListener(() -> eventBus
                .post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INFORMATION_MENU_SCREEN_2))));
        buttons.get(4).addListener(new ExceptionLoggingChangeListener(
                () -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.MAIN_MENU_SCREEN))));
    }

}

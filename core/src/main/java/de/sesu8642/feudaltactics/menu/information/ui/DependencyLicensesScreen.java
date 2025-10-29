// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.information.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingClickListener;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.information.dagger.DependencyLicenses;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Map.Entry;

/**
 * {@link Screen} for learning about dependencies and their licensing.
 */
@Singleton
public class DependencyLicensesScreen extends GameScreen {

    private final DependencyLicensesStage dependencyListStage;
    private final DependencyDetailsStage dependencyDetailsStage;
    private final EventBus eventBus;

    Map<String, Map<String, String>> dependencyLicenses;

    /**
     * Constructor.
     */
    @Inject
    public DependencyLicensesScreen(@MenuViewport Viewport viewport, @MenuCamera OrthographicCamera camera,
                                    DependencyLicensesStage dependencyListStage,
                                    DependencyDetailsStage dependencyDetailsStage,
                                    EventBus eventBus,
                                    @DependencyLicenses Map<String, Map<String, String>> dependencyLicenses) {
        super(camera, viewport, dependencyListStage);
        this.eventBus = eventBus;
        this.dependencyListStage = dependencyListStage;
        this.dependencyDetailsStage = dependencyDetailsStage;
        this.dependencyLicenses = dependencyLicenses;
        registerEventListeners();
    }

    private void registerEventListeners() {
        dependencyListStage.setFinishedCallback(() -> eventBus
            .post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.INFORMATION_MENU_SCREEN_2)));

        for (Label dependencyNameLabel : dependencyListStage.dependencyListSlide.dependencyNameLabels) {
            dependencyNameLabel.addListener(new ExceptionLoggingClickListener(() -> {
                dependencyDetailsStage.reset();
                Gdx.input.setInputProcessor(dependencyDetailsStage);
                final String dependencyName = dependencyNameLabel.getText().toString();
                dependencyDetailsStage.dependencyDetailsSlide.setHeadline(dependencyName);
                final String dependencyDetailsText = generateLicenseDetailsText(dependencyName);
                dependencyDetailsStage.dependencyDetailsSlide.label.setText(dependencyDetailsText);
                setActiveStage(dependencyDetailsStage);
            }));
        }

        dependencyDetailsStage.setFinishedCallback(() -> {
            setActiveStage(dependencyListStage);
            Gdx.input.setInputProcessor(dependencyListStage);
        });

    }

    private String generateLicenseDetailsText(String dependencyName) {
        final Map<String, String> dependencyLicenseFiles = dependencyLicenses.get(dependencyName);
        final StringBuilder resultBuilder = new StringBuilder();
        for (Entry<String, String> entry : dependencyLicenseFiles.entrySet()) {
            resultBuilder.append(entry.getKey());
            resultBuilder.append("\n");
            resultBuilder.append(Strings.repeat("=", entry.getKey().length()));
            resultBuilder.append("\n\n");
            resultBuilder.append(entry.getValue());
            resultBuilder.append("\n\n");
        }

        return resultBuilder.toString();
    }

}

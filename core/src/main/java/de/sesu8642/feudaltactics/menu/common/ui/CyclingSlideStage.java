// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
import lombok.Setter;

import java.util.List;

/**
 * {@link com.badlogic.gdx.scenes.scene2d.Stage} that displays slides with a cycling button
 * and a fixed back button. The cycling button advances through slides with looping behavior,
 * while the back button provides navigation away from the screen.
 */
public class CyclingSlideStage extends AbstractSlideStage {

    private final LocalizationManager localizationManager;
    private final List<String> slideNames;
    private TextButton cyclingButton;
    @Setter
    private Runnable finishedCallback;

    /**
     * Constructor.
     *
     * @param viewport               viewport for the stage
     * @param slides                 list of slides to display (must have at least 1)
     * @param slideNames             names for each slide (must match slides size)
     * @param platformInsetsProvider provider for platform-specific insets
     * @param camera                 camera for the stage
     * @param skin                   game skin
     */
    public CyclingSlideStage(Viewport viewport, List<Slide> slides, List<String> slideNames,
                             PlatformInsetsProvider platformInsetsProvider, Runnable finishedCallback,
                             OrthographicCamera camera, Skin skin, LocalizationManager localizationManager) {
        super(viewport, slides, platformInsetsProvider, camera, skin);
        this.localizationManager = localizationManager;
        if (slideNames.size() != slides.size()) {
            throw new IllegalArgumentException("slideNames size must match slides size");
        }
        this.slideNames = slideNames;
        this.finishedCallback = finishedCallback;
        initUi();
    }

    private void initUi() {
        currentSlide = slides.get(0);
        slideContainer.setActor(currentSlide.getTable());

        cyclingButton = ButtonFactory.createTextButton("", skin);   // Text will be set later on reset()
        final TextButton finishButton = ButtonFactory.createTextButton(localizationManager.localizeText("finish"),
            skin);

        cyclingButton.addListener(new ExceptionLoggingChangeListener(() -> switchToSlide(getNextIndex())));
        finishButton.addListener(new ExceptionLoggingChangeListener(() -> finishedCallback.run()));

        initCommonUi(cyclingButton, finishButton);

        switchToSlide(0);
    }

    /**
     * Get the index of the next slide. When reaching the last slide, cycles back to the first.
     */
    private int getNextIndex() {
        return (currentSlideIndex + 1) % slides.size();
    }

    @Override
    protected void updateNavigationButtonStates() {
        cyclingButton.setText(slideNames.get(getNextIndex()));  // show next slide name
    }
}

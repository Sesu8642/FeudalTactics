// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
import lombok.Setter;

import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link Stage} that can display multiple slides that the user can go through.
 */
public class SlideStage extends AbstractSlideStage {

    private final List<Table> slides;
    private Table currentSlide;
    private TextButton backButton;
    private TextButton nextButton;
    @Setter
    private Runnable finishedCallback;

    /**
     * Constructor.
     */
    public SlideStage(Viewport viewport, List<Slide> slides, PlatformInsetsProvider platformInsetsProvider,
                      OrthographicCamera camera, Skin skin) {
        this(viewport, slides, platformInsetsProvider, () -> {
        }, camera, skin);
    }

    /**
     * Constructor.
     */
    public SlideStage(Viewport viewport, List<Slide> slides, PlatformInsetsProvider platformInsetsProvider,
                      Runnable finishedCallback, OrthographicCamera camera, Skin skin) {
        super(viewport, platformInsetsProvider, camera, skin);
        if (slides.isEmpty()) {
            throw new IllegalArgumentException("at least one slide is required");
        }
        this.slides = slides.stream().map(Slide::getTable).collect(Collectors.toList());
        this.finishedCallback = finishedCallback;
        initUi();
    }

    private void initUi() {
        backButton = ButtonFactory.createTextButton("", skin);
        backButton.setDisabled(true);
        backButton.setTouchable(Touchable.disabled);

        nextButton = ButtonFactory.createTextButton("", skin);

        initCommonUi(backButton, nextButton);

        currentSlide = slides.get(0);
        slideContainer.setActor(currentSlide);

        nextButton.addListener(new ExceptionLoggingChangeListener(() -> {
            final int currentSlideIndex = slides.indexOf(currentSlide);
            if (slides.size() > currentSlideIndex + 1) {
                final Table newSlide = slides.get(currentSlideIndex + 1);
                slideContainer.setActor(newSlide);
                currentSlide = newSlide;
                if (slides.size() == currentSlideIndex + 2) {
                    nextButton.setText("Finish");
                }
                backButton.setTouchable(Touchable.enabled);
                backButton.setDisabled(false);
                backButton.setText("Back");
                camera.update();
            } else {
                finishedCallback.run();
            }
        }));

        backButton.addListener(new ExceptionLoggingChangeListener(() -> {
            final int currentSlideIndex = slides.indexOf(currentSlide);
            if (currentSlideIndex > 0) {
                final Table newSlide = slides.get(currentSlideIndex - 1);
                slideContainer.setActor(newSlide);
                currentSlide = newSlide;
                nextButton.setText("Next");
                if (currentSlideIndex == 1) {
                    backButton.setTouchable(Touchable.disabled);
                    backButton.setDisabled(true);
                    backButton.setText("");
                }
            }
        }));
    }

    @Override
    public void reset() {
        backButton.setTouchable(Touchable.disabled);
        backButton.setDisabled(true);
        backButton.setText("");
        final String nextButtonText = slides.size() == 1 ? "Finish" : "Next";
        nextButton.setText(nextButtonText);
        currentSlide = slides.get(0);
        slideContainer.setActor(currentSlide);
        scrollPane.setScrollY(0);
    }

    @Override
    public void updateOnResize(int width, int height) {
        updateSlidesOnResize(slides);
    }
}

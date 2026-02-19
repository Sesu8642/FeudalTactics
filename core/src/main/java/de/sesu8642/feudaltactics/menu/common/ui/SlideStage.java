// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
import lombok.Setter;

import java.util.List;

/**
 * {@link Stage} that can display multiple slides that the user can go through.
 */
public class SlideStage extends AbstractSlideStage {

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
        super(viewport, slides, platformInsetsProvider, camera, skin);
        if (slides.isEmpty()) {
            throw new IllegalArgumentException("at least one slide is required");
        }
        this.finishedCallback = finishedCallback;
        initUi();
    }

    private void initUi() {
        backButton = ButtonFactory.createTextButton("", skin);
        backButton.setDisabled(true);
        backButton.setTouchable(Touchable.disabled);

        nextButton = ButtonFactory.createTextButton("", skin);

        initCommonUi(backButton, nextButton);

        nextButton.addListener(new ExceptionLoggingChangeListener(() -> {
            if (slides.size() > currentSlideIndex + 1) {
                switchToSlide(currentSlideIndex + 1);
            } else {
                finishedCallback.run();
            }
        }));

        backButton.addListener(new ExceptionLoggingChangeListener(() -> {
            switchToSlide(currentSlideIndex - 1);
        }));

        switchToSlide(0);
    }

    @Override
    protected void updateNavigationButtonStates() {
        if (slides.size() == currentSlideIndex + 1) {
            nextButton.setText("Finish");
        } else {
            nextButton.setText("Next");
        }
        if (currentSlideIndex == 0) {
            backButton.setTouchable(Touchable.disabled);
            backButton.setDisabled(true);
            backButton.setText("");
        } else {
            backButton.setTouchable(Touchable.enabled);
            backButton.setDisabled(false);
            backButton.setText("Back");
        }
    }
}

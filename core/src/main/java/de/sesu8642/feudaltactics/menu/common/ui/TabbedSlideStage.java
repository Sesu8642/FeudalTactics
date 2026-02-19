// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link com.badlogic.gdx.scenes.scene2d.Stage} that can display multiple slides with tab-like navigation.
 * Unlike SlideStage which uses Next/Back navigation, this stage shows multiple buttons at the bottom
 * for direct tab switching.
 */
public class TabbedSlideStage extends AbstractSlideStage {

    private final List<String> tabNames;
    @Getter
    private final List<TextButton> tabButtons = new ArrayList<>();
    @Setter
    private Runnable finishedCallback;

    /**
     * Constructor.
     *
     * @param viewport               viewport for the stage
     * @param slides                 list of slides to display
     * @param tabNames               names for each tab button (must match slides size + 1 for back button)
     * @param platformInsetsProvider provider for platform-specific insets
     * @param backCallback           callback to run when back button is pressed
     * @param camera                 camera for the stage
     * @param skin                   game skin
     */
    public TabbedSlideStage(Viewport viewport, List<Slide> slides, List<String> tabNames,
                            PlatformInsetsProvider platformInsetsProvider, Runnable finishedCallback,
                            OrthographicCamera camera, Skin skin) {
        super(viewport, slides, platformInsetsProvider, camera, skin);
        if (tabNames.size() != slides.size() + 1) {
            throw new IllegalArgumentException("tabNames size must be slides size + 1 (for back button)");
        }
        this.tabNames = tabNames;
        this.finishedCallback = finishedCallback;
        initUi();
    }

    private void initUi() {
        currentSlide = slides.get(0);
        slideContainer.setActor(currentSlide.getTable());

        // Create tab buttons
        for (int i = 0; i < tabNames.size(); i++) {
            final TextButton tabButton = ButtonFactory.createTextButton(tabNames.get(i), skin);
            tabButtons.add(tabButton);

            final int index = i;
            if (i < slides.size()) {
                tabButton.addListener(new ExceptionLoggingChangeListener(() -> switchToSlide(index)));
            } else {
                tabButton.addListener(new ExceptionLoggingChangeListener(() -> finishedCallback.run()));
            }
        }

        initCommonUi(tabButtons.toArray(new TextButton[0]));

        switchToSlide(0);
    }

    @Override
    protected void updateNavigationButtonStates() {
        for (int i = 0; i < tabButtons.size() - 1; i++) {
            final TextButton button = tabButtons.get(i);
            if (i == currentSlideIndex) {
                button.setDisabled(true);
                button.setTouchable(Touchable.disabled);
            } else {
                button.setDisabled(false);
                button.setTouchable(Touchable.enabled);
            }
        }
    }
}

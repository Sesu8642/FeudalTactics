// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.platformspecific.Insets;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link Stage} that can display multiple slides that the user can go through.
 */
public class SlideStage extends ResizableResettableStage {

    private final List<Table> slides;
    private final Insets insets;
    private final Skin skin;
    private final OrthographicCamera camera;
    private final Container<Table> slideContainer = new Container<>();
    Set<Disposable> disposables = new HashSet<>();
    private Table rootTable;
    private Table currentSlide;
    private TextButton backButton;
    private TextButton nextButton;
    private ScrollPane scrollPane;
    private Runnable finishedCallback;

    /**
     * Constructor.
     */
    public SlideStage(Viewport viewport, List<Slide> slides, Insets insets, OrthographicCamera camera, Skin skin) {
        this(viewport, slides, insets, () -> {
        }, camera, skin);
    }

    /**
     * Constructor.
     */
    public SlideStage(Viewport viewport, List<Slide> slides, Insets insets, Runnable finishedCallback,
                      OrthographicCamera camera,
                      Skin skin) {
        super(viewport);
        if (slides.isEmpty()) {
            throw new IllegalArgumentException("at least one slide is required");
        }
        this.insets = insets;
        this.camera = camera;
        this.skin = skin;
        this.slides = slides.stream().map(Slide::getTable).collect(Collectors.toList());
        this.finishedCallback = finishedCallback;
        initUi(this.slides);
    }

    private void initUi(List<Table> slides) {
        backButton = ButtonFactory.createTextButton("", skin);
        backButton.setDisabled(true);
        backButton.setTouchable(Touchable.disabled);

        nextButton = ButtonFactory.createTextButton("", skin);

        currentSlide = slides.get(0);

        TextArea backgroundArea = new TextArea(null, skin);
        backgroundArea.setDisabled(true);

        slideContainer.fill();
        slideContainer.pad(20, 25, 20, 20);
        slideContainer.setActor(currentSlide);

        Stack slideAreaStack = new Stack(backgroundArea, slideContainer);

        scrollPane = new ScrollPane(slideAreaStack, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setOverscroll(false, false);

        rootTable = new Table();
        rootTable.padTop(insets.getTopInset());
        rootTable.padBottom(insets.getBottomInset());
        rootTable.setFillParent(true);
        rootTable.defaults().minSize(0);
        rootTable.add(scrollPane).expand().fill().colspan(2);
        rootTable.row();
        rootTable.defaults().minHeight(100).pad(0).expandX().bottom().fillX();
        rootTable.add(backButton);
        rootTable.add(nextButton);

        this.addActor(rootTable);

        nextButton.addListener(new ExceptionLoggingChangeListener(() -> {
            int currentSlideIndex = slides.indexOf(currentSlide);
            if (slides.size() > currentSlideIndex + 1) {
                Table newSlide = slides.get(currentSlideIndex + 1);
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
            int currentSlideIndex = slides.indexOf(currentSlide);
            if (currentSlideIndex > 0) {
                Table newSlide = slides.get(currentSlideIndex - 1);
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
        String nextButtonText = slides.size() == 1 ? "Finish" : "Next";
        nextButton.setText(nextButtonText);
        currentSlide = slides.get(0);
        slideContainer.setActor(currentSlide);
        scrollPane.setScrollY(0);
    }

    @Override
    public void updateOnResize(int width, int height) {
        camera.viewportHeight = height;
        camera.viewportWidth = width;
        camera.update();
        rootTable.pack();
        slides.forEach(slide -> {
            slide.pack();
            slide.getChildren().forEach(child -> {
                if (ClassReflection.isAssignableFrom(Table.class, child.getClass())) {
                    ((Table) child).pack();
                }
            });
        });
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
    }

    public void setFinishedCallback(Runnable finishedCallback) {
        this.finishedCallback = finishedCallback;
    }

}

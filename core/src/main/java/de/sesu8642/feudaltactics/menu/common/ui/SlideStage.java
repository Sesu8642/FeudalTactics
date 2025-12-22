// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * {@link Stage} that can display multiple slides that the user can go through.
 */
public class SlideStage extends ResizableResettableStage {

    private final List<Table> slides;
    private final PlatformInsetsProvider platformInsetsProvider;
    private final Skin skin;
    private final OrthographicCamera camera;
    private final Container<Table> slideContainer = new Container<>();
    Set<Disposable> disposables = new HashSet<>();
    private Table rootTable;
    private Table currentSlide;
    private TextButton backButton;
    private TextButton nextButton;
    private ScrollPane scrollPane;
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
        super(viewport);
        if (slides.isEmpty()) {
            throw new IllegalArgumentException("at least one slide is required");
        }
        this.platformInsetsProvider = platformInsetsProvider;
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

        final TextArea backgroundArea = new TextArea(null, skin);
        backgroundArea.setDisabled(true);

        slideContainer.fill();
        slideContainer.pad(20, 25, 20, 20);
        slideContainer.setActor(currentSlide);

        final Stack slideAreaStack = new Stack(backgroundArea, slideContainer);

        scrollPane = new ScrollPane(slideAreaStack, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setOverscroll(false, false);

        rootTable = new Table();
        rootTable.padTop(platformInsetsProvider.getInsets(Gdx.app).getTopInset());
        rootTable.padBottom(platformInsetsProvider.getInsets(Gdx.app).getBottomInset());
        rootTable.setFillParent(true);
        rootTable.defaults().minSize(0);
        rootTable.add(scrollPane).expand().fill().colspan(2);
        rootTable.row();
        rootTable.defaults().minHeight(100).pad(0).expandX().bottom().fillX();
        rootTable.add(backButton);
        rootTable.add(nextButton);

        addActor(rootTable);

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
        rootTable.padTop(platformInsetsProvider.getInsets(Gdx.app).getTopInset());
        rootTable.padBottom(platformInsetsProvider.getInsets(Gdx.app).getBottomInset());
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

}

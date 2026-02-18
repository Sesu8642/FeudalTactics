// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Abstract base class for stages that display slides with scrollable content and bottom buttons.
 */
public abstract class AbstractSlideStage extends ResizableResettableStage {

    protected final PlatformInsetsProvider platformInsetsProvider;
    protected final Skin skin;
    protected final OrthographicCamera camera;
    protected final Container<Table> slideContainer = new Container<>();
    protected final Set<Disposable> disposables = new HashSet<>();
    protected Table rootTable;
    protected ScrollPane scrollPane;
    protected final List<Slide> slides;
    protected Slide currentSlide;
    protected int currentSlideIndex = 0;

    /**
     * Constructor.
     *
     * @param viewport                viewport for the stage
     * @param slides                  list of slides to display
     * @param platformInsetsProvider  provider for platform-specific insets
     * @param camera                  camera for the stage
     * @param skin                    game skin
     */
    protected AbstractSlideStage(Viewport viewport, List<Slide> slides, PlatformInsetsProvider platformInsetsProvider,
                                  OrthographicCamera camera, Skin skin) {
        super(viewport);

        if (slides.isEmpty()) {
            throw new IllegalArgumentException("at least one slide is required");
        }

        this.slides = slides;
        this.platformInsetsProvider = platformInsetsProvider;
        this.camera = camera;
        this.skin = skin;
    }

    /**
     * Initializes the common UI elements. Subclasses should call this and then add their buttons.
     *
     * @param buttons the buttons that will be added at the bottom
     */
    protected void initCommonUi(TextButton... buttons) {
        final TextArea backgroundArea = new TextArea(null, skin);
        backgroundArea.setDisabled(true);

        slideContainer.fill();
        slideContainer.pad(20, 25, 20, 20);

        final Stack slideAreaStack = new Stack(backgroundArea, slideContainer);

        scrollPane = new ScrollPane(slideAreaStack, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setOverscroll(false, false);

        rootTable = new Table();
        rootTable.padTop(platformInsetsProvider.getInsets(Gdx.app).getTopInset());
        rootTable.padBottom(platformInsetsProvider.getInsets(Gdx.app).getBottomInset());
        rootTable.setFillParent(true);
        rootTable.defaults().minSize(0);
        rootTable.add(scrollPane).expand().fill().colspan(buttons.length);
        rootTable.row();
        rootTable.defaults().minHeight(100).pad(0).expandX().bottom().fillX();

        for (TextButton button : buttons) {
            rootTable.add(button);
        }

        addActor(rootTable);
    }

    /**
     * Switches to the slide at the given index.
     *
     * @param index index of the slide to switch to
     */
    public void switchToSlide(int index) {
        if (index < 0 || index >= slides.size()) {
            return;
        }
        currentSlideIndex = index;
        currentSlide = slides.get(index);
        slideContainer.setActor(currentSlide.getTable());
        scrollPane.setScrollY(0);
        updateNavigationButtonStates();
        camera.update();
    }

    protected abstract void updateNavigationButtonStates();

    /**
     * Updates the resize handling for the given slides.
     */
    protected void updateSlidesOnResize(List<? extends Table> slides) {
        rootTable.padTop(platformInsetsProvider.getInsets(Gdx.app).getTopInset());
        rootTable.padBottom(platformInsetsProvider.getInsets(Gdx.app).getBottomInset());
        camera.viewportHeight = getViewport().getWorldHeight();
        camera.viewportWidth = getViewport().getWorldWidth();
        camera.update();
    }

    @Override
    public void updateOnResize(int width, int height) {
        updateSlidesOnResize(slides.stream().map(Slide::getTable).collect(Collectors.toList()));
    }

    @Override
    public void reset() {
        switchToSlide(0);
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
    }
}

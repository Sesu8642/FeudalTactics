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

    protected AbstractSlideStage(Viewport viewport, PlatformInsetsProvider platformInsetsProvider,
                                  OrthographicCamera camera, Skin skin) {
        super(viewport);
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
     * Updates the resize handling for the given slides.
     */
    protected void updateSlidesOnResize(List<? extends Table> slides) {
        rootTable.padTop(platformInsetsProvider.getInsets(Gdx.app).getTopInset());
        rootTable.padBottom(platformInsetsProvider.getInsets(Gdx.app).getBottomInset());
        camera.viewportHeight = getViewport().getWorldHeight();
        camera.viewportWidth = getViewport().getWorldWidth();
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

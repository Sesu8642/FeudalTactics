// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.platformspecific.Insets;
import de.sesu8642.feudaltactics.renderer.MapRenderer;
import lombok.AccessLevel;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Generic {@link Stage} for displaying an in-game menu.
 */
public class MenuStage extends ResizableResettableStage {

    @Getter
    private final List<TextButton> buttons = new ArrayList<>();
    private final MapRenderer mapRenderer;
    private final Insets insets;
    private final Skin skin;
    private final OrthographicCamera camera;
    Set<Disposable> disposables = new HashSet<>();
    private Table rootTable;
    @Getter(AccessLevel.PROTECTED)
    private Table bottomLeftTable;
    @Getter(AccessLevel.PROTECTED)
    private Table bottomRightTable;

    /**
     * Constructor.
     *
     * @param viewport    viewport for the stage
     * @param camera      camera to use
     * @param mapRenderer renderer for the sea background
     * @param skin        game skin
     */
    public MenuStage(Viewport viewport, List<String> buttonTexts, OrthographicCamera camera, Insets insets,
                     MapRenderer mapRenderer, Skin skin) {
        super(viewport);
        this.camera = camera;
        this.insets = insets;
        this.mapRenderer = mapRenderer;
        this.skin = skin;
        initUi(buttonTexts);
    }

    private void initUi(List<String> buttonTexts) {
        final Texture logoTexture = new Texture(Gdx.files.internal("logo.png"));
        disposables.add(logoTexture);
        for (String buttonText : buttonTexts) {
            final TextButton button = ButtonFactory.createTextButton(buttonText, skin);
            buttons.add(button);
        }
        bottomLeftTable = new Table();
        bottomRightTable = new Table();

        rootTable = new Table();
        rootTable.padBottom(insets.getBottomInset());
        rootTable.padTop(insets.getTopInset());
        rootTable.setFillParent(true);
        rootTable.defaults().minSize(0).fillX().expandY().colspan(2);
        final Image logo = new Image(logoTexture);
        rootTable.add(logo).prefHeight(Value.percentWidth(0.51F, rootTable)).minHeight(150).width(Value.percentHeight(1.91F));
        rootTable.row();
        rootTable.defaults().minHeight(100).pad(5);
        for (TextButton button : buttons) {
            rootTable.add(button).prefWidth(Value.percentWidth(0.5F, rootTable));
            rootTable.row();
        }
        rootTable.row();
        rootTable.add(bottomLeftTable).fill(false).left().bottom().pad(10).minHeight(0).colspan(1);
        rootTable.add(bottomRightTable).fill(false).right().bottom().pad(10).minHeight(0).colspan(1);

        addActor(rootTable);
    }

    @Override
    public void updateOnResize(int width, int height) {
        rootTable.pack();
        camera.viewportHeight = height;
        camera.viewportWidth = width;
        camera.update();
    }

    @Override
    public void draw() {
        mapRenderer.render();
        super.draw();
    }

    @Override
    public void dispose() {
        super.dispose();
        for (Disposable disposable : disposables) {
            disposable.dispose();
        }
    }

    @Override
    public void reset() {
        // nothing to reset
    }
}

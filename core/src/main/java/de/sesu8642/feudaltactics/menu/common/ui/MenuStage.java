// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.Scaling;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
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

    private final static float BUTTON_HEIGHT = Gdx.graphics.getDensity() * 150;
    @Getter
    private final List<TextButton> buttons = new ArrayList<>();
    private final MapRenderer mapRenderer;
    private final PlatformInsetsProvider platformInsetsProvider;
    private final Skin skin;
    private final OrthographicCamera camera;
    Set<Disposable> disposables = new HashSet<>();
    LocalizationManager localizationManager;
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
    public MenuStage(Viewport viewport, List<String> buttonTexts, OrthographicCamera camera,
                     PlatformInsetsProvider platformInsetsProvider,
                     MapRenderer mapRenderer, Skin skin, LocalizationManager localizationManager) {
        super(viewport);
        this.camera = camera;
        this.platformInsetsProvider = platformInsetsProvider;
        this.mapRenderer = mapRenderer;
        this.skin = skin;
        this.localizationManager = localizationManager;
        initUi(buttonTexts);
    }

    private void initUi(List<String> buttonTexts) {
        final Texture logoTexture = new Texture(Gdx.files.internal("logo.png"));
        disposables.add(logoTexture);
        for (String buttonText : buttonTexts) {
            final TextButton button =
                ButtonFactory.createTextButton(localizationManager.localizeText(buttonText), skin);
            buttons.add(button);
        }
        bottomLeftTable = new Table();
        bottomRightTable = new Table();

        rootTable = new Table();
        rootTable.padBottom(platformInsetsProvider.getInsets(Gdx.app).getBottomInset());
        rootTable.padTop(platformInsetsProvider.getInsets(Gdx.app).getTopInset());
        rootTable.setFillParent(true);
        rootTable.defaults().minSize(0).fillX().colspan(2);
        final Image logoImage = new Image(logoTexture);
        logoImage.setScaling(Scaling.contain);
        rootTable.add(logoImage).minHeight(150).prefHeight(Value.percentWidth(0.51F));

        rootTable.row();
        rootTable.defaults().minHeight(100).pad(5);
        for (TextButton button : buttons) {
            rootTable.add(button).prefWidth(Value.percentWidth(0.5F, rootTable)).expandY();
            rootTable.row();
        }
        rootTable.row();
        rootTable.add(bottomLeftTable).fill(false).left().bottom().pad(10).minHeight(0).colspan(1);
        rootTable.add(bottomRightTable).fill(false).right().bottom().pad(10).minHeight(0).colspan(1);

        addActor(rootTable);
    }

    @Override
    public void updateOnResize(int width, int height) {
        // need to pack the table in order for it's size to be calculated so the logo image doesnt do weird things
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

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ButtonFactory;
import de.sesu8642.feudaltactics.menu.common.ui.ResizableResettableStage;
import de.sesu8642.feudaltactics.menu.common.ui.SkinConstants;
import de.sesu8642.feudaltactics.menu.common.ui.ValueWithSize;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
import de.sesu8642.feudaltactics.renderer.TextureAtlasHelper;

import javax.inject.Inject;

/**
 * {@link Stage} that displays the in-game heads up display.
 */
public class EditorHudStage extends ResizableResettableStage {

    private final Skin skin;
    private final PlatformInsetsProvider platformInsetsProvider;
    private final TextureAtlasHelper textureAtlasHelper;
    Label infoTextLabel;
    ImageButton menuButton;
    ImageButton tileButton;
    ImageButton tileContentButton;
    private Table rootTable;
    private Stack handStack;
    private Table handContentTable;
    private Image handContent;

    /**
     * Constructor.
     */
    @Inject
    public EditorHudStage(@MenuViewport Viewport viewport, PlatformInsetsProvider platformInsetsProvider, Skin skin,
                          TextureAtlasHelper textureAtlasHelper) {
        super(viewport);
        this.skin = skin;
        this.platformInsetsProvider = platformInsetsProvider;
        this.textureAtlasHelper = textureAtlasHelper;
        initUi(platformInsetsProvider);
    }

    private void initUi(PlatformInsetsProvider platformInsetsProvider) {
        final Table bottomTable;
        final Sprite tileSprite = textureAtlasHelper.getTileSprite();
        final SpriteDrawable tileSpriteDrawable = new SpriteDrawable(tileSprite);
        tileButton = new ImageButton(tileSpriteDrawable);

        final Sprite unitSprite = textureAtlasHelper.getSpearmanSprite();
        final SpriteDrawable unitSpriteDrawable = new SpriteDrawable(unitSprite);
        tileContentButton = new ImageButton(unitSpriteDrawable);

        menuButton = ButtonFactory.createImageButton(SkinConstants.BUTTON_PAUSE, skin);
        menuButton.getImage().setColor(skin.getColor(SkinConstants.COLOR_HIGHLIGHT2));
        menuButton.getImageCell().expand().fill();

        handStack = new Stack();
        handContentTable = new Table();
        handContent = new Image();

        final Sprite handSprite = skin.getSprite(SkinConstants.SPRITE_HAND);
        handSprite.setFlip(true, false);
        final Image handImage = new Image(handSprite);
        handImage.setColor(skin.getColor(SkinConstants.COLOR_HIGHLIGHT2));
        final Sprite thumbSprite = skin.getSprite(SkinConstants.SPRITE_HAND_THUMB);
        thumbSprite.setFlip(true, false);
        final Image thumbImage = new Image(thumbSprite);
        thumbImage.setColor(skin.getColor(SkinConstants.COLOR_HIGHLIGHT2));

        infoTextLabel = new Label("", skin.get(SkinConstants.FONT_OVERLAY_WITH_BACKGROUND, LabelStyle.class));

        rootTable = new Table();
        rootTable.padTop(platformInsetsProvider.getInsets(Gdx.app).getTopInset());
        rootTable.padBottom(platformInsetsProvider.getInsets(Gdx.app).getBottomInset());
        rootTable.setFillParent(true);
        rootTable.add(infoTextLabel).left().top().pad(10).expandX();
        rootTable.add(menuButton).right().size(ValueWithSize.percentSize(0.075F, rootTable)).pad(10);
        rootTable.row();
        rootTable.add();
        rootTable.add(handStack).right().size(ValueWithSize.percentSize(0.1F, rootTable));
        rootTable.row();

        bottomTable = new Table();
        bottomTable.defaults().fill().expand().minSize(0);

        bottomTable.add(tileButton);
        bottomTable.add(tileContentButton);

        rootTable.add(bottomTable).fill().expand().bottom().colspan(3)
            .height(ValueWithSize.percentSize(0.1F, rootTable));

        handStack.add(handImage);
        handStack.add(handContentTable);
        handStack.add(thumbImage);
        handStack.setVisible(false);
        handContentTable.setFillParent(true);
        handContentTable.add(handContent).height(Value.percentHeight(.5F, handContentTable))
            .width(Value.percentHeight(1.16F));
        addActor(rootTable);
    }

    @Override
    public void updateOnResize(int width, int height) {
        rootTable.padTop(platformInsetsProvider.getInsets(Gdx.app).getTopInset());
        rootTable.padBottom(platformInsetsProvider.getInsets(Gdx.app).getBottomInset());
        rootTable.pack();
        handContentTable.pack();
    }

    /**
     * See {@link #updateHandContent(Sprite)} with additional coloring (meant for the tiles)
     */
    public void updateHandContent(Sprite sprite, Color color) {
        if (sprite != null) {
            handStack.setVisible(true);

            final TextureRegionDrawable drawable = new TextureRegionDrawable(sprite);
            handContent.setDrawable(drawable);
            handContent.setColor(color);
        } else {
            handStack.setVisible(false);
        }
    }

    /**
     * Sets the hand content to a given sprite.
     *
     * @param sprite sprite to set the hand content to; can be null to show an empty hand
     */
    public void updateHandContent(Sprite sprite) {
        updateHandContent(sprite, Color.WHITE);
    }

    @Override
    public void reset() {
        // nothing to reset
    }

}

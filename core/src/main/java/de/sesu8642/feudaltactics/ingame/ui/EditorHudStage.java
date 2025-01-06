// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.lib.gamestate.Unit;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ResizableResettableStage;
import de.sesu8642.feudaltactics.menu.common.ui.SkinConstants;
import de.sesu8642.feudaltactics.menu.common.ui.ValueWithSize;

import javax.inject.Inject;

/**
 * {@link Stage} that displays the in-game heads up display.
 */
public class EditorHudStage extends ResizableResettableStage {

    private final TextureAtlas textureAtlas;
    private final Skin skin;
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
     *
     * @param viewport     viewport for the stage
     * @param textureAtlas texture atlas containing the button textures
     * @param skin         game skin
     */
    @Inject
    public EditorHudStage(@MenuViewport Viewport viewport, TextureAtlas textureAtlas, Skin skin) {
        super(viewport);
        this.textureAtlas = textureAtlas;
        this.skin = skin;
        initUi();
    }

    private void initUi() {
        Table bottomTable;
        Sprite tileSprite = textureAtlas.createSprite("tile_bw");
        SpriteDrawable tileSpriteDrawable = new SpriteDrawable(tileSprite);
        tileButton = new ImageButton(tileSpriteDrawable);

        Sprite unitSprite = textureAtlas.createSprite(Unit.UnitTypes.SPEARMAN.spriteName());
        SpriteDrawable unitSpriteDrawable = new SpriteDrawable(unitSprite);
        tileContentButton = new ImageButton(unitSpriteDrawable);

        menuButton = new ImageButton(skin.get(SkinConstants.BUTTON_PAUSE, ImageButtonStyle.class));
        menuButton.getImage().setColor(skin.getColor(SkinConstants.COLOR_HIGHLIGHT2));
        menuButton.getImageCell().expand().fill();

        handStack = new Stack();
        handContentTable = new Table();
        handContent = new Image();

        Sprite handSprite = skin.getSprite(SkinConstants.SPRITE_HAND);
        handSprite.setFlip(true, false);
        Image handImage = new Image(handSprite);
        handImage.setColor(skin.getColor(SkinConstants.COLOR_HIGHLIGHT2));
        Sprite thumbSprite = skin.getSprite(SkinConstants.SPRITE_HAND_THUMB);
        thumbSprite.setFlip(true, false);
        Image thumbImage = new Image(thumbSprite);
        thumbImage.setColor(skin.getColor(SkinConstants.COLOR_HIGHLIGHT2));

        infoTextLabel = new Label("", skin.get(SkinConstants.FONT_OVERLAY_WITH_BACKGROUND, LabelStyle.class));

        rootTable = new Table();
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
        this.addActor(rootTable);
    }

    @Override
    public void updateOnResize(int width, int height) {
        rootTable.pack();
        handContentTable.pack();
    }

    /**
     * See {@link #updateHandContent(String)} with additional coloring (meant for the tiles)
     */
    public void updateHandContent(String spriteName, Color color) {
        if (spriteName != null) {
            handStack.setVisible(true);

            Sprite sprite = textureAtlas.createSprite(spriteName);
            TextureRegionDrawable drawable = new TextureRegionDrawable(sprite);
            handContent.setDrawable(drawable);
            handContent.setColor(color);
        } else {
            handStack.setVisible(false);
        }
    }

    /**
     * Sets the hand content to a given sprite.
     *
     * @param spriteName name if the sprite to set the hand content to; can be null
     *                   to show an empty hand
     */
    public void updateHandContent(String spriteName) {
        updateHandContent(spriteName, Color.WHITE);
    }

    @Override
    public void reset() {
        // nothing to reset
    }

}

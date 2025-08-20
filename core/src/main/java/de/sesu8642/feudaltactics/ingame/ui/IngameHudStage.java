// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ButtonFactory;
import de.sesu8642.feudaltactics.menu.common.ui.ResizableResettableStage;
import de.sesu8642.feudaltactics.menu.common.ui.SkinConstants;
import de.sesu8642.feudaltactics.menu.common.ui.ValueWithSize;
import de.sesu8642.feudaltactics.platformspecific.Insets;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * {@link Stage} that displays the in-game heads up display.
 */
public class IngameHudStage extends ResizableResettableStage {

    private final TextureAtlas textureAtlas;
    private final Skin skin;
    private final List<ImageButton> playerTurnButtons = new ArrayList<>();
    private final List<ImageButton> enemyTurnButtons = new ArrayList<>();
    Label infoHexagonLabel;
    Label infoTextLabel;
    ImageButton undoButton;
    ImageButton endTurnButton;
    ImageButton buyPeasantButton;
    ImageButton buyCastleButton;
    ImageButton speedButton;
    ImageButton skipButton;
    ImageButton menuButton;
    ImageButton infoButton;
    ImageButtonStyle halfSpeedButtonStyle;
    ImageButtonStyle regularSpeedButtonStyle;
    ImageButtonStyle doubleSpeedButtonStyle;
    private Table rootTable;
    private Stack handStack;
    private Table handContentTable;
    private Image handContent;
    private Table bottomTable;
    private boolean enemyTurnButtonsShown = false;

    /**
     * Constructor.
     *
     * @param viewport     viewport for the stage
     * @param textureAtlas texture atlas containing the button textures
     * @param skin         game skin
     */
    @Inject
    public IngameHudStage(@MenuViewport Viewport viewport, TextureAtlas textureAtlas, Insets insets, Skin skin) {
        super(viewport);
        this.textureAtlas = textureAtlas;
        this.skin = skin;
        initUi(insets);
    }

    private void initUi(Insets insets) {
        halfSpeedButtonStyle = skin.get(SkinConstants.BUTTON_SPEED_HALF, ImageButtonStyle.class);
        ButtonFactory.createImageButton(SkinConstants.BUTTON_PAUSE, skin);
        regularSpeedButtonStyle = skin.get(SkinConstants.BUTTON_SPEED_REGULAR, ImageButtonStyle.class);
        doubleSpeedButtonStyle = skin.get(SkinConstants.BUTTON_SPEED_DOUBLE, ImageButtonStyle.class);

        menuButton = ButtonFactory.createImageButton(SkinConstants.BUTTON_PAUSE, skin);
        menuButton.getImage().setColor(skin.getColor(SkinConstants.COLOR_HIGHLIGHT2));
        menuButton.getImageCell().expand().fill();

        infoButton = ButtonFactory.createImageButton(SkinConstants.BUTTON_INFO, skin);
        infoButton.getImage().setColor(skin.getColor(SkinConstants.COLOR_HIGHLIGHT2));
        infoButton.getImageCell().expand().fill();

        // buttons visible during the local player turn
        undoButton = ButtonFactory.createImageButton(SkinConstants.BUTTON_UNDO, skin);
        buyPeasantButton = ButtonFactory.createImageButton(SkinConstants.BUTTON_BUY_PEASANT, skin);
        buyCastleButton = ButtonFactory.createImageButton(SkinConstants.BUTTON_BUY_CASTLE, skin);
        endTurnButton = ButtonFactory.createImageButton(SkinConstants.BUTTON_END_TURN, skin);
        playerTurnButtons.add(undoButton);
        playerTurnButtons.add(buyPeasantButton);
        playerTurnButtons.add(buyCastleButton);
        playerTurnButtons.add(endTurnButton);
        for (ImageButton button : playerTurnButtons) {
            button.getImageCell().expand().fill();
        }

        // buttons visible during enemies' turns
        speedButton = ButtonFactory.createImageButton(SkinConstants.BUTTON_SPEED_REGULAR, skin);
        skipButton = ButtonFactory.createImageButton(SkinConstants.BUTTON_SKIP_TURN, skin);
        enemyTurnButtons.add(speedButton);
        enemyTurnButtons.add(skipButton);
        for (ImageButton button : enemyTurnButtons) {
            button.getImageCell().expand().fill();
            button.getImage().setColor(skin.getColor(SkinConstants.COLOR_HIGHLIGHT2));
        }

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

        infoHexagonLabel = new Label("", skin.get(SkinConstants.FONT_HEXAGON, LabelStyle.class));

        infoTextLabel = new Label("", skin.get(SkinConstants.FONT_OVERLAY_WITH_BACKGROUND, LabelStyle.class));

        rootTable = new Table();
        rootTable.padTop(insets.getTopInset());
        rootTable.setFillParent(true);
        rootTable.add(infoHexagonLabel).left().top().pad(10);
        rootTable.add(infoTextLabel).left().top().pad(10).expandX();
        rootTable.add(menuButton).right().size(ValueWithSize.percentSize(0.075F, rootTable)).pad(10);
        rootTable.row();
        rootTable.add();
        rootTable.add();
        rootTable.add(infoButton).right().size(ValueWithSize.percentSize(0.075F, rootTable)).pad(10);
        rootTable.row();
        rootTable.add();
        rootTable.add();
        rootTable.add(handStack).right().size(ValueWithSize.percentSize(0.1F, rootTable));
        rootTable.row();

        bottomTable = new Table(skin);
        bottomTable.setTouchable(Touchable.enabled);
        // do not propagate clicks on the bottom table to the map
        bottomTable.addListener(new ClickListener());

        bottomTable.background(SkinConstants.SEMI_TRANSPARENT_BACKGROUND_DRAWABLE);
        bottomTable.defaults().fill().expand().minSize(0);
        bottomTable.add(undoButton);
        bottomTable.add(buyPeasantButton);
        bottomTable.add(buyCastleButton);
        bottomTable.add(endTurnButton);
        rootTable.add(bottomTable).fill().expand().bottom().colspan(3)
            .height(ValueWithSize.percentSizeDensityMin(0.1F, rootTable, 100));
        rootTable.row();

        Table bottomInsetTable = new Table(skin);
        bottomInsetTable.background(SkinConstants.SEMI_TRANSPARENT_BACKGROUND_DRAWABLE);
        rootTable.add(bottomInsetTable).height(insets.getBottomInset()).colspan(3).fill();


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
     * Sets the hand content to a given sprite.
     *
     * @param spriteName name if the sprite to set the hand content to; can be null
     *                   to show an empty hand
     */
    public void updateHandContent(String spriteName) {
        if (spriteName != null) {
            handStack.setVisible(true);
            handContent.setDrawable(new TextureRegionDrawable(textureAtlas.createSprite(spriteName)));
        } else {
            handStack.setVisible(false);
        }
    }

    /**
     * Sets the enabled status of the buttons that are visible during the player
     * turn.
     *
     * @param undoButtonState       state of the unto button
     * @param buyPeasantButtonState state of the buy peasant button
     * @param buyCastleButtonState  state of the buy castle button
     * @param endTurnButtonState    state of the end turn button
     */
    public void setActiveTurnButtonEnabledStatus(boolean undoButtonState, boolean buyPeasantButtonState,
                                                 boolean buyCastleButtonState, boolean endTurnButtonState) {
        setButtonEnabledStatus(undoButtonState, undoButton);
        setButtonEnabledStatus(buyPeasantButtonState, buyPeasantButton);
        setButtonEnabledStatus(buyCastleButtonState, buyCastleButton);
        setButtonEnabledStatus(endTurnButtonState, endTurnButton);
    }

    private void setButtonEnabledStatus(boolean enabled, ImageButton button) {
        if (enabled) {
            button.setTouchable(Touchable.enabled);
            button.getImage().setColor(skin.getColor(SkinConstants.COLOR_HIGHLIGHT2));
        } else {
            button.setTouchable(Touchable.disabled);
            button.getImage().setColor(skin.getColor(SkinConstants.COLOR_DISABLED));
        }
    }

    /**
     * Shows the buttons for the local player to do their turn.
     */
    public void showPlayerTurnButtons() {
        enemyTurnButtonsShown = false;
        bottomTable.clear();
        for (ImageButton button : playerTurnButtons) {
            bottomTable.add(button);
        }
    }

    /**
     * Shows the buttons to monitor the other players' turns.
     */
    public void showEnemyTurnButtons() {
        enemyTurnButtonsShown = true;
        bottomTable.clear();
        for (ImageButton button : enemyTurnButtons) {
            bottomTable.add(button);
        }
    }

    @Override
    public void reset() {
        // nothing to reset
    }

    public boolean isEnemyTurnButtonsShown() {
        return enemyTurnButtonsShown;
    }

    public void setEnemyTurnButtonsShown(boolean enemyTurnButtonsShown) {
        this.enemyTurnButtonsShown = enemyTurnButtonsShown;
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.collect.ImmutableMap;

import de.sesu8642.feudaltactics.FeudalTactics;
import de.sesu8642.feudaltactics.lib.ingame.botai.Speed;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ResizableResettableStage;
import de.sesu8642.feudaltactics.menu.common.ui.ValueWithSize;

/**
 * {@link Stage} that displays the in-game heads up display.
 */
public class HudStage extends ResizableResettableStage {

	static final Map<Speed, String> SPEED_BUTTON_TEXTURE_NAMES = ImmutableMap.of(Speed.HALF, "0.5x", Speed.NORMAL, "1x",
			Speed.TIMES_TWO, "2x");

	private TextureAtlas textureAtlas;
	private Skin skin;

	private Table rootTable;
	private Stack handStack;
	private Label infoTextLabel;
	private Table handContentTable;
	private Image handContent;
	ImageButton undoButton;
	ImageButton endTurnButton;
	ImageButton buyPeasantButton;
	ImageButton buyCastleButton;
	ImageButton speedButton;
	ImageButton skipButton;
	ImageButton menuButton;
	private Table bottomTable;
	private List<ImageButton> playerTurnButtons = new ArrayList<>();
	private List<ImageButton> enemyTurnButtons = new ArrayList<>();

	private boolean enemyTurnButtonsShown = false;

	/**
	 * Constructor.
	 * 
	 * @param viewport     viewport for the stage
	 * @param textureAtlas texture atlas containing the button textures
	 * @param skin         game skin
	 */
	@Inject
	public HudStage(@MenuViewport Viewport viewport, TextureAtlas textureAtlas, Skin skin) {
		super(viewport);
		this.textureAtlas = textureAtlas;
		this.skin = skin;
		initUi();
	}

	private void initUi() {
		// TODO: put the buttons in a custom skin
		menuButton = new ImageButton(new SpriteDrawable(textureAtlas.createSprite("pause")),
				new SpriteDrawable(textureAtlas.createSprite("pause_pressed")));
		menuButton.getImage().setColor(FeudalTactics.buttonIconColor);
		menuButton.getImageCell().expand().fill();

		// buttons visible during the local player turn
		undoButton = new ImageButton(new SpriteDrawable(textureAtlas.createSprite("undo")),
				new SpriteDrawable(textureAtlas.createSprite("undo_pressed")));
		buyPeasantButton = new ImageButton(new SpriteDrawable(textureAtlas.createSprite("buy_peasant")),
				new SpriteDrawable(textureAtlas.createSprite("buy_peasant_pressed")));
		buyCastleButton = new ImageButton(new SpriteDrawable(textureAtlas.createSprite("buy_castle")),
				new SpriteDrawable(textureAtlas.createSprite("buy_castle_pressed")));
		endTurnButton = new ImageButton(new SpriteDrawable(textureAtlas.createSprite("end_turn")),
				new SpriteDrawable(textureAtlas.createSprite("end_turn_pressed")));
		playerTurnButtons.add(undoButton);
		playerTurnButtons.add(buyPeasantButton);
		playerTurnButtons.add(buyCastleButton);
		playerTurnButtons.add(endTurnButton);
		for (ImageButton button : playerTurnButtons) {
			button.getImageCell().expand().fill();
		}

		// buttons visible during enemies' turns
		speedButton = new ImageButton(new SpriteDrawable(textureAtlas.createSprite("1x")),
				new SpriteDrawable(textureAtlas.createSprite("1x_pressed")));
		skipButton = new ImageButton(new SpriteDrawable(textureAtlas.createSprite("skip_turn")),
				new SpriteDrawable(textureAtlas.createSprite("skip_turn_pressed")));
		enemyTurnButtons.add(speedButton);
		enemyTurnButtons.add(skipButton);
		for (ImageButton button : enemyTurnButtons) {
			button.getImageCell().expand().fill();
			button.getImage().setColor(FeudalTactics.buttonIconColor);
		}

		handStack = new Stack();
		handContentTable = new Table();
		handContent = new Image();

		Sprite handSprite = new Sprite(textureAtlas.createSprite("hand"));
		handSprite.setFlip(true, false);
		Image handImage = new Image(handSprite);
		handImage.setColor(FeudalTactics.buttonIconColor);
		Sprite thumbSprite = new Sprite(textureAtlas.createSprite("hand_thumb"));
		thumbSprite.setFlip(true, false);
		Image thumbImage = new Image(thumbSprite);
		thumbImage.setColor(FeudalTactics.buttonIconColor);
		infoTextLabel = new Label("", skin);

		rootTable = new Table();
		rootTable.setFillParent(true);
		rootTable.add(infoTextLabel).left().top().pad(10);
		rootTable.add(menuButton).right().size(ValueWithSize.percentSize(0.075F, rootTable)).pad(10);
		rootTable.row();
		rootTable.add();
		rootTable.add(handStack).right().size(ValueWithSize.percentSize(0.1F, rootTable));
		rootTable.row();

		bottomTable = new Table();
		bottomTable.defaults().fill().expand().minSize(0);
		bottomTable.add(undoButton);
		bottomTable.add(buyPeasantButton);
		bottomTable.add(buyCastleButton);
		bottomTable.add(endTurnButton);
		rootTable.add(bottomTable).fill().expand().bottom().colspan(2)
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
			button.getImage().setColor(FeudalTactics.buttonIconColor);
		} else {
			button.setTouchable(Touchable.disabled);
			button.getImage().setColor(FeudalTactics.disabledButtonIconColor);
		}
	}

	/** Shows the buttons for the local player to do their turn. */
	public void showPlayerTurnButtons() {
		enemyTurnButtonsShown = false;
		bottomTable.clear();
		for (ImageButton button : playerTurnButtons) {
			bottomTable.add(button);
		}
	}

	/** Shows the buttons to monitor the other players' turns. */
	public void showEnemyTurnButtons() {
		enemyTurnButtonsShown = true;
		bottomTable.clear();
		for (ImageButton button : enemyTurnButtons) {
			bottomTable.add(button);
		}
	}

	public void setInfoText(String newText) {
		infoTextLabel.setText(newText);
	}

	public void setFontScale(Float fontScale) {
		infoTextLabel.setFontScale(fontScale);
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

package com.sesu8642.feudaltactics.ui.stages;

import javax.inject.Inject;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.dagger.MenuViewport;
import com.sesu8642.feudaltactics.libgdx.ValueWithSize;

public class HudStage extends ResizableResettableStage {

	public enum EventTypes {
		UNDO, END_TURN, BUY_PEASANT, BUY_CASTLE, MENU
	}

	private Table rootTable;
	private Stack handStack;
	private Label infoTextLabel;
	private Table handContentTable;
	private Image handContent;
	private ImageButton undoButton;
	private ImageButton endTurnButton;
	private ImageButton buyPeasantButton;
	private ImageButton buyCastleButton;
	private ImageButton menuButton;
	private TextureAtlas textureAtlas;
	private Skin skin;

	@Inject
	public HudStage(@MenuViewport Viewport viewport, TextureAtlas textureAtlas, Skin skin) {
		super(viewport);
		this.textureAtlas = textureAtlas;
		this.skin = skin;
		initUI();
	}

	private void initUI() {
		// TODO: put the buttons in a custom skin
		undoButton = new ImageButton(new SpriteDrawable(textureAtlas.createSprite("undo")),
				new SpriteDrawable(textureAtlas.createSprite("undo_pressed")));
		undoButton.getImageCell().expand().fill();
		endTurnButton = new ImageButton(new SpriteDrawable(textureAtlas.createSprite("end_turn")),
				new SpriteDrawable(textureAtlas.createSprite("end_turn_pressed")));
		endTurnButton.getImageCell().expand().fill();
		buyPeasantButton = new ImageButton(new SpriteDrawable(textureAtlas.createSprite("buy_peasant")),
				new SpriteDrawable(textureAtlas.createSprite("buy_peasant_pressed")));
		buyPeasantButton.getImageCell().expand().fill();
		buyCastleButton = new ImageButton(new SpriteDrawable(textureAtlas.createSprite("buy_castle")),
				new SpriteDrawable(textureAtlas.createSprite("buy_castle_pressed")));
		buyCastleButton.getImageCell().expand().fill();
		menuButton = new ImageButton(new SpriteDrawable(textureAtlas.createSprite("pause")),
				new SpriteDrawable(textureAtlas.createSprite("pause_pressed")));
		menuButton.getImageCell().expand().fill();
		menuButton.getImage().setColor(FeudalTactics.buttonIconColor);

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
		rootTable.add(menuButton).right().size(ValueWithSize.percentSize(0.05F, rootTable)).pad(10);
		rootTable.row();
		rootTable.add();
		rootTable.add(handStack).right().size(ValueWithSize.percentSize(0.1F, rootTable));
		rootTable.row();

		Table bottomTable = new Table();
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

	public void registerEventListener(EventTypes type, Runnable listener) {
		Actor uIElement = null;
		switch (type) {
		case UNDO:
			uIElement = undoButton;
			break;
		case END_TURN:
			uIElement = endTurnButton;
			break;
		case BUY_PEASANT:
			uIElement = buyPeasantButton;
			break;
		case BUY_CASTLE:
			uIElement = buyCastleButton;
			break;
		case MENU:
			uIElement = menuButton;
			break;
		default:
			break;
		}
		uIElement.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				listener.run();
			}
		});
	}
	
	@Override
	public void updateOnResize(int width, int height) {
		rootTable.pack();
		handContentTable.pack();
	}

	public void updateHandContent(String spritename) {
		if (spritename != null) {
			handStack.setVisible(true);
			handContent.setDrawable(new TextureRegionDrawable(textureAtlas.createSprite(spritename)));
		} else {
			handStack.setVisible(false);
		}
	}

	public void setButtonEnabledStatus(boolean undoButtonState, boolean buyPeasantButtonState,
			boolean buyCastleButtonState, boolean endTurnButtonState) {
		if (undoButtonState) {
			undoButton.setTouchable(Touchable.enabled);
			undoButton.getImage().setColor(FeudalTactics.buttonIconColor);
		} else {
			undoButton.setTouchable(Touchable.disabled);
			undoButton.getImage().setColor(FeudalTactics.disabledButtonIconColor);
		}
		if (buyPeasantButtonState) {
			buyPeasantButton.setTouchable(Touchable.enabled);
			buyPeasantButton.getImage().setColor(FeudalTactics.buttonIconColor);
		} else {
			buyPeasantButton.setTouchable(Touchable.disabled);
			buyPeasantButton.getImage().setColor(FeudalTactics.disabledButtonIconColor);
		}
		if (buyCastleButtonState) {
			buyCastleButton.setTouchable(Touchable.enabled);
			buyCastleButton.getImage().setColor(FeudalTactics.buttonIconColor);
		} else {
			buyCastleButton.setTouchable(Touchable.disabled);
			buyCastleButton.getImage().setColor(FeudalTactics.disabledButtonIconColor);
		}
		if (endTurnButtonState) {
			endTurnButton.setTouchable(Touchable.enabled);
			endTurnButton.getImage().setColor(FeudalTactics.buttonIconColor);
		} else {
			endTurnButton.setTouchable(Touchable.disabled);
			endTurnButton.getImage().setColor(FeudalTactics.disabledButtonIconColor);
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
	}

}

package com.sesu8642.feudaltactics.stages;

import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.libgdx.ValueWithSize;

public class HudStage extends Stage {

	public enum ActionUIElements {
		UNDO, END_TURN, BUY_PEASANT, BUY_CASTLE, MENU
	}
	
	private Stack handStack;
	private Label infoTextLabel;
	private Image handContent;

	public HudStage(Map<ActionUIElements, UIAction> actions) {
		initUI(actions);
	}

	public HudStage(Viewport viewport, Map<ActionUIElements, UIAction> actions) {
		super(viewport);
		initUI(actions);
	}

	public HudStage(Viewport viewport, Batch batch, Map<ActionUIElements, UIAction> actions) {
		super(viewport, batch);
		initUI(actions);
	}

	private void initUI(Map<ActionUIElements, UIAction> actions) {
		ImageButton undoButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("undo")),
				new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("undo_pressed")));
		undoButton.getImageCell().expand().fill();
		ImageButton endTurnButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("end_turn")),
				new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("end_turn_pressed")));
		endTurnButton.getImageCell().expand().fill();
		ImageButton buyPeasantButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy_peasant")),
				new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy_peasant_pressed")));
		buyPeasantButton.getImageCell().expand().fill();
		ImageButton buyCastleButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy_castle")),
				new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy_castle_pressed")));
		buyCastleButton.getImageCell().expand().fill();
		ImageButton menuButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("pause")),
				new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("pause_pressed")));
		menuButton.getImageCell().expand().fill();

		handStack = new Stack();
		Table handContentTable = new Table();
		handContent = new Image();

		Sprite handSprite = new Sprite(FeudalTactics.textureAtlas.createSprite("hand"));
		handSprite.setFlip(true, false);
		Image handImage = new Image(handSprite);
		Sprite thumbSprite = new Sprite(FeudalTactics.textureAtlas.createSprite("hand_thumb"));
		thumbSprite.setFlip(true, false);
		Image thumbImage = new Image(thumbSprite);
		infoTextLabel = new Label("", FeudalTactics.skin);

		Table rootTable = new Table();
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
		
		// add actions
		for (Entry<ActionUIElements, UIAction> action : actions.entrySet()) {
			Actor uIElement = null;
			switch (action.getKey()) {
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
					action.getValue().action();
				}
			});
		}
	}

	public void updateHandContent(String spritename) {
		if (spritename != null) {
			handStack.setVisible(true);
			handContent.setDrawable(new TextureRegionDrawable(FeudalTactics.textureAtlas.createSprite(spritename)));
		} else {
			handStack.setVisible(false);
		}
	}

	public void setInfoText(String newText) {
		infoTextLabel.setText(newText);
	}

	public void setFontScale(Float fontScale) {
		infoTextLabel.setFontScale(fontScale);
	}

}

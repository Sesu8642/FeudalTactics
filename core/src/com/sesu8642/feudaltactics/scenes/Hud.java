package com.sesu8642.feudaltactics.scenes;

import com.badlogic.gdx.graphics.OrthographicCamera;
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
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.engine.GameController;
import com.sesu8642.feudaltactics.engine.LocalInputHandler;

public class Hud {
	private Stage stage;
	private Table rootTable;
	private Viewport viewport;
	private Stack handStack;
	private Image handContent;
	private Label infoTextLabel;
	
	public Hud(final LocalInputHandler inputValidator) {
		ImageButton undoButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("undo")), new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("undo_pressed")));
		ImageButton endTurnButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("end_turn")), new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("end_turn_pressed")));
		ImageButton buyPeasantButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy_peasant")), new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy_peasant_pressed")));
		ImageButton buyCastleButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy_castle")), new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy_castle_pressed")));
		handStack = new Stack();
		Table handContentTable = new Table();
		handContent = new Image();
		undoButton.getImageCell().expand().fill();
		undoButton.addListener(new ChangeListener() {
	        @Override
	        public void changed (ChangeEvent event, Actor actor) {
	        	inputValidator.inputUndo();
	        }
	    });
		endTurnButton.getImageCell().expand().fill();
		endTurnButton.addListener(new ChangeListener() {
	        @Override
	        public void changed (ChangeEvent event, Actor actor) {
	        	inputValidator.inputEndTurn();
	        }
	    });
		buyPeasantButton.getImageCell().expand().fill();
		buyPeasantButton.addListener(new ChangeListener() {
	        @Override
	        public void changed (ChangeEvent event, Actor actor) {
	        	inputValidator.inputBuyPeasant();
	        }
	    });
		buyCastleButton.getImageCell().expand().fill();
		buyCastleButton.addListener(new ChangeListener() {
	        @Override
	        public void changed (ChangeEvent event, Actor actor) {
	        	inputValidator.inputBuyCastle();
	        }
	    });
	    Image handImage = new Image(new Sprite(FeudalTactics.textureAtlas.createSprite("hand")));
	    Image thumbImage = new Image(new Sprite(FeudalTactics.textureAtlas.createSprite("hand_thumb")));
	    infoTextLabel = new Label("", FeudalTactics.skin);
		viewport = new ScreenViewport(new OrthographicCamera());
		stage = new Stage(viewport);
	    
	    rootTable = new Table();
	    rootTable.setFillParent(true);
	    //rootTable.setDebug(true);
	    
	    rootTable.defaults().uniformY().expandY().minSize(0);
	    rootTable.columnDefaults(0).expandX();
	    rootTable.columnDefaults(1).width(Value.percentHeight(1F)).fillY().pad(15);
	    rootTable.add(infoTextLabel).left().top().padLeft(10);
	    rootTable.add(undoButton);
	    rootTable.row();
	    rootTable.add(new Image());
	    rootTable.add(endTurnButton);
	    rootTable.row();
	    rootTable.add(new Image());
	    rootTable.add(buyPeasantButton);
	    rootTable.row();
	    rootTable.add(handStack).bottom().left().width(Value.percentHeight(1F)).fillY();
	    rootTable.add(buyCastleButton);
	    handStack.add(handImage);
	    handStack.add(handContentTable);
	    handStack.add(thumbImage);
	    handStack.setVisible(false);
	    //handContentTable.debug();
	    handContentTable.setFillParent(true);
	    handContentTable.add(handContent).height(Value.percentHeight(.5F, handContentTable)).width(Value.percentHeight(1.16F));
	    stage.addActor(rootTable);
	}
	
	public void updateHandContent(String spritename) {
		if (spritename != null) {
			handStack.setVisible(true);
			handContent.setDrawable(new TextureRegionDrawable(FeudalTactics.textureAtlas.createSprite(spritename)));
		}else {
			handStack.setVisible(false);
		}
	}
	
	public void render() {
		viewport.apply();
		stage.draw();
		stage.act();
	}
	
	public void resize(int width, int height) {	
		infoTextLabel.setFontScale(height/800F);
		viewport.update(width, height, true);
		viewport.apply();
		rootTable.pack(); // VERY IMPORTANT!!! makes everything scale correctly on startup and going fullscreen etc.; took me hours to find out
	}
	
	public void dispose() {
		stage.dispose();
	}
	
	public void setInfoText(String newText) {
		infoTextLabel.setText(newText);
	}
	
	public Stage getStage() {
		return stage;
	}
}

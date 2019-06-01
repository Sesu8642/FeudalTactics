package com.sesu8642.feudaltactics.scenes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.engine.GameController;
import com.sesu8642.feudaltactics.engine.InputValidator;

public class Hud {
	private Stage stage;
	private Table table;
	private Viewport viewport;
	
	private Label infoTextLabel;
	
	public Hud(final InputValidator inputValidator) {
		ImageButton undoButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("undo")), new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("hand")));
		ImageButton endTurnButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("end_turn")), new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("hand")));
		ImageButton buyPeasantButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy")), new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("hand")));
		ImageButton buyCastleButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy_castle")), new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("hand")));
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
	    infoTextLabel = new Label("", FeudalTactics.skin);
		viewport = new ScreenViewport(new OrthographicCamera());
		stage = new Stage(viewport);
	    
	    table = new Table();
	    table.setFillParent(true);
	    //table.setDebug(true);
	    
	    table.defaults().uniformY().expandY().minSize(0);
	    table.columnDefaults(0).expandX().pad(10);
	    table.columnDefaults(1).width(Value.percentHeight(1F)).fillY();
	    table.add(infoTextLabel).left().top();
	    table.add(undoButton);
	    table.row();
	    table.add(new Image());
	    table.add(endTurnButton);
	    table.row();
	    table.add(new Image());
	    table.add(buyPeasantButton);
	    table.row();
	    table.add(handImage).bottom().left().width(Value.percentHeight(1F)).fillY();
	    table.add(buyCastleButton);
	    
	    stage.addActor(table);
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
		table.pack(); // VERY IMPORTANT!!! makes everything scale correctly on startup and going fullscreen etc.; took me hours to find out
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

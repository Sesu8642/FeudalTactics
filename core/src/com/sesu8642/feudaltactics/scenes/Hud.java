package com.sesu8642.feudaltactics.scenes;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
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
import com.sesu8642.feudaltactics.screens.IngameScreen;

public class Hud {
	private Stage stage;
	private Table table;
	private Viewport viewport;
	
	private Label infoText;
	
	public Hud(final IngameScreen gameScreen) {
		ImageButton undoButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("undo")), new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("hand")));
		ImageButton endTurnButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("end_turn")), new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("hand")));
		ImageButton buyPeasantButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy")), new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("hand")));
		ImageButton buyCastleButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy_castle")), new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("hand")));
		undoButton.getImageCell().expand().fill();
		endTurnButton.getImageCell().expand().fill();
		buyPeasantButton.getImageCell().expand().fill();
		buyCastleButton.getImageCell().expand().fill();
		undoButton.addListener(new ChangeListener() {
	        @Override
	        public void changed (ChangeEvent event, Actor actor) {
	            System.out.println("Button Pressed");
	            gameScreen.generateMap();
	        }
	    });
	    Image handImage = new Image(new Sprite(FeudalTactics.textureAtlas.createSprite("hand")));
	    infoText = new Label("This is some important info.\nlike money\nand stuff", FeudalTactics.skin);
		viewport = new ScreenViewport(new OrthographicCamera());
		stage = new Stage(viewport);
	    
	    table = new Table();
	    table.setFillParent(true);
	    //table.setDebug(true);
	    
	    table.defaults().uniformY().expandY().minSize(0);
	    table.columnDefaults(0).expandX().pad(10);
	    table.columnDefaults(1).width(Value.percentHeight(1F)).fillY();
	    table.add(infoText).left().top();
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
		infoText.setFontScale(height/800F);
		viewport.update(width, height, true);
		viewport.apply();
		table.pack(); // VERY IMPORTANT!!! makes everything scale correctly on startup and going fullscreen etc.; took me hours to find out
	}
	
	public void dispose() {
		stage.dispose();
	}
	
	public Stage getStage() {
		return stage;
	}
}

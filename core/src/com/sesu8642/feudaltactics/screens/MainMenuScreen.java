package com.sesu8642.feudaltactics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;

public class MainMenuScreen implements Screen {
	private Stage stage;
	private Table rootTable;
	private Viewport viewport;
	private Label versionTextLabel;
		
	public MainMenuScreen(FeudalTactics game) {
		Camera camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);
		stage = new Stage(viewport);

		TextButton playButton = new TextButton("Play", FeudalTactics.skin);
		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new IngameScreen(game));
			}
		});
		TextButton tutorialButton = new TextButton("Tutorial", FeudalTactics.skin);
		tutorialButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new EditorScreen(game));
			}
		});
		TextButton aboutButton = new TextButton("About", FeudalTactics.skin);
		versionTextLabel = new Label("Version 1.0", FeudalTactics.skin);
		
		rootTable = new Table();
		rootTable.setFillParent(true);
		rootTable.setDebug(true);
		rootTable.defaults().expand().fill().uniformY().minSize(0);
		rootTable.add();
		rootTable.add();
		rootTable.add();
		rootTable.row();
		rootTable.add();
		rootTable.row();
		rootTable.add();
		rootTable.add(playButton);
		rootTable.row();
		rootTable.add();
		rootTable.row();
		rootTable.add();
		rootTable.add(tutorialButton);
		rootTable.row();
		rootTable.add();
		rootTable.row();
		rootTable.add();
		rootTable.add(aboutButton);
		rootTable.row();
		rootTable.add(versionTextLabel).fill(false).right().bottom().pad(10).colspan(3);
		stage.addActor(rootTable);
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render(float delta) {
		Gdx.gl.glClearColor(0, 0.2f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		viewport.apply();
		stage.draw();
		stage.act();
	}

	@Override
	public void resize(int width, int height) {
		versionTextLabel.setFontScale(height / 1000F);
		viewport.update(width, height, true);
		viewport.apply();
		rootTable.pack();
	}

	@Override
	public void pause() {
		
	}

	@Override
	public void resume() {
		
	}

	@Override
	public void hide() {
		dispose();
	}

	@Override
	public void dispose() {
		stage.dispose();
	}

}

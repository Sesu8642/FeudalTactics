package com.sesu8642.feudaltactics.scenes;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.engine.EditorInputHandler;

public class EditorUIOverlay {
	private Stage stage;
	private Table rootTable;
	private Viewport viewport;

	public EditorUIOverlay(final EditorInputHandler inputHandler) {
		Camera camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);
		stage = new Stage(viewport);

		rootTable = new Table();
		rootTable.setFillParent(true);
		rootTable.row();		
		stage.addActor(rootTable);
	}

	public void render() {
		viewport.apply();
		stage.draw();
		stage.act();
	}

	public void resize(int width, int height) {
		viewport.update(width, height, true);
		viewport.apply();
		rootTable.pack();
	}

	public void dispose() {
		stage.dispose();
	}

	public Stage getStage() {
		return stage;
	}
}

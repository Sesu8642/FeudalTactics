package com.sesu8642.feudaltactics.stages;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.viewport.Viewport;

public class SplashImageStage extends Stage {

	private Texture logoTexture;

	public SplashImageStage() {
		initUI();
	}

	public SplashImageStage(Viewport viewport) {
		super(viewport);
		initUI();
	}

	public SplashImageStage(Viewport viewport, Batch batch) {
		super(viewport, batch);
		initUI();
	}

	private void initUI() {
		logoTexture = new Texture(Gdx.files.internal("logo.png"));
		Image logo = new Image(logoTexture);
		Table rootTable = new Table();
		rootTable.setFillParent(true);
		rootTable.defaults().minSize(0).fillX().expandY();
		rootTable.add(logo).prefHeight(Value.percentWidth(0.51F, rootTable)).width(Value.percentHeight(1.95F));
		addActor(rootTable);
	}

	@Override
	public void dispose() {
		logoTexture.dispose();
	}

}

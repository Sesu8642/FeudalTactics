package com.sesu8642.feudaltactics.ui.stages;

import com.badlogic.gdx.graphics.g2d.Batch;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.MapRenderer;

public class GenericMenuStage extends Stage {

	private List<TextButton> buttons = new ArrayList<TextButton>();
	private Label bottomLabel;
	Set<Disposable> disposables = new HashSet<Disposable>();
	private MapRenderer mapRenderer;
	private OrthographicCamera camera;

	public GenericMenuStage(LinkedHashMap<String, Runnable> buttonData) {
		initUI(buttonData);
	}

	public GenericMenuStage(Viewport viewport, LinkedHashMap<String, Runnable> buttonData) {
		super(viewport);
		initUI(buttonData);
	}

	public GenericMenuStage(Viewport viewport, Batch batch, LinkedHashMap<String, Runnable> buttonData) {
		super(viewport, batch);
		initUI(buttonData);
	}

	private void initUI(LinkedHashMap<String, Runnable> buttonData) {
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		camera.zoom = 0.2F;
		mapRenderer = new MapRenderer(camera);
		Texture logoTexture = new Texture(Gdx.files.internal("logo.png"));
		disposables.add(logoTexture);
		Image logo = new Image(logoTexture);
		for (Entry<String, Runnable> buttonDataPoint : buttonData.entrySet()) {
			TextButton button = new TextButton(buttonDataPoint.getKey(), FeudalTactics.skin);
			button.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					buttonDataPoint.getValue().run();
				}
			});
			buttons.add(button);
		}
		bottomLabel = new Label("", FeudalTactics.skin);

		Table rootTable = new Table();
		rootTable.setFillParent(true);
		rootTable.defaults().minSize(0).fillX().expandY();
		rootTable.add(logo).prefHeight(Value.percentWidth(0.51F, rootTable)).width(Value.percentHeight(1.95F));
		rootTable.row();
		rootTable.defaults().minHeight(100).pad(10);
		for (TextButton button : buttons) {
			rootTable.add(button).prefWidth(Value.percentWidth(0.5F, rootTable));
			rootTable.row();
		}
		rootTable.row();
		rootTable.add(bottomLabel).fill(false).right().bottom().pad(10).minHeight(0);

		this.addActor(rootTable);
	}

	public void setBottomLabelText(String text) {
		bottomLabel.setText(text);
	}

	private void setFontScale(Float fontScale) {
		bottomLabel.setFontScale(fontScale);
	}

	public List<TextButton> getButtons() {
		return buttons;
	}

	public void updateOnResize(int width, int height) {
		setFontScale(height / 1000F);
		camera.viewportHeight = height;
		camera.viewportWidth = width;
		camera.update();
	}

	@Override
	public void draw() {
		mapRenderer.render();
		super.draw();
	}

	@Override
	public void dispose() {
		super.dispose();
		for (Disposable disposable : disposables) {
			disposable.dispose();
		}
	}
}

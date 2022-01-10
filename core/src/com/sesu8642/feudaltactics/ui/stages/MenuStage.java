package com.sesu8642.feudaltactics.ui.stages;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.MapRenderer;

public class MenuStage extends ResizableResettableStage {

	private Table rootTable;
	private List<TextButton> buttons = new ArrayList<>();
	private Label bottomLabel;
	Set<Disposable> disposables = new HashSet<>();
	private MapRenderer mapRenderer;
	private Skin skin;
	private OrthographicCamera camera;

	public MenuStage(Viewport viewport, OrthographicCamera camera, MapRenderer mapRenderer, Skin skin) {
		this(viewport, new LinkedHashMap<>(), camera, mapRenderer, skin);
	}

	public MenuStage(Viewport viewport, Map<String, Runnable> buttonData, OrthographicCamera camera,
			MapRenderer mapRenderer, Skin skin) {
		super(viewport);
		this.camera = camera;
		this.mapRenderer = mapRenderer;
		this.skin = skin;
		initUI(buttonData);
	}

	private void initUI(Map<String, Runnable> buttonData) {
		Texture logoTexture = new Texture(Gdx.files.internal("logo.png"));
		disposables.add(logoTexture);
		Image logo = new Image(logoTexture);
		for (Entry<String, Runnable> buttonDataPoint : buttonData.entrySet()) {
			TextButton button = new TextButton(buttonDataPoint.getKey(), skin);
			button.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					buttonDataPoint.getValue().run();
				}
			});
			buttons.add(button);
		}
		bottomLabel = new Label("", skin);

		rootTable = new Table();
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

	public void addButton(String text, Runnable callback) {
		TextButton button = new TextButton(text, skin);
		button.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				callback.run();
			}
		});
		rootTable.removeActor(bottomLabel);
		rootTable.row();
		rootTable.add(button).prefWidth(Value.percentWidth(0.5F, rootTable));
		rootTable.row();
		rootTable.add(bottomLabel).fill(false).right().bottom().pad(10).minHeight(0);
	}

	public void setBottomLabelText(String text) {
		bottomLabel.setText(text);
	}

	public List<TextButton> getButtons() {
		return buttons;
	}

	@Override
	public void updateOnResize(int width, int height) {
		rootTable.pack();
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

	@Override
	public void reset() {
		// nothing to reset
	}
}

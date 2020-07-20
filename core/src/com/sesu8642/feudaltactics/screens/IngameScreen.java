package com.sesu8642.feudaltactics.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.input.GestureDetector;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.engine.CombinedInputProcessor;
import com.sesu8642.feudaltactics.engine.GameController;
import com.sesu8642.feudaltactics.engine.LocalInputHandler;
import com.sesu8642.feudaltactics.engine.MapRenderer;
import com.sesu8642.feudaltactics.libgdx.ValueWithSize;

public class IngameScreen implements Screen {

	private FeudalTactics game;
	private OrthographicCamera camera;
	private MapRenderer mapRenderer;
	private InputMultiplexer multiplexer;
	private LocalInputHandler inputValidator;
	private CombinedInputProcessor inputProcessor;

	private Stage hudStage;
	private Stage menuStage;
	private Stage activeStage;
	private Table bottomTable;
	private Viewport viewport;
	private Stack handStack;
	private Image handContent;
	private Label infoTextLabel;
	private Label seedTextLabel;

	public enum IngameStages {
		HUD, MENU
	}

	public IngameScreen(FeudalTactics game) {
		this.game = game;
		GameController gameController = new GameController();
		inputValidator = new LocalInputHandler(gameController);
		gameController.setHud(this);
		camera = new OrthographicCamera();
		mapRenderer = new MapRenderer(camera);
		gameController.setMapRenderer(mapRenderer);
		inputProcessor = new CombinedInputProcessor(inputValidator, camera);
		multiplexer = new InputMultiplexer();
		initUI();
		gameController.generateDummyMap();
	}

	private void initUI() {
		Camera camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);

		// hud
		hudStage = new Stage(viewport);

		ImageButton undoButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("undo")),
				new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("undo_pressed")));
		undoButton.getImageCell().expand().fill();
		undoButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				inputValidator.inputUndo();
			}
		});
		ImageButton endTurnButton = new ImageButton(
				new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("end_turn")),
				new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("end_turn_pressed")));
		endTurnButton.getImageCell().expand().fill();
		endTurnButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				inputValidator.inputEndTurn();
			}
		});
		ImageButton buyPeasantButton = new ImageButton(
				new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy_peasant")),
				new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy_peasant_pressed")));
		buyPeasantButton.getImageCell().expand().fill();
		buyPeasantButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				inputValidator.inputBuyPeasant();
			}
		});
		ImageButton buyCastleButton = new ImageButton(
				new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy_castle")),
				new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("buy_castle_pressed")));
		buyCastleButton.getImageCell().expand().fill();
		buyCastleButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				inputValidator.inputBuyCastle();
			}
		});
		ImageButton menuButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("pause")),
				new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("pause_pressed")));
		menuButton.getImageCell().expand().fill();
		menuButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				activateStage(IngameStages.MENU);
			}
		});

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

		Table hudRootTable = new Table();
		hudRootTable.setFillParent(true);
		hudRootTable.add(infoTextLabel).left().top().pad(10);
		hudRootTable.add(menuButton).right().size(ValueWithSize.percentSize(0.05F, hudRootTable)).pad(10);
		hudRootTable.row();
		hudRootTable.add();
		hudRootTable.add(handStack).right().size(ValueWithSize.percentSize(0.1F, hudRootTable));
		hudRootTable.row();

		bottomTable = new Table();
		bottomTable.defaults().fill().expand().minSize(0);
		bottomTable.add(undoButton);
		bottomTable.add(buyPeasantButton);
		bottomTable.add(buyCastleButton);
		bottomTable.add(endTurnButton);
		hudRootTable.add(bottomTable).fill().expand().bottom().colspan(2)
				.height(ValueWithSize.percentSize(0.1F, hudRootTable));

		handStack.add(handImage);
		handStack.add(handContentTable);
		handStack.add(thumbImage);
		handStack.setVisible(false);
		handContentTable.setFillParent(true);
		handContentTable.add(handContent).height(Value.percentHeight(.5F, handContentTable))
				.width(Value.percentHeight(1.16F));
		hudStage.addActor(hudRootTable);

		// menu
		menuStage = new Stage(viewport);

		Image logo = new Image(new Texture(Gdx.files.internal("logo.png")));
		TextButton saveButton = new TextButton("Save", FeudalTactics.skin);
		saveButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
			}
		});
		TextButton loadButton = new TextButton("Load", FeudalTactics.skin);
		loadButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
			}
		});
		TextButton exitButton = new TextButton("Exit", FeudalTactics.skin);
		exitButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				game.setScreen(new MainMenuScreen(game));
			}
		});
		TextButton continueButton = new TextButton("Continue", FeudalTactics.skin);
		continueButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				activateStage(IngameStages.HUD);
			}
		});
		seedTextLabel = new Label("Seed: ", FeudalTactics.skin);

		Table menuRootTable = new Table();
		menuRootTable.setFillParent(true);
		menuRootTable.defaults().minSize(0).fillX().expandY();
		menuRootTable.add(logo).prefHeight(Value.percentWidth(0.51F, menuRootTable)).width(Value.percentHeight(1.95F));
		menuRootTable.row();
		menuRootTable.defaults().minHeight(100).pad(10);
		menuRootTable.add(saveButton).prefWidth(Value.percentWidth(0.5F, menuRootTable));
		menuRootTable.row();
		menuRootTable.add(loadButton);
		menuRootTable.row();
		menuRootTable.add(exitButton);
		menuRootTable.row();
		menuRootTable.add(continueButton);
		menuRootTable.row();
		menuRootTable.add(seedTextLabel).fill(false).right().bottom().pad(10).minHeight(0);

		menuStage.addActor(menuRootTable);

		// initially show the hud
		activateStage(IngameStages.HUD);
	}

	public void activateStage(IngameStages ingameStages) {
		if (ingameStages == IngameStages.MENU) {
			multiplexer.clear();
			multiplexer.addProcessor(menuStage);
			activeStage = menuStage;
		} else if (ingameStages == IngameStages.HUD) {
			multiplexer.clear();
			multiplexer.addProcessor(hudStage);
			multiplexer.addProcessor(new GestureDetector(inputProcessor));
			multiplexer.addProcessor(inputProcessor);
			activeStage = hudStage;
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

	public void setSeedText(String seedText) {
		seedTextLabel.setText("Seed: " + seedText);
	}

	public Stage getStage() {
		return activeStage;
	}

	@Override
	public void show() {
		Gdx.input.setInputProcessor(multiplexer);
		camera.position.set(camera.viewportWidth, camera.viewportHeight, 0);
		camera.update();
		camera.zoom = 0.2F;
	}

	@Override
	public void render(float delta) {
		camera.update();
		Gdx.gl.glClearColor(0, 0.2f, 0.8f, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		mapRenderer.render();
		viewport.apply();
		activeStage.draw();
		activeStage.act();
	}

	@Override
	public void resize(int width, int height) {
		infoTextLabel.setFontScale(height / 1000F);
		seedTextLabel.setFontScale(height / 1000F);
		viewport.update(width, height, true);
		viewport.apply();
		((Table) hudStage.getActors().get(0)).pack(); // VERY IMPORTANT!!! makes everything scale correctly on startup
														// and going fullscreen etc.; took me hours to find out
		((Table) menuStage.getActors().get(0)).pack();
		camera.viewportHeight = height;
		camera.viewportWidth = width;
		camera.update();
	}

	@Override
	public void pause() {

	}

	@Override
	public void resume() {

	}

	@Override
	public void hide() {

	}

	@Override
	public void dispose() {
		mapRenderer.dispose();
		activeStage.dispose();
	}

	public OrthographicCamera getCamera() {
		return camera;
	}

}

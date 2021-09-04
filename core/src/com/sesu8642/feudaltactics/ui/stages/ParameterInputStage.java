package com.sesu8642.feudaltactics.ui.stages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.inject.Inject;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.BotAI;
import com.sesu8642.feudaltactics.BotAI.Intelligence;
import com.sesu8642.feudaltactics.dagger.MenuViewport;
import com.sesu8642.feudaltactics.preferences.NewGamePreferences;
import com.sesu8642.feudaltactics.preferences.PreferencesHelper;
import com.sesu8642.feudaltactics.preferences.NewGamePreferences.Densities;
import com.sesu8642.feudaltactics.preferences.NewGamePreferences.MapSizes;

public class ParameterInputStage extends ResizableResettableStage {

	// for map centering calculation
	public static final int NO_OF_INPUTS = 4;

	public enum EventTypes {
		CHANGE, REGEN, PLAY
	}

	private Table rootTable;
	private Label seedLabel;
	private Label sizeLabel;
	private Label densityLabel;
	private Label difficultyLabel;
	private SelectBox<String> sizeSelect;
	private SelectBox<String> densitySelect;
	private SelectBox<String> difficultySelect;
	private ImageButton randomButton;
	private TextButton playButton;
	private TextField seedTextField;
	private Collection<Runnable> regenListeners = new ArrayList<Runnable>();
	private Skin skin;
	private TextureAtlas textureAtlas;

	@Inject
	public ParameterInputStage(@MenuViewport Viewport viewport, TextureAtlas textureAtlas, Skin skin) {
		super(viewport);
		this.textureAtlas = textureAtlas;
		this.skin = skin;
		initUI();
	}

	private void initUI() {
		NewGamePreferences prefs = PreferencesHelper.getNewGamePreferences();
		difficultyLabel = new Label("CPU\nDifficulty", skin);
		difficultySelect = new SelectBox<String>(skin);
		String[] difficulties = { "Easy", "Medium", "Hard" };
		difficultySelect.setItems(difficulties);
		difficultySelect.setSelectedIndex(prefs.getBotIntelligence().ordinal());
		sizeLabel = new Label("Map\nSize", skin);
		sizeSelect = new SelectBox<String>(skin);
		String[] sizes = { "Small", "Medium", "Large" };
		sizeSelect.setItems(sizes);
		sizeSelect.setSelectedIndex(prefs.getMapSize().ordinal());
		densityLabel = new Label("Map\nDensity", skin);
		densitySelect = new SelectBox<String>(skin);
		String[] densities = { "Dense", "Medium", "Loose" };
		densitySelect.setItems(densities);
		densitySelect.setSelectedIndex(prefs.getDensity().ordinal());
		seedLabel = new Label("Seed", skin);
		seedTextField = new TextField(String.valueOf(System.currentTimeMillis()), skin);
		seedTextField.setTextFieldFilter(new DigitsOnlyFilter());
		seedTextField.setMaxLength(18);
		randomButton = new ImageButton(new SpriteDrawable(textureAtlas.createSprite("die")),
				new SpriteDrawable(textureAtlas.createSprite("die_pressed")));
		randomButton.getImageCell().expand().fill();
		playButton = new TextButton("Play", skin);

		rootTable = new Table();
		rootTable.setFillParent(true);
		rootTable.defaults().left();
		rootTable.columnDefaults(0).pad(0, 10, 0, 10);
//		rootTable.debug();
		rootTable.add().expandY();
		rootTable.row();
		rootTable.add(difficultyLabel);
		rootTable.add(difficultySelect);
		rootTable.add().expandX();
		rootTable.row();
		rootTable.add(sizeLabel);
		rootTable.add(sizeSelect);
		rootTable.row();
		rootTable.add(densityLabel);
		rootTable.add(densitySelect);
		rootTable.row();
		rootTable.add(seedLabel);
		rootTable.add(seedTextField).prefWidth(Value.percentWidth(1, difficultySelect));
		rootTable.add(randomButton).height(Value.percentHeight(1, seedTextField)).width(Value.percentHeight(1))
				.padLeft(10);
		rootTable.row();
		rootTable.add(playButton).colspan(4).fillX();
		this.addActor(rootTable);

		randomButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				seedTextField.setText(String.valueOf(System.currentTimeMillis()));
			}
		});
	}

	public void registerEventListener(EventTypes type, Runnable listener) {
		Collection<Actor> uIElements = new HashSet<Actor>();
		switch (type) {
		case CHANGE:
			uIElements.add(difficultySelect);
			uIElements.add(sizeSelect);
			uIElements.add(densitySelect);
			break;
		case REGEN:
			regenListeners.add(listener);
			uIElements.add(seedTextField);
			uIElements.add(randomButton);
			uIElements.add(sizeSelect);
			uIElements.add(densitySelect);
			break;
		case PLAY:
			uIElements.add(playButton);
			break;
		default:
			break;
		}
		for (Actor uIElement : uIElements) {
			uIElement.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					listener.run();
				}
			});
		}
	}

	public void regenerateMap(Long seed) {
		if (seed != null) {
			seedTextField.setText(seed.toString());
		}
		regenListeners.forEach((Runnable action) -> {
			action.run();
		});
	}

	public Long getSeedParam() {
		try {
			return Long.valueOf(seedTextField.getText());
		} catch (NumberFormatException e) {
			return 0L;
		}
	}

	public MapSizes getMapSize() {
		return MapSizes.values()[sizeSelect.getSelectedIndex()];
	}

	public int getMapSizeParam() {
		switch (sizeSelect.getSelectedIndex()) {
		case 0:
			return 50;
		case 1:
			return 150;
		case 2:
			return 250;
		default:
			return 150;
		}
	}

	public Densities getMapDensity() {
		return Densities.values()[densitySelect.getSelectedIndex()];
	}

	public float getMapDensityParam() {
		switch (densitySelect.getSelectedIndex()) {
		case 0:
			return -3;
		case 1:
			return 0;
		case 2:
			return 3;
		default:
			return 0;
		}
	}

	public Intelligence getBotIntelligence() {
		return Intelligence.values()[difficultySelect.getSelectedIndex()];
	}

	public BotAI.Intelligence getBotIntelligenceParam() {
		switch (difficultySelect.getSelectedIndex()) {
		case 0:
			return BotAI.Intelligence.DUMB;
		case 1:
			return BotAI.Intelligence.MEDIUM;
		case 2:
			return BotAI.Intelligence.SMART;
		default:
			return BotAI.Intelligence.MEDIUM;
		}
	}

	@Override
	public void updateOnResize(int width, int height) {
		// VERY IMPORTANT!!! makes everything scale correctly hours to find out
		rootTable.pack();
	}

	@Override
	public void reset() {
	}

}

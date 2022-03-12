package com.sesu8642.feudaltactics.ui.stages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

import javax.inject.Inject;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
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
import com.sesu8642.feudaltactics.BotAi;
import com.sesu8642.feudaltactics.BotAi.Intelligence;
import com.sesu8642.feudaltactics.dagger.MenuViewport;
import com.sesu8642.feudaltactics.preferences.NewGamePreferences;
import com.sesu8642.feudaltactics.preferences.NewGamePreferences.Densities;
import com.sesu8642.feudaltactics.preferences.NewGamePreferences.MapSizes;
import com.sesu8642.feudaltactics.preferences.PreferencesHelper;

/**
 * {@link Stage} for displaying the input mask for a new game.
 */
public class ParameterInputStage extends ResizableResettableStage {

	// for map centering calculation
	public static final int NO_OF_INPUTS = 4;

	/** Event types that can be invoked by this stage. */
	public enum EventTypes {
		CHANGE, REGEN, PLAY
	}

	private Table rootTable;
	private SelectBox<String> sizeSelect;
	private SelectBox<String> densitySelect;
	private SelectBox<String> difficultySelect;
	private ImageButton randomButton;
	private TextButton playButton;
	private TextField seedTextField;
	private Collection<Runnable> regenListeners = new ArrayList<>();
	private Skin skin;
	private TextureAtlas textureAtlas;

	/**
	 * Constructor.
	 * 
	 * @param viewport     viewport for the stage
	 * @param textureAtlas texture atlas containing the button textures
	 * @param skin         game skin
	 */
	@Inject
	public ParameterInputStage(@MenuViewport Viewport viewport, TextureAtlas textureAtlas, Skin skin) {
		super(viewport);
		this.textureAtlas = textureAtlas;
		this.skin = skin;
		initUi();
	}

	private void initUi() {
		// note about checktyle: widgets are declared in the order they appear in the UI
		NewGamePreferences prefs = PreferencesHelper.getNewGamePreferences();
		Label difficultyLabel = new Label("CPU\nDifficulty", skin);
		difficultySelect = new SelectBox<>(skin);
		String[] difficulties = { "Easy", "Medium", "Hard" };
		difficultySelect.setItems(difficulties);
		difficultySelect.setSelectedIndex(prefs.getBotIntelligence().ordinal());
		Label sizeLabel = new Label("Map\nSize", skin);
		sizeSelect = new SelectBox<>(skin);
		String[] sizes = { "Small", "Medium", "Large" };
		sizeSelect.setItems(sizes);
		sizeSelect.setSelectedIndex(prefs.getMapSize().ordinal());
		Label densityLabel = new Label("Map\nDensity", skin);
		densitySelect = new SelectBox<>(skin);
		String[] densities = { "Dense", "Medium", "Loose" };
		densitySelect.setItems(densities);
		densitySelect.setSelectedIndex(prefs.getDensity().ordinal());
		Label seedLabel = new Label("Seed", skin);
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

	/**
	 * Registers an event listener to an event type.
	 * 
	 * @param type     event type to listen to
	 * @param listener listener to execute
	 */
	public void registerEventListener(EventTypes type, Runnable listener) {
		Collection<Actor> uiElements = new HashSet<>();
		switch (type) {
		case CHANGE:
			uiElements.add(difficultySelect);
			uiElements.add(sizeSelect);
			uiElements.add(densitySelect);
			break;
		case REGEN:
			regenListeners.add(listener);
			uiElements.add(seedTextField);
			uiElements.add(randomButton);
			uiElements.add(sizeSelect);
			uiElements.add(densitySelect);
			break;
		case PLAY:
			uiElements.add(playButton);
			break;
		default:
			break;
		}
		for (Actor uiElement : uiElements) {
			uiElement.addListener(new ChangeListener() {
				@Override
				public void changed(ChangeEvent event, Actor actor) {
					listener.run();
				}
			});
		}
	}

	/** Regenerates the map. */
	public void regenerateMap() {
		regenerateMap(null);
	}

	/**
	 * Regenerates the map.
	 * 
	 * @param seed map seed to use
	 */
	public void regenerateMap(Long seed) {
		if (seed != null) {
			seedTextField.setText(seed.toString());
		}
		regenListeners.forEach(Runnable::run);
	}

	/**
	 * Getter for map seed.
	 * 
	 * @return map seed input by the user
	 */
	public Long getSeedParam() {
		try {
			return Long.valueOf(seedTextField.getText());
		} catch (NumberFormatException e) {
			return 0L;
		}
	}

	/**
	 * Getter for map size.
	 * 
	 * @return map size input by the user
	 */
	public MapSizes getMapSize() {
		return MapSizes.values()[sizeSelect.getSelectedIndex()];
	}

	/**
	 * Getter for map size.
	 * 
	 * @return map size input by the user converted to the amount of tiles
	 */
	public int getMapSizeParam() {
		return MapSizes.values()[sizeSelect.getSelectedIndex()].getAmountOfTiles();
	}

	/**
	 * Getter for map density.
	 * 
	 * @return map density input by the user
	 */
	public Densities getMapDensity() {
		return Densities.values()[densitySelect.getSelectedIndex()];
	}

	/**
	 * Getter for map density.
	 * 
	 * @return map density input by the user converted to int
	 */
	public float getMapDensityParam() {
		return Densities.values()[densitySelect.getSelectedIndex()].getDensityFloat();
	}

	public Intelligence getBotIntelligence() {
		return Intelligence.values()[difficultySelect.getSelectedIndex()];
	}

	/**
	 * Getter for bot intelligence.
	 * 
	 * @return bot intelligence input by the user
	 */
	public BotAi.Intelligence getBotIntelligenceParam() {
		switch (difficultySelect.getSelectedIndex()) {
		case 0:
			return BotAi.Intelligence.DUMB;
		case 1:
			return BotAi.Intelligence.MEDIUM;
		case 2:
			return BotAi.Intelligence.SMART;
		default:
			return BotAi.Intelligence.MEDIUM;
		}
	}

	@Override
	public void updateOnResize(int width, int height) {
		// VERY IMPORTANT!!! makes everything scale correctly on startup and going
		// fullscreen etc.; took me hours to find out
		rootTable.pack();
	}

	@Override
	public void reset() {
		// nothing to reset
	}

}

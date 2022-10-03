// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.ui.stages;

import java.util.stream.Stream;

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
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.backend.gamelogic.MapParameters;
import de.sesu8642.feudaltactics.backend.gamelogic.ingame.BotAi.Intelligence;
import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.events.moves.GameStartEvent;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MenuViewport;
import de.sesu8642.feudaltactics.frontend.persistence.NewGamePreferences;
import de.sesu8642.feudaltactics.frontend.persistence.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.frontend.persistence.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.frontend.persistence.NewGamePreferencesDao;

/**
 * {@link Stage} for displaying the input mask for a new game.
 */
public class ParameterInputStage extends ResizableResettableStage {

	// for map centering calculation
	public static final int NO_OF_INPUTS = 4;

	private EventBus eventBus;
	private NewGamePreferencesDao newGamePrefDao;
	private TextureAtlas textureAtlas;
	private Skin skin;

	private Table rootTable;
	private SelectBox<String> sizeSelect;
	private SelectBox<String> densitySelect;
	private SelectBox<String> difficultySelect;
	private ImageButton randomButton;
	private TextButton playButton;
	private TextField seedTextField;

	/**
	 * Constructor.
	 * 
	 * @param eventBus     event bus
	 * @param viewport     viewport for the stage
	 * @param textureAtlas texture atlas containing the button textures
	 * @param skin         game skin
	 */
	@Inject
	public ParameterInputStage(EventBus eventBus, NewGamePreferencesDao newGamePrefDao, @MenuViewport Viewport viewport,
			TextureAtlas textureAtlas, Skin skin) {
		super(viewport);
		this.eventBus = eventBus;
		this.newGamePrefDao = newGamePrefDao;
		this.textureAtlas = textureAtlas;
		this.skin = skin;
		initUi();
	}

	private void initUi() {
		// note about checktyle: widgets are declared in the order they appear in the UI
		NewGamePreferences prefs = newGamePrefDao.getNewGamePreferences();
		Label difficultyLabel = new Label("CPU\nDifficulty", skin);
		difficultySelect = new SelectBox<>(skin);
		String[] difficulties = { "Easy", "Medium  ", "Hard" };
		difficultySelect.setItems(difficulties);
		difficultySelect.setSelectedIndex(prefs.getBotIntelligence().ordinal());
		Label sizeLabel = new Label("Map\nSize", skin);
		sizeSelect = new SelectBox<>(skin);
		String[] sizes = { "Small", "Medium  ", "Large", "XLarge", "XXLarge" };
		sizeSelect.setItems(sizes);
		sizeSelect.setSelectedIndex(prefs.getMapSize().ordinal());
		Label densityLabel = new Label("Map\nDensity", skin);
		densitySelect = new SelectBox<>(skin);
		String[] densities = { "Dense", "Medium  ", "Loose" };
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

		registerEventListeners();
	}

	private void registerEventListeners() {

		randomButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				seedTextField.setText(String.valueOf(System.currentTimeMillis()));
			}
		});

		Stream.of(seedTextField, randomButton, difficultySelect, sizeSelect, densitySelect)
				.forEach(actor -> actor.addListener(new ChangeListener() {
					@Override
					public void changed(ChangeEvent event, Actor actor) {
						eventBus.post(new RegenerateMapEvent(getBotIntelligence(), new MapParameters(getSeedParam(),
								getMapSizeParam().getAmountOfTiles(), getMapDensityParam().getDensityFloat())));
						newGamePrefDao.saveNewGamePreferences(
								new NewGamePreferences(getBotIntelligence(), getMapSizeParam(), getMapDensityParam()));
					}
				}));

		playButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				eventBus.post(new GameStartEvent());
			}
		});
	}

	/**
	 * Updates the seed.
	 * 
	 * @param seed map seed to display
	 */
	public void updateSeed(Long seed) {
		seedTextField.setText(seed.toString());
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

	public MapSizes getMapSizeParam() {
		return MapSizes.values()[sizeSelect.getSelectedIndex()];
	}

	public Densities getMapDensityParam() {
		return Densities.values()[densitySelect.getSelectedIndex()];
	}

	public Intelligence getBotIntelligence() {
		return Intelligence.values()[difficultySelect.getSelectedIndex()];
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

package com.sesu8642.feudaltactics.stages;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.SpriteDrawable;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.engine.BotAI;
import com.sesu8642.feudaltactics.engine.BotAI.Intelligence;
import com.sesu8642.feudaltactics.engine.NewGamePreferences;
import com.sesu8642.feudaltactics.engine.NewGamePreferences.Densities;
import com.sesu8642.feudaltactics.engine.NewGamePreferences.MapSizes;
import com.sesu8642.feudaltactics.engine.PreferencesHelper;

public class ParameterInputStage extends Stage {

	public static final int NO_OF_INPUTS = 4;
	
	public enum ActionUIElements {
		CHANGE, REGEN, PLAY
	}
	
	private Label seedLabel;
	private Label sizeLabel;
	private Label densityLabel;
	private Label difficultyLabel;
	private SelectBox<String> sizeSelect;
	private SelectBox<String> densitySelect;
	private SelectBox<String> difficultySelect;
	private TextField seedTextField;
	private Runnable regenAction;

	public ParameterInputStage(Map<ActionUIElements, Runnable> actions) {
		initUI(actions);
	}

	public ParameterInputStage(Viewport viewport, Map<ActionUIElements, Runnable> actions) {
		super(viewport);
		initUI(actions);
	}

	public ParameterInputStage(Viewport viewport, Batch batch, Map<ActionUIElements, Runnable> actions) {
		super(viewport, batch);
		initUI(actions);
	}

	private void initUI(Map<ActionUIElements, Runnable> actions) {
		NewGamePreferences prefs = PreferencesHelper.getNewGamePreferences();
		difficultyLabel = new Label("CPU\nDifficulty", FeudalTactics.skin);
		difficultySelect = new SelectBox<String>(FeudalTactics.skin);
		String[] difficulties = { "Easy", "Medium", "Hard" };
		difficultySelect.setItems(difficulties);
		difficultySelect.setSelectedIndex(prefs.getBotIntelligence().ordinal());
		sizeLabel = new Label("Map\nSize", FeudalTactics.skin);
		sizeSelect = new SelectBox<String>(FeudalTactics.skin);
		String[] sizes = { "Small", "Medium", "Large" };
		sizeSelect.setItems(sizes);
		sizeSelect.setSelectedIndex(prefs.getMapSize().ordinal());
		densityLabel = new Label("Map\nDensity", FeudalTactics.skin);
		densitySelect = new SelectBox<String>(FeudalTactics.skin);
		String[] densities = { "Dense", "Medium", "Loose" };
		densitySelect.setItems(densities);
		densitySelect.setSelectedIndex(prefs.getDensity().ordinal());
		seedLabel = new Label("Seed", FeudalTactics.skin);
		seedTextField = new TextField(String.valueOf(System.currentTimeMillis()), FeudalTactics.skin);
		seedTextField.setTextFieldFilter(new DigitsOnlyFilter());
		seedTextField.setMaxLength(18);
		ImageButton randomButton = new ImageButton(new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("die")),
				new SpriteDrawable(FeudalTactics.textureAtlas.createSprite("die_pressed")));
		randomButton.getImageCell().expand().fill();
		TextButton playButton = new TextButton("Play", FeudalTactics.skin);

		Table rootTable = new Table();
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
		rootTable.add(randomButton).height(Value.percentHeight(1, seedTextField)).width(Value.percentHeight(1)).padLeft(10);
		rootTable.row();
		rootTable.add(playButton).colspan(4).fillX();
		this.addActor(rootTable);

		// add actions
		randomButton.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				seedTextField.setText(String.valueOf(System.currentTimeMillis()));
			}
		});
		for (Entry<ActionUIElements, Runnable> action : actions.entrySet()) {
			Collection<Actor> uIElements = new HashSet<Actor>();
			switch (action.getKey()) {
			case CHANGE:
				uIElements.add(difficultySelect);
				uIElements.add(sizeSelect);
				uIElements.add(densitySelect);
				break;
			case REGEN:
				regenAction = action.getValue();
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
						action.getValue().run();
					}
				});
			}
		}
	}

	public void setFontScale(Float fontScale) {
		Label[] labels = { sizeLabel, densityLabel, difficultyLabel };
		for (Label label : labels) {
			label.setFontScale(fontScale);
		}
	}

	public void regenerateMap(Long seed) {
		if (seed != null) {
			seedTextField.setText(seed.toString());
		}
		regenAction.run();
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

}

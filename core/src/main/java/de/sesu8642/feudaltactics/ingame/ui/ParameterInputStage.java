// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter;
import com.badlogic.gdx.utils.viewport.Viewport;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.Densities;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences.MapSizes;
import de.sesu8642.feudaltactics.lib.ingame.botai.Intelligence;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ButtonFactory;
import de.sesu8642.feudaltactics.menu.common.ui.ResizableResettableStage;
import de.sesu8642.feudaltactics.menu.common.ui.SkinConstants;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

import javax.inject.Inject;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@link Stage} for displaying the input mask for a new game.
 */
public class ParameterInputStage extends ResizableResettableStage {

    private static final int OUTER_PADDING_PX = 10;
    private static final int INPUT_PADDING_PX = 20;
    private final PlatformInsetsProvider platformInsetsProvider;
    private final Skin skin;
    private final LocalizationManager localizationManager;
    SelectBox<String> startingPositionSelect;
    SelectBox<String> sizeSelect;
    SelectBox<String> densitySelect;
    SelectBox<String> difficultySelect;
    ImageButton randomButton;
    ImageTextButton copyButton;
    ImageButton pasteButton;
    TextButton backButton;
    TextButton playButton;
    TextField seedTextField;
    private Table rootTable;
    private Table seedTable;

    /**
     * Constructor.
     *
     * @param viewport viewport for the stage
     * @param skin     game skin
     */
    @Inject
    public ParameterInputStage(@MenuViewport Viewport viewport, PlatformInsetsProvider platformInsetsProvider,
                               Skin skin, LocalizationManager localizationManager) {
        super(viewport);
        this.platformInsetsProvider = platformInsetsProvider;
        this.skin = skin;
        this.localizationManager = localizationManager;
        initUi();
    }

    private void initUi() {
        final Label startingPositionLabel = new Label(localizationManager.localizeText("starting-position")
            .replace(" ", "\n"),
            skin.get(SkinConstants.FONT_OVERLAY, LabelStyle.class));
        startingPositionSelect = new SelectBox<>(skin, SkinConstants.SELECT_BOX_STYLE_COLOR_SELECT);

        updateNumberOfStartingPositions(MapRenderer.PLAYER_COLOR_PALETTE.size());

        final Label difficultyLabel = new Label(localizationManager.localizeText("cpu-difficulty")
            .replace(" ", "\n"), skin.get(SkinConstants.FONT_OVERLAY,
            LabelStyle.class));
        difficultySelect = new SelectBox<>(skin);
        difficultySelect.setItems(localizationManager.localizeTextBatch(EnumDisplayNameConverter.DIFFICULTIES).toArray(new String[0]));

        final Label sizeLabel = new Label(localizationManager.localizeText("map-size")
            .replace(" ", "\n"), skin.get(SkinConstants.FONT_OVERLAY, LabelStyle.class));
        sizeSelect = new SelectBox<>(skin);
        sizeSelect.setItems(localizationManager.localizeTextBatch(EnumDisplayNameConverter.MAP_SIZES).toArray(new String[0]));

        final Label densityLabel = new Label(localizationManager.localizeText("map-density")
            .replace(" ", "\n"), skin.get(SkinConstants.FONT_OVERLAY, LabelStyle.class));
        densitySelect = new SelectBox<>(skin);
        densitySelect.setItems(localizationManager.localizeTextBatch(EnumDisplayNameConverter.DENSITIES).toArray(new String[0]));

        final Label seedLabel = new Label(localizationManager.localizeText("seed"),
            skin.get(SkinConstants.FONT_OVERLAY, LabelStyle.class));
        seedTextField = new TextField("", skin);
        seedTextField.setTextFieldFilter(new DigitsOnlyFilter());
        seedTextField.setMaxLength(18);

        randomButton = ButtonFactory.createImageButton(SkinConstants.BUTTON_DIE, skin);
        randomButton.getImageCell().expand().fill();

        copyButton = ButtonFactory.createCopyButton("", skin, true);
        copyButton.getImageCell().expand().fill();

        pasteButton = ButtonFactory.createImageButton(SkinConstants.BUTTON_PASTE, skin);
        pasteButton.getImageCell().expand().fill();

        /*
         * The longest text on the screen is the seed text field. It allows for 18
         * characters at max and 7 is the widest number. Scrolling the text is possible.
         * Generated seeds are normally much shorter than the worst case (only 7s).
         */
        final float maxSeedNumberWidth = new GlyphLayout(seedTextField.getStyle().font, "7").width;
        final float seedTextFieldWidth = maxSeedNumberWidth * 20;

        seedTable = new Table();
        seedTable.defaults().uniformX();
        seedTable.add(seedTextField).colspan(2).fill().expand();
        seedTable.add(randomButton).height(Value.percentHeight(1, difficultySelect)).width(Value.percentHeight(1,
            difficultySelect));

        backButton = ButtonFactory.createTextButton(localizationManager.localizeText("back"), skin);
        playButton = ButtonFactory.createTextButton(localizationManager.localizeText("play"), skin);

        final Table buttonTable = new Table();
        buttonTable.add(backButton).expandX().fill();
        buttonTable.add(playButton).expandX().fill();

        rootTable = new Table();
        rootTable.padTop(platformInsetsProvider.getInsets(Gdx.app).getTopInset());
        rootTable.padBottom(platformInsetsProvider.getInsets(Gdx.app).getBottomInset());
        rootTable.defaults().left().pad(INPUT_PADDING_PX / 2F, 0, INPUT_PADDING_PX / 2F, 0);
        rootTable.columnDefaults(0).pad(0, OUTER_PADDING_PX, 0, OUTER_PADDING_PX);
        rootTable.setFillParent(true);
        rootTable.add().expandY();
        rootTable.row();
        rootTable.add(seedLabel);
        rootTable.add(seedTable).minWidth(seedTextFieldWidth).fillX();
        rootTable.add().expandX();
        rootTable.row();
        rootTable.add(startingPositionLabel);
        rootTable.add(startingPositionSelect).fillX();
        rootTable.row();
        rootTable.add(difficultyLabel);
        rootTable.add(difficultySelect).fillX();
        rootTable.row();
        rootTable.add(sizeLabel);
        rootTable.add(sizeSelect).fillX();
        rootTable.add(copyButton).right().padLeft(OUTER_PADDING_PX).padRight(OUTER_PADDING_PX).height(Value.percentHeight(1, difficultySelect)).width(Value.percentHeight(1));
        rootTable.row();
        rootTable.add(densityLabel);
        rootTable.add(densitySelect).fillX();
        rootTable.add(pasteButton).right().padLeft(OUTER_PADDING_PX).padRight(OUTER_PADDING_PX).height(Value.percentHeight(1, difficultySelect)).width(Value.percentHeight(1));
        rootTable.row();
        rootTable.add(buttonTable).colspan(3).fillX().pad(INPUT_PADDING_PX / 2F, OUTER_PADDING_PX, OUTER_PADDING_PX,
            OUTER_PADDING_PX);
        addActor(rootTable);
    }

    /**
     * @param numberOfStartingPositions number of starting positions to offer. Cutting the ones that are the last
     *                                  ones in the list.
     */
    public void updateNumberOfStartingPositions(int numberOfStartingPositions) {
        // markup must be enabled in the font for this coloring to work
        // h is a hexagon character in the font
        final List<String> startingPositions = MapRenderer.PLAYER_COLOR_PALETTE.stream()
            .map(color -> String.format("[#%s]hhh", color.toString())).collect(Collectors.toList());
        final List<String> limitedStartingPositions = startingPositions.subList(0, numberOfStartingPositions);
        startingPositionSelect.setItems(limitedStartingPositions.toArray(new String[0]));
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

    public int getStartingPosition() {
        return startingPositionSelect.getSelectedIndex();
    }

    /**
     * Returns the vertical distance above the inputs.
     */
    public float getAvailableHeightAboveInputs() {
        return Gdx.graphics.getHeight() - seedTable.getY() - seedTable.getHeight();
    }

    /**
     * Returns the horizontal distance available above the inputs.
     */
    public float getAvailableWidthAboveInputs() {
        return Gdx.graphics.getWidth();
    }

    /**
     * Returns the vertical distance available next to the inputs.
     */
    public float getAvailableHeightNextToInputs() {
        return Gdx.graphics.getHeight() - playButton.getY() - playButton.getHeight();
    }

    /**
     * Returns the horizontal distance available next to the inputs.
     */
    public float getAvailableWidthNextToInputs() {
        return Gdx.graphics.getWidth() - sizeSelect.getWidth() - sizeSelect.getX();
    }

    /**
     * Returns the bottom left point of empty space next to the inputs.
     */
    public Vector2 getAreaNextToInputsBottomLeftPoint() {
        return new Vector2(sizeSelect.getX() + sizeSelect.getWidth(), playButton.getY() + playButton.getHeight());
    }

    @Override
    public void updateOnResize(int width, int height) {
        rootTable.padTop(platformInsetsProvider.getInsets(Gdx.app).getTopInset());
        rootTable.padBottom(platformInsetsProvider.getInsets(Gdx.app).getBottomInset());
        if (rootTable.getWidth() == 0) {
            // packing the root table so the buttons take their correct square shape initially
            rootTable.pack();
        }
    }

    @Override
    public void reset() {
        // nothing to reset
    }

}

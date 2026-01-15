// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Label.LabelStyle;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldFilter.DigitsOnlyFilter;
import com.badlogic.gdx.utils.viewport.Viewport;
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

    /**
     * Outer padding around all the inputs.
     */
    public static final int OUTER_PADDING_PX = 10;
    /**
     * Height of the play button.
     */
    public static final long BUTTON_HEIGHT_PX = 85;

    // for map centering calculation
    /**
     * Padding below all the inputs.
     */
    public static final long BOTTOM_PADDING_PX = 11;
    /**
     * Width of all parameter inputs combined; depends on label texts and used font.
     */
    public static final long TOTAL_INPUT_WIDTH = 457;
    private static final long INPUT_HEIGHT_PX = 74;
    private static final int INPUT_PADDING_PX = 20;
    /**
     * Height of all parameter inputs combined.
     */
    public static final long TOTAL_INPUT_HEIGHT = OUTER_PADDING_PX + 5 * INPUT_PADDING_PX + BUTTON_HEIGHT_PX
        + 5 * INPUT_HEIGHT_PX;
    private final PlatformInsetsProvider platformInsetsProvider;
    private final Skin skin;
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

    /**
     * Constructor.
     *
     * @param viewport viewport for the stage
     * @param skin     game skin
     */
    @Inject
    public ParameterInputStage(@MenuViewport Viewport viewport, PlatformInsetsProvider platformInsetsProvider,
                               Skin skin) {
        super(viewport);
        this.platformInsetsProvider = platformInsetsProvider;
        this.skin = skin;
        initUi();
    }

    private void initUi() {
        final Label startingPositionLabel = new Label("Starting\nPosition",
            skin.get(SkinConstants.FONT_OVERLAY, LabelStyle.class));
        startingPositionSelect = new SelectBox<>(skin, SkinConstants.SELECT_BOX_STYLE_COLOR_SELECT);

        updateNumberOfStartingPositions(MapRenderer.PLAYER_COLOR_PALETTE.size());

        final Label difficultyLabel = new Label("CPU\nDifficulty", skin.get(SkinConstants.FONT_OVERLAY,
            LabelStyle.class));
        difficultySelect = new SelectBox<>(skin);
        difficultySelect.setItems(EnumDisplayNameConverter.DIFFICULTIES.toArray(new String[0]));

        final Label sizeLabel = new Label("Map\nSize", skin.get(SkinConstants.FONT_OVERLAY, LabelStyle.class));
        sizeSelect = new SelectBox<>(skin);
        sizeSelect.setItems(EnumDisplayNameConverter.MAP_SIZES.toArray(new String[0]));

        final Label densityLabel = new Label("Map\nDensity", skin.get(SkinConstants.FONT_OVERLAY, LabelStyle.class));
        densitySelect = new SelectBox<>(skin);
        densitySelect.setItems(EnumDisplayNameConverter.DENSITIES.toArray(new String[0]));

        final Label seedLabel = new Label("Seed", skin.get(SkinConstants.FONT_OVERLAY, LabelStyle.class));
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

        final Table seedTable = new Table();
        seedTable.defaults().uniformX();
        seedTable.add(seedTextField).colspan(2).fill().expand();
        seedTable.add(randomButton).height(INPUT_HEIGHT_PX).width(INPUT_HEIGHT_PX);

        backButton = ButtonFactory.createTextButton("Back", skin);
        playButton = ButtonFactory.createTextButton("Play", skin);

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
        rootTable.add(startingPositionSelect).fillX().minHeight(INPUT_HEIGHT_PX);
        rootTable.row();
        rootTable.add(difficultyLabel);
        rootTable.add(difficultySelect).fillX();
        rootTable.row();
        rootTable.add(sizeLabel);
        rootTable.add(sizeSelect).fillX();
        rootTable.add(copyButton).right().padLeft(OUTER_PADDING_PX).padRight(OUTER_PADDING_PX).height(INPUT_HEIGHT_PX)
            .width(INPUT_HEIGHT_PX);
        rootTable.row();
        rootTable.add(densityLabel);
        rootTable.add(densitySelect).fillX();
        rootTable.add(pasteButton).right().padLeft(OUTER_PADDING_PX).padRight(OUTER_PADDING_PX).height(INPUT_HEIGHT_PX)
            .width(INPUT_HEIGHT_PX);
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

    @Override
    public void updateOnResize(int width, int height) {
        rootTable.padTop(platformInsetsProvider.getInsets(Gdx.app).getTopInset());
        rootTable.padBottom(platformInsetsProvider.getInsets(Gdx.app).getBottomInset());
        // VERY IMPORTANT!!! makes everything scale correctly on startup and going
        // fullscreen etc.; took me hours to find out
        rootTable.pack();
    }

    @Override
    public void reset() {
        // nothing to reset
    }

}

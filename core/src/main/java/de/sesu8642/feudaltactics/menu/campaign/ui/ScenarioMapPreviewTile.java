// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.campaign.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioGameStateLoader;
import de.sesu8642.feudaltactics.lib.gamestate.ScenarioMap;
import de.sesu8642.feudaltactics.menu.common.ui.MapPreviewFactory;
import de.sesu8642.feudaltactics.menu.common.ui.MapPreviewWidget;
import de.sesu8642.feudaltactics.menu.common.ui.SkinConstants;

import static de.sesu8642.feudaltactics.menu.common.ui.UiScalingConstants.UI_SCALING_FACTOR;

/**
 * UI for previewing a scenario map and displaying some metadata.
 */
public class ScenarioMapPreviewTile extends Container<Actor> {

    private static final int BORDER_WIDTH = (int) (Gdx.graphics.getDensity() * 5 * UI_SCALING_FACTOR);
    private static final int OUTER_PAD = (int) (Gdx.graphics.getDensity() * 10);
    private final Drawable borderDrawable;
    private final Drawable backgroundDrawable;
    private MapPreviewWidget mapPreviewWidget;


    /**
     * Constructor.
     */
    public ScenarioMapPreviewTile(ScenarioMap scenarioMap, ScenarioGameStateLoader scenarioGameStateLoader,
                                  MapPreviewFactory mapPreviewFactory, boolean unlocked, Medals bestMedal,
                                  int displayIndex, Skin skin) {
        final GameState gameState = scenarioGameStateLoader.loadScenarioGameState(scenarioMap);
        borderDrawable = skin.newDrawable(SkinConstants.DRAWABLE_WHITE, Color.BLACK);
        backgroundDrawable = skin.newDrawable(SkinConstants.DRAWABLE_WHITE, skin.getColor(SkinConstants.COLOR_FIELD));
        final String caption = String.format("#%s %s", displayIndex, scenarioMap.displayName);
        initUi(mapPreviewFactory, gameState, caption, unlocked, bestMedal, skin);
    }

    private static void addLockOverlay(Skin skin, Stack mapPreviewStack) {
        final Drawable lockOverlay = skin.newDrawable(SkinConstants.DRAWABLE_LOCK,
            skin.getColor(SkinConstants.COLOR_HIGHLIGHT2));
        final Image lockOverlayImage = new Image(lockOverlay);
        mapPreviewStack.add(lockOverlayImage);

        final Drawable darkeningOverlay = skin.newDrawable(SkinConstants.DRAWABLE_WHITE, new Color(0, 0, 0, 0.6F));
        final Image darkeningOverlayImage = new Image(darkeningOverlay);
        mapPreviewStack.add(darkeningOverlayImage);
    }

    private static void addMedalOverlay(Medals bestMedal, Skin skin, Stack mapPreviewStack) {
        final Drawable medalDrawable = skin.newDrawable(SkinConstants.DRAWABLE_MEDAL);
        // using a table instead of a horizontal group because the latter cannot scale the image
        final Table medalOverlayTable = new Table();
        medalOverlayTable.add().colspan(5).expand().row();
        medalOverlayTable.defaults().pad(Gdx.graphics.getDensity() * 10).height(Value.percentHeight(0.15F,
            medalOverlayTable)).width(Value.percentHeight(0.15F, medalOverlayTable));

        switch (bestMedal) {
            // fall-through intentional
            case TROPHY:
                final Drawable trophyDrawable = skin.newDrawable(SkinConstants.DRAWABLE_TROPHY);
                final Image trophyImage = new Image(trophyDrawable);
                medalOverlayTable.add(trophyImage);
            case GOLD:
                final Image goldMedalImage = createMedalImage(skin, medalDrawable, SkinConstants.COLOR_GOLD);
                medalOverlayTable.add(goldMedalImage);
            case SILVER:
                final Image silverMedalImage = createMedalImage(skin, medalDrawable, SkinConstants.COLOR_SILVER);
                medalOverlayTable.add(silverMedalImage);
            case BRONCE:
                final Image bronceMedalImage = createMedalImage(skin, medalDrawable, SkinConstants.COLOR_BRONCE);
                medalOverlayTable.add(bronceMedalImage);
            case NONE:
                break;
            default:
                throw new IllegalStateException("Unknown Medal " + bestMedal);
        }

        medalOverlayTable.add().expandX();
        mapPreviewStack.add(medalOverlayTable);
    }

    private static Image createMedalImage(Skin skin, Drawable medalDrawable, String colorName) {
        final Drawable tintedMedalDrawable = skin.newDrawable(medalDrawable, skin.getColor(colorName));
        return new Image(tintedMedalDrawable);
    }

    private void initUi(MapPreviewFactory mapPreviewFactory, GameState gameState, String title, boolean unlocked,
                        Medals bestMedal, Skin skin) {
        mapPreviewWidget = mapPreviewFactory.createPreviewWidget(gameState);

        setBackground(borderDrawable);
        pad(BORDER_WIDTH);
        fill();

        final Table contentTable = new Table();
        contentTable.background(backgroundDrawable);
        setActor(contentTable);

        final Stack mapPreviewStack = new Stack();
        mapPreviewStack.add(mapPreviewWidget);

        if (unlocked) {
            addMedalOverlay(bestMedal, skin, mapPreviewStack);
        } else {
            addLockOverlay(skin, mapPreviewStack);
        }

        contentTable.defaults().pad(0, OUTER_PAD, 0, OUTER_PAD);

        contentTable.add(mapPreviewStack).padTop(OUTER_PAD).height(Value.percentWidth(1)).expand().fill();
        contentTable.row();

        final Label titleLabel = new Label(title, skin);
        titleLabel.setWrap(true);
        contentTable.add(titleLabel).padBottom(OUTER_PAD).fillX();
    }

    @Override
    protected void sizeChanged() {
        final float mapPreviewSize = getWidth() - 2 * OUTER_PAD - 2 * BORDER_WIDTH;
        mapPreviewWidget.setSize(mapPreviewSize, mapPreviewSize);
        super.sizeChanged();
    }

}

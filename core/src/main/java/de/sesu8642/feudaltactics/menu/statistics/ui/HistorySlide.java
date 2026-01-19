// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.EnumMap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.ui.EnumDisplayNameConverter;
import de.sesu8642.feudaltactics.menu.common.ui.ButtonFactory;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.SkinConstants;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;
import de.sesu8642.feudaltactics.menu.statistics.HistoricGame;
import de.sesu8642.feudaltactics.menu.statistics.HistoryDao;
import de.sesu8642.feudaltactics.menu.statistics.HistoricGame.GameResult;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

/**
 * Represents the slide for displaying game history.
 */
@Singleton
public class HistorySlide extends Slide {

    private final HistoryDao historyDao;
    private final Skin skin;
    private final Table historyTable;
    private final EnumMap<HistoricGame.GameResult, Drawable> resultBackgrounds;

    /**
     * Constructor.
     *
     * @param skin       game skin
     * @param historyDao data access object for game history
     */
    @Inject
    public HistorySlide(Skin skin, HistoryDao historyDao) {
        super(skin, "Game History");
        this.historyDao = historyDao;
        this.skin = skin;
        this.historyTable = new Table();
        this.resultBackgrounds = new EnumMap<>(HistoricGame.GameResult.class);
        getTable().add(historyTable).fill().expand();
        refreshHistory();
    }

    /*
     * Places a single history entry into the history table.
     */
    private void placeHistoryEntry(HistoricGame game, int index) {
        // Game number
        final Label indexLabel = new Label(String.format("#%d", index + 1), skin);
        historyTable.add(indexLabel).left().padRight(10);

        // Result
        final Container<Label> resultCell = createResultCell(game.getGameResult());
        final Label turnsLabel = new Label("in turn " +String.valueOf(game.getTurnsPlayed()), skin);
        final Table resultTable = new Table();
        resultTable.add(resultCell).left();
        resultTable.row();
        resultTable.add(turnsLabel).left();
        historyTable.add(resultTable).left().padRight(10);

        // Settings
        String settingsString = "Unknown settings";
        String hexagonString = "[#808080]h"; // Gray hexagon for unknown
        NewGamePreferences gamePreferences = game.getGameSettings();
        if (gamePreferences != null) {
            String difficulty = gamePreferences.getBotIntelligence() != null
                    ? EnumDisplayNameConverter.getDisplayName(gamePreferences.getBotIntelligence())
                    : "Unknown";
            String mapSize = gamePreferences.getMapSize() != null
                    ? EnumDisplayNameConverter.getDisplayName(gamePreferences.getMapSize())
                    : "Unknown";
            String mapDensity = gamePreferences.getDensity() != null
                    ? EnumDisplayNameConverter.getDisplayName(gamePreferences.getDensity())
                    : "Unknown";
            settingsString = String.format("%s AI\n%s map, %s density",
                difficulty, mapSize, mapDensity);

            Color playerColor = MapRenderer.PLAYER_COLOR_PALETTE.get(gamePreferences.getStartingPosition());

                // markup must be enabled in the font for this coloring to work
                // h is a hexagon character in the font
            hexagonString = String.format("[#%s]h", playerColor.toString());
        }
        final Label settingsLabel = new Label(settingsString, skin);
        historyTable.add(settingsLabel).left().expandX();


        final Label startingPositionLabel = new Label(hexagonString, skin, SkinConstants.FONT_HEXAGON);
        historyTable.add(startingPositionLabel).left().expandX();

        // Copy settings button
        final ImageTextButton copyButton = ButtonFactory.createCopyButton("", skin, true);
        copyButton.getImageCell().expand().fill();
        if (gamePreferences != null) {
            copyButton.addListener(new ExceptionLoggingChangeListener(() ->
                Gdx.app.getClipboard().setContents(gamePreferences.toSharableString())));
        } else {
            copyButton.setDisabled(true);
        }
        historyTable.add(copyButton).left().height(74).width(74);

        historyTable.row();
        historyTable.add().height(10).colspan(3);
        historyTable.row();
    }

    private Container<Label> createResultCell(HistoricGame.GameResult result) {
        final Label resultLabel = new Label(result.toString(), skin);
        resultLabel.setColor(Color.BLACK);

        final Container<Label> container = new Container<>(resultLabel);
        container.background(getResultBackground(result));
        container.pad(4f, 8f, 4f, 8f);
        return container;
    }

    private Drawable getResultBackground(HistoricGame.GameResult result) {
        return resultBackgrounds.computeIfAbsent(result,
                r -> skin.newDrawable("white", gameResult2BackgroundColor(r)));
    }

    static private Color gameResult2BackgroundColor(HistoricGame.GameResult result)
    {
        switch (result) {
            case WIN: {
                return Color.GREEN;
            }
            case LOSS: {
                return Color.RED;
            }
            case ABORTED: {
                return Color.YELLOW;
            }
            default: {
                return Color.DARK_GRAY;
            }
        }
    }

    /**
     * Refreshes the history UI with the latest values.
     */
    public void refreshHistory() {
        historyTable.clear();
        HistoricGame[] history = historyDao.getGameHistory();

        if (history.length == 0) {
            final Label noHistoryLabel = new Label("No games recorded yet.", skin);
            noHistoryLabel.setWrap(true);
            historyTable.add(noHistoryLabel).fill().expand();
            return;
        }

        // placeHeading();
        long lastDateHeadingTimestamp = Long.MAX_VALUE;

        // Show most recent games first
        for (int i = history.length - 1; i >= 0; i--) {
            HistoricGame game = history[i];
            if (game.getTimestamp() / (1000L * 60L * 60L * 24L) < lastDateHeadingTimestamp / (1000L * 60L * 60L * 24L)) {
                // New day, add a heading
                lastDateHeadingTimestamp = game.getTimestamp();
                final Label dateHeading = new Label(
                        new java.text.SimpleDateFormat("yyyy-MM-dd").format(new java.util.Date(lastDateHeadingTimestamp)),
                        skin, SkinConstants.FONT_HEADLINE);
                historyTable.add(dateHeading).colspan(5).left().padTop(10).padBottom(5);
                historyTable.row();
            }
            placeHistoryEntry(game, i);
        }
    }
}

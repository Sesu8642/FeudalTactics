// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import de.sesu8642.feudaltactics.ingame.GameParameters;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.ingame.ui.EnumDisplayNameConverter;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.menu.common.ui.*;
import de.sesu8642.feudaltactics.menu.statistics.HistoricGame;
import de.sesu8642.feudaltactics.menu.statistics.HistoryDao;
import de.sesu8642.feudaltactics.renderer.MapRenderer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.EnumMap;

/**
 * Represents the slide for displaying game history.
 */
@Singleton
public class HistorySlide extends Slide {

    private static final int MAX_DISPLAYED_GAMES = 100;
    private final Skin skin;
    private final HistoryDao historyDao;
    private final MapPreviewFactory mapPreviewFactory;
    private final Table historyTable;
    private final EnumMap<HistoricGame.GameResult, Drawable> resultBackgrounds;
    private final Drawable rowBorderDrawable;
    private final Drawable rowBackgroundDrawable;
    private final DateTimeFormatter localDateTimeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.MEDIUM);

    /**
     * Constructor.
     *
     * @param skin       game skin
     * @param historyDao data access object for game history
     */
    @Inject
    public HistorySlide(Skin skin, HistoryDao historyDao, MapPreviewFactory mapPreviewFactory) {
        super(skin, "Game History");
        this.skin = skin;
        this.historyDao = historyDao;
        this.mapPreviewFactory = mapPreviewFactory;
        historyTable = new Table();
        resultBackgrounds = new EnumMap<>(HistoricGame.GameResult.class);
        rowBorderDrawable = skin.newDrawable(SkinConstants.DRAWABLE_WHITE, Color.BLACK);
        rowBackgroundDrawable = skin.newDrawable(SkinConstants.DRAWABLE_WHITE,
            skin.getColor(SkinConstants.COLOR_FIELD));
        getTable().add(historyTable).fill().expand();
        refreshHistory();
    }

    private static Color gameResult2BackgroundColor(HistoricGame.GameResult result) {
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

    /*
     * Places a single history entry into the history table.
     */
    private void placeHistoryEntry(HistoricGame game) {
        // Create table for the actual content
        final Table contentTable = new Table();
        contentTable.pad(10);
        contentTable.background(rowBackgroundDrawable);

        // Map preview
        final GameParameters gameParams = game.getGameSettings().toGameParameters();
        final GameState gameStateToPreview = new GameState();
        GameStateHelper.initializeMap(gameStateToPreview, gameParams.getPlayers(), gameParams.getLandMass(),
            gameParams.getDensity(), null, gameParams.getSeed());
        final MapPreviewWidget previewWidget = mapPreviewFactory.createPreviewWidget(gameStateToPreview);
        contentTable.add(previewWidget).minSize(100).fill().prefWidth(Value.percentHeight(1));

        // Result
        final Container<Label> resultCell = createResultCell(game.getGameResult());
        final Label roundsLabel = new Label("in round " + game.getRoundsPlayed(), skin);
        final Table resultTable = new Table();
        resultTable.add(resultCell).left();
        resultTable.row();
        resultTable.add(roundsLabel).left();
        contentTable.add(resultTable).left().width(200f).padRight(10).padLeft(10);

        // Settings
        String settingsString = "Unknown settings";
        String hexagonString = "[#808080]h"; // Gray hexagon for unknown
        final NewGamePreferences gamePreferences = game.getGameSettings();
        if (gamePreferences != null) {
            final String difficulty = gamePreferences.getBotIntelligence() != null
                ? EnumDisplayNameConverter.getDisplayName(gamePreferences.getBotIntelligence())
                : "Unknown";
            final String mapSize = gamePreferences.getMapSize() != null
                ? EnumDisplayNameConverter.getDisplayName(gamePreferences.getMapSize())
                : "Unknown";
            final String mapDensity = gamePreferences.getDensity() != null
                ? EnumDisplayNameConverter.getDisplayName(gamePreferences.getDensity())
                : "Unknown";
            settingsString = String.format("%s AI\n%s map\n%s density",
                difficulty, mapSize, mapDensity);

            final Color playerColor = MapRenderer.PLAYER_COLOR_PALETTE.get(gamePreferences.getStartingPosition());

            // markup must be enabled in the font for this coloring to work
            // h is a hexagon character in the font
            hexagonString = String.format("[#%s]h", playerColor.toString());
        }
        final Label settingsLabel = new Label(settingsString, skin);
        contentTable.add(settingsLabel).left().expandX();


        final Label startingPositionLabel = new Label(hexagonString, skin, SkinConstants.FONT_HEXAGON);
        startingPositionLabel.setFontScale(1.5f);
        contentTable.add(startingPositionLabel).left().padRight(20f);

        // Copy settings button
        final ImageTextButton copyButton = ButtonFactory.createCopyButton("", skin, true);
        copyButton.getImageCell().expand().fill();
        if (gamePreferences != null) {
            copyButton.addListener(new ExceptionLoggingChangeListener(() ->
                Gdx.app.getClipboard().setContents(gamePreferences.toSharableString())));
        } else {
            copyButton.setDisabled(true);
        }
        contentTable.add(copyButton).left().height(74).width(74);

        final Actor borderedRow = wrapInBorder(contentTable);

        // Add the container to the history table
        historyTable.row().padBottom(10f).padTop(10f);
        historyTable.add(borderedRow).fill().expandX().colspan(5);
    }

    private Actor wrapInBorder(Actor innerContent) {
        final Container<Actor> container = new Container<>(innerContent);
        container.background(rowBorderDrawable);
        container.pad(3); // Border width
        container.fill();
        return container;
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
            r -> skin.newDrawable(SkinConstants.DRAWABLE_WHITE, gameResult2BackgroundColor(r)));
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

        if (history.length > MAX_DISPLAYED_GAMES) {
            Gdx.app.log("HistorySlide",
                "Displaying only the most recent " + MAX_DISPLAYED_GAMES + " games out of " + history.length + " " +
                    "total games.");
            history = java.util.Arrays.copyOfRange(history, history.length - MAX_DISPLAYED_GAMES, history.length);
        }

        // Group by LocalDate instead of millis math
        LocalDate lastDateHeading = null;

        // Show most recent games first
        for (int i = history.length - 1; i >= 0; i--) {
            final HistoricGame game = history[i];

            final LocalDate gameDate = Instant.ofEpochMilli(game.getTimestamp())
                .atZone(ZoneId.systemDefault())
                .toLocalDate();

            if (!gameDate.equals(lastDateHeading)) {
                lastDateHeading = gameDate;
                final Label dateHeading = new Label(
                    localDateTimeFormatter.format(gameDate),
                    skin, SkinConstants.FONT_HEADLINE);
                historyTable.row();
                historyTable.add(dateHeading).colspan(4).left().padTop(10).padBottom(5);
            }

            placeHistoryEntry(game);
        }
    }
}

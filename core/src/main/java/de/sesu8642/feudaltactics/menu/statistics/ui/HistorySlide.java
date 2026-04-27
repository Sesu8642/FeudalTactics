// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.utils.Align;
import de.sesu8642.TranslationKeys;
import de.sesu8642.feudaltactics.ingame.GameParameters;
import de.sesu8642.feudaltactics.ingame.NewGamePreferences;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.localization.LocalizationManager;
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

import static de.sesu8642.feudaltactics.menu.common.ui.UiScalingConstants.*;

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
    private final LocalizationManager localizationManager;

    /**
     * Constructor.
     *
     * @param skin       game skin
     * @param historyDao data access object for game history
     */
    @Inject
    public HistorySlide(Skin skin, HistoryDao historyDao, MapPreviewFactory mapPreviewFactory,
                        LocalizationManager localizationManager) {
        super(skin, localizationManager.localizeText(TranslationKeys.HISTORY_PAGE_HEADLINE));
        this.skin = skin;
        this.historyDao = historyDao;
        this.mapPreviewFactory = mapPreviewFactory;
        this.localizationManager = localizationManager;
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
        // Create table as the container, specifying the background drawable
        final Table entryTable = new Table();
        entryTable.pad(Gdx.graphics.getDensity() * 20);
        entryTable.background(rowBackgroundDrawable);

        // Put a horizontal group inside for breaking on smaller screens
        final HorizontalGroup contentGroup = new HorizontalGroup();
        contentGroup.wrap();
        contentGroup.center();
        contentGroup.space(Gdx.graphics.getDensity() * 20);
        contentGroup.wrapSpace(Gdx.graphics.getDensity() * 20);
        entryTable.add(contentGroup).expand().fill();

        // Map preview
        final GameParameters gameParams = game.getGameSettings().toGameParameters();
        final GameState gameStateToPreview = new GameState();
        GameStateHelper.initializeMap(gameStateToPreview, gameParams.getPlayers(), gameParams.getLandMass(),
            gameParams.getDensity(), null, gameParams.getSeed());
        final MapPreviewWidget previewWidget = mapPreviewFactory.createPreviewWidget(gameStateToPreview);
        final float mapPreviewSize = Gdx.graphics.getDensity() * 200 * UI_SCALING_FACTOR;
        previewWidget.setSize(mapPreviewSize, mapPreviewSize);
        contentGroup.addActor(previewWidget);

        // Result
        final Container<Label> resultCell = createResultCell(game.getGameResult());
        final Label roundsLabel =
            new Label(localizationManager.localizeText(TranslationKeys.HISTORY_PAGE_GAME_RESULT_ROUND,
                game.getRoundsPlayed()), skin);
        roundsLabel.setAlignment(Align.center);
        final Table resultTable = new Table();
        resultTable.add(resultCell);
        resultTable.row();
        resultTable.add(roundsLabel).minWidth(Gdx.graphics.getDensity() * TEXT_SCALING_FACTOR * 400);
        contentGroup.addActor(resultTable);

        // Settings
        String settingsString = "";
        String hexagonString = "[#808080]h"; // Gray hexagon for unknown
        final NewGamePreferences gamePreferences = game.getGameSettings();
        if (gamePreferences != null) {
            settingsString = gamePreferencesToDisplayString(gamePreferences);

            final Color playerColor = MapRenderer.PLAYER_COLOR_PALETTE.get(gamePreferences.getStartingPosition());

            // markup must be enabled in the font for this coloring to work
            // h is a hexagon character in the font
            hexagonString = String.format("[#%s]h", playerColor.toString());
        }
        final Label settingsLabel = new Label(settingsString, skin);
        settingsLabel.setAlignment(Align.center);
        final Table settingsTable = new Table();
        settingsTable.add(settingsLabel).minWidth(Gdx.graphics.getDensity() * TEXT_SCALING_FACTOR * 500);
        contentGroup.addActor(settingsTable);

        final Label startingPositionLabel = new Label(hexagonString, skin, SkinConstants.FONT_HEXAGON);
        startingPositionLabel.setFontScale(HEXAGON_FONT_SCALING_FACTOR);
        contentGroup.addActor(startingPositionLabel);

        // Copy settings button
        final ImageTextButton copyButton = ButtonFactory.createCopyButton("", skin, true);
        copyButton.getImageCell().size(BUTTON_TEXT_SIZE);
        if (gamePreferences != null) {
            copyButton.addListener(new ExceptionLoggingChangeListener(() ->
                Gdx.app.getClipboard().setContents(gamePreferences.toSharableString(localizationManager))));
        } else {
            copyButton.setDisabled(true);
        }
        contentGroup.addActor(copyButton);

        final Actor borderedRow = wrapInBorder(entryTable);

        // Add the container to the history table
        historyTable.row().padBottom(Gdx.graphics.getDensity() * 20).padTop(Gdx.graphics.getDensity() * 20);
        historyTable.add(borderedRow).colspan(5).prefWidth(Gdx.graphics.getDensity() * 1500 * TEXT_SCALING_FACTOR);
    }

    private String gamePreferencesToDisplayString(NewGamePreferences gamePreferences) {
        final String settingsString;
        final String botIntelligenceTranslationKey;
        switch (gamePreferences.getBotIntelligence()) {
            case LEVEL_1:
                botIntelligenceTranslationKey = TranslationKeys.HISTORY_PAGE_PARAMETER_DIFFICULTY_EASY;
                break;
            case LEVEL_2:
                botIntelligenceTranslationKey = TranslationKeys.HISTORY_PAGE_PARAMETER_DIFFICULTY_MEDIUM;
                break;
            case LEVEL_3:
                botIntelligenceTranslationKey = TranslationKeys.HISTORY_PAGE_PARAMETER_DIFFICULTY_HARD;
                break;
            case LEVEL_4:
                botIntelligenceTranslationKey = TranslationKeys.HISTORY_PAGE_PARAMETER_DIFFICULTY_VERY_HARD;
                break;
            default:
                throw new IllegalStateException("Unknown bot intelligence " + gamePreferences.getBotIntelligence());
        }
        final String difficulty = localizationManager.localizeText(botIntelligenceTranslationKey);
        final String mapSizeTranslationKey;
        switch (gamePreferences.getMapSize()) {
            case SMALL:
                mapSizeTranslationKey = TranslationKeys.HISTORY_PAGE_PARAMETER_SIZE_SMALL;
                break;
            case MEDIUM:
                mapSizeTranslationKey = TranslationKeys.HISTORY_PAGE_PARAMETER_SIZE_MEDIUM;
                break;
            case LARGE:
                mapSizeTranslationKey = TranslationKeys.HISTORY_PAGE_PARAMETER_SIZE_LARGE;
                break;
            case XLARGE:
                mapSizeTranslationKey = TranslationKeys.HISTORY_PAGE_PARAMETER_SIZE_XLARGE;
                break;
            case XXLARGE:
                mapSizeTranslationKey = TranslationKeys.HISTORY_PAGE_PARAMETER_SIZE_XXLARGE;
                break;
            default:
                throw new IllegalStateException("Unknown map size " + gamePreferences.getMapSize());
        }
        final String mapSize = localizationManager.localizeText(mapSizeTranslationKey);
        final String mapDensityTranslationKey;
        switch (gamePreferences.getDensity()) {
            case DENSE:
                mapDensityTranslationKey = TranslationKeys.HISTORY_PAGE_PARAMETER_DENSITY_DENSE;
                break;
            case MEDIUM:
                mapDensityTranslationKey = TranslationKeys.HISTORY_PAGE_PARAMETER_DENSITY_MEDIUM;
                break;
            case LOOSE:
                mapDensityTranslationKey = TranslationKeys.HISTORY_PAGE_PARAMETER_DENSITY_LOOSE;
                break;
            default:
                throw new IllegalStateException("Unknown map density " + gamePreferences.getDensity());
        }
        final String mapDensity = localizationManager.localizeText(mapDensityTranslationKey);
        settingsString = String.format("%s\n%s\n%s", difficulty, mapSize, mapDensity);
        return settingsString;
    }

    private Actor wrapInBorder(Actor innerContent) {
        final Container<Actor> container = new Container<>(innerContent);
        container.background(rowBorderDrawable);
        container.pad(Gdx.graphics.getDensity() * 5 * UI_SCALING_FACTOR); // Border width
        container.fill();
        return container;
    }

    private Container<Label> createResultCell(HistoricGame.GameResult result) {
        final String gameResultKey;
        switch (result) {
            case WIN:
                gameResultKey = TranslationKeys.HISTORY_PAGE_GAME_RESULT_VICTORY;
                break;
            case LOSS:
                gameResultKey = TranslationKeys.HISTORY_PAGE_GAME_RESULT_LOSS;
                break;
            case ABORTED:
                gameResultKey = TranslationKeys.HISTORY_PAGE_GAME_RESULT_ABORTED;
                break;
            default:
                throw new IllegalStateException("Unknown game result " + result);
        }
        final String gameResultDisplayName = localizationManager.localizeText(gameResultKey);

        final Label resultLabel = new Label(gameResultDisplayName, skin);
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
            final Label noHistoryLabel =
                new Label(localizationManager.localizeText(TranslationKeys.HISTORY_PAGE_TEXT_NO_HISTORY), skin);
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

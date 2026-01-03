// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import java.util.EnumMap;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Container;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;

import de.sesu8642.feudaltactics.ingame.ui.EnumDisplayNameConverter;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;
import de.sesu8642.feudaltactics.menu.statistics.HistoricGame;
import de.sesu8642.feudaltactics.menu.statistics.HistoryDao;

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

    private void placeHistoryEntry(HistoricGame game, int index) {
        // Game number
        final Label indexLabel = new Label(String.format("#%d", index + 1), skin);
        historyTable.add(indexLabel).left().padRight(10);

        // Result
        final Container<Label> resultCell = createResultCell(game.getGameResult());
        historyTable.add(resultCell).left().padRight(10);

        // AI Difficulty
        String difficulty = game.getGameSettings() != null && game.getGameSettings().getBotIntelligence() != null
                ? EnumDisplayNameConverter.getDisplayName(game.getGameSettings().getBotIntelligence())
                : "Unknown";
        final Label difficultyLabel = new Label(difficulty, skin);
        historyTable.add(difficultyLabel).left().expandX();

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

    private void placeHeading() {
        final Label indexHeading = new Label("Game", skin);
        historyTable.add(indexHeading).left().padRight(10);

        final Label resultHeading = new Label("Result", skin);
        historyTable.add(resultHeading).left().padRight(10);

        final Label difficultyHeading = new Label("Difficulty", skin);
        historyTable.add(difficultyHeading).left().expandX();

        historyTable.row();
        historyTable.add().height(20).colspan(3);
        historyTable.row();
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

        placeHeading();

        // Show most recent games first
        for (int i = history.length - 1; i >= 0; i--) {
            placeHistoryEntry(history[i], i);
        }
    }
}

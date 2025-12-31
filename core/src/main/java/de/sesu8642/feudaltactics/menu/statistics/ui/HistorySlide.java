// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.statistics.ui;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

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
        getTable().add(historyTable).fill().expand();
        refreshHistory();
    }

    private void placeHistoryEntry(HistoricGame game, int index) {
        // Game number
        final Label indexLabel = new Label(String.format("#%d", index + 1), skin);
        historyTable.add(indexLabel).left().padRight(10);

        // Result
        final Label resultLabel = new Label(game.getGameResult().toString(), skin);
        historyTable.add(resultLabel).left().padRight(10);

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

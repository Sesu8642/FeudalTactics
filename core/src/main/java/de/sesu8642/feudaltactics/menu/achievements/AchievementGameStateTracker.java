// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.achievements;

import com.badlogic.gdx.Preferences;
import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.events.moves.BuyAndPlaceCastleEvent;
import de.sesu8642.feudaltactics.events.moves.BuyCastleEvent;
import de.sesu8642.feudaltactics.events.moves.GameStartEvent;
import de.sesu8642.feudaltactics.events.moves.UndoMoveEvent;
import de.sesu8642.feudaltactics.ingame.AutoSaveRepository;
import de.sesu8642.feudaltactics.lib.ingame.PlayerMove;
import de.sesu8642.feudaltactics.lib.ingame.PlayerMove.PlayerMoveType;
import de.sesu8642.feudaltactics.menu.achievements.dagger.AchievementsPrefStore;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Singleton tracker that monitors game events and persists achievement-related
 * statistics to preferences. This prevents multiple achievement instances from
 * redundantly tracking and persisting the same data.
 */
@Singleton
public class AchievementGameStateTracker {

    private static final String PREF_KEY_CASTLES_BUILT = "tracker-castles_built_in_current_game";

    private final Preferences achievementsPrefs;
    private final AutoSaveRepository autoSaveRepository;

    @Getter
    private int castlesBuiltInCurrentGame;

    @Inject
    public AchievementGameStateTracker(
            @AchievementsPrefStore Preferences achievementsPrefs,
            AutoSaveRepository autoSaveRepository) {
        this.achievementsPrefs = achievementsPrefs;
        this.autoSaveRepository = autoSaveRepository;
        
        // Load persisted value
        this.castlesBuiltInCurrentGame = achievementsPrefs.getInteger(PREF_KEY_CASTLES_BUILT, 0);
    }

    @Subscribe
    public void handleBuyCastleEvent(BuyCastleEvent event) {
        incrementCastlesBuilt();
    }

    @Subscribe
    public void handleBuyAndPlaceCastleEvent(BuyAndPlaceCastleEvent event) {
        incrementCastlesBuilt();
    }

    @Subscribe
    public void handleUndoMoveEvent(UndoMoveEvent event) {
        // Check whether the last move was a castle purchase
        if (autoSaveRepository.isUndoPossible()) {
            final PlayerMove lastMove = autoSaveRepository.peekLastMove();
            if (lastMove != null && (lastMove.getPlayerActionType() == PlayerMoveType.BUY_CASTLE ||
                    lastMove.getPlayerActionType() == PlayerMoveType.BUY_AND_PLACE_CASTLE)) {
                // User has undone a castle purchase
                decrementCastlesBuilt();
            }
        }
    }

    @Subscribe void handleGameStart(GameStartEvent event) {
        resetCastlesBuilt();
    }

    private void incrementCastlesBuilt() {
        castlesBuiltInCurrentGame++;
        persistCastlesBuilt();
    }

    private void decrementCastlesBuilt() {
        castlesBuiltInCurrentGame--;
        persistCastlesBuilt();
    }

    private void resetCastlesBuilt() {
        castlesBuiltInCurrentGame = 0;
        persistCastlesBuilt();
    }

    private void persistCastlesBuilt() {
        achievementsPrefs.putInteger(PREF_KEY_CASTLES_BUILT, castlesBuiltInCurrentGame);
        achievementsPrefs.flush();
    }
}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import lombok.Getter;
import lombok.Setter;

/**
 * Event: Map needs to be centered in the UI.
 */
public class CenterMapEvent {

    @Getter
    @Setter
    private GameState gameState;
    @Getter
    @Setter
    private long marginLeftPx;
    @Getter
    @Setter
    private long marginBottomPx;
    @Getter
    @Setter
    private long marginRightPx;
    @Getter
    @Setter
    private long marginTopPx;

    /**
     * Constructor.
     *
     * @param gameState game state to be centered
     */
    public CenterMapEvent(GameState gameState, long marginBottomPx, long marginLeftPx, long marginTopPx,
                          long marginRightPx) {
        this.gameState = gameState;
        this.marginLeftPx = marginLeftPx;
        this.marginBottomPx = marginBottomPx;
        this.marginRightPx = marginRightPx;
        this.marginTopPx = marginTopPx;
    }

}

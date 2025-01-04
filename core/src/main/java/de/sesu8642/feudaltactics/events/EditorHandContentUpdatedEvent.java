// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.lib.gamestate.TileContent;

/**
 * Event: The user changed the held object in the map editor.
 */
public class EditorHandContentUpdatedEvent {

    /**
     * Tile content for the hand, if a tile content is to be placed.
     */
    private TileContent heldTileContent;

    /**
     * Tile player index for the hand, if a tile is to be placed.
     */
    private Integer heldTilePlayerIndex;

    public EditorHandContentUpdatedEvent(TileContent heldTileContent) {
        this.heldTileContent = heldTileContent;
    }

    public EditorHandContentUpdatedEvent(Integer heldTilePlayerIndex) {
        this.heldTilePlayerIndex = heldTilePlayerIndex;
    }

    /**
     * Use this to go into delete mode.
     */
    public EditorHandContentUpdatedEvent() {
    }

    public TileContent getHeldTileContent() {
        return heldTileContent;
    }

    public Integer getHeldTilePlayerIndex() {
        return heldTilePlayerIndex;
    }
}


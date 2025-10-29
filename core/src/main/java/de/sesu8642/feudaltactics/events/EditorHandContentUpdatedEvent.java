// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

import de.sesu8642.feudaltactics.lib.gamestate.TileContent;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Event: The user changed the held object in the map editor.
 */
// Use the NoArgsConstructor to go into delete mode.
@NoArgsConstructor
public class EditorHandContentUpdatedEvent {

    /**
     * Tile content for the hand, if a tile content is to be placed.
     */
    @Getter
    private TileContent heldTileContent;

    /**
     * Tile player index for the hand, if a tile is to be placed.
     */
    @Getter
    private Integer heldTilePlayerIndex;

    public EditorHandContentUpdatedEvent(TileContent heldTileContent) {
        this.heldTileContent = heldTileContent;
    }

    public EditorHandContentUpdatedEvent(Integer heldTilePlayerIndex) {
        this.heldTilePlayerIndex = heldTilePlayerIndex;
    }
}


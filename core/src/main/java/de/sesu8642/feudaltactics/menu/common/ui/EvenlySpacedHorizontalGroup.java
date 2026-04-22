// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.scenes.scene2d.ui.HorizontalGroup;

/**
 * {@link HorizontalGroup} that spaces all items evenly (horizontally).
 */
public class EvenlySpacedHorizontalGroup extends HorizontalGroup {

    private final float itemWidth;
    private float minSpace = 0;

    /**
     * Constructor.
     */
    public EvenlySpacedHorizontalGroup(float itemWidth) {
        this.itemWidth = itemWidth;
    }

    @Override
    public void layout() {
        final float evenSpace = calculateHorizontalSpace();
        super.space(evenSpace);
        super.layout();
    }

    /**
     * Sets the minimum horizontal space between items.
     *
     * @return this
     */
    @Override
    public HorizontalGroup space(float space) {
        minSpace = space;
        return this;
    }

    private float calculateHorizontalSpace() {
        final float totalPad = getPadLeft() + getPadRight();
        final float availableWidth = getWidth() - totalPad;
        final int numberOfItemsPerFullRow = (int) Math.floor((availableWidth + minSpace) / (itemWidth + minSpace));
        final int numberOfGapsPerFullRow = numberOfItemsPerFullRow + 1;
        final float widthUsedUpByTiles = numberOfItemsPerFullRow * itemWidth;
        final float availableWidthSpace = availableWidth - widthUsedUpByTiles;
        final float availableWidthSpacePerGap = availableWidthSpace / numberOfGapsPerFullRow;
        return Math.max(availableWidthSpacePerGap, minSpace);
    }
}

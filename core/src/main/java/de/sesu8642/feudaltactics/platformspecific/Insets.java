// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.platformspecific;

import lombok.Data;

/**
 * Parameter object for display insets (e.g. display cutout, overlaying navigation bar).
 */
@Data
public class Insets {

    public static final Insets NONE = new Insets(0, 0, 0, 0);

    private final int topInset;

    private final int bottomInset;

    private final int leftInset;

    private final int rightInset;

}

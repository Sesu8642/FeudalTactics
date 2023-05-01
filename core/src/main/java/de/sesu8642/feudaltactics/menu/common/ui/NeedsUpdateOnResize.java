// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

/**
 * Interface to mark classes that need to be updated when a window resize
 * happens.
 */
public interface NeedsUpdateOnResize {

	void updateOnResize(int width, int height);

}

// SPDX-License-Identifier: GPL-3.0-or-later

package com.sesu8642.feudaltactics.ui;

/**
 * Interface to mark classes that need to be updated when a window resize
 * happens.
 */
public interface NeedsUpdateOnResize {

	void updateOnResize(int width, int height);

}

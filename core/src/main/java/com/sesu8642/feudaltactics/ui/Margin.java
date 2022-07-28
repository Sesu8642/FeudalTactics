// SPDX-License-Identifier: GPL-3.0-or-later

package com.sesu8642.feudaltactics.ui;

/** Value class for margins. */
public class Margin {

	public long marginLeft;
	public long marginBottom;
	public long marginRight;
	public long marginTop;

	/**
	 * Constructor.
	 * 
	 * @param marginLeft   left margin
	 * @param marginTop    top margin
	 * @param marginRight  right margin
	 * @param marginBottom bottom margin
	 */
	public Margin(long marginLeft, long marginBottom, long marginRight, long marginTop) {
		this.marginLeft = marginLeft;
		this.marginTop = marginTop;
		this.marginRight = marginRight;
		this.marginBottom = marginBottom;
	}

}

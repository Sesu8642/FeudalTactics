// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.ui;

/**
 * Value object: Preferences found in the main preferences menu, with default
 * values.
 **/
public class MainPreferences {

	private boolean warnAboutForgottenKingdoms = true;

	private boolean showEnemyTurns = true;

	/** Constructor for creating default preferences. */
	public MainPreferences() {
	}

	/** Constructor with parameters for all fields. */
	public MainPreferences(boolean warnAboutForgottenKingdoms, boolean showEnemyTurns) {
		this.warnAboutForgottenKingdoms = warnAboutForgottenKingdoms;
		this.showEnemyTurns = showEnemyTurns;
	}

	public boolean isWarnAboutForgottenKingdoms() {
		return warnAboutForgottenKingdoms;
	}

	public void setWarnAboutForgottenKingdoms(boolean warnAboutForgottenKingdoms) {
		this.warnAboutForgottenKingdoms = warnAboutForgottenKingdoms;
	}

	public boolean isShowEnemyTurns() {
		return showEnemyTurns;
	}

	public void setShowEnemyTurns(boolean showEnemyTurns) {
		this.showEnemyTurns = showEnemyTurns;
	}

}

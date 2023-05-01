// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences;

import java.util.Objects;

/** Value object: main preferences. */
public class MainGamePreferences {

	private boolean warnAboutForgottenKingdoms;
	private boolean showEnemyTurns;

	/**
	 * Constructor.
	 * 
	 * @param warnAboutForgottenKingdoms whether to display a warning about a
	 *                                   potentially forgotten kingdom when ending
	 *                                   the turn.
	 * @param showEnemyTurns             whether to visualize the enemies doing
	 *                                   their turns
	 */
	public MainGamePreferences(boolean warnAboutForgottenKingdoms, boolean showEnemyTurns) {
		super();
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

	@Override
	public int hashCode() {
		return Objects.hash(showEnemyTurns, warnAboutForgottenKingdoms);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MainGamePreferences other = (MainGamePreferences) obj;
		return showEnemyTurns == other.showEnemyTurns && warnAboutForgottenKingdoms == other.warnAboutForgottenKingdoms;
	}

}

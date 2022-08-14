// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.events;

/** Event: Screen transition triggered. */
public class ScreenTransitionTriggerEvent {

	/** Possible screens that can be transitioned to. */
	public enum ScreenTransitionTarget {
		SPLASH_SCREEN, MAIN_MENU_SCREEN, INGAME_SCREEN, EDITOR_SCREEN, TUTORIAL_SCREEN, ABOUT_SCREEN
	}

	private ScreenTransitionTarget transitionTarget;

	public ScreenTransitionTriggerEvent(ScreenTransitionTarget transitionTarget) {
		this.transitionTarget = transitionTarget;
	}

	public ScreenTransitionTarget getTransitionTarget() {
		return transitionTarget;
	}

	public void setTransitionTarget(ScreenTransitionTarget transitionTarget) {
		this.transitionTarget = transitionTarget;
	}

}

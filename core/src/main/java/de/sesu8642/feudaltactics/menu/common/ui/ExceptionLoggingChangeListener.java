// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener.ChangeEvent;

/**
 * Change listener that causes a proper crash that is logged if an unexpected
 * Exception happens.
 */
public class ExceptionLoggingChangeListener implements EventListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private final Runnable listener;

	public ExceptionLoggingChangeListener(Runnable listener) {
		this.listener = listener;
	}

	@Override
	public boolean handle(Event event) {
		if (!(event instanceof ChangeEvent)) {
			return false;
		}
		try {
			listener.run();
		} catch (Exception e) {
			logger.error("an unexpected exception happened in a change listener", e);
		}
		return false;
	}

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Click listener that causes a proper crash that is logged if an unexpected
 * Exception happens.
 */
public class ExceptionLoggingClickListener extends ClickListener {

	private final Logger logger = LoggerFactory.getLogger(this.getClass().getName());

	private final Runnable listener;

	/**
	 * Constructor.
	 *
	 * @param listener runnable to be executed on click
	 */
	public ExceptionLoggingClickListener(Runnable listener) {
		this.listener = listener;
	}

	@Override
	public void clicked(InputEvent event, float x, float y) {
		try {
			listener.run();
		} catch (Exception e) {
			logger.error("an unexpected exception happened in a cliock listener", e);
		}
	}
	
}

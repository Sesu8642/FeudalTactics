// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.ImageTextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

/**
 * Button with a copy icon and tick for feedback.
 */
public class CopyButton extends ImageTextButton {

	private ScheduledFuture<?> copyButtonFeedBackFuture;
	private final ScheduledExecutorService copyButtonFeedbackExecutorService = Executors
			.newSingleThreadScheduledExecutor(
					new ThreadFactoryBuilder().setNameFormat("copy-button-%d").setDaemon(true).build());

	/**
	 * Constructor.
	 */
	public CopyButton(String text, Skin skin) {
		super(text, skin.get(SkinConstants.BUTTON_COPY, ImageTextButtonStyle.class));
		this.addListener(new ExceptionLoggingChangeListener(() -> {
			// give feedback to the user by adding the tick for a moment
			this.setStyle(skin.get(SkinConstants.BUTTON_COPY_TICK, ImageTextButtonStyle.class));
			if (copyButtonFeedBackFuture != null) {
				copyButtonFeedBackFuture.cancel(false);
			}
			copyButtonFeedBackFuture = copyButtonFeedbackExecutorService.schedule(
					() -> Gdx.app.postRunnable(
							() -> this.setStyle(skin.get(SkinConstants.BUTTON_COPY, ImageTextButtonStyle.class))),
					1000, TimeUnit.MILLISECONDS);
		}));
	}

}

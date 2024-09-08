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

	private final ImageTextButtonStyle defaultStyle;
	private final ImageTextButtonStyle tickStyle;
	private ScheduledFuture<?> copyButtonFeedBackFuture;
	private final ScheduledExecutorService copyButtonFeedbackExecutorService = Executors
			.newSingleThreadScheduledExecutor(
					new ThreadFactoryBuilder().setNameFormat("copy-button-%d").setDaemon(true).build());

	/**
	 * Constructor.
	 */
	public CopyButton(String text, Skin skin, boolean renderButtonBackground) {
		super(text,
				skin.get(renderButtonBackground ? SkinConstants.BUTTON_COPY : SkinConstants.BUTTON_COPY_NO_BACKGROUND,
						ImageTextButtonStyle.class));

		if (renderButtonBackground) {
			defaultStyle = skin.get(SkinConstants.BUTTON_COPY, ImageTextButtonStyle.class);
			tickStyle = skin.get(SkinConstants.BUTTON_COPY_TICK, ImageTextButtonStyle.class);
		} else {
			defaultStyle = skin.get(SkinConstants.BUTTON_COPY_NO_BACKGROUND, ImageTextButtonStyle.class);
			tickStyle = skin.get(SkinConstants.BUTTON_COPY_TICK_NO_BACKGROUND, ImageTextButtonStyle.class);
		}

		this.addListener(new ExceptionLoggingChangeListener(() -> {
			// give feedback to the user by adding the tick for a moment
			this.setStyle(tickStyle);
			if (copyButtonFeedBackFuture != null) {
				copyButtonFeedBackFuture.cancel(false);
			}
			copyButtonFeedBackFuture = copyButtonFeedbackExecutorService.schedule(
					() -> Gdx.app.postRunnable(() -> this.setStyle(defaultStyle)), 1000, TimeUnit.MILLISECONDS);
		}));
	}

}

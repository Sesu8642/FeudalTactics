// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.crashreporting.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.base.Strings;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.crashreporting.CrashReportDao;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Screen for crash reporting.
 */
@Singleton
public class CrashReportScreen extends GameScreen {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final EventBus eventBus;
    private final CrashReportStage crashReportStage;
    private final CrashReportDao crashReportDao;
    /**
     * Whether the screen is shown on startup. Causes the splash screen to be shown
     * on finish instead of the menu.
     */
    @Getter
    private boolean isGameStartup = false;

    /**
     * Constructor.
     */
    @Inject
    public CrashReportScreen(EventBus eventBus, @MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                             CrashReportStage crashReportStage, CrashReportDao crashReportDao) {
        super(camera, viewport, crashReportStage);
        this.eventBus = eventBus;
        this.crashReportStage = crashReportStage;
        this.crashReportDao = crashReportDao;
        registerEventListeners();
    }

    @Override
    public void show() {
        final String crashReportText = crashReportDao.getLastCrashReport();
        if (!Strings.isNullOrEmpty(crashReportText)) {
            crashReportStage.crashReportSlide.textArea.setText(crashReportText);
        } else {
            crashReportStage.crashReportSlide.descriptionLabel.setText("No crashes were detected previously!");
            crashReportStage.crashReportSlide.textArea.setText("Nothing to see here.");
            crashReportStage.crashReportSlide.buttonGroup.removeActor(crashReportStage.crashReportSlide.sendMailButton);
            crashReportStage.crashReportSlide.buttonGroup.removeActor(crashReportStage.crashReportSlide.copyButton);
        }
        super.show();
    }

    private void registerEventListeners() {
        crashReportStage.setFinishedCallback(() -> {
            final ScreenTransitionTarget onFinishedTarget = isGameStartup ? ScreenTransitionTarget.SPLASH_SCREEN
                : ScreenTransitionTarget.INFORMATION_MENU_SCREEN;
            eventBus.post(new ScreenTransitionTriggerEvent(onFinishedTarget));
        });
        crashReportStage.crashReportSlide.sendMailButton.addListener(new ExceptionLoggingChangeListener(() -> {
            try {
                final String totallyUnharvestableEmail = "con" + "tact@sesu" + "8642.de";
                final URI mailtoUri = new URI("mailto", totallyUnharvestableEmail, null,
                    "subject=FeudalTactics Crash Report&body="
                        + crashReportStage.crashReportSlide.textArea.getText(),
                    null);
                // couldn't find a way to build a proper mailto uri without the forward slashes
                final String mailtoUriString = mailtoUri.toString().replaceFirst("://", ":");
                logger.debug("Opening bug report mailto URI.");
                Gdx.net.openURI(mailtoUriString);
            } catch (URISyntaxException e) {
                // do not throw an exception while the user is trying to report an exception
                logger.warn("unable to build mailto URI with body: {}",
                    crashReportStage.crashReportSlide.textArea.getText());
            }
        }));
        crashReportStage.crashReportSlide.copyButton.addListener(new ExceptionLoggingChangeListener(() -> {
            logger.debug("copying bug report info to clipboard.");
            Gdx.app.getClipboard().setContents(crashReportStage.crashReportSlide.textArea.getText());
        }));
        crashReportStage.crashReportSlide.openGithubButton.addListener(new ExceptionLoggingChangeListener(() -> {
            logger.debug("opening GitHub issue URI.");
            Gdx.net.openURI("https://github.com/Sesu8642/FeudalTactics/issues");
        }));
    }

    public void setGameStartup(boolean isGameStartup) {
        this.isGameStartup = isGameStartup;
    }

}

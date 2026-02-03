// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.crashreporting.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.base.Strings;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.ScreenNavigationController;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuCamera;
import de.sesu8642.feudaltactics.menu.common.dagger.MenuViewport;
import de.sesu8642.feudaltactics.menu.common.ui.ExceptionLoggingChangeListener;
import de.sesu8642.feudaltactics.menu.common.ui.GameScreen;
import de.sesu8642.feudaltactics.menu.crashreporting.CrashReportDao;
import lombok.Getter;
import lombok.Setter;
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

    private final CrashReportStage crashReportStage;
    private final CrashReportDao crashReportDao;
    private final LocalizationManager localizationManager;
    /**
     * Whether the screen is shown on startup. Causes the splash screen to be shown
     * on finish instead of the menu.
     */
    @Setter
    @Getter
    private boolean isGameStartup = false;

    /**
     * Constructor.
     */
    @Inject
    public CrashReportScreen(ScreenNavigationController screenNavigationController,
                             @MenuCamera OrthographicCamera camera, @MenuViewport Viewport viewport,
                             CrashReportStage crashReportStage, CrashReportDao crashReportDao,
                             LocalizationManager localizationManager) {
        super(camera, viewport, crashReportStage);
        this.crashReportStage = crashReportStage;
        this.crashReportDao = crashReportDao;
        this.localizationManager = localizationManager;
        registerEventListeners(screenNavigationController);
    }

    @Override
    public void show() {
        final String crashReportText = crashReportDao.getLastCrashReport();
        if (!Strings.isNullOrEmpty(crashReportText)) {
            crashReportStage.crashReportSlide.textArea.setText(crashReportText);
        } else {
            crashReportStage.crashReportSlide.descriptionLabel.setText(localizationManager.localizeText("no-crashes"));
            crashReportStage.crashReportSlide.textArea.setText(localizationManager.localizeText("nothing-to-see-here"));
            crashReportStage.crashReportSlide.buttonGroup.removeActor(crashReportStage.crashReportSlide.sendMailButton);
            crashReportStage.crashReportSlide.buttonGroup.removeActor(crashReportStage.crashReportSlide.copyButton);
        }
        super.show();
    }

    private void registerEventListeners(ScreenNavigationController screenNavigationController) {
        crashReportStage.setFinishedCallback(() -> {
            if (isGameStartup) {
                screenNavigationController.transitionToSplashScreen();
            } else {
                screenNavigationController.transitionToInformationMenuScreenPage1();
            }
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

}

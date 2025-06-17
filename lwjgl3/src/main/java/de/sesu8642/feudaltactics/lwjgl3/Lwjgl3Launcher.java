package de.sesu8642.feudaltactics.lwjgl3;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import de.sesu8642.feudaltactics.FeudalTactics;

/**
 * Launches the desktop (LWJGL3) application.
 */
public class Lwjgl3Launcher {
    private static final String TAG = Lwjgl3Launcher.class.getName();

    public static void main(String[] args) {
        try {
            if (StartupHelper.startNewJvmIfRequired()) {
                return; // This handles macOS support and helps on Windows.
            }
            createApplication();
        } catch (Exception e) {
            Gdx.app.error(TAG, "the game crashed because of an unexpected exception", e);
            System.exit(1);
        }
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new FeudalTactics(text -> {
            throw new UnsupportedOperationException("Sharing is not implemented for LWJGL3!");
        }), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration configuration = new Lwjgl3ApplicationConfiguration();
        configuration.setTitle("FeudalTactics");
        configuration.setWindowIcon("square_logo_64.png");
        //// Vsync limits the frames per second to what your hardware can display, and
        //// helps eliminate
        //// screen tearing. This setting doesn't always work on Linux, so the line
        //// after is a safeguard.
        configuration.useVsync(true);
        //// Limits FPS to the refresh rate of the currently active monitor, plus 1 to
        //// try to match fractional
        //// refresh rates. The Vsync setting above should limit the actual FPS to match
        //// the monitor.
        configuration.setForegroundFPS(Lwjgl3ApplicationConfiguration.getDisplayMode().refreshRate + 1);
        //// If you remove the above line and set Vsync to false, you can get unlimited
        //// FPS, which can be
        //// useful for testing performance, but can also be very stressful to some
        //// hardware.
        //// You may also need to configure GPU drivers to fully disable Vsync; this can
        //// cause screen tearing.
        configuration.setWindowedMode(1600, 900);
        //// You can change these files; they are in lwjgl3/src/main/resources/ .
        return configuration;
    }
}
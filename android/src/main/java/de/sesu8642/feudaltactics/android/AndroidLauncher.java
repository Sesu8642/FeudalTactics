package de.sesu8642.feudaltactics.android;

import android.os.Bundle;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import de.sesu8642.feudaltactics.FeudalTactics;

/**
 * Launches the Android application.
 */
public class AndroidLauncher extends AndroidApplication {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidApplicationConfiguration configuration = new AndroidApplicationConfiguration();
        configuration.useImmersiveMode = false; // Recommended, but not required.
        initialize(new FeudalTactics(new AndroidPlatformSharing(getContext()), new AndroidInsetProvider()),
            configuration);
    }
}

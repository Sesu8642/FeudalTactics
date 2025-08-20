// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.android;


import android.os.Build;
import android.view.WindowInsets;
import com.badlogic.gdx.Application;
import com.badlogic.gdx.backends.android.AndroidApplicationBase;
import de.sesu8642.feudaltactics.platformspecific.Insets;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;

/**
 * Android implementation of {@link PlatformInsetsProvider}.
 */
public class AndroidInsetProvider implements PlatformInsetsProvider {

    @Override
    public Insets getInsets(Application app) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.VANILLA_ICE_CREAM) {
            android.graphics.Insets androidInsets =
                ((AndroidApplicationBase) app).getApplicationWindow().getDecorView().getRootWindowInsets().getInsets(
                    WindowInsets.Type.navigationBars()
                        | WindowInsets.Type.statusBars()
                        | WindowInsets.Type.displayCutout());
            return new Insets(androidInsets.top, androidInsets.bottom, androidInsets.left, androidInsets.right);
        } else {
            return Insets.NONE;
        }
    }
}

package de.sesu8642.feudaltactics.android;

import android.content.Context;
import android.content.Intent;
import de.sesu8642.feudaltactics.platformspecific.PlatformSharing;

/**
 * Android implementation of {@link PlatformSharing}.
 */
public class AndroidPlatformSharing implements PlatformSharing {

    private final Context context;

    public AndroidPlatformSharing(Context context) {
        this.context = context;
    }

    @Override
    public void shareText(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        context.startActivity(shareIntent);
    }

}
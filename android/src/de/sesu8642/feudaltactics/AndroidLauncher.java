// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import android.os.Bundle;

import com.badlogic.gdx.Files.FileType;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import de.sesu8642.feudaltactics.FeudalTactics;

public class AndroidLauncher extends AndroidApplication {
	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new FeudalTactics(), config);
	}
}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.crashreporting.ui;

import com.badlogic.gdx.Screen;

import de.sesu8642.feudaltactics.exceptions.FatalErrorException;

/**
 * Crashes the game when shown. Throwing something in the render method is the
 * only way I found to crash the game on Android.
 */
public class CrashingScreen implements Screen {

	private final Throwable throwable;

	public CrashingScreen(Throwable throwable) {
		this.throwable = throwable;
	}

	@Override
	public void render(float delta) {
		throw new FatalErrorException(throwable);
	}

	@Override
	public void show() {
	}

	@Override
	public void resize(int width, int height) {
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public void hide() {
	}

	@Override
	public void dispose() {
	}

}

package com.sesu8642.feudaltactics.engine;

import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.sesu8642.feudaltactics.screens.IngameScreen;

public class CombinedInputProcessor implements GestureListener, InputProcessor {

	private IngameScreen ingameScreen;

	public CombinedInputProcessor(IngameScreen ingameScreen) {
		this.ingameScreen = ingameScreen;
	}

	@Override
	public boolean scrolled(int amount) {
		if (ingameScreen.getCamera().zoom + amount > 0 && ingameScreen.getCamera().zoom + amount < 50) {
			ingameScreen.getCamera().zoom += amount;
		}
		return true;
	}

	@Override
	public boolean pan(float x, float y, float deltaX, float deltaY) {
		// not very good and dependent on resolution; refactor later
		final int DRAG_MULTIPLIER = 50; // 50 seems to work pretty well; no idea why
		ingameScreen.getCamera().translate(-deltaX * ingameScreen.getCamera().zoom / DRAG_MULTIPLIER, deltaY * ingameScreen.getCamera().zoom / DRAG_MULTIPLIER);
		return true;
	}

	@Override
	public boolean zoom(float initialDistance, float distance) {
		// kind of works... good enough for now
		float amount = (initialDistance - distance) / 2500;
		if (ingameScreen.getCamera().zoom + amount > 0 && ingameScreen.getCamera().zoom + amount < 50) {
			ingameScreen.getCamera().zoom += amount;
		}
		return true;
	}

	@Override
	public boolean panStop(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDown(float x, float y, int pointer, int button) {
		return false;
	}

	@Override
	public boolean tap(float x, float y, int count, int button) {
		Vector3 fullWorldCoords = ingameScreen.getCamera().unproject(new Vector3(x, y, 0));
		Vector2 worldCoords = new Vector2(fullWorldCoords.x, fullWorldCoords.y);
		//gameController.printTileInfo(worldCoords);
		//gameController.updateInfoText(worldCoords);
		return false;
	}

	@Override
	public boolean longPress(float x, float y) {
		return false;
	}

	@Override
	public boolean fling(float velocityX, float velocityY, int button) {
		return false;
	}

	@Override
	public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
		return false;
	}

	@Override
	public void pinchStop() {
	}

	@Override
	public boolean keyDown(int keycode) {
		return false;
	}

	@Override
	public boolean keyUp(int keycode) {
		return false;
	}

	@Override
	public boolean keyTyped(char character) {
		return false;
	}

	@Override
	public boolean touchDown(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchUp(int screenX, int screenY, int pointer, int button) {
		return false;
	}

	@Override
	public boolean touchDragged(int screenX, int screenY, int pointer) {
		return false;
	}

	@Override
	public boolean mouseMoved(int screenX, int screenY) {
		return false;
	}
}

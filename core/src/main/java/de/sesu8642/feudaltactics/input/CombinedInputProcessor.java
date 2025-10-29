// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Buttons;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.input.GestureDetector.GestureListener;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.google.common.eventbus.EventBus;
import de.sesu8642.feudaltactics.events.TapInputEvent;
import de.sesu8642.feudaltactics.events.input.BackInputEvent;
import de.sesu8642.feudaltactics.events.input.EscInputEvent;
import de.sesu8642.feudaltactics.events.moves.BuyAndPlaceCastleEvent;
import de.sesu8642.feudaltactics.events.moves.BuyCastleEvent;
import de.sesu8642.feudaltactics.events.moves.BuyPeasantEvent;
import de.sesu8642.feudaltactics.ingame.dagger.IngameCamera;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Class that handles touch as well as gesture inputs.
 **/
@Singleton
public class CombinedInputProcessor implements GestureListener, InputProcessor {

    public static final float MIN_ZOOM = 0.01F;
    public static final float MAX_ZOOM = 1;
    private final EventBus eventBus;
    private final OrthographicCamera camera;
    Float cameraZoomBeforePinch;

    @Inject
    public CombinedInputProcessor(EventBus eventBus, @IngameCamera OrthographicCamera camera) {
        this.eventBus = eventBus;
        this.camera = camera;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        final float adjAmount = (amountY * camera.zoom) / 3;
        if ((adjAmount < 0 && camera.zoom + adjAmount > MIN_ZOOM)
            || (adjAmount > 0 && camera.zoom + adjAmount < MAX_ZOOM)) {
            final Vector3 oldMousePosition = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            camera.zoom += adjAmount;
            camera.update();
            final Vector3 newMousePosition = camera.unproject(new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0));
            camera.translate(oldMousePosition.sub(newMousePosition));
            camera.update();
        }
        return true;
    }

    @Override
    public boolean pan(float x, float y, float deltaX, float deltaY) {
        if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
            final Vector3 worldDistance = camera.unproject(new Vector3(x, y, 0))
                .sub(camera.unproject(new Vector3(x + deltaX, y + deltaY, 0)));
            camera.translate(worldDistance);
            camera.update();
            return true;
        }
        return false;
    }

    @Override
    public boolean zoom(float initialDistance, float distance) {
        return false;
    }

    @Override
    public boolean panStop(float x, float y, int pointer, int button) {
        return false;
    }

    // TODO: move this logic somewhere else
    @Override
    public boolean tap(float x, float y, int count, int button) {
        switch (button) {
            case Buttons.LEFT:
                final Vector2 worldCoords = eventCoordsToXYWorldCoords(x, y);
                eventBus.post(new TapInputEvent(worldCoords, count));
                break;
            case Buttons.RIGHT:
                eventBus.post(new BuyPeasantEvent());
                break;
            case Buttons.MIDDLE:
                eventBus.post(new BuyCastleEvent());
                break;
            default:
                return false;
        }
        return true;
    }

    @Override
    public boolean longPress(float x, float y) {
        if (Gdx.input.isButtonPressed(Buttons.LEFT)) {
            final Vector2 worldCoords = eventCoordsToXYWorldCoords(x, y);
            eventBus.post(new BuyAndPlaceCastleEvent(worldCoords));
        }
        return true;
    }

    @Override
    public boolean fling(float velocityX, float velocityY, int button) {
        return false;
    }

    // TODO: move this logic somwhere else
    @Override
    public boolean pinch(Vector2 initialPointer1, Vector2 initialPointer2, Vector2 pointer1, Vector2 pointer2) {
        if (cameraZoomBeforePinch == null) {
            cameraZoomBeforePinch = camera.zoom;
        }
        final float initialDistance = initialPointer1.dst(initialPointer2);
        final float currentDistance = pointer1.dst(pointer2);
        final float zoomAmount = initialDistance / currentDistance;
        final float newZoom = cameraZoomBeforePinch * zoomAmount;
        if ((zoomAmount < 1 && newZoom > MIN_ZOOM) || (zoomAmount > 1 && newZoom < MAX_ZOOM)) {
            final Vector2 oldPointerCenter = new Vector2((pointer1.x + pointer2.x) / 2, (pointer1.y + pointer2.y) / 2);
            final Vector3 oldPointerCenterInWorld = camera.unproject(new Vector3(oldPointerCenter, 0));
            camera.zoom = newZoom;
            camera.update();
            final Vector3 newPointerCenterInWorld = camera.unproject(new Vector3(oldPointerCenter, 0));
            camera.translate(oldPointerCenterInWorld.sub(newPointerCenterInWorld));
            camera.update();
        }
        return true;
    }

    @Override
    public void pinchStop() {
        cameraZoomBeforePinch = null;
    }

    @Override
    public boolean keyDown(int keycode) {
        return false;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Keys.ESCAPE:
                eventBus.post(new EscInputEvent());
                break;
            case Keys.BACK:
                eventBus.post(new BackInputEvent());
                break;
            default:
                // noop: ignore all other keys
        }
        return true;
    }

    @Override
    public boolean keyTyped(char character) {
        return false;
    }

    @Override
    public boolean touchDown(float x, float y, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        if (button == Buttons.BACK) {
            eventBus.post(new BackInputEvent());
        }
        return true;
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

    private Vector2 eventCoordsToXYWorldCoords(float eventX, float eventY) {
        final Vector3 fullWorldCoords = camera.unproject(new Vector3(eventX, eventY, 0));
        final Vector2 worldCoords = new Vector2(fullWorldCoords.x, fullWorldCoords.y);
        return worldCoords;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }
}

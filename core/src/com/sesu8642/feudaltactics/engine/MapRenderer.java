package com.sesu8642.feudaltactics.engine;

import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.gamestate.HexMap;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Capital;
import com.sesu8642.feudaltactics.gamestate.mapobjects.MapObject;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;

public class MapRenderer {

	final float SPRITE_SIZE_MULTIPLIER = 1.5F;

	private float width = HexMap.HEX_OUTER_RADIUS * 2;
	private float height = HexMap.HEX_OUTER_RADIUS * (float) Math.sqrt(3);
	private GameController gameController;
	private HashMap<Vector2, Color> tiles;
	private HashMap<String, TextureRegion> textureRegions;
	private HashMap<String, Animation<TextureRegion>> animations;
	private HashMap<Vector2, TextureRegion> nonAnimatedContents;
	private HashMap<Vector2, Animation<TextureRegion>> animatedContents;
	private float stateTime; // for keeping animations at the correct pace
	private SpriteBatch batch;
	private TextureRegion tileRegion;

	public MapRenderer(GameController gameController) {
		this.gameController = gameController;
		tiles = new HashMap<Vector2, Color>();
		animatedContents = new HashMap<Vector2, Animation<TextureRegion>>();
		nonAnimatedContents = new HashMap<Vector2, TextureRegion>();
		tileRegion = FeudalTactics.textureAtlas.findRegion("tile_bw");
		batch = new SpriteBatch();
		textureRegions = new HashMap<String, TextureRegion>();
		animations = new HashMap<String, Animation<TextureRegion>>();
		stateTime = 0F;
	}

	public void updateMap() {
		// create tiles
		tiles.clear();
		nonAnimatedContents.clear();
		animatedContents.clear();
		for (Entry<Vector2, HexTile> hexTileEntry : (gameController.getGameState().getMap().getTiles()).entrySet()) {
			Vector2 hexCoords = hexTileEntry.getKey();
			Vector2 mapCoords = getMapCoordinatesFromHexCoordinates(hexCoords);
			// create tiles
			tiles.put(mapCoords, hexTileEntry.getValue().getPlayer().getColor());
			// create content (units etc)
			MapObject tileContent = hexTileEntry.getValue().getContent();
			if (tileContent != null) {
				boolean animate = false;
				if (tileContent.getClass().isAssignableFrom(Unit.class) && ((Unit) tileContent).isCanAct()) {
					// animate units that can act
					animate = true;
				} else if (tileContent.getClass().isAssignableFrom(Capital.class)
						&& gameController.getGameState().getActivePlayer() == tileContent.getKingdom().getPlayer()
						&& tileContent.getKingdom().getSavings() > Unit.COST) {
					// animate capitals if they can buy something
					animate = true;
				}
				if (animate) {
					animatedContents.put(
							new Vector2(mapCoords.x - HexMap.HEX_OUTER_RADIUS, mapCoords.y - HexMap.HEX_OUTER_RADIUS),
							getAnimationFromName(tileContent.getSpriteName()));
				} else {
					nonAnimatedContents.put(
							new Vector2(mapCoords.x - HexMap.HEX_OUTER_RADIUS, mapCoords.y - HexMap.HEX_OUTER_RADIUS),
							getTextureRegionFromName(tileContent.getSpriteName()));
				}

			}
		}
	}

	private TextureRegion getTextureRegionFromName(String name) {
		TextureRegion textureRegion = textureRegions.get(name);
		if (textureRegion == null) {
			textureRegion = FeudalTactics.textureAtlas.findRegion(name);
			textureRegions.put(name, textureRegion);
		}
		return textureRegion;
	}

	private Animation<TextureRegion> getAnimationFromName(String name) {
		Animation<TextureRegion> animation = animations.get(name);
		if (animation == null) {
			animation = new Animation<TextureRegion>(1F, FeudalTactics.textureAtlas.findRegions(name));
			animations.put(name, animation);
		}
		return animation;
	}

	public void render(OrthographicCamera camera) {
		HashMap<Vector2, TextureRegion> frames = new HashMap<Vector2, TextureRegion>(); // current frame for each map
		batch.setProjectionMatrix(camera.combined); // object
		stateTime += Gdx.graphics.getDeltaTime();
		// get the correct frames
		for (Entry<Vector2, Animation<TextureRegion>> content : animatedContents.entrySet()) {
			frames.put(content.getKey(), ((Animation<TextureRegion>) content.getValue()).getKeyFrame(stateTime, true));
		}
		float objectSize = SPRITE_SIZE_MULTIPLIER * HexMap.HEX_OUTER_RADIUS;
		float offset = (HexMap.HEX_OUTER_RADIUS - objectSize) / 2;
		batch.begin();
		// draw all the tiles
		for (Entry<Vector2, Color> tile : tiles.entrySet()) {
			batch.setColor(tile.getValue());
			batch.draw(tileRegion, tile.getKey().x - width / 2, tile.getKey().y - height / 2, width, height);
		}
		batch.setColor(1, 1, 1, 1);
		// draw all the animated contents
		for (Entry<Vector2, TextureRegion> currentFrame : frames.entrySet()) {
			batch.draw(currentFrame.getValue(), currentFrame.getKey().x - offset, currentFrame.getKey().y - offset,
					objectSize, objectSize);
		}
		// draw all the non-animated contents
		for (Entry<Vector2, TextureRegion> content : nonAnimatedContents.entrySet()) {
			batch.draw(content.getValue(), content.getKey().x - offset, content.getKey().y - offset, objectSize,
					objectSize);
		}
		batch.end();
	}

	public Vector2 getMapCoordinatesFromHexCoordinates(Vector2 hexCoords) {
		float x = 0.75F * width * hexCoords.x;
		float y = (float) (HexMap.HEX_OUTER_RADIUS
				* (Math.sqrt(3) / 2 * hexCoords.x + Math.sqrt(3) * (-hexCoords.y - hexCoords.x)));
		return new Vector2(x, y);
	}

	public void dispose() {
		batch.dispose();
	}

	public void resize() {
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	}
}

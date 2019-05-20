package com.sesu8642.feudaltactics.engine;

import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
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
	private HashMap<Vector2, Animation<TextureRegion>> contents;
	private float stateTime; // for keeping animations at the correct pace
	private SpriteBatch batch;
	private AtlasRegion tileRegion;

	public MapRenderer(GameController gameController) {
		this.gameController = gameController;
		tiles = new HashMap<Vector2, Color>();
		contents = new HashMap<Vector2, Animation<TextureRegion>>();
		tileRegion = FeudalTactics.textureAtlas.findRegion("tile_bw");
		batch = new SpriteBatch();
		stateTime = 0F;
	}

	public void updateMap() {
		// create tiles
		tiles.clear();
		contents.clear();
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
				Array<AtlasRegion> atlasregions;
				if (animate) {
					atlasregions = FeudalTactics.textureAtlas.findRegions(tileContent.getSpriteName());
				} else {
					atlasregions = new Array<AtlasRegion>();
					atlasregions.add(FeudalTactics.textureAtlas.findRegions(tileContent.getSpriteName()).first());
				}
				contents.put(new Vector2(mapCoords.x - HexMap.HEX_OUTER_RADIUS, mapCoords.y - HexMap.HEX_OUTER_RADIUS),
						new Animation<TextureRegion>(1F, atlasregions));
			}
		}
	}

	public void render(OrthographicCamera camera) {
		HashMap<Vector2, TextureRegion> frames = new HashMap<Vector2, TextureRegion>(); // current frame for each map
		batch.setProjectionMatrix(camera.combined); // object
		stateTime += Gdx.graphics.getDeltaTime();
		// get the correct frames
		for (Entry<Vector2, Animation<TextureRegion>> content : contents.entrySet()) {
			frames.put(content.getKey(), content.getValue().getKeyFrame(stateTime, true));
		}
		float objectSize = SPRITE_SIZE_MULTIPLIER * HexMap.HEX_OUTER_RADIUS;
		float offset = (HexMap.HEX_OUTER_RADIUS - objectSize) / 2;
		float tileWidth = HexMap.HEX_OUTER_RADIUS * 2;
		float tileHeight = (float) (HexMap.HEX_OUTER_RADIUS * Math.sqrt(3));
		batch.begin();
		// draw all the tiles
		for (Entry<Vector2, Color> tile : tiles.entrySet()) {
			batch.setColor(tile.getValue());
			batch.draw(tileRegion, tile.getKey().x - tileWidth/2, tile.getKey().y - tileHeight/2, tileWidth, tileHeight);
		}
		batch.setColor(1,1,1,1);
		// draw all the contents
		for (Entry<Vector2, TextureRegion> currentFrame : frames.entrySet()) {
			batch.draw(currentFrame.getValue(), currentFrame.getKey().x - offset, currentFrame.getKey().y - offset,
					objectSize, objectSize);
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

package com.sesu8642.feudaltactics.engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.HexMap;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Capital;
import com.sesu8642.feudaltactics.gamestate.mapobjects.MapObject;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;

public class MapRenderer {

	final float SPRITE_SIZE_MULTIPLIER = 1.05F;
	final float LINE_EXTENSION = 0.14F;

	private float width = HexMap.HEX_OUTER_RADIUS * 2;
	private float height = HexMap.HEX_OUTER_RADIUS * (float) Math.sqrt(3);
	private OrthographicCamera camera;
	private HashMap<Vector2, Color> tiles;
	private HashMap<String, TextureRegion> textureRegions;
	private HashMap<String, Animation<TextureRegion>> animations;
	private HashMap<Vector2, TextureRegion> nonAnimatedContents;
	private HashMap<Vector2, Animation<TextureRegion>> animatedContents;
	private ArrayList<Vector2> whitelineStartPoints;
	private ArrayList<Vector2> whitelineEndPoints;
	private float stateTime; // for keeping animations at the correct pace
	private SpriteBatch batch;
	private TextureRegion tileRegion;
	private ShapeRenderer shapeRenderer;

	public MapRenderer(OrthographicCamera camera) {
		this.camera = camera;
		shapeRenderer = new ShapeRenderer();
		tiles = new HashMap<Vector2, Color>();
		animatedContents = new HashMap<Vector2, Animation<TextureRegion>>();
		nonAnimatedContents = new HashMap<Vector2, TextureRegion>();
		tileRegion = FeudalTactics.textureAtlas.findRegion("tile_bw");
		batch = new SpriteBatch();
		textureRegions = new HashMap<String, TextureRegion>();
		animations = new HashMap<String, Animation<TextureRegion>>();
		whitelineStartPoints = new ArrayList<Vector2>();
		whitelineEndPoints = new ArrayList<Vector2>();
		stateTime = 0F;
	}

	public void updateMap(GameState gameState) {
		// create tiles
		tiles.clear();
		nonAnimatedContents.clear();
		animatedContents.clear();
		whitelineStartPoints.clear();
		whitelineEndPoints.clear();
		for (Entry<Vector2, HexTile> hexTileEntry : (gameState.getMap().getTiles()).entrySet()) {
			Vector2 hexCoords = hexTileEntry.getKey();
			Vector2 mapCoords = getMapCoordinatesFromHexCoordinates(hexCoords);
			HexTile tile = hexTileEntry.getValue();
			// create tiles
			tiles.put(mapCoords, tile.getPlayer().getColor());
			// create content (units etc)
			MapObject tileContent = tile.getContent();
			if (tileContent != null) {
				boolean animate = false;
				if (tileContent.getKingdom() != null
						&& tileContent.getKingdom().getPlayer() == gameState.getActivePlayer()) {
					if (ClassReflection.isAssignableFrom(tileContent.getClass(), Unit.class)
							&& ((Unit) tileContent).isCanAct()) {
						// animate units that can act
						animate = true;
					} else if (ClassReflection.isAssignableFrom(tileContent.getClass(), Capital.class)
							&& gameState.getActivePlayer() == tileContent.getKingdom().getPlayer()
							&& tileContent.getKingdom().getSavings() > Unit.COST) {
						// animate capitals if they can buy something
						animate = true;
					}
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
			// create lines for highlighting active kingdom
			if (gameState.getActiveKingdom() != null && tile.getKingdom() != null
					&& tile.getKingdom() == gameState.getActiveKingdom()) {
				int index = 0;
				for (HexTile neighborTile : gameState.getMap().getNeighborTiles(tile)) {
					if (neighborTile == null || neighborTile.getKingdom() == null
							|| neighborTile.getKingdom() != tile.getKingdom()) {
						// index contains the information where the neighbor tile is positioned
						switch (index) {
						case 0:
							// top left
							whitelineStartPoints.add(new Vector2(mapCoords.x - width / 4 + LINE_EXTENSION,
									mapCoords.y + height / 2 + LINE_EXTENSION));
							whitelineEndPoints.add(new Vector2(mapCoords.x - width / 2 - LINE_EXTENSION,
									mapCoords.y - LINE_EXTENSION));
							break;
						case 1:
							// top
							whitelineStartPoints.add(
									new Vector2(mapCoords.x - width / 4 - LINE_EXTENSION, mapCoords.y + height / 2));
							whitelineEndPoints.add(
									new Vector2(mapCoords.x + width / 4 + LINE_EXTENSION, mapCoords.y + height / 2));
							break;
						case 2:
							// top right
							whitelineStartPoints.add(new Vector2(mapCoords.x + width / 4 - LINE_EXTENSION,
									mapCoords.y + height / 2 + LINE_EXTENSION));
							whitelineEndPoints.add(new Vector2(mapCoords.x + width / 2 + LINE_EXTENSION,
									mapCoords.y - LINE_EXTENSION));
							break;
						case 3:
							// bottom right
							whitelineStartPoints.add(new Vector2(mapCoords.x + width / 4 - LINE_EXTENSION,
									mapCoords.y - height / 2 - LINE_EXTENSION));
							whitelineEndPoints.add(new Vector2(mapCoords.x + width / 2 + LINE_EXTENSION,
									mapCoords.y + LINE_EXTENSION));
							break;
						case 4:
							// bottom
							whitelineStartPoints.add(
									new Vector2(mapCoords.x - width / 4 - LINE_EXTENSION, mapCoords.y - height / 2));
							whitelineEndPoints.add(
									new Vector2(mapCoords.x + width / 4 + LINE_EXTENSION, mapCoords.y - height / 2));
							break;
						case 5:
							// bottom left
							whitelineStartPoints.add(new Vector2(mapCoords.x - width / 4 + LINE_EXTENSION,
									mapCoords.y - height / 2 - LINE_EXTENSION));
							whitelineEndPoints.add(new Vector2(mapCoords.x - width / 2 - LINE_EXTENSION,
									mapCoords.y + LINE_EXTENSION));
							break;
						}
					}
					index++;
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

	public void render() {
		HashMap<Vector2, TextureRegion> frames = new HashMap<Vector2, TextureRegion>(); // current frame for each map
		batch.setProjectionMatrix(camera.combined); // object
		stateTime += Gdx.graphics.getDeltaTime();
		// get the correct frames
		for (Entry<Vector2, Animation<TextureRegion>> content : animatedContents.entrySet()) {
			frames.put(content.getKey(), ((Animation<TextureRegion>) content.getValue()).getKeyFrame(stateTime, true));
		}
		// float objectSize = height * SPRITE_SIZE_MULTIPLIER;
		float itemOffsetX = width * 0.0F;
		float itemOffsetY = height * -0.075F;
		batch.begin();
		// draw all the tiles
		for (Entry<Vector2, Color> tile : tiles.entrySet()) {
			batch.setColor(tile.getValue());
			batch.draw(tileRegion, tile.getKey().x - width / 2, tile.getKey().y - height / 2, width, height);
		}
		batch.setColor(1, 1, 1, 1);
		// draw all the animated contents
		for (Entry<Vector2, TextureRegion> currentFrame : frames.entrySet()) {
			batch.draw(currentFrame.getValue(), currentFrame.getKey().x - itemOffsetX,
					currentFrame.getKey().y - itemOffsetY, width, height);
		}
		// draw all the non-animated contents
		for (Entry<Vector2, TextureRegion> content : nonAnimatedContents.entrySet()) {
			batch.draw(content.getValue(), content.getKey().x - itemOffsetX, content.getKey().y - itemOffsetY, width,
					height);
		}
		batch.end();
		// draw all the lines
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		for (int i = 0; i < whitelineStartPoints.size(); i++) {
			shapeRenderer.rectLine(whitelineStartPoints.get(i).x, whitelineStartPoints.get(i).y,
					whitelineEndPoints.get(i).x, whitelineEndPoints.get(i).y, 0.6F);
		}
		shapeRenderer.end();
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

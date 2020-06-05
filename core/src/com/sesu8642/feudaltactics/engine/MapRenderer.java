package com.sesu8642.feudaltactics.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

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
	private float stateTime; // for keeping animations at the correct pace
	private SpriteBatch batch;
	private TextureRegion tileRegion;
	private ShapeRenderer shapeRenderer;

	// stuff that is to be drawn
	private HashMap<Vector2, Color> tiles;
	private Set<Vector2> darkenedTiles;
	private HashMap<String, TextureRegion> textureRegions;
	private HashMap<String, Animation<TextureRegion>> animations;
	private HashMap<Vector2, TextureRegion> nonAnimatedContents;
	private HashMap<Vector2, TextureRegion> darkenedNonAnimatedContents;
	private HashMap<Vector2, Animation<TextureRegion>> animatedContents;
	private ArrayList<Vector2> whiteLineStartPoints;
	private ArrayList<Vector2> whiteLineEndPoints;
	private ArrayList<Vector2> redLineStartPoints;
	private ArrayList<Vector2> redLineEndPoints;

	private class Line {
		private Vector2 start;
		private Vector2 end;
	}

	public MapRenderer(OrthographicCamera camera) {
		this.camera = camera;
		shapeRenderer = new ShapeRenderer();
		tiles = new HashMap<Vector2, Color>();
		animatedContents = new HashMap<Vector2, Animation<TextureRegion>>();
		nonAnimatedContents = new HashMap<Vector2, TextureRegion>();
		darkenedNonAnimatedContents = new HashMap<Vector2, TextureRegion>();
		tileRegion = FeudalTactics.textureAtlas.findRegion("tile_bw");
		batch = new SpriteBatch();
		textureRegions = new HashMap<String, TextureRegion>();
		animations = new HashMap<String, Animation<TextureRegion>>();
		whiteLineStartPoints = new ArrayList<Vector2>();
		whiteLineEndPoints = new ArrayList<Vector2>();
		redLineStartPoints = new ArrayList<Vector2>();
		redLineEndPoints = new ArrayList<Vector2>();
		darkenedTiles = new HashSet<Vector2>();
		stateTime = 0F;
	}

	public void updateMap(GameState gameState) {
		// create tiles
		tiles.clear();
		nonAnimatedContents.clear();
		animatedContents.clear();
		darkenedNonAnimatedContents.clear();
		whiteLineStartPoints.clear();
		whiteLineEndPoints.clear();
		redLineStartPoints.clear();
		redLineEndPoints.clear();
		darkenedTiles.clear();
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
					if (gameState.getActiveKingdom() != null && gameState.getHeldObject() != null
							&& tile.getKingdom() != gameState.getActiveKingdom()
							&& !InputValidator.checkConquer(gameState, gameState.getActivePlayer(), tile)) {
						// darkened content
						darkenedNonAnimatedContents.put(
								new Vector2(mapCoords.x - HexMap.HEX_OUTER_RADIUS,
										mapCoords.y - HexMap.HEX_OUTER_RADIUS),
								getTextureRegionFromName(tileContent.getSpriteName()));
					} else {
						nonAnimatedContents.put(
								new Vector2(mapCoords.x - HexMap.HEX_OUTER_RADIUS,
										mapCoords.y - HexMap.HEX_OUTER_RADIUS),
								getTextureRegionFromName(tileContent.getSpriteName()));
					}
				}

			}
			// create lines for highlighting active kingdom
			if (gameState.getActiveKingdom() != null && tile.getKingdom() != null
					&& tile.getKingdom() == gameState.getActiveKingdom()) {
				int index = 0;
				for (HexTile neighborTile : gameState.getMap().getNeighborTiles(tile)) {
					if (neighborTile == null || neighborTile.getKingdom() == null
							|| neighborTile.getKingdom() != tile.getKingdom()) {
						Line line = getNeighborLine(mapCoords, index);
						whiteLineStartPoints.add(line.start);
						whiteLineEndPoints.add(line.end);
					}
					index++;
				}
			} else if (gameState.getHeldObject() != null) {
				// red lines for indicating if able to conquer
				if (InputValidator.checkConquer(gameState, gameState.getActivePlayer(), tile)) {
					int index = 0;
					for (HexTile neighborTile : gameState.getMap().getNeighborTiles(tile)) {
						if (neighborTile == null || (neighborTile.getKingdom() != gameState.getActiveKingdom() && !InputValidator.checkConquer(gameState, gameState.getActivePlayer(), neighborTile))) {
							Line line = getNeighborLine(mapCoords, index);
							Collection<Line> dottedLineParts = lineToDottedLine(line);
							for (Line linePart : dottedLineParts) {
								redLineStartPoints.add(linePart.start);
								redLineEndPoints.add(linePart.end);
							}
						}
						index++;
					}
				} else {
					darkenedTiles.add(mapCoords);
				}
			}
		}
	}

	private Collection<Line> lineToDottedLine(Line line) {
		int PART_AMOUNT = 3;
		Collection<Line> resultLines = new HashSet<Line>();
		float lineXDiff = line.end.x - line.start.x;
		float lineYDiff = line.end.y - line.start.y;
		for (int i = 1; i <= PART_AMOUNT; i += 2) {
			Line linePart = new Line();
			linePart.start = new Vector2(line.start.x + (lineXDiff / PART_AMOUNT) * (i - 1),
					line.start.y + (lineYDiff / PART_AMOUNT) * (i - 1));
			linePart.end = new Vector2(line.start.x + (lineXDiff / PART_AMOUNT) * i,
					line.start.y + (lineYDiff / PART_AMOUNT) * i);
			resultLines.add(linePart);
		}
		return resultLines;
	}

	private Line getNeighborLine(Vector2 mapCoords, int index) {
		Vector2 start = new Vector2();
		Vector2 end = new Vector2();
		switch (index) {
		case 0:
			// top left
			start = new Vector2(mapCoords.x - width / 4 + LINE_EXTENSION, mapCoords.y + height / 2 + LINE_EXTENSION);
			end = new Vector2(mapCoords.x - width / 2 - LINE_EXTENSION, mapCoords.y - LINE_EXTENSION);
			break;
		case 1:
			// top

			start = new Vector2(mapCoords.x - width / 4 - LINE_EXTENSION, mapCoords.y + height / 2);

			end = new Vector2(mapCoords.x + width / 4 + LINE_EXTENSION, mapCoords.y + height / 2);
			break;
		case 2:
			// top right
			start = new Vector2(mapCoords.x + width / 4 - LINE_EXTENSION, mapCoords.y + height / 2 + LINE_EXTENSION);
			end = new Vector2(mapCoords.x + width / 2 + LINE_EXTENSION, mapCoords.y - LINE_EXTENSION);
			break;
		case 3:
			// bottom right
			start = new Vector2(mapCoords.x + width / 4 - LINE_EXTENSION, mapCoords.y - height / 2 - LINE_EXTENSION);
			end = new Vector2(mapCoords.x + width / 2 + LINE_EXTENSION, mapCoords.y + LINE_EXTENSION);
			break;
		case 4:
			// bottom
			start = new Vector2(mapCoords.x - width / 4 - LINE_EXTENSION, mapCoords.y - height / 2);
			end = new Vector2(mapCoords.x + width / 4 + LINE_EXTENSION, mapCoords.y - height / 2);
			break;
		case 5:
			// bottom left
			start = new Vector2(mapCoords.x - width / 4 + LINE_EXTENSION, mapCoords.y - height / 2 - LINE_EXTENSION);
			end = new Vector2(mapCoords.x - width / 2 - LINE_EXTENSION, mapCoords.y + LINE_EXTENSION);
			break;
		}
		Line result = new Line();
		result.start = start;
		result.end = end;
		return result;
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
			Color color = new Color(tile.getValue());
			// darken tile
			if (darkenedTiles.contains(tile.getKey())) {
				color.mul(0.5F, 0.5F, 0.5F, 1);
			}
			batch.setColor(color);
			batch.draw(tileRegion, tile.getKey().x - width / 2, tile.getKey().y - height / 2, width, height);
		}
		// draw all the animated contents
		Color normalColor = new Color(1, 1, 1, 1);
		Color darkenedColor = new Color(0, 0, 0, 0.4F);
		batch.setColor(normalColor);
		for (Entry<Vector2, TextureRegion> currentFrame : frames.entrySet()) {
			batch.draw(currentFrame.getValue(), currentFrame.getKey().x - itemOffsetX,
					currentFrame.getKey().y - itemOffsetY, width, height);
		}
		// draw all the non-animated contents
		for (Entry<Vector2, TextureRegion> content : nonAnimatedContents.entrySet()) {
			batch.draw(content.getValue(), content.getKey().x - itemOffsetX, content.getKey().y - itemOffsetY, width,
					height);
		}
		// draw the darkened contents like normal but then draw a shadow over them
		for (Entry<Vector2, TextureRegion> content : darkenedNonAnimatedContents.entrySet()) {
			batch.draw(content.getValue(), content.getKey().x - itemOffsetX, content.getKey().y - itemOffsetY, width,
					height);
		}
		batch.setColor(darkenedColor);
		for (Entry<Vector2, TextureRegion> content : darkenedNonAnimatedContents.entrySet()) {
			batch.draw(content.getValue(), content.getKey().x - itemOffsetX, content.getKey().y - itemOffsetY, width,
					height);
		}
		batch.end();
		// draw all the lines
		shapeRenderer.setProjectionMatrix(camera.combined);
		shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
		shapeRenderer.setColor(1F, 1F, 1F, 1);
		for (int i = 0; i < whiteLineStartPoints.size(); i++) {
			shapeRenderer.rectLine(whiteLineStartPoints.get(i).x, whiteLineStartPoints.get(i).y,
					whiteLineEndPoints.get(i).x, whiteLineEndPoints.get(i).y, 0.6F);
		}
		shapeRenderer.setColor(1F, 0F, 0F, 1);
		for (int i = 0; i < redLineStartPoints.size(); i++) {
			shapeRenderer.rectLine(redLineStartPoints.get(i).x, redLineStartPoints.get(i).y, redLineEndPoints.get(i).x,
					redLineEndPoints.get(i).y, 0.6F);
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

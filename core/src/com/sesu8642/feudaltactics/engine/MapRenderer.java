package com.sesu8642.feudaltactics.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.gamestate.GameState;
import com.sesu8642.feudaltactics.gamestate.HexMap;
import com.sesu8642.feudaltactics.gamestate.HexTile;
import com.sesu8642.feudaltactics.gamestate.HexMap.MapDimensions;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Capital;
import com.sesu8642.feudaltactics.gamestate.mapobjects.MapObject;
import com.sesu8642.feudaltactics.gamestate.mapobjects.Unit;

public class MapRenderer {

	final float SPRITE_SIZE_MULTIPLIER = 1.05F;
	final float LINE_EXTENSION = 0.14F;
	final float WATER_TILE_SIZE = 12;
	final Color BEACH_WATER_COLOR = new Color(0F, 1F, 1F, 1F);

	private float width = HexMap.HEX_OUTER_RADIUS * 2;
	private float height = HexMap.HEX_OUTER_RADIUS * (float) Math.sqrt(3);
	private OrthographicCamera camera;
	private float stateTime; // for keeping animations at the correct pace
	private SpriteBatch batch;
	private TextureRegion tileRegion;
	private Animation<TextureRegion> waterAnimation;
	private Animation<TextureRegion> beachSandAnimation;
	private Animation<TextureRegion> beachWaterAnimation;
	private ShapeRenderer shapeRenderer;
	private boolean darkenBeaches;

	// stuff that is to be drawn
	private List<DrawTile> tiles;
	private HashMap<String, TextureRegion> textureRegions;
	private HashMap<String, Animation<TextureRegion>> animations;
	private HashMap<Vector2, TextureRegion> nonAnimatedContents;
	private HashMap<Vector2, TextureRegion> darkenedNonAnimatedContents;
	private HashMap<Vector2, Animation<TextureRegion>> animatedContents;
	private HashMap<Vector2, Animation<TextureRegion>> darkenedAnimatedContents;
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
		tiles = new ArrayList<DrawTile>();
		animatedContents = new HashMap<Vector2, Animation<TextureRegion>>();
		nonAnimatedContents = new HashMap<Vector2, TextureRegion>();
		darkenedNonAnimatedContents = new HashMap<Vector2, TextureRegion>();
		darkenedAnimatedContents = new HashMap<Vector2, Animation<TextureRegion>>();
		animations = new HashMap<String, Animation<TextureRegion>>();
		tileRegion = FeudalTactics.textureAtlas.findRegion("tile_bw");
		waterAnimation = getAnimationFromName("water");
		beachSandAnimation = getAnimationFromName("beach_sand");
		beachWaterAnimation = getAnimationFromName("beach_water");

		batch = new SpriteBatch();
		textureRegions = new HashMap<String, TextureRegion>();
		whiteLineStartPoints = new ArrayList<Vector2>();
		whiteLineEndPoints = new ArrayList<Vector2>();
		redLineStartPoints = new ArrayList<Vector2>();
		redLineEndPoints = new ArrayList<Vector2>();
		stateTime = 0F;
	}

	public void updateMap(GameState gameState) {
		// create tiles
		tiles.clear();
		nonAnimatedContents.clear();
		animatedContents.clear();
		darkenedNonAnimatedContents.clear();
		darkenedAnimatedContents.clear();
		whiteLineStartPoints.clear();
		whiteLineEndPoints.clear();
		redLineStartPoints.clear();
		redLineEndPoints.clear();
		darkenBeaches = gameState.getHeldObject() != null;
		for (Entry<Vector2, HexTile> hexTileEntry : (gameState.getMap().getTiles()).entrySet()) {
			Vector2 hexCoords = hexTileEntry.getKey();
			Vector2 mapCoords = getMapCoordinatesFromHexCoordinates(hexCoords);
			HexTile tile = hexTileEntry.getValue();
			// create tiles
			DrawTile drawTile = new DrawTile();
			drawTile.mapCoords = mapCoords;
			drawTile.color = tile.getPlayer().getColor();
			// create beaches on the edges
			ArrayList<HexTile> neighbors = gameState.getMap().getNeighborTiles(tile);
			if (neighbors.get(0) == null) {
				// top left
				drawTile.topLeftBeach = true;
			}
			if (neighbors.get(1) == null) {
				// top
				drawTile.topBeach = true;
			}
			if (neighbors.get(2) == null) {
				// top right
				drawTile.topRightBeach = true;
			}
			if (neighbors.get(3) == null) {
				// bottom right
				drawTile.bottomRightBeach = true;
			}
			if (neighbors.get(4) == null) {
				// bottom
				drawTile.bottomBeach = true;
			}
			if (neighbors.get(5) == null) {
				// bottom left
				drawTile.bottomLeftBeach = true;
			}
			// create content (units etc)
			MapObject tileContent = tile.getContent();
			if (tileContent != null) {
				boolean animate = false;
				if (tile.getKingdom() != null && tile.getKingdom().getPlayer() == gameState.getActivePlayer()) {
					if (ClassReflection.isAssignableFrom(tileContent.getClass(), Unit.class)
							&& ((Unit) tileContent).isCanAct()) {
						// animate units that can act
						animate = true;
					} else if (ClassReflection.isAssignableFrom(tileContent.getClass(), Capital.class)
							&& gameState.getActivePlayer() == tile.getKingdom().getPlayer()
							&& tile.getKingdom().getSavings() > Unit.COST) {
						// animate capitals if they can buy something
						animate = true;
					}
				}
				if (animate) {
					if (gameState.getActiveKingdom() != null && gameState.getHeldObject() != null
							&& (!InputValidator.checkPlaceOwn(gameState, gameState.getActivePlayer(), tile)
									&& !InputValidator.checkConquer(gameState, gameState.getActivePlayer(), tile)
									&& !InputValidator.checkCombineUnits(gameState, gameState.getActivePlayer(),
											tile))) {
						// darkened content
						darkenedAnimatedContents.put(
								new Vector2(mapCoords.x - HexMap.HEX_OUTER_RADIUS,
										mapCoords.y - HexMap.HEX_OUTER_RADIUS),
								getAnimationFromName(tileContent.getSpriteName()));
					} else {
						animatedContents.put(
								new Vector2(mapCoords.x - HexMap.HEX_OUTER_RADIUS,
										mapCoords.y - HexMap.HEX_OUTER_RADIUS),
								getAnimationFromName(tileContent.getSpriteName()));
					}
				} else {
					if (gameState.getActiveKingdom() != null && gameState.getHeldObject() != null
							&& (!InputValidator.checkPlaceOwn(gameState, gameState.getActivePlayer(), tile)
									&& !InputValidator.checkConquer(gameState, gameState.getActivePlayer(), tile)
									&& !InputValidator.checkCombineUnits(gameState, gameState.getActivePlayer(),
											tile))) {
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
				// darken the tile if placing is impossible
				if (gameState.getHeldObject() != null
						&& !InputValidator.checkPlaceOwn(gameState, gameState.getActivePlayer(), tile)
						&& !InputValidator.checkCombineUnits(gameState, gameState.getActivePlayer(), tile)) {
					drawTile.darken = true;
				}
			} else if (gameState.getHeldObject() != null) {
				// red lines for indicating if able to conquer
				if (InputValidator.checkConquer(gameState, gameState.getActivePlayer(), tile)) {
					int index = 0;
					for (HexTile neighborTile : gameState.getMap().getNeighborTiles(tile)) {
						if (neighborTile == null
								|| (neighborTile.getKingdom() != gameState.getActiveKingdom() && !InputValidator
										.checkConquer(gameState, gameState.getActivePlayer(), neighborTile))) {
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
					drawTile.darken = true;
				}
			}
			tiles.add(drawTile);
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
																						// object
		HashMap<Vector2, TextureRegion> darkenedFrames = new HashMap<Vector2, TextureRegion>(); // current frame for
																								// each map object
		batch.setProjectionMatrix(camera.combined);
		stateTime += Gdx.graphics.getDeltaTime();
		// get the correct frames
		for (Entry<Vector2, Animation<TextureRegion>> content : animatedContents.entrySet()) {
			frames.put(content.getKey(), ((Animation<TextureRegion>) content.getValue()).getKeyFrame(stateTime, true));
		}
		for (Entry<Vector2, Animation<TextureRegion>> content : darkenedAnimatedContents.entrySet()) {
			darkenedFrames.put(content.getKey(),
					((Animation<TextureRegion>) content.getValue()).getKeyFrame(stateTime, true));
		}
		TextureRegion waterRegion = waterAnimation.getKeyFrame(stateTime, true);
		TextureRegion bottomRightBeachSandRegion = beachSandAnimation.getKeyFrame(stateTime, true);
		TextureRegion bottomLeftBeachSandRegion = new TextureRegion(bottomRightBeachSandRegion);
		bottomLeftBeachSandRegion.flip(true, false);
		TextureRegion topLeftBeachSandRegion = new TextureRegion(bottomRightBeachSandRegion);
		topLeftBeachSandRegion.flip(true, true);
		TextureRegion topRightBeachSandRegion = new TextureRegion(bottomRightBeachSandRegion);
		topRightBeachSandRegion.flip(false, true);
		TextureRegion bottomRightBeachWaterRegion = beachWaterAnimation.getKeyFrame(stateTime, true);
		TextureRegion bottomLeftBeachWaterRegion = new TextureRegion(bottomRightBeachWaterRegion);
		bottomLeftBeachWaterRegion.flip(true, false);
		TextureRegion topLeftBeachWaterRegion = new TextureRegion(bottomRightBeachWaterRegion);
		topLeftBeachWaterRegion.flip(true, true);
		TextureRegion topRightBeachWaterRegion = new TextureRegion(bottomRightBeachWaterRegion);
		topRightBeachWaterRegion.flip(false, true);
		Vector3 bottomLeftPoint = camera.unproject(new Vector3(0, camera.viewportHeight, 0));
		// float objectSize = height * SPRITE_SIZE_MULTIPLIER;
		float itemOffsetX = width * 0.0F;
		float itemOffsetY = height * -0.075F;

		// colors for normal and darkened stuff
		Color normalColor = new Color(1, 1, 1, 1);
		Color darkenedColor = new Color(0, 0, 0, 0.4F);
		batch.setColor(normalColor);

		batch.begin();

		// draw sea background
		for (int i = 0; (i - 2) * WATER_TILE_SIZE <= camera.viewportWidth * camera.zoom; i++) {
			for (int j = 0; (j - 2) * WATER_TILE_SIZE <= camera.viewportHeight * camera.zoom; j++) {
				batch.draw(waterRegion,
						bottomLeftPoint.x + i * WATER_TILE_SIZE - camera.position.x % WATER_TILE_SIZE - WATER_TILE_SIZE,
						bottomLeftPoint.y + j * WATER_TILE_SIZE - camera.position.y % WATER_TILE_SIZE - WATER_TILE_SIZE,
						WATER_TILE_SIZE, WATER_TILE_SIZE);
			}
		}

		// draw all the beaches first because they would cover some of the tiles
		// otherwise
		// beach water first (should not cover any sand)
		Color beachWaterColor = new Color(BEACH_WATER_COLOR);
		if (darkenBeaches) {
			beachWaterColor.mul(0.75F, 0.75F, 0.75F, 1);
		}
		batch.setColor(beachWaterColor);
		for (DrawTile tile : tiles) {
			if (tile.bottomBeach || tile.bottomRightBeach) {
				batch.draw(bottomRightBeachWaterRegion, tile.mapCoords.x, tile.mapCoords.y - height * 2, width * 2,
						height * 2);
			}
			if (tile.bottomBeach || tile.bottomLeftBeach) {
				batch.draw(bottomLeftBeachWaterRegion, tile.mapCoords.x - width * 2, tile.mapCoords.y - height * 2,
						width * 2, height * 2);
			}
			if (tile.topBeach || tile.topLeftBeach) {
				batch.draw(topLeftBeachWaterRegion, tile.mapCoords.x - width * 2, tile.mapCoords.y, width * 2,
						height * 2);
			}
			if (tile.topBeach || tile.topRightBeach) {
				batch.draw(topRightBeachWaterRegion, tile.mapCoords.x, tile.mapCoords.y, width * 2, height * 2);
			}
		}
		// beach sand
		Color beachSandColor = new Color(normalColor);
		if (darkenBeaches) {
			beachSandColor.mul(0.5F, 0.5F, 0.5F, 1);
		}
		batch.setColor(beachSandColor);
		for (DrawTile tile : tiles) {
			if (tile.bottomBeach || tile.bottomRightBeach) {
				batch.draw(bottomRightBeachSandRegion, tile.mapCoords.x, tile.mapCoords.y - height, width, height);
			}
			if (tile.bottomBeach || tile.bottomLeftBeach) {
				batch.draw(bottomLeftBeachSandRegion, tile.mapCoords.x - width, tile.mapCoords.y - height, width,
						height);
			}
			if (tile.topBeach || tile.topLeftBeach) {
				batch.draw(topLeftBeachSandRegion, tile.mapCoords.x - width, tile.mapCoords.y, width, height);
			}
			if (tile.topBeach || tile.topRightBeach) {
				batch.draw(topRightBeachSandRegion, tile.mapCoords.x, tile.mapCoords.y, width, height);
			}
		}

		// draw all the tiles
		for (DrawTile tile : tiles) {
			Color color = new Color(tile.color);
			// darken tile
			if (tile.darken) {
				color.mul(0.5F, 0.5F, 0.5F, 1);
			}
			batch.setColor(color);
			batch.draw(tileRegion, tile.mapCoords.x - width / 2, tile.mapCoords.y - height / 2, width, height);
		}

		// draw all the animated contents
		batch.setColor(normalColor);
		for (Entry<Vector2, TextureRegion> currentFrame : frames.entrySet()) {
			batch.draw(currentFrame.getValue(), currentFrame.getKey().x - itemOffsetX,
					currentFrame.getKey().y - itemOffsetY, width, height);
		}
		// draw the darkened contents like normal but then draw a shadow over them
		for (Entry<Vector2, TextureRegion> currentFrame : darkenedFrames.entrySet()) {
			batch.draw(currentFrame.getValue(), currentFrame.getKey().x - itemOffsetX,
					currentFrame.getKey().y - itemOffsetY, width, height);
		}
		batch.setColor(darkenedColor);
		for (Entry<Vector2, TextureRegion> currentFrame : darkenedFrames.entrySet()) {
			batch.draw(currentFrame.getValue(), currentFrame.getKey().x - itemOffsetX,
					currentFrame.getKey().y - itemOffsetY, width, height);
		}
		batch.setColor(normalColor);
		// draw all the non-animated contents
		for (Entry<Vector2, TextureRegion> content : nonAnimatedContents.entrySet()) {
			batch.draw(content.getValue(), content.getKey().x - itemOffsetX, content.getKey().y - itemOffsetY, width,
					height);
		}
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

	public void placeCameraForFullMapView(GameState gameState, long marginLeftPx, long marginBottomPx,
			long marginRightPx, long marginTopPx) {
		System.out.println(camera.viewportWidth);
		System.out.println(camera.viewportHeight);
		MapDimensions dims = gameState.getMap().getMapDimensionsInWorldCoords();
		// calculate how much 1 pixel is in world coordinates
		Vector3 testWorldCoordinates1 = camera.unproject(new Vector3(0, 0, 0));
		Vector3 testWorldCoordinates2 = camera.unproject(new Vector3(1, 0, 0));
		float onePixelInWorldSpace = testWorldCoordinates2.x - testWorldCoordinates1.x;
		// get the factors needed to adjust the camera zoom
		float useViewportWidth = (camera.viewportWidth - marginLeftPx - marginRightPx);
		float useViewportHeight = (camera.viewportHeight - marginBottomPx - marginTopPx);
		float xFactor = (dims.width / onePixelInWorldSpace) / useViewportWidth; // lol
		float yFactor = (dims.height / onePixelInWorldSpace) / useViewportHeight;
		// use the bigger factor because both dimensions must fit
		float scaleFactor = Math.max(xFactor, yFactor);
		camera.zoom *= scaleFactor;
		camera.update();
		// re-calculate how much 1 pixel is in world coordinates after zooming
		testWorldCoordinates1 = camera.unproject(new Vector3(0, 0, 0));
		testWorldCoordinates2 = camera.unproject(new Vector3(1, 0, 0));
		onePixelInWorldSpace = testWorldCoordinates2.x - testWorldCoordinates1.x;
		// move camera to center
		camera.position.set(dims.center, 0);
		// offset to apply the margin
		camera.translate(marginRightPx * onePixelInWorldSpace / 2 - marginLeftPx * onePixelInWorldSpace / 2,
				marginTopPx * onePixelInWorldSpace / 2 - marginBottomPx * onePixelInWorldSpace / 2);
		camera.update();
	}

	public void dispose() {
		batch.dispose();
	}

	public void resize() {
		batch.getProjectionMatrix().setToOrtho2D(0, 0, width, height);
	}

	private class DrawTile {
		public Vector2 mapCoords;
		public Color color;
		public boolean darken = false;
		public boolean topLeftBeach = false;
		public boolean topBeach = false;
		public boolean topRightBeach = false;
		public boolean bottomRightBeach = false;
		public boolean bottomBeach = false;
		public boolean bottomLeftBeach = false;
	}
}

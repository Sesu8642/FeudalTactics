// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.google.common.collect.ImmutableList;
import de.sesu8642.feudaltactics.lib.gamestate.*;
import de.sesu8642.feudaltactics.lib.gamestate.Player.Type;

import java.util.*;
import java.util.Map.Entry;

/**
 * Renderer for the map and the water.
 */
public class MapRenderer {

    public static final float SPRITE_SIZE_MULTIPLIER = 1.05F;
    public static final float LINE_EXTENSION = 0.14F;
    public static final float WATER_TILE_SIZE = 12;
    public static final float SHIELD_SIZE = 2;
    public static final Color BEACH_WATER_COLOR = new Color(0F, 1F, 1F, 1F);
    public static final float HEXTILE_WIDTH = HexMapHelper.HEX_OUTER_RADIUS * 2;
    public static final float HEXTILE_HEIGHT = HexMapHelper.HEX_OUTER_RADIUS * (float) Math.sqrt(3);
    public static final List<Color> PLAYER_COLOR_PALETTE = ImmutableList.of(new Color(0.2F, 0.45F, 0.8F, 1),
            new Color(0.75F, 0.5F, 0F, 1), new Color(1F, 0.67F, 0.67F, 1), new Color(1F, 1F, 0F, 1),
            new Color(1F, 1F, 1F, 1), new Color(0F, 1F, 0F, 1));
    /**
     * If the stateTime reaches this value, it will be reduced by this value.
     * The reason is that the stateTime must stay relatively small because of float limitations.
     * See https://github.com/libgdx/libgdx/issues/7536.
     * The subtracted amount must be a multiple of the animation duration.
     * Equal to one day in seconds.
     */
    private static final float STATE_TIME_THRESHOLD = 24 * 60 * 60f;
    /**
     * Map of sprite names and their sprites. Functions as cache to avoid frequent
     * lookups.
     */
    private final Map<String, TextureRegion> textureRegions = new HashMap<>();

    /**
     * Map of sprite names and their animations. Functions as cache to avoid
     * frequent lookups.
     */
    private final Map<String, Animation<TextureRegion>> animations = new HashMap<>();
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final SpriteBatch spriteBatch;
    private final TextureAtlas textureAtlas;
    private final TextureRegion tileRegion;
    private final TextureRegion shieldRegion;
    private final Animation<TextureRegion> waterAnimation;
    private final Animation<TextureRegion> beachSandAnimation;
    private final Animation<TextureRegion> beachWaterAnimation;
    // stuff that is to be drawn
    // keeping those in separate, flat collections is more efficient when rendering
    private final Map<Vector2, DrawTile> tiles = new HashMap<>();
    private final Map<Vector2, TextureRegion> nonAnimatedContents = new HashMap<>();
    private final Map<Vector2, TextureRegion> darkenedNonAnimatedContents = new HashMap<>();
    private final Map<Vector2, Animation<TextureRegion>> animatedContents = new HashMap<>();
    private final Map<Vector2, Animation<TextureRegion>> darkenedAnimatedContents = new HashMap<>();
    private final Map<Vector2, Boolean> shields = new HashMap<>();
    private final List<Vector2> whiteLineStartPoints = new ArrayList<>();
    private final List<Vector2> whiteLineEndPoints = new ArrayList<>();
    private final List<Vector2> redLineStartPoints = new ArrayList<>();
    private final List<Vector2> redLineEndPoints = new ArrayList<>();
    // settings
    private final boolean enableDeepWaterRendering;
    private float stateTime = 0F; // for keeping animations at the correct pace
    private boolean darkenBeaches;

    /**
     * Constructor.
     *
     * @param camera        in-game camera
     * @param textureAtlas  texture atlas contaiing the sprites
     * @param shapeRenderer renderer for shapes
     * @param spriteBatch   spriteBatch to use for rendering
     */
    public MapRenderer(OrthographicCamera camera, TextureAtlas textureAtlas, ShapeRenderer shapeRenderer,
                       SpriteBatch spriteBatch, boolean enableDeepWaterRendering) {
        this.camera = camera;
        this.shapeRenderer = shapeRenderer;
        this.spriteBatch = spriteBatch;
        this.textureAtlas = textureAtlas;
        this.enableDeepWaterRendering = enableDeepWaterRendering;

        tileRegion = textureAtlas.findRegion(HexTile.SPRITE_NAME);
        shieldRegion = textureAtlas.findRegion("shield");
        waterAnimation = getAnimationFromName("water");
        beachSandAnimation = getAnimationFromName("beach_sand");
        beachWaterAnimation = getAnimationFromName("beach_water");
    }

    /**
     * Updates the map that is rendered.
     *
     * @param gameState game state containing the map
     */
    // this method and rendering must be synchronized to not happen at the same time
    public synchronized void updateMap(GameState gameState) {
        // create tiles
        tiles.clear();
        nonAnimatedContents.clear();
        animatedContents.clear();
        darkenedNonAnimatedContents.clear();
        darkenedAnimatedContents.clear();
        shields.clear();
        whiteLineStartPoints.clear();
        whiteLineEndPoints.clear();
        redLineStartPoints.clear();
        redLineEndPoints.clear();
        darkenBeaches = gameState.getHeldObject() != null;
        for (Entry<Vector2, HexTile> hexTileEntry : gameState.getMap().entrySet()) {
            Vector2 hexCoords = hexTileEntry.getKey();
            Vector2 mapCoords = getMapCoordinatesFromHexCoordinates(hexCoords);
            HexTile tile = hexTileEntry.getValue();

            // create tiles
            DrawTile drawTile = new DrawTile();
            drawTile.mapCoords = mapCoords;
            drawTile.color = PLAYER_COLOR_PALETTE.get(tile.getPlayer().getPlayerIndex());
            // create beaches on the edges
            List<HexTile> neighbors = HexMapHelper.getNeighborTiles(gameState.getMap(), tile);
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

            // create lines for highlighting active kingdom
            if (gameState.getActiveKingdom() != null && tile.getKingdom() != null
                    && tile.getKingdom() == gameState.getActiveKingdom()) {
                int index = 0;
                for (HexTile neighborTile : HexMapHelper.getNeighborTiles(gameState.getMap(), tile)) {
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
                        && !InputValidationHelper.checkPlaceOwn(gameState, gameState.getActivePlayer(), tile)
                        && !InputValidationHelper.checkCombineUnits(gameState, gameState.getActivePlayer(), tile)) {
                    drawTile.darken = true;
                }
            } else if (gameState.getHeldObject() != null) {
                // red lines for indicating if able to conquer
                if (InputValidationHelper.checkConquer(gameState, gameState.getActivePlayer(), tile)) {
                    int index = 0;
                    for (HexTile neighborTile : HexMapHelper.getNeighborTiles(gameState.getMap(), tile)) {
                        if (neighborTile == null
                                || (neighborTile.getKingdom() != gameState.getActiveKingdom() && !InputValidationHelper
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
            } else if (gameState.getActivePlayer().getType() == Type.LOCAL_BOT
                    && gameState.getActiveKingdom() != null) {
                drawTile.darken = true;
            }
            tiles.put(tile.getPosition(), drawTile);

            // create content (units etc)
            TileContent tileContent = tile.getContent();
            if (tileContent != null) {
                boolean animate = false;
                if (tile.getKingdom() != null && tile.getKingdom().getPlayer() == gameState.getActivePlayer()) {
                    if (ClassReflection.isAssignableFrom(Unit.class, tileContent.getClass())
                            && ((Unit) tileContent).isCanAct()) {
                        // animate units that can act
                        animate = true;
                    } else if (ClassReflection.isAssignableFrom(Capital.class, tileContent.getClass())
                            && gameState.getActivePlayer() == tile.getKingdom().getPlayer()
                            && tile.getKingdom().getSavings() > Unit.COST) {
                        // animate capitals if they can buy something
                        animate = true;
                    }
                }
                if (animate) {
                    if (drawTile.darken) {
                        // darkened content
                        darkenedAnimatedContents.put(
                                new Vector2(mapCoords.x - HexMapHelper.HEX_OUTER_RADIUS,
                                        mapCoords.y - HexMapHelper.HEX_OUTER_RADIUS),
                                getAnimationFromName(tileContent.getSpriteName()));
                    } else {
                        animatedContents.put(
                                new Vector2(mapCoords.x - HexMapHelper.HEX_OUTER_RADIUS,
                                        mapCoords.y - HexMapHelper.HEX_OUTER_RADIUS),
                                getAnimationFromName(tileContent.getSpriteName()));
                    }
                } else {
                    if (drawTile.darken
                            // darken own units that have already acted
                            || (tile.getPlayer() == gameState.getActivePlayer() && gameState.getHeldObject() == null
                            && tile.getContent() != null
                            && ClassReflection.isAssignableFrom(Unit.class, tileContent.getClass())
                            && !((Unit) tile.getContent()).isCanAct())) {
                        // darkened content
                        darkenedNonAnimatedContents.put(
                                new Vector2(mapCoords.x - HexMapHelper.HEX_OUTER_RADIUS,
                                        mapCoords.y - HexMapHelper.HEX_OUTER_RADIUS),
                                getTextureRegionFromName(tileContent.getSpriteName()));
                    } else {
                        nonAnimatedContents.put(
                                new Vector2(mapCoords.x - HexMapHelper.HEX_OUTER_RADIUS,
                                        mapCoords.y - HexMapHelper.HEX_OUTER_RADIUS),
                                getTextureRegionFromName(tileContent.getSpriteName()));
                    }
                }

            }

            if (gameState.getHeldObject() != null) {
                // create protection indicators
                int protectionLevel = GameStateHelper.getProtectionLevel(gameState, tile);
                switch (protectionLevel) {
                    case 4:
                        shields.put(new Vector2(mapCoords.x - 2 * SHIELD_SIZE, mapCoords.y - SHIELD_SIZE / 2),
                                drawTile.darken);
                        shields.put(new Vector2(mapCoords.x - SHIELD_SIZE, mapCoords.y - SHIELD_SIZE / 2),
                                drawTile.darken);
                        shields.put(new Vector2(mapCoords.x, mapCoords.y - SHIELD_SIZE / 2), drawTile.darken);
                        shields.put(new Vector2(mapCoords.x + SHIELD_SIZE, mapCoords.y - SHIELD_SIZE / 2),
                                drawTile.darken);
                        break;
                    case 3:
                        shields.put(new Vector2(mapCoords.x - SHIELD_SIZE * 1.5F, mapCoords.y - SHIELD_SIZE / 2),
                                drawTile.darken);
                        shields.put(new Vector2(mapCoords.x - SHIELD_SIZE / 2, mapCoords.y - SHIELD_SIZE / 2),
                                drawTile.darken);
                        shields.put(new Vector2(mapCoords.x + SHIELD_SIZE / 2, mapCoords.y - SHIELD_SIZE / 2),
                                drawTile.darken);
                        break;
                    case 2:
                        shields.put(new Vector2(mapCoords.x - SHIELD_SIZE, mapCoords.y - SHIELD_SIZE / 2),
                                drawTile.darken);
                        shields.put(new Vector2(mapCoords.x, mapCoords.y - SHIELD_SIZE / 2), drawTile.darken);
                        break;
                    case 1:
                        shields.put(new Vector2(mapCoords.x - SHIELD_SIZE / 2, mapCoords.y - SHIELD_SIZE / 2),
                                drawTile.darken);
                        break;
                    case 0:
                        break;
                    default:
                        throw new AssertionError("Unexpected protection level " + protectionLevel);
                }
            }

        }
    }

    private Collection<Line> lineToDottedLine(Line line) {
        final int partAmount = 3;
        Collection<Line> resultLines = new HashSet<>();
        float lineDiffX = line.end.x - line.start.x;
        float lineDiffY = line.end.y - line.start.y;
        for (int i = 1; i <= partAmount; i += 2) {
            Line linePart = new Line();
            linePart.start = new Vector2(line.start.x + (lineDiffX / partAmount) * (i - 1),
                    line.start.y + (lineDiffY / partAmount) * (i - 1));
            linePart.end = new Vector2(line.start.x + (lineDiffX / partAmount) * i,
                    line.start.y + (lineDiffY / partAmount) * i);
            resultLines.add(linePart);
        }
        return resultLines;
    }

    private Line getNeighborLine(Vector2 mapCoords, int index) {
        Vector2 start;
        Vector2 end;
        switch (index) {
            case 0:
                // top left
                start = new Vector2(mapCoords.x - HEXTILE_WIDTH / 4 + LINE_EXTENSION,
                        mapCoords.y + HEXTILE_HEIGHT / 2 + LINE_EXTENSION);
                end = new Vector2(mapCoords.x - HEXTILE_WIDTH / 2 - LINE_EXTENSION, mapCoords.y - LINE_EXTENSION);
                break;
            case 1:
                // top
                start = new Vector2(mapCoords.x - HEXTILE_WIDTH / 4 - LINE_EXTENSION, mapCoords.y + HEXTILE_HEIGHT / 2);

                end = new Vector2(mapCoords.x + HEXTILE_WIDTH / 4 + LINE_EXTENSION, mapCoords.y + HEXTILE_HEIGHT / 2);
                break;
            case 2:
                // top right
                start = new Vector2(mapCoords.x + HEXTILE_WIDTH / 4 - LINE_EXTENSION,
                        mapCoords.y + HEXTILE_HEIGHT / 2 + LINE_EXTENSION);
                end = new Vector2(mapCoords.x + HEXTILE_WIDTH / 2 + LINE_EXTENSION, mapCoords.y - LINE_EXTENSION);
                break;
            case 3:
                // bottom right
                start = new Vector2(mapCoords.x + HEXTILE_WIDTH / 4 - LINE_EXTENSION,
                        mapCoords.y - HEXTILE_HEIGHT / 2 - LINE_EXTENSION);
                end = new Vector2(mapCoords.x + HEXTILE_WIDTH / 2 + LINE_EXTENSION, mapCoords.y + LINE_EXTENSION);
                break;
            case 4:
                // bottom
                start = new Vector2(mapCoords.x - HEXTILE_WIDTH / 4 - LINE_EXTENSION, mapCoords.y - HEXTILE_HEIGHT / 2);
                end = new Vector2(mapCoords.x + HEXTILE_WIDTH / 4 + LINE_EXTENSION, mapCoords.y - HEXTILE_HEIGHT / 2);
                break;
            case 5:
                // bottom left
                start = new Vector2(mapCoords.x - HEXTILE_WIDTH / 4 + LINE_EXTENSION,
                        mapCoords.y - HEXTILE_HEIGHT / 2 - LINE_EXTENSION);
                end = new Vector2(mapCoords.x - HEXTILE_WIDTH / 2 - LINE_EXTENSION, mapCoords.y + LINE_EXTENSION);
                break;
            default:
                throw new AssertionError(String.format("Cannot map index %s to a hexagon side", index));
        }
        Line result = new Line();
        result.start = start;
        result.end = end;
        return result;
    }

    private TextureRegion getTextureRegionFromName(String name) {
        return textureRegions.computeIfAbsent(name, textureAtlas::findRegion);
    }

    private Animation<TextureRegion> getAnimationFromName(String name) {
        return animations.computeIfAbsent(name, n2 -> new Animation<TextureRegion>(1F, textureAtlas.findRegions(n2)));
    }

    /**
     * Renders the map.
     */
    public synchronized void render() {
        HashMap<Vector2, TextureRegion> frames = new HashMap<>(); // current frame for each map object
        HashMap<Vector2, TextureRegion> darkenedFrames = new HashMap<>(); // current frame for each map object
        spriteBatch.setProjectionMatrix(camera.combined);
        updateStateTime(Gdx.graphics.getDeltaTime());
        // get the correct frames
        for (Entry<Vector2, Animation<TextureRegion>> content : animatedContents.entrySet()) {
            frames.put(content.getKey(), (content.getValue()).getKeyFrame(stateTime, true));
        }
        for (Entry<Vector2, Animation<TextureRegion>> content : darkenedAnimatedContents.entrySet()) {
            darkenedFrames.put(content.getKey(), content.getValue().getKeyFrame(stateTime, true));
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

        Vector2 waterOriginPoint = calculateWaterOriginPoint();

        float itemOffsetX = HEXTILE_WIDTH * 0.0F;
        float itemOffsetY = HEXTILE_HEIGHT * -0.075F;

        // colors for normal and darkened stuff
        final Color normalColor = new Color(1, 1, 1, 1);
        final Color darkenedColor = new Color(0, 0, 0, 0.4F);
        spriteBatch.setColor(normalColor);

        spriteBatch.begin();

        if (enableDeepWaterRendering) {
            // draw sea background
            // subtract -3 in the loop condition because some room is needed due to the
            // movement offset on the top right
            // start with -1 to do the same on the bottom left
            for (int i = -1; (i - 3) * WATER_TILE_SIZE <= camera.viewportWidth * camera.zoom; i++) {
                for (int j = -1; (j - 3) * WATER_TILE_SIZE <= camera.viewportHeight * camera.zoom; j++) {
                    // there is a bug that causes tiny gaps between the water tiles on the sides
                    // (but not top/bottom); probably some rounding issue
                    // a similar thing happens with TiledDrawable so maybe look into that and report
                    // it later
                    // the 0.01F is a hack that seems to mitigate this
                    spriteBatch.draw(waterRegion, waterOriginPoint.x + i * WATER_TILE_SIZE,
                            waterOriginPoint.y + j * WATER_TILE_SIZE, WATER_TILE_SIZE + 0.01F, WATER_TILE_SIZE);
                }
            }
        } else {
            // when making art like the logo, use black background instead of water
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        }

        // draw all the beaches first because they would cover some of the tiles
        // otherwise
        // beach water first (should not cover any sand)
        Color beachWaterColor = new Color(BEACH_WATER_COLOR);
        if (darkenBeaches) {
            beachWaterColor.mul(0.75F, 0.75F, 0.75F, 1);
        }
        spriteBatch.setColor(beachWaterColor);
        for (DrawTile tile : tiles.values()) {
            if (tile.bottomBeach || tile.bottomRightBeach) {
                spriteBatch.draw(bottomRightBeachWaterRegion, tile.mapCoords.x, tile.mapCoords.y - HEXTILE_HEIGHT * 2,
                        HEXTILE_WIDTH * 2, HEXTILE_HEIGHT * 2);
            }
            if (tile.bottomBeach || tile.bottomLeftBeach) {
                spriteBatch.draw(bottomLeftBeachWaterRegion, tile.mapCoords.x - HEXTILE_WIDTH * 2,
                        tile.mapCoords.y - HEXTILE_HEIGHT * 2, HEXTILE_WIDTH * 2, HEXTILE_HEIGHT * 2);
            }
            if (tile.topBeach || tile.topLeftBeach) {
                spriteBatch.draw(topLeftBeachWaterRegion, tile.mapCoords.x - HEXTILE_WIDTH * 2, tile.mapCoords.y,
                        HEXTILE_WIDTH * 2, HEXTILE_HEIGHT * 2);
            }
            if (tile.topBeach || tile.topRightBeach) {
                spriteBatch.draw(topRightBeachWaterRegion, tile.mapCoords.x, tile.mapCoords.y, HEXTILE_WIDTH * 2,
                        HEXTILE_HEIGHT * 2);
            }
        }
        // beach sand
        Color beachSandColor = new Color(normalColor);
        if (darkenBeaches) {
            beachSandColor.mul(0.5F, 0.5F, 0.5F, 1);
        }
        spriteBatch.setColor(beachSandColor);
        for (DrawTile tile : tiles.values()) {
            if (tile.bottomBeach || tile.bottomRightBeach) {
                spriteBatch.draw(bottomRightBeachSandRegion, tile.mapCoords.x, tile.mapCoords.y - HEXTILE_HEIGHT,
                        HEXTILE_WIDTH, HEXTILE_HEIGHT);
            }
            if (tile.bottomBeach || tile.bottomLeftBeach) {
                spriteBatch.draw(bottomLeftBeachSandRegion, tile.mapCoords.x - HEXTILE_WIDTH,
                        tile.mapCoords.y - HEXTILE_HEIGHT, HEXTILE_WIDTH, HEXTILE_HEIGHT);
            }
            if (tile.topBeach || tile.topLeftBeach) {
                spriteBatch.draw(topLeftBeachSandRegion, tile.mapCoords.x - HEXTILE_WIDTH, tile.mapCoords.y,
                        HEXTILE_WIDTH, HEXTILE_HEIGHT);
            }
            if (tile.topBeach || tile.topRightBeach) {
                spriteBatch.draw(topRightBeachSandRegion, tile.mapCoords.x, tile.mapCoords.y, HEXTILE_WIDTH,
                        HEXTILE_HEIGHT);
            }
        }

        // draw all the tiles
        for (DrawTile tile : tiles.values()) {
            Color color = new Color(tile.color);
            // darken tile
            if (tile.darken) {
                color.mul(0.5F, 0.5F, 0.5F, 1);
            }
            spriteBatch.setColor(color);
            spriteBatch.draw(tileRegion, tile.mapCoords.x - HEXTILE_WIDTH / 2, tile.mapCoords.y - HEXTILE_HEIGHT / 2,
                    HEXTILE_WIDTH, HEXTILE_HEIGHT);
        }

        // draw all the shields
        Color shieldColor = new Color(normalColor);
        shieldColor.sub(0, 0, 0, 0.7F);
        Color darkenedShieldColor = new Color(shieldColor);
        darkenedShieldColor.mul(0.5F, 0.5F, 0.5F, 1);
        for (Entry<Vector2, Boolean> shield : shields.entrySet()) {
            if (Boolean.TRUE.equals(shield.getValue())) {
                spriteBatch.setColor(darkenedShieldColor);
            } else {
                spriteBatch.setColor(shieldColor);
            }
            spriteBatch.draw(shieldRegion, shield.getKey().x, shield.getKey().y, SHIELD_SIZE, SHIELD_SIZE);
        }

        // draw all the animated contents
        spriteBatch.setColor(normalColor);
        for (Entry<Vector2, TextureRegion> currentFrame : frames.entrySet()) {
            spriteBatch.draw(currentFrame.getValue(), currentFrame.getKey().x - itemOffsetX,
                    currentFrame.getKey().y - itemOffsetY, HEXTILE_WIDTH, HEXTILE_HEIGHT);
        }
        // draw the darkened contents like normal but then draw a shadow over them
        for (Entry<Vector2, TextureRegion> currentFrame : darkenedFrames.entrySet()) {
            spriteBatch.draw(currentFrame.getValue(), currentFrame.getKey().x - itemOffsetX,
                    currentFrame.getKey().y - itemOffsetY, HEXTILE_WIDTH, HEXTILE_HEIGHT);
        }
        spriteBatch.setColor(darkenedColor);
        for (Entry<Vector2, TextureRegion> currentFrame : darkenedFrames.entrySet()) {
            spriteBatch.draw(currentFrame.getValue(), currentFrame.getKey().x - itemOffsetX,
                    currentFrame.getKey().y - itemOffsetY, HEXTILE_WIDTH, HEXTILE_HEIGHT);
        }
        spriteBatch.setColor(normalColor);
        // draw all the non-animated contents
        for (Entry<Vector2, TextureRegion> content : nonAnimatedContents.entrySet()) {
            spriteBatch.draw(content.getValue(), content.getKey().x - itemOffsetX, content.getKey().y - itemOffsetY,
                    HEXTILE_WIDTH, HEXTILE_HEIGHT);
        }
        for (Entry<Vector2, TextureRegion> content : darkenedNonAnimatedContents.entrySet()) {
            spriteBatch.draw(content.getValue(), content.getKey().x - itemOffsetX, content.getKey().y - itemOffsetY,
                    HEXTILE_WIDTH, HEXTILE_HEIGHT);
        }
        spriteBatch.setColor(darkenedColor);
        for (Entry<Vector2, TextureRegion> content : darkenedNonAnimatedContents.entrySet()) {
            spriteBatch.draw(content.getValue(), content.getKey().x - itemOffsetX, content.getKey().y - itemOffsetY,
                    HEXTILE_WIDTH, HEXTILE_HEIGHT);
        }
        spriteBatch.end();
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

    /**
     * Calculates map coords from hex coords.
     *
     * @param hexCoords hex coords
     * @return map coords
     */
    public Vector2 getMapCoordinatesFromHexCoordinates(Vector2 hexCoords) {
        float x = 0.75F * HEXTILE_WIDTH * hexCoords.x;
        float y = (float) (HexMapHelper.HEX_OUTER_RADIUS
                * (Math.sqrt(3) / 2 * hexCoords.x + Math.sqrt(3) * (-hexCoords.y - hexCoords.x)));
        return new Vector2(x, y);
    }

    private Vector2 calculateWaterOriginPoint() {
        float waterTileSizePx = WATER_TILE_SIZE / camera.zoom;
        int waterTilesNeededToCoverScreenHorizonally = (int) Math
                .ceil((camera.viewportWidth * camera.zoom) / WATER_TILE_SIZE);
        int waterTilesNeededToCoverScreenVertically = (int) Math
                .ceil((camera.viewportHeight * camera.zoom) / WATER_TILE_SIZE);
        // make sure the tile number is even or there will be visible steps when zooming
        if (waterTilesNeededToCoverScreenHorizonally % 2 != 0) {
            waterTilesNeededToCoverScreenHorizonally += 1;
        }
        if (waterTilesNeededToCoverScreenVertically % 2 != 0) {
            waterTilesNeededToCoverScreenVertically += 1;
        }
        float waterOffsetForZoomInPxX = (waterTilesNeededToCoverScreenHorizonally * waterTileSizePx
                - camera.viewportWidth) / 2;
        float waterOffsetForZoomInPxY = (waterTilesNeededToCoverScreenVertically * waterTileSizePx
                - camera.viewportHeight) / 2;
        float waterOffsetForMovementX = camera.position.x % WATER_TILE_SIZE;
        float waterOffsetForMovementY = camera.position.y % WATER_TILE_SIZE;
        // bottom left point from where the water is drawn
        Vector3 waterOriginPoint = camera
                .unproject(new Vector3(-waterOffsetForZoomInPxX, camera.viewportHeight + waterOffsetForZoomInPxY, 0));
        waterOriginPoint.x -= waterOffsetForMovementX;
        waterOriginPoint.y -= waterOffsetForMovementY;
        return new Vector2(waterOriginPoint.x, waterOriginPoint.y);
    }

    /**
     * Places the camera in a way so that the whole map is visible.
     *
     * @param gameState      game state containing the map
     * @param marginLeftPx   margin to leave on the left in pixel
     * @param marginBottomPx margin to leave on the bottom in pixel
     * @param marginRightPx  margin to leave on the right in pixel
     * @param marginTopPx    margin to leave on the top in pixel
     */
    public void placeCameraForFullMapView(GameState gameState, long marginLeftPx, long marginBottomPx,
                                          long marginRightPx, long marginTopPx) {
        MapDimensions dims = HexMapHelper.getMapDimensionsInWorldCoords(gameState.getMap());
        // get the factors needed to adjust the camera zoom
        float useViewportWidth = (camera.viewportWidth - marginLeftPx - marginRightPx);
        float useViewportHeight = (camera.viewportHeight - marginBottomPx - marginTopPx);
        final float xFactor = (dims.getWidth() / camera.zoom) / useViewportWidth; // lol
        final float yFactor = (dims.getHeight() / camera.zoom) / useViewportHeight;
        // use the bigger factor because both dimensions must fit
        float scaleFactor = Math.max(xFactor, yFactor);
        camera.zoom *= scaleFactor;
        camera.update();
        // move camera to center
        camera.position.set(dims.getCenter(), 0);
        // offset to apply the margin
        camera.translate(marginRightPx * camera.zoom / 2 - marginLeftPx * camera.zoom / 2,
                marginTopPx * camera.zoom / 2 - marginBottomPx * camera.zoom / 2);
        camera.update();
    }

    /**
     * See {@link MapRenderer#STATE_TIME_THRESHOLD}
     */
    private void updateStateTime(float delta) {
        stateTime += delta;
        if (stateTime >= STATE_TIME_THRESHOLD) {
            stateTime -= STATE_TIME_THRESHOLD;
        }
    }

    /**
     * Disposes all the disposables used.
     */
    public void dispose() {
        spriteBatch.dispose();
    }

    private class DrawTile {
        Vector2 mapCoords;
        Color color;
        boolean darken = false;
        boolean topLeftBeach = false;
        boolean topBeach = false;
        boolean topRightBeach = false;
        boolean bottomRightBeach = false;
        boolean bottomBeach = false;
        boolean bottomLeftBeach = false;
    }

    private class Line {
        private Vector2 start;
        private Vector2 end;
    }
}

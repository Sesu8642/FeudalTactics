// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.renderer;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.google.common.collect.ImmutableList;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.HexMapHelper;
import de.sesu8642.feudaltactics.lib.gamestate.Kingdom;
import de.sesu8642.feudaltactics.lib.gamestate.MapDimensions;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Renderer for the map and the water.
 */
public class MapRenderer {

    public static final float WATER_TILE_SIZE = 12;
    public static final float SHIELD_SIZE = 2;
    public static final Color BEACH_WATER_COLOR = new Color(0F, 1F, 1F, 1F);
    public static final float HEXTILE_WIDTH = HexMapHelper.HEX_OUTER_RADIUS * 2;
    public static final float HEXTILE_HEIGHT = HexMapHelper.HEX_OUTER_RADIUS * (float) Math.sqrt(3);
    public static final List<Color> PLAYER_COLOR_PALETTE = ImmutableList.of(new Color(0.2F, 0.45F, 0.8F, 1),
        new Color(0.75F, 0.5F, 0F, 1), new Color(1F, 0.67F, 0.67F, 1), new Color(1F, 1F, 0F, 1),
        new Color(1F, 1F, 1F, 1), new Color(0F, 1F, 0F, 1));

    // offset for drawing the tile contents
    private static final float TILE_CONTENT_OFFSET_X = 0.0F;
    private static final float TILE_CONTENT_OFFSET_Y = HEXTILE_HEIGHT * -0.075F;
    // colors for normal and darkened stuff
    private static final Color NORMAL_COLOR = new Color(1, 1, 1, 1);
    private static final Color DARKENED_COLOR = new Color(NORMAL_COLOR).mul(0.5F, 0.5F, 0.5F, 1);
    private static final Color SHIELD_COLOR = new Color(NORMAL_COLOR).sub(0, 0, 0, 0.7F);
    private static final Color DARKENED_SHIELD_COLOR = new Color(SHIELD_COLOR).mul(0.5F, 0.5F, 0.5F, 1);
    private static final Color GRAVESTONE_INDICATOR_COLOR = new Color(NORMAL_COLOR).sub(0, 0, 0, 0.3F);
    private static final Color DARKENED_GRAVESTONE_INDICATOR_COLOR = new Color(GRAVESTONE_INDICATOR_COLOR).mul(0.5F,
        0.5F, 0.5F, 1);
    private static final Color TREE_INDICATOR_COLOR = new Color(NORMAL_COLOR).sub(0, 0, 0, 0.5F);
    /**
     * If the stateTime reaches this value, it will be reduced by this value.
     * The reason is that the stateTime must stay relatively small because of float limitations.
     * See https://github.com/libgdx/libgdx/issues/7536.
     * The subtracted amount must be a multiple of the animation duration.
     * Equal to one day in seconds.
     */
    private static final float STATE_TIME_THRESHOLD = 24 * 60 * 60f;

    /**
     * Map of sprite names and their animations. Functions as cache to avoid
     * frequent lookups.
     */
    private final ShapeRenderer shapeRenderer;
    private final OrthographicCamera camera;
    private final SpriteBatch spriteBatch;
    private final TextureAtlasHelper textureAtlasHelper;
    private final GameStateConverter gameStateConverter;
    // settings
    private final boolean enableDeepWaterRendering;
    private ItemsToBeRendered itemsToBeRendered = new ItemsToBeRendered();
    private float stateTime = 0F; // for keeping animations at the correct pace

    /**
     * Constructor.
     */
    public MapRenderer(OrthographicCamera camera, TextureAtlasHelper textureAtlasHelper,
                       ShapeRenderer shapeRenderer, SpriteBatch spriteBatch, GameStateConverter gameStateConverter,
                       boolean enableDeepWaterRendering) {
        this.camera = camera;
        this.shapeRenderer = shapeRenderer;
        this.spriteBatch = spriteBatch;
        this.textureAtlasHelper = textureAtlasHelper;
        this.gameStateConverter = gameStateConverter;
        this.enableDeepWaterRendering = enableDeepWaterRendering;
    }

    // this method and rendering must be synchronized to not happen at the same time
    public synchronized void updateGameState(GameState gameState) {
        itemsToBeRendered = gameStateConverter.convertGameState(gameState);
    }

    /**
     * Renders the map.
     */
    public synchronized void render() {
        // current frame for each tile content
        final Map<Vector2, TextureRegion> animatedTileContentframes = new HashMap<>();
        // current frame for each tile content
        final Map<Vector2, TextureRegion> darkenedAnimatedTileContentframes = new HashMap<>();
        updateStateTime(Gdx.graphics.getDeltaTime());
        // get the correct frames
        for (Entry<Vector2, Animation<TextureRegion>> content : itemsToBeRendered.getAnimatedContents().entrySet()) {
            animatedTileContentframes.put(content.getKey(), (content.getValue()).getKeyFrame(stateTime, true));
        }
        for (Entry<Vector2, Animation<TextureRegion>> content :
            itemsToBeRendered.getDarkenedAnimatedContents().entrySet()) {
            darkenedAnimatedTileContentframes.put(content.getKey(), content.getValue().getKeyFrame(stateTime, true));
        }
        final TextureRegion waterRegion = textureAtlasHelper.getWaterAnimation().getKeyFrame(stateTime, true);

        spriteBatch.setProjectionMatrix(camera.combined);
        spriteBatch.begin();

        if (enableDeepWaterRendering) {
            drawDeepWater(waterRegion);
        } else {
            // when making art like the logo, use black background instead of water
            Gdx.gl.glClearColor(0, 0, 0, 1);
            Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);
        }

        // draw all the beaches before the tiles because they would cover some of the tiles otherwise
        // beach water first (should not cover any sand)
        drawBeachWater();
        drawBeachSand();

        drawTiles();

        drawShields();

        // draw animated tile contents
        drawRegularColorContents(animatedTileContentframes.entrySet());
        drawDarkenedContents(darkenedAnimatedTileContentframes.entrySet());

        // draw non-animated tile contents
        drawRegularColorContents(itemsToBeRendered.getNonAnimatedContents().entrySet());
        drawDarkenedContents(itemsToBeRendered.getDarkenedNonAnimatedContents().entrySet());

        drawSemitransparentGraveStones();
        drawSemitransparentTrees(textureAtlasHelper.getBlinkingOakTreeAnimation(),
            itemsToBeRendered.getSemitransparentOakTrees());
        drawSemitransparentTrees(textureAtlasHelper.getBlinkingPalmTreeAnimation(),
            itemsToBeRendered.getSemitransparentPalmTrees());

        spriteBatch.end();

        drawLines();
    }

    private void drawLines() {
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1F, 1F, 1F, 1);
        for (int i = 0; i < itemsToBeRendered.getWhiteLineStartPoints().size(); i++) {
            shapeRenderer.rectLine(itemsToBeRendered.getWhiteLineStartPoints().get(i).x,
                itemsToBeRendered.getWhiteLineStartPoints().get(i).y,
                itemsToBeRendered.getWhiteLineEndPoints().get(i).x, itemsToBeRendered.getWhiteLineEndPoints().get(i).y,
                0.6F);
        }
        shapeRenderer.setColor(1F, 0F, 0F, 1);
        for (int i = 0; i < itemsToBeRendered.getRedLineStartPoints().size(); i++) {
            shapeRenderer.rectLine(itemsToBeRendered.getRedLineStartPoints().get(i).x,
                itemsToBeRendered.getRedLineStartPoints().get(i).y, itemsToBeRendered.getRedLineEndPoints().get(i).x,
                itemsToBeRendered.getRedLineEndPoints().get(i).y, 0.6F);
        }
        shapeRenderer.end();
    }

    private void drawRegularColorContents(Set<Entry<Vector2, TextureRegion>> textureRegions) {
        spriteBatch.setColor(NORMAL_COLOR);
        drawContents(textureRegions);
    }

    private void drawDarkenedContents(Set<Entry<Vector2, TextureRegion>> darkenedTextureRegions) {
        spriteBatch.setColor(DARKENED_COLOR);
        drawContents(darkenedTextureRegions);
    }

    private void drawContents(Set<Entry<Vector2, TextureRegion>> textureRegions) {
        for (Entry<Vector2, TextureRegion> currentFrame : textureRegions) {
            spriteBatch.draw(currentFrame.getValue(), currentFrame.getKey().x - TILE_CONTENT_OFFSET_X,
                currentFrame.getKey().y - TILE_CONTENT_OFFSET_Y, HEXTILE_WIDTH, HEXTILE_HEIGHT);
        }
    }

    private void drawSemitransparentGraveStones() {
        final TextureRegion gravestoneRegion =
            textureAtlasHelper.getBlinkingGravestoneAnimation().getKeyFrame(stateTime, true);
        if (gravestoneRegion == textureAtlasHelper.getBlinkingGravestoneAnimation().getKeyFrames()[1]) {
            // return on second frame for blinking effect (second frame would be a dark square otherwise)
            return;
        }
        for (Entry<Vector2, Boolean> gravestone : itemsToBeRendered.getSemitransparentGraveStones().entrySet()) {
            if (Boolean.TRUE.equals(gravestone.getValue())) {
                spriteBatch.setColor(DARKENED_GRAVESTONE_INDICATOR_COLOR);
            } else {
                spriteBatch.setColor(GRAVESTONE_INDICATOR_COLOR);
            }
            spriteBatch.draw(gravestoneRegion, gravestone.getKey().x - TILE_CONTENT_OFFSET_X,
                gravestone.getKey().y - TILE_CONTENT_OFFSET_Y, HEXTILE_WIDTH, HEXTILE_HEIGHT);
        }
    }

    private void drawSemitransparentTrees(Animation<TextureRegion> treeTypeAnimation, List<Vector2> mapCoordinates) {
        final TextureRegion treeRegion =
            treeTypeAnimation.getKeyFrame(stateTime, true);
        if (treeRegion == treeTypeAnimation.getKeyFrames()[1]) {
            // return on second frame for blinking effect (second frame would be a dark square otherwise)
            return;
        }
        spriteBatch.setColor(TREE_INDICATOR_COLOR);
        for (Vector2 treeCoordinates : mapCoordinates) {
            spriteBatch.draw(treeRegion, treeCoordinates.x - TILE_CONTENT_OFFSET_X,
                treeCoordinates.y - TILE_CONTENT_OFFSET_Y, HEXTILE_WIDTH, HEXTILE_HEIGHT);
        }
    }

    private void drawShields() {
        for (Entry<Vector2, Boolean> shield : itemsToBeRendered.getShields().entrySet()) {
            if (Boolean.TRUE.equals(shield.getValue())) {
                spriteBatch.setColor(DARKENED_SHIELD_COLOR);
            } else {
                spriteBatch.setColor(SHIELD_COLOR);
            }
            spriteBatch.draw(textureAtlasHelper.getShieldRegion(), shield.getKey().x, shield.getKey().y, SHIELD_SIZE,
                SHIELD_SIZE);
        }
    }

    private void drawTiles() {
        for (ItemsToBeRendered.DrawTile tile : itemsToBeRendered.getTiles().values()) {
            final Color color = new Color(tile.color);
            // darken tile
            if (tile.darken) {
                color.mul(0.5F, 0.5F, 0.5F, 1);
            }
            spriteBatch.setColor(color);
            spriteBatch.draw(textureAtlasHelper.getTileRegion(), tile.mapCoords.x - HEXTILE_WIDTH / 2,
                tile.mapCoords.y - HEXTILE_HEIGHT / 2,
                HEXTILE_WIDTH, HEXTILE_HEIGHT);
        }
    }

    private void drawBeachSand() {
        final TextureRegion bottomRightBeachSandRegion =
            textureAtlasHelper.getBeachSandAnimation().getKeyFrame(stateTime, true);
        final TextureRegion bottomLeftBeachSandRegion = new TextureRegion(bottomRightBeachSandRegion);
        bottomLeftBeachSandRegion.flip(true, false);
        final TextureRegion topLeftBeachSandRegion = new TextureRegion(bottomRightBeachSandRegion);
        topLeftBeachSandRegion.flip(true, true);
        final TextureRegion topRightBeachSandRegion = new TextureRegion(bottomRightBeachSandRegion);
        topRightBeachSandRegion.flip(false, true);

        final Color beachSandColor = new Color(NORMAL_COLOR);
        if (itemsToBeRendered.isDarkenBeaches()) {
            beachSandColor.mul(0.5F, 0.5F, 0.5F, 1);
        }
        spriteBatch.setColor(beachSandColor);
        for (ItemsToBeRendered.DrawTile tile : itemsToBeRendered.getTiles().values()) {
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
    }

    private void drawBeachWater() {
        final TextureRegion bottomRightBeachWaterRegion =
            textureAtlasHelper.getBeachWaterAnimation().getKeyFrame(stateTime, true);
        final TextureRegion bottomLeftBeachWaterRegion = new TextureRegion(bottomRightBeachWaterRegion);
        bottomLeftBeachWaterRegion.flip(true, false);
        final TextureRegion topLeftBeachWaterRegion = new TextureRegion(bottomRightBeachWaterRegion);
        topLeftBeachWaterRegion.flip(true, true);
        final TextureRegion topRightBeachWaterRegion = new TextureRegion(bottomRightBeachWaterRegion);
        topRightBeachWaterRegion.flip(false, true);

        final Color beachWaterColor = new Color(BEACH_WATER_COLOR);
        if (itemsToBeRendered.isDarkenBeaches()) {
            beachWaterColor.mul(0.75F, 0.75F, 0.75F, 1);
        }
        spriteBatch.setColor(beachWaterColor);
        for (ItemsToBeRendered.DrawTile tile : itemsToBeRendered.getTiles().values()) {
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
    }

    private void drawDeepWater(TextureRegion waterRegion) {
        spriteBatch.setColor(NORMAL_COLOR);
        final Vector2 waterOriginPoint = calculateWaterOriginPoint();
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
    }

    private Vector2 calculateWaterOriginPoint() {
        final float waterTileSizePx = WATER_TILE_SIZE / camera.zoom;
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
        final float waterOffsetForZoomInPxX = (waterTilesNeededToCoverScreenHorizonally * waterTileSizePx
            - camera.viewportWidth) / 2;
        final float waterOffsetForZoomInPxY = (waterTilesNeededToCoverScreenVertically * waterTileSizePx
            - camera.viewportHeight) / 2;
        final float waterOffsetForMovementX = camera.position.x % WATER_TILE_SIZE;
        final float waterOffsetForMovementY = camera.position.y % WATER_TILE_SIZE;
        // bottom left point from where the water is drawn
        final Vector3 waterOriginPoint = camera
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
        final MapDimensions dims = HexMapHelper.getMapDimensionsInWorldCoords(gameState.getMap().keySet());
        // get the factors needed to adjust the camera zoom
        final float useViewportWidth = (camera.viewportWidth - marginLeftPx - marginRightPx);
        final float useViewportHeight = (camera.viewportHeight - marginBottomPx - marginTopPx);
        final float xFactor = (dims.getWidth() / camera.zoom) / useViewportWidth; // lol
        final float yFactor = (dims.getHeight() / camera.zoom) / useViewportHeight;
        // use the bigger factor because both dimensions must fit
        final float scaleFactor = Math.max(xFactor, yFactor);
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
     * Places the camera in the center of a given kingdom, without changing the zoom.
     *
     * @param kingdom kingdom to focus
     */
    public void placeCameraForOnKingdom(GameState gameState, Kingdom kingdom) {
        final Set<Vector2> kingdomTileCoordinates =
            gameState.getMap().entrySet().stream().filter(entry ->
                    kingdom.getTiles().contains(entry.getValue()))
                .map(Entry::getKey)
                .collect(Collectors.toSet());
        final MapDimensions dims = HexMapHelper.getMapDimensionsInWorldCoords(kingdomTileCoordinates);
        // move camera to center
        camera.position.set(dims.getCenter(), 0);
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

}

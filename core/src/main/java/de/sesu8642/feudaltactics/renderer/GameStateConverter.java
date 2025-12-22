// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.renderer;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import de.sesu8642.feudaltactics.lib.gamestate.*;
import lombok.RequiredArgsConstructor;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static de.sesu8642.feudaltactics.renderer.MapRenderer.*;

/**
 * For converting a game state into a representation optimized for the map renderer.
 */
@RequiredArgsConstructor(onConstructor_ = @Inject)
public class GameStateConverter {

    private static final float LINE_EXTENSION = 0.14F;
    private static final int NUMBER_OF_HEXAGON_SIDES = 6;

    private final TextureAtlasHelper textureAtlasHelper;

    private static void determineBeachesOnTileEdges(GameState gameState, HexTile tile,
                                                    ItemsToBeRendered.DrawTile drawTile) {
        final List<HexTile> neighbors = HexMapHelper.getNeighborTiles(gameState.getMap(), tile);
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
    }

    private static ItemsToBeRendered.DrawTile createDrawTile(Vector2 mapCoords, HexTile tile) {
        final ItemsToBeRendered.DrawTile drawTile = new ItemsToBeRendered.DrawTile();
        drawTile.mapCoords = mapCoords;
        drawTile.color = PLAYER_COLOR_PALETTE.get(tile.getPlayer().getPlayerIndex());
        return drawTile;
    }

    private static boolean shouldTileInActiveKingdomBeDarkened(GameState gameState, HexTile tile) {
        // darken the tile if there is some held object and placing it on the tile is impossible
        return gameState.getHeldObject() != null
            && !InputValidationHelper.checkPlaceOwn(gameState, gameState.getActivePlayer(), tile)
            && !InputValidationHelper.checkCombineUnits(gameState, gameState.getActivePlayer(), tile);
    }

    private static boolean isTileInActiveKingdom(GameState gameState, HexTile tile) {
        return gameState.getActiveKingdom() != null && tile.getKingdom() != null
            && tile.getKingdom() == gameState.getActiveKingdom();
    }

    private static boolean shouldTileContentBeAnimated(GameState gameState, HexTile tile, TileContent tileContent) {
        return doesTileBelongToAKingdomOfTheActivePlayer(gameState, tile) && (doesTileContainAUnitThatCanAct(tileContent) || doesTileContainACapitalThatCanBuySomething(gameState, tile, tileContent));
    }

    private static boolean doesTileContainACapitalThatCanBuySomething(GameState gameState, HexTile tile,
                                                                      TileContent tileContent) {
        return ClassReflection.isAssignableFrom(Capital.class, tileContent.getClass())
            && gameState.getActivePlayer() == tile.getKingdom().getPlayer()
            && tile.getKingdom().getSavings() > Unit.COST;
    }

    private static boolean doesTileContainAUnitThatCanAct(TileContent tileContent) {
        return ClassReflection.isAssignableFrom(Unit.class, tileContent.getClass())
            && ((Unit) tileContent).isCanAct();
    }

    private static boolean doesTileBelongToAKingdomOfTheActivePlayer(GameState gameState, HexTile tile) {
        return tile.getKingdom() != null && tile.getKingdom().getPlayer() == gameState.getActivePlayer();
    }

    private static void createProtectionIndicators(GameState gameState, HexTile tile, ItemsToBeRendered result,
                                                   Vector2 mapCoords, ItemsToBeRendered.DrawTile drawTile) {
        if (shouldTileHaveProtectionIndicators(gameState, tile)) {
            // create protection indicators
            final int protectionLevel = GameStateHelper.getProtectionLevel(gameState, tile);
            switch (protectionLevel) {
                case 4:
                    result.getShields().put(new Vector2(mapCoords.x - 2 * SHIELD_SIZE,
                            mapCoords.y - SHIELD_SIZE / 2),
                        drawTile.darken);
                    result.getShields().put(new Vector2(mapCoords.x - SHIELD_SIZE,
                            mapCoords.y - SHIELD_SIZE / 2),
                        drawTile.darken);
                    result.getShields().put(new Vector2(mapCoords.x, mapCoords.y - SHIELD_SIZE / 2),
                        drawTile.darken);
                    result.getShields().put(new Vector2(mapCoords.x + SHIELD_SIZE,
                            mapCoords.y - SHIELD_SIZE / 2),
                        drawTile.darken);
                    break;
                case 3:
                    result.getShields().put(new Vector2(mapCoords.x - SHIELD_SIZE * 1.5F,
                            mapCoords.y - SHIELD_SIZE / 2),
                        drawTile.darken);
                    result.getShields().put(new Vector2(mapCoords.x - SHIELD_SIZE / 2,
                            mapCoords.y - SHIELD_SIZE / 2),
                        drawTile.darken);
                    result.getShields().put(new Vector2(mapCoords.x + SHIELD_SIZE / 2,
                            mapCoords.y - SHIELD_SIZE / 2),
                        drawTile.darken);
                    break;
                case 2:
                    result.getShields().put(new Vector2(mapCoords.x - SHIELD_SIZE,
                            mapCoords.y - SHIELD_SIZE / 2),
                        drawTile.darken);
                    result.getShields().put(new Vector2(mapCoords.x, mapCoords.y - SHIELD_SIZE / 2),
                        drawTile.darken);
                    break;
                case 1:
                    result.getShields().put(new Vector2(mapCoords.x - SHIELD_SIZE / 2,
                            mapCoords.y - SHIELD_SIZE / 2),
                        drawTile.darken);
                    break;
                case 0:
                    break;
                default:
                    throw new AssertionError("Unexpected protection level " + protectionLevel);
            }
        }
    }

    private static boolean shouldTileHaveProtectionIndicators(GameState gameState, HexTile tile) {
        return gameState.getHeldObject() != null || (gameState.getActiveKingdom() != null && tile.getKingdom() == gameState.getActiveKingdom());
    }

    private static boolean shouldTileContentBeDarkened(GameState gameState, HexTile tile,
                                                       ItemsToBeRendered.DrawTile drawTile, TileContent tileContent) {
        return drawTile.darken
            // darken own units that have already acted
            || (tile.getPlayer() == gameState.getActivePlayer() && gameState.getHeldObject() == null
            && tile.getContent() != null
            && ClassReflection.isAssignableFrom(Unit.class, tileContent.getClass())
            && !((Unit) tile.getContent()).isCanAct());
    }

    /**
     * Converts a game state into a representation optimized for the map renderer.
     *
     * @param gameState game state containing the map
     */
    ItemsToBeRendered convertGameState(GameState gameState) {
        final ItemsToBeRendered result = new ItemsToBeRendered();
        result.setDarkenBeaches(gameState.getHeldObject() != null);

        // create tiles
        for (Map.Entry<Vector2, HexTile> hexTileEntry : gameState.getMap().entrySet()) {
            createTileAndContent(gameState, hexTileEntry, result);
        }
        return result;
    }

    private void createTileAndContent(GameState gameState, Map.Entry<Vector2, HexTile> hexTileEntry,
                                      ItemsToBeRendered result) {
        final Vector2 hexCoords = hexTileEntry.getKey();
        final Vector2 mapCoords = getMapCoordinatesFromHexCoordinates(hexCoords);
        final HexTile tile = hexTileEntry.getValue();

        final ItemsToBeRendered.DrawTile drawTile = createDrawTile(mapCoords, tile);
        determineBeachesOnTileEdges(gameState, tile, drawTile);

        if (isTileInActiveKingdom(gameState, tile)) {
            createWhiteLinesAroundTile(gameState, tile, mapCoords, result);
            drawTile.darken = shouldTileInActiveKingdomBeDarkened(gameState, tile);
        } else if (gameState.getHeldObject() != null) {
            // red lines for indicating if able to conquer
            if (InputValidationHelper.checkConquer(gameState, gameState.getActivePlayer(), tile)) {
                createRedLinesAroundTile(gameState, tile, mapCoords, result);
            } else {
                // darken non-conquerable tiles
                drawTile.darken = true;
            }
        } else if (gameState.getActivePlayer().getType() == Player.Type.LOCAL_BOT
            && gameState.getActiveKingdom() != null) {
            // darken non-active kingdom tiles during bot turns
            drawTile.darken = true;
        }
        result.getTiles().put(tile.getPosition(), drawTile);

        createTileContents(gameState, tile, drawTile, result, mapCoords);

        createProtectionIndicators(gameState, tile, result, mapCoords, drawTile);
    }

    private void createTileContents(GameState gameState, HexTile tile, ItemsToBeRendered.DrawTile drawTile,
                                    ItemsToBeRendered result, Vector2 mapCoords) {
        // create content (units etc)
        final TileContent tileContent = tile.getContent();
        if (tileContent != null) {
            final boolean animate = shouldTileContentBeAnimated(gameState, tile, tileContent);
            if (animate) {
                createAnimatedTileContent(drawTile, result, mapCoords, tileContent);
            } else {
                createNonAnimatedTileContent(gameState, tile, drawTile, result, mapCoords, tileContent);
            }
            if (isUnitAboutToDie(gameState, tile, tileContent)) {
                result.semitransparentGraveStones.put(new Vector2(mapCoords.x - HexMapHelper.HEX_OUTER_RADIUS,
                    mapCoords.y - HexMapHelper.HEX_OUTER_RADIUS), drawTile.darken);
            }
        }
    }

    private boolean isUnitAboutToDie(GameState gameState, HexTile tile, TileContent tileContent) {
        return tile.getPlayer().getType() == Player.Type.LOCAL_PLAYER && tile.getKingdom() != null && !canKingdomSustainUnitsForOneTurn(gameState, tile.getKingdom()) && ClassReflection.isAssignableFrom(Unit.class, tileContent.getClass());
    }

    private boolean canKingdomSustainUnitsForOneTurn(GameState gameState, Kingdom kingdom) {
        final int income = GameStateHelper.getKingdomIncome(kingdom);
        final int salaries = GameStateHelper.getKingdomSalaries(gameState, kingdom);
        final int budgetBalance = income - salaries;
        final int savings = kingdom.getSavings();
        return savings + budgetBalance >= 0;
    }

    private void createNonAnimatedTileContent(GameState gameState, HexTile tile, ItemsToBeRendered.DrawTile drawTile,
                                              ItemsToBeRendered result, Vector2 mapCoords, TileContent tileContent) {
        if (shouldTileContentBeDarkened(gameState, tile, drawTile, tileContent)) {
            // darkened content
            result.getDarkenedNonAnimatedContents().put(
                new Vector2(mapCoords.x - HexMapHelper.HEX_OUTER_RADIUS,
                    mapCoords.y - HexMapHelper.HEX_OUTER_RADIUS),
                textureAtlasHelper.findTextureRegionForTileContent(tileContent));
        } else {
            result.getNonAnimatedContents().put(
                new Vector2(mapCoords.x - HexMapHelper.HEX_OUTER_RADIUS,
                    mapCoords.y - HexMapHelper.HEX_OUTER_RADIUS),
                textureAtlasHelper.findTextureRegionForTileContent(tileContent));
        }
    }

    private void createAnimatedTileContent(ItemsToBeRendered.DrawTile drawTile, ItemsToBeRendered result,
                                           Vector2 mapCoords, TileContent tileContent) {
        if (drawTile.darken) {
            // darkened content
            result.getDarkenedAnimatedContents().put(
                new Vector2(mapCoords.x - HexMapHelper.HEX_OUTER_RADIUS,
                    mapCoords.y - HexMapHelper.HEX_OUTER_RADIUS),
                textureAtlasHelper.findAnimationForTileContent(tileContent));
        } else {
            result.getAnimatedContents().put(
                new Vector2(mapCoords.x - HexMapHelper.HEX_OUTER_RADIUS,
                    mapCoords.y - HexMapHelper.HEX_OUTER_RADIUS),
                textureAtlasHelper.findAnimationForTileContent(tileContent));
        }
    }

    private void createRedLinesAroundTile(GameState gameState, HexTile tile, Vector2 mapCoords,
                                          ItemsToBeRendered result) {
        for (int neighborTileIndex = 0; neighborTileIndex < NUMBER_OF_HEXAGON_SIDES; neighborTileIndex++) {
            final HexTile neighborTile = HexMapHelper.getNeighborTiles(gameState.getMap(), tile).get(neighborTileIndex);
            if (neighborTile == null
                || (neighborTile.getKingdom() != gameState.getActiveKingdom() && !InputValidationHelper
                .checkConquer(gameState, gameState.getActivePlayer(), neighborTile))) {
                final Line line = getNeighborLine(mapCoords, neighborTileIndex);
                final Collection<Line> dottedLineParts = lineToDottedLine(line);
                for (Line linePart : dottedLineParts) {
                    result.getRedLineStartPoints().add(linePart.start);
                    result.getRedLineEndPoints().add(linePart.end);
                }
            }
        }
    }

    private void createWhiteLinesAroundTile(GameState gameState, HexTile tile, Vector2 mapCoords,
                                            ItemsToBeRendered result) {
        for (int neighborTileIndex = 0; neighborTileIndex < NUMBER_OF_HEXAGON_SIDES; neighborTileIndex++) {
            final HexTile neighborTile = HexMapHelper.getNeighborTiles(gameState.getMap(), tile).get(neighborTileIndex);
            if (neighborTile == null || neighborTile.getKingdom() == null
                || neighborTile.getKingdom() != tile.getKingdom()) {
                final Line line = getNeighborLine(mapCoords, neighborTileIndex);
                result.getWhiteLineStartPoints().add(line.start);
                result.getWhiteLineEndPoints().add(line.end);
            }
        }
    }

    private Collection<Line> lineToDottedLine(Line line) {
        final int partAmount = 3;
        final Collection<Line> resultLines = new HashSet<>();
        final float lineDiffX = line.end.x - line.start.x;
        final float lineDiffY = line.end.y - line.start.y;
        for (int i = 1; i <= partAmount; i += 2) {
            final Line linePart = new Line();
            linePart.start = new Vector2(line.start.x + (lineDiffX / partAmount) * (i - 1),
                line.start.y + (lineDiffY / partAmount) * (i - 1));
            linePart.end = new Vector2(line.start.x + (lineDiffX / partAmount) * i,
                line.start.y + (lineDiffY / partAmount) * i);
            resultLines.add(linePart);
        }
        return resultLines;
    }

    private Line getNeighborLine(Vector2 mapCoords, int index) {
        final Vector2 start;
        final Vector2 end;
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
        final Line result = new Line();
        result.start = start;
        result.end = end;
        return result;
    }

    /**
     * Calculates map coords from hex coords.
     *
     * @param hexCoords hex coords
     * @return map coords
     */
    public Vector2 getMapCoordinatesFromHexCoordinates(Vector2 hexCoords) {
        final float x = 0.75F * HEXTILE_WIDTH * hexCoords.x;
        final float y = (float) (HexMapHelper.HEX_OUTER_RADIUS
            * (Math.sqrt(3) / 2 * hexCoords.x + Math.sqrt(3) * (-hexCoords.y - hexCoords.x)));
        return new Vector2(x, y);
    }

    private static class Line {
        private Vector2 start;
        private Vector2 end;
    }

}

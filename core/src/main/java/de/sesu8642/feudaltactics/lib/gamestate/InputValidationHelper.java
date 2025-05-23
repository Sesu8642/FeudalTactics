// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.lib.gamestate;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import de.sesu8642.feudaltactics.ingame.AutoSaveRepository;
import de.sesu8642.feudaltactics.lib.gamestate.Unit.UnitTypes;
import de.sesu8642.feudaltactics.lib.ingame.PlayerMove;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Utility class that checks whether certain user action are allowed according
 * to the rules.
 */
@Singleton
public class InputValidationHelper {

    private final AutoSaveRepository autoSaveRepo;

    @Inject
    public InputValidationHelper(AutoSaveRepository autoSaveRepo) {
        this.autoSaveRepo = autoSaveRepo;
    }

    private static HexTile findTileAtPosition(GameState gameState, Vector2 position) {
        return gameState.getMap().get(position);
    }

    /**
     * Checks whether a player is allowed to change the active kingdom.
     *
     * @param gameState game state of the current game
     * @param player    player attempting the action
     * @param tile      tile that was clicked
     * @return whether the action is allowed
     */
    public static boolean checkChangeActiveKingdom(GameState gameState, Player player, HexTile tile) {
        if (!isCorrectPlayersTurn(gameState, player)) {
            return false;
        }
        if (isWater(tile)) {
            return false;
        }
        if (player != tile.getPlayer()) {
            return false;
        }
        if (gameState.getHeldObject() != null) {
            return false;
        }
        if (tile.getKingdom() == null) {
            return false;
        }
        return gameState.getActiveKingdom() != tile.getKingdom();
    }

    /**
     * Checks whether a player is allowed to pick up an object.
     *
     * @param gameState game state of the current game
     * @param player    player attempting the action
     * @param tile      tile that was clicked
     * @return whether the action is allowed
     */
    public static boolean checkPickupObject(GameState gameState, Player player, HexTile tile) {
        if (!isCorrectPlayersTurn(gameState, player)) {
            return false;
        }
        if (isWater(tile)) {
            return false;
        }
        if (player != tile.getPlayer()) {
            return false;
        }
        if (gameState.getHeldObject() != null) {
            return false;
        }
        if (tile.getContent() == null) {
            return false;
        }
        if (tile.getPlayer() != player) {
            return false;
        }
        if (!ClassReflection.isAssignableFrom(Unit.class, tile.getContent().getClass())) {
            return false;
        }
        return ((Unit) tile.getContent()).isCanAct();
    }

    /**
     * Checks whether a player is allowed to place their own object.
     *
     * @param gameState game state of the current game
     * @param player    player attempting the action
     * @param tile      tile that was clicked
     * @return whether the action is allowed
     */
    public static boolean checkPlaceOwn(GameState gameState, Player player, HexTile tile) {
        if (!isCorrectPlayersTurn(gameState, player)) {
            return false;
        }
        if (isWater(tile)) {
            return false;
        }
        if (gameState.getHeldObject() == null) {
            return false;
        }
        if (player != tile.getPlayer()) {
            return false;
        }
        if (tile.getKingdom() == null) {
            return false;
        }
        if (gameState.getActiveKingdom() != tile.getKingdom()) {
            return false;
        }
        if (tile.getContent() != null && ClassReflection.isAssignableFrom(Blocking.class, tile.getContent().getClass())
                && !ClassReflection.isAssignableFrom(Unit.class, gameState.getHeldObject().getClass())) {
            // non-unit on blocking object
            return false;
        }
        // not empty or blocking object
        return tile.getContent() == null
                || ClassReflection.isAssignableFrom(Blocking.class, tile.getContent().getClass());
    }

    /**
     * Checks whether a player is allowed to buy and place their own unit instantly,
     * without it being in held before.
     *
     * @param gameState game state of the current game
     * @param player    player attempting the action
     * @param tile      tile that was clicked
     * @return whether the action is allowed
     */
    public static boolean checkBuyAndPlaceUnitInstantly(GameState gameState, Player player, HexTile tile) {
        if (!isCorrectPlayersTurn(gameState, player)) {
            return false;
        }
        // first check whether buying is possible
        if (!checkBuyObject(gameState, player, Unit.class)) {
            return false;
        }
        // then check whether placing is possible
        if (isWater(tile)) {
            return false;
        }
        if (player != tile.getPlayer()) {
            return false;
        }
        if (gameState.getActiveKingdom() != tile.getKingdom()) {
            return false;
        }
        // not empty or blocking object
        return tile.getContent() == null
                || ClassReflection.isAssignableFrom(Blocking.class, tile.getContent().getClass());
    }

    /**
     * Checks whether a player is allowed to buy and place a castle instantly,
     * without it being in held before.
     *
     * @param gameState game state of the current game
     * @param player    player attempting the action
     * @param tile      tile that was clicked
     * @return whether the action is allowed
     */
    public static boolean checkBuyAndPlaceCastleInstantly(GameState gameState, Player player, HexTile tile) {
        if (!isCorrectPlayersTurn(gameState, player)) {
            return false;
        }
        // first check whether buying is possible
        Kingdom activeKingdom = gameState.getActiveKingdom();
        if (activeKingdom == null) {
            return false;
        }
        if (activeKingdom.getSavings() < Castle.COST) {
            return false;
        }
        if (gameState.getHeldObject() != null) {
            return false;
        }
        // then check whether placing is possible
        if (isWater(tile)) {
            return false;
        }
        if (player != tile.getPlayer()) {
            return false;
        }
        if (gameState.getActiveKingdom() != tile.getKingdom()) {
            return false;
        }
        // not empty
        return tile.getContent() == null;
    }

    /**
     * Checks whether a player is allowed to combine units.
     *
     * @param gameState game state of the current game
     * @param player    player attempting the action
     * @param tile      tile that was clicked
     * @return whether the action is allowed
     */
    public static boolean checkCombineUnits(GameState gameState, Player player, HexTile tile) {
        if (!isCorrectPlayersTurn(gameState, player)) {
            return false;
        }
        if (isWater(tile)) {
            return false;
        }
        if (tile.getContent() == null) {
            return false;
        }
        if (gameState.getHeldObject() == null) {
            return false;
        }
        if (player != tile.getPlayer()) {
            return false;
        }
        if (tile.getKingdom() == null) {
            return false;
        }
        if (gameState.getActiveKingdom() != tile.getKingdom()) {
            return false;
        }
        if (!ClassReflection.isAssignableFrom(Unit.class, gameState.getHeldObject().getClass())) {
            return false;
        }
        if (!ClassReflection.isAssignableFrom(Unit.class, tile.getContent().getClass())) {
            return false;
        }
        int heldUnitStrength = gameState.getHeldObject().getStrength();
        int tileUnitStrength = tile.getContent().getStrength();
        // cannot create unit stronger than the strongest unit
        return heldUnitStrength + tileUnitStrength <= UnitTypes.strongest().strength();
    }

    /**
     * Checks whether a player is allowed to conquer a tile.
     *
     * @param gameState game state of the current game
     * @param player    player attempting the action
     * @param tile      tile that was clicked
     * @return whether the action is allowed
     */
    public static boolean checkConquer(GameState gameState, Player player, HexTile tile) {
        if (!isCorrectPlayersTurn(gameState, player)) {
            return false;
        }
        if (isWater(tile)) {
            return false;
        }
        if (gameState.getHeldObject() == null) {
            return false;
        }
        if (tile.getPlayer() == player) {
            return false;
        }
        if (!ClassReflection.isAssignableFrom(Unit.class, gameState.getHeldObject().getClass())) {
            // not a unit
            return false;
        }
        if (tile.getContent() != null && tile.getContent().getStrength() >= gameState.getHeldObject().getStrength()) {
            // too strong object on the tile
            return false;
        }
        boolean isNextoToOwnKingdom = false;
        for (HexTile neighborTile : HexMapHelper.getNeighborTiles(gameState.getMap(), tile)) {
            if (isWater(neighborTile)) {
                // skip water
                continue;
            }
            // check if tile is next to own kingdom
            if (neighborTile.getKingdom() == gameState.getActiveKingdom()) {
                isNextoToOwnKingdom = true;
            }
            TileContent neighborContent = neighborTile.getContent();
            // check if there is no stronger object next to it protecting it
            if (tile.getKingdom() != null && neighborTile.getKingdom() == tile.getKingdom() && neighborContent != null
                    && neighborContent.getStrength() >= gameState.getHeldObject().getStrength()) {
                return false;
            }
        }
        // not next to the unit's kingdom
        return isNextoToOwnKingdom;
    }

    /**
     * Checks whether the player is allowed to end the current turn.
     *
     * @param gameState game state of the current game
     * @param player    player attempting the action
     * @return whether the action is allowed
     */
    public static boolean checkEndTurn(GameState gameState, Player player) {
        if (!isCorrectPlayersTurn(gameState, player)) {
            return false;
        }
        return (gameState.getHeldObject() == null);
    }

    /**
     * Checks whether a player is allowed to buy an object.
     *
     * @param gameState game state of the current game
     * @param player    player attempting the action
     * @param cost      cost of the object
     * @return whether the action is allowed
     */
    public static boolean checkBuyObject(GameState gameState, Player player, Class<?> targetClass) {
        if (!isCorrectPlayersTurn(gameState, player)) {
            return false;
        }
        Kingdom activeKingdom = gameState.getActiveKingdom();
        if (activeKingdom == null) {
            return false;
        }
        int cost;
        if (Unit.class.isAssignableFrom(targetClass)) {
            cost = Unit.COST;
        } else if (Castle.class.isAssignableFrom(targetClass)) {
            cost = Castle.COST;
        } else {
            throw new IllegalStateException("Unexpected class to buy " + targetClass);
        }
        if (activeKingdom.getSavings() < cost) {
            return false;
        }
        // allow upgrading a held unit
        return gameState.getHeldObject() == null || (Unit.class.isAssignableFrom(gameState.getHeldObject().getClass())
                && gameState.getHeldObject().getStrength() < UnitTypes.strongest().strength()
                && Unit.class.isAssignableFrom(targetClass));
    }

    /**
     * Checks whether a player is allowed undo the previous action.
     *
     * @param gameState      game state of the current game
     * @param player         player attempting the action
     * @param isUndoPossible whether undoing is possible from the repository's
     *                       perspective
     * @return whether the action is allowed
     */
    public static boolean checkUndoAction(GameState gameState, Player player, boolean isUndoPossible) {
        if (!isCorrectPlayersTurn(gameState, player)) {
            return false;
        }
        return isUndoPossible;
    }

    private static boolean isWater(HexTile tile) {
        return (tile == null);
    }

    private static boolean isCorrectPlayersTurn(GameState gameState, Player actingPlayer) {
        return gameState.getActivePlayer() == actingPlayer;
    }

    public boolean checkPlayerMove(GameState gameState, Player player, PlayerMove move) {
        switch (move.getPlayerActionType()) {
            case PICK_UP:
                return checkPickupObject(gameState, player, findTileAtPosition(gameState, move.getTilePosition()));
            case PLACE_OWN:
                return checkPlaceOwn(gameState, player, findTileAtPosition(gameState, move.getTilePosition()));
            case COMBINE_UNITS:
                return checkCombineUnits(gameState, player, findTileAtPosition(gameState, move.getTilePosition()));
            case CONQUER:
                return checkConquer(gameState, player, findTileAtPosition(gameState, move.getTilePosition()));
            case BUY_PEASANT:
                return checkBuyObject(gameState, player, Unit.class);
            case BUY_CASTLE:
                return checkBuyObject(gameState, player, Castle.class);
            case BUY_AND_PLACE_PEASANT:
                return checkBuyAndPlaceUnitInstantly(gameState, player,
                        findTileAtPosition(gameState, move.getTilePosition()));
            case BUY_AND_PLACE_CASTLE:
                return checkBuyAndPlaceCastleInstantly(gameState, player,
                        findTileAtPosition(gameState, move.getTilePosition()));
            case ACTIVATE_KINGDOM:
                return checkChangeActiveKingdom(gameState, player, findTileAtPosition(gameState,
                        move.getTilePosition()));
            case END_TURN:
                return checkEndTurn(gameState, player);
            case UNDO_LAST_MOVE:
                return checkUndoAction(gameState, player, autoSaveRepo.isUndoPossible());
            default:
                throw new IllegalStateException("Unknown player move type " + move.getPlayerActionType());
        }
    }

}

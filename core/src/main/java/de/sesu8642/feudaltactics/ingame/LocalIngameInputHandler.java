// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ingame;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.reflect.ClassReflection;
import com.google.common.eventbus.Subscribe;
import de.sesu8642.feudaltactics.events.InitializeScenarioEvent;
import de.sesu8642.feudaltactics.events.RegenerateMapEvent;
import de.sesu8642.feudaltactics.events.TapInputEvent;
import de.sesu8642.feudaltactics.events.input.BackInputEvent;
import de.sesu8642.feudaltactics.events.moves.*;
import de.sesu8642.feudaltactics.lib.gamestate.*;
import de.sesu8642.feudaltactics.lib.ingame.GameController;
import de.sesu8642.feudaltactics.lib.ingame.PlayerMove;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;
import java.util.Optional;

/**
 * Handles inputs of a local player in-game.
 **/
@Singleton
public class LocalIngameInputHandler {

    private final GameController gameController;
    private final InputValidationHelper inputValidationHelper;

    /**
     * Constructor.
     *
     * @param gameController game controller
     */
    @Inject
    public LocalIngameInputHandler(GameController gameController, InputValidationHelper inputValidationHelper) {
        this.gameController = gameController;
        this.inputValidationHelper = inputValidationHelper;
    }

    private void carryOutPlayerMoveIfLegal(PlayerMove move) {
        Optional<Player> playerOptional = GameStateHelper.determineActingLocalPlayer(gameController.getGameState());
        if (!playerOptional.isPresent()) {
            return;
        }
        if (inputValidationHelper.checkPlayerMove(gameController.getGameState(), playerOptional.get(), move)) {
            gameController.carryOutPlayerMove(move);
        }
    }

    /**
     * Event handler for tap input events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleTapInput(TapInputEvent event) {
        Vector2 hexCoords = HexMapHelper.worldCoordsToHexCoords(event.getWorldCoords());
        Optional<Player> playerOptional = GameStateHelper.determineActingLocalPlayer(gameController.getGameState());
        if (!playerOptional.isPresent()) {
            return;
        }
        Player player = playerOptional.get();
        Map<Vector2, HexTile> map = gameController.getGameState().getMap();
        HexTile tile = map.get(hexCoords);

        gameController.printTileInfo(hexCoords);

        carryOutPlayerMoveIfLegal(PlayerMove.activateKingdom(hexCoords));

        Optional<PlayerMove> moveOptional = determineTapMove(player, tile, event.getCount());
        if (moveOptional.isPresent()) {
            carryOutPlayerMoveIfLegal(moveOptional.get());
        }
    }

    /**
     * Event handler for map re-generation events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleRegenerateMap(RegenerateMapEvent event) {
        gameController.generateGameState(event.getBotIntelligence(), event.getMapParams());
    }

    /**
     * Event handler for scenario initialization events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleInitializeScenario(InitializeScenarioEvent event) {
        gameController.initializeScenario(event.getBotIntelligence(), event.getScenarioMap());
    }

    /**
     * Event handler for back button input events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleBackInput(BackInputEvent event) {
        carryOutPlayerMoveIfLegal(PlayerMove.undoLastMove());
    }

    /**
     * Event handler for undo move events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleUndoMove(UndoMoveEvent event) {
        carryOutPlayerMoveIfLegal(PlayerMove.undoLastMove());
    }

    /**
     * Event handler for buy peasant events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleBuyPeasant(BuyPeasantEvent event) {
        carryOutPlayerMoveIfLegal(PlayerMove.buyPeasant());
    }

    /**
     * Event handler for buy castle events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleBuyCastle(BuyCastleEvent event) {
        carryOutPlayerMoveIfLegal(PlayerMove.buyCastle());
    }

    /**
     * Event handler for buy and place castle events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleBuyAndPlaceCastle(BuyAndPlaceCastleEvent event) {
        Vector2 hexCoords = HexMapHelper.worldCoordsToHexCoords(event.getWorldCoords());
        carryOutPlayerMoveIfLegal(PlayerMove.buyAndPlaceCastle(hexCoords));
    }

    /**
     * Event handler for confirmed end turn events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleEndTurn(EndTurnEvent event) {
        carryOutPlayerMoveIfLegal(PlayerMove.endTurn());
    }

    /**
     * Event handler for select kingdom events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleActivateKingdom(ActivateKingdomEvent event) {
        carryOutPlayerMoveIfLegal(PlayerMove.activateKingdom(event.getKingdom().getTiles().get(0).getPosition()));
    }

    /**
     * Event handler for game start events.
     *
     * @param event event to handle
     */
    @Subscribe
    public void handleGameStart(GameStartEvent event) {
        gameController.startGame();
    }

    private Optional<PlayerMove> determineTapMove(Player player, HexTile tile, int count) {
        if (tile == null) {
            return Optional.empty();
        }
        if (gameController.getGameState().getHeldObject() == null) {
            if (count == 2) {
                return Optional.of(PlayerMove.buyAndPlacePeasant(tile.getPosition()));
            }
            return Optional.of(PlayerMove.pickUp(tile.getPosition()));
        } else {
            if (tile.getPlayer() != null && tile.getPlayer() == player) {
                if (tile.getContent() == null
                        || ClassReflection.isAssignableFrom(Blocking.class, tile.getContent().getClass())) {
                    return Optional.of(PlayerMove.placeOwn(tile.getPosition()));
                } else {
                    return Optional.of(PlayerMove.combineUnits(tile.getPosition()));
                }
            } else {
                return Optional.of(PlayerMove.conquer(tile.getPosition()));
            }
        }
    }

}

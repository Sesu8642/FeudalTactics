package de.sesu8642.feudaltactics.lib.gamestate;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.reflect.ClassReflection;

/**
 * Checks for completed objectives during the tutorial and progresses the
 * tutorial accordingly. Can also make other changes to the gameState to enable playing through the tutorial smoothly.
 */
public class ScenarioRuleEnforcerTutorial {

    private ScenarioRuleEnforcerTutorial() {
        // prevent instantiation
        throw new AssertionError();
    }

    static void updateGameState(GameState gameState) {

        switch (gameState.getObjectiveProgress()) {
            case 1:
                updateObjectiveProgressFrom1(gameState);
                break;
            case 2:
                updateObjectiveProgressFrom2(gameState);
                break;
            case 3:
                updateObjectiveProgressFrom3(gameState);
                break;
            case 4:
                updateObjectiveProgressFrom4(gameState);
                break;
            case 5:
                updateObjectiveProgressFrom5(gameState);
                break;
            case 6:
                updateObjectiveProgressFrom6(gameState);
                break;
            case 7:
                updateObjectiveProgressFrom7(gameState);
                break;
            case 8:
                updateObjectiveProgressFrom8(gameState);
                break;
            case 9:
                updateObjectiveProgressFrom9(gameState);
                break;
            case 10:
                updateObjectiveProgressFrom10(gameState);
                break;
            default:
                // tutorial is over, just let the player play
        }

        if (gameState.getObjectiveProgress() < 7) {
            resetBotKingdomSavings(gameState);
        }
    }

    private static void resetBotKingdomSavings(GameState gameState) {
        for (Kingdom kingdom : gameState.getKingdoms()) {
            if (kingdom.getPlayer().getType() == Player.Type.LOCAL_BOT) {
                kingdom.setSavings(3);
            }
        }
    }

    private static void updateObjectiveProgressFrom1(GameState gameState) {
        if (gameState.getActiveKingdom() != null) {
            incrementObjectiveProgress(gameState);
        }
    }

    private static void updateObjectiveProgressFrom2(GameState gameState) {
        if (gameState.getHeldObject() != null && ClassReflection.isAssignableFrom(Unit.class,
            gameState.getHeldObject().getClass())) {
            incrementObjectiveProgress(gameState);
        }
    }

    private static void updateObjectiveProgressFrom3(GameState gameState) {
        if (gameState.getMap().get(new Vector2(10, -1)).getPlayer().getType() == Player.Type.LOCAL_PLAYER) {
            incrementObjectiveProgress(gameState);
        }
    }

    private static void updateObjectiveProgressFrom4(GameState gameState) {
        if (gameState.getRound() > 0 && gameState.getActiveKingdom() != null) {
            incrementObjectiveProgress(gameState);
        }
    }

    private static void updateObjectiveProgressFrom5(GameState gameState) {
        if (gameState.getHeldObject() != null && ClassReflection.isAssignableFrom(Unit.class,
            gameState.getHeldObject().getClass())
            && gameState.getHeldObject().getStrength() == Unit.UnitTypes.SPEARMAN.strength()) {
            incrementObjectiveProgress(gameState);
            return;
        }
        for (HexTile tile : gameState.getMap().values()) {
            if (tile.getPlayer().getType() == Player.Type.LOCAL_PLAYER
                && tile.getContent() != null
                && ClassReflection.isAssignableFrom(Unit.class, tile.getContent().getClass())
                && tile.getContent().getStrength() == Unit.UnitTypes.SPEARMAN.strength()) {
                incrementObjectiveProgress(gameState);
                return;
            }
        }
    }

    private static void updateObjectiveProgressFrom6(GameState gameState) {
        if (gameState.getMap().get(new Vector2(9, -2)).getPlayer().getType() == Player.Type.LOCAL_PLAYER) {
            incrementObjectiveProgress(gameState);
        }
    }

    private static void updateObjectiveProgressFrom7(GameState gameState) {
        HexTile treeTile = gameState.getMap().get(new Vector2(8, -2));
        if (treeTile.getContent() == null || !ClassReflection.isAssignableFrom(PalmTree.class,
            treeTile.getContent().getClass())) {
            incrementObjectiveProgress(gameState);
        }
    }

    private static void updateObjectiveProgressFrom8(GameState gameState) {
        for (HexTile tile : gameState.getMap().values()) {
            if (tile.getPlayer().getType() == Player.Type.LOCAL_PLAYER
                && tile.getContent() != null && ClassReflection.isAssignableFrom(Castle.class,
                tile.getContent().getClass())) {
                incrementObjectiveProgress(gameState);
                return;
            }
        }
    }

    private static void updateObjectiveProgressFrom9(GameState gameState) {
        for (Kingdom kingdom : gameState.getKingdoms()) {
            if (kingdom.getPlayer().getType() == Player.Type.LOCAL_PLAYER && kingdom.getTiles().size() >= 20) {
                incrementObjectiveProgress(gameState);
                return;
            }
        }
    }

    private static void updateObjectiveProgressFrom10(GameState gameState) {
        for (Kingdom kingdom : gameState.getKingdoms()) {
            if (kingdom.getPlayer().getType() == Player.Type.LOCAL_PLAYER && kingdom.getTiles().size() >= 25) {
                incrementObjectiveProgress(gameState);
                return;
            }
        }
    }

    private static void incrementObjectiveProgress(GameState gameState) {
        gameState.setObjectiveProgress(gameState.getObjectiveProgress() + 1);
    }

}

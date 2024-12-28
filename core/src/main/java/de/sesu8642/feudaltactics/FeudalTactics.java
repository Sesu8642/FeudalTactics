package de.sesu8642.feudaltactics;

import com.badlogic.gdx.Game;
import de.sesu8642.feudaltactics.dagger.DaggerFeudalTacticsComponent;
import de.sesu8642.feudaltactics.dagger.FeudalTacticsComponent;

/**
 * The game's entry point.
 */
public class FeudalTactics extends Game {

    // this needs to be accessed somehow by the other classes and cannot be provided
    // by DI because it is created by the launcher
    public static FeudalTactics game;

    private FeudalTacticsComponent component;

    @Override
    public void create() {
        game = this;

        component = DaggerFeudalTacticsComponent.create();

        GameInitializer gameInitializer = component.getGameInitializer();
        gameInitializer.initializeGame();
    }

    @Override
    public void dispose() {
        super.dispose();
    }

    /**
     * For accessing dependencies from non-DI-capable classes like the custom JUL
     * handler.
     */
    public FeudalTacticsComponent getComponent() {
        return component;
    }

}

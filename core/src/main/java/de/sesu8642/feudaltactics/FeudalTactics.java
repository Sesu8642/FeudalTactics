package de.sesu8642.feudaltactics;

import com.badlogic.gdx.Game;
import de.sesu8642.feudaltactics.dagger.DaggerFeudalTacticsComponent;
import de.sesu8642.feudaltactics.dagger.FeudalTacticsComponent;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;
import de.sesu8642.feudaltactics.platformspecific.PlatformSharing;

/**
 * The game's entry point.
 */
public class FeudalTactics extends Game {

    private static FeudalTacticsComponent component;

    private final PlatformSharing platformSharing;

    private final PlatformInsetsProvider platformInsetsProvider;

    public FeudalTactics(PlatformSharing platformSharing, PlatformInsetsProvider platformInsetsProvider) {
        this.platformSharing = platformSharing;
        this.platformInsetsProvider = platformInsetsProvider;
    }

    /**
     * For accessing dependencies from non-DI-capable classes like the custom JUL
     * handler.
     */
    public static FeudalTacticsComponent getDaggerComponent() {
        return component;
    }

    @Override
    public void create() {
        component = DaggerFeudalTacticsComponent.builder()
            .gameInstance(this)
            .platformSharing(platformSharing)
            .platformInsetProvider(platformInsetsProvider)
            .build();

        final GameInitializer gameInitializer = component.getGameInitializer();
        gameInitializer.initializeGame();
    }

}

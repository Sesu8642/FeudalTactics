package com.sesu8642.feudaltactics.ui.stages;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.sesu8642.feudaltactics.FeudalTactics;
import com.sesu8642.feudaltactics.MapRenderer;
import com.sesu8642.feudaltactics.dagger.AboutScreen;
import com.sesu8642.feudaltactics.dagger.MenuBackgroundCamera;
import com.sesu8642.feudaltactics.dagger.MenuBackgroundRenderer;
import com.sesu8642.feudaltactics.dagger.MenuViewport;
import com.sesu8642.feudaltactics.dagger.TutorialScreen;
import com.sesu8642.feudaltactics.dagger.VersionProperty;
import com.sesu8642.feudaltactics.ui.screens.EditorScreen;
import com.sesu8642.feudaltactics.ui.screens.GameScreen;
import com.sesu8642.feudaltactics.ui.screens.IngameScreen;

@Singleton
public class MainMenuStage extends MenuStage {

	@Inject
	public MainMenuStage(@MenuViewport Viewport viewport, @MenuBackgroundCamera OrthographicCamera camera,
			@MenuBackgroundRenderer MapRenderer mapRenderer, Skin skin, @VersionProperty String gameVersion,
			Provider<IngameScreen> ingameScreenProvider, Provider<EditorScreen> editorScreenProvider,
			@TutorialScreen Provider<GameScreen> tutorialScreenProvider,
			@AboutScreen Provider<GameScreen> aboutScreenProvider) {
		super(viewport, camera, mapRenderer, skin);
		addButton("Play", () -> FeudalTactics.game.setScreen(ingameScreenProvider.get()));
		addButton("Tutorial", () -> FeudalTactics.game.setScreen(tutorialScreenProvider.get()));
		addButton("About", () -> FeudalTactics.game.setScreen(aboutScreenProvider.get()));
		setBottomLabelText(String.format("Version %s", gameVersion));
	}

}
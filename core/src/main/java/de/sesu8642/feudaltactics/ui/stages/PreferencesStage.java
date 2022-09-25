// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.ui.stages;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.dagger.qualifierannotations.MenuCamera;
import de.sesu8642.feudaltactics.dagger.qualifierannotations.MenuViewport;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.ui.stages.slidestage.Slide;
import de.sesu8642.feudaltactics.ui.stages.slidestage.SlideStage;

/**
 * {@link Stage} that that displays the global preferences menu.
 */
@Singleton
public class PreferencesStage extends SlideStage {

	/**
	 * Constructor.
	 * 
	 * @param viewport viewport for the stage
	 * @param camera   camera to use
	 * @param skin     game skin
	 */
	@Inject
	public PreferencesStage(EventBus eventBus, @MenuViewport Viewport viewport, @MenuCamera OrthographicCamera camera,
			Skin skin) {
		super(viewport, Arrays.asList(createPreferencesSlide(skin)),
				() -> eventBus.post(new ScreenTransitionTriggerEvent(ScreenTransitionTarget.MAIN_MENU_SCREEN)), camera,
				skin);
	}

	private static Slide createPreferencesSlide(Skin skin) {
		Slide result = new Slide(skin, "Preferences");

		Table preferencesTable = new Table();

		Label forgottenKingdomLabel = result.newNiceLabel("Warn about forgotten kingdoms");
		forgottenKingdomLabel.setWrap(true);
		preferencesTable.add(forgottenKingdomLabel).left().fill().expandX().prefWidth(200);
		preferencesTable.add(newBooleanSelect(skin)).center().fillX().expandX();
		preferencesTable.row();

		preferencesTable.add().height(20);
		preferencesTable.row();

		Label showEnemyTurnLabel = result.newNiceLabel("Show enemy turns");
		showEnemyTurnLabel.setWrap(true);
		preferencesTable.add(showEnemyTurnLabel).left().fill().expandX().prefWidth(200);
		preferencesTable.add(newBooleanSelect(skin)).center().fillX().expandX();

		// add a row to fill the rest of the space in order for the other options to be
		// at the top of the page
		preferencesTable.row();
		preferencesTable.add().fill().expand();

		result.getTable().add(preferencesTable).fill().expand();
		return result;
	}

	private static SelectBox<Boolean> newBooleanSelect(Skin skin) {
		SelectBox<Boolean> result = new SelectBox<>(skin);
		result.setItems(true, false);
		return result;
	}

}

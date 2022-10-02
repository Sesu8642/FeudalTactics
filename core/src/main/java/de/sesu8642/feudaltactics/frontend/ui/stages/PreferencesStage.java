// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.frontend.ui.stages;

import java.util.Arrays;

import javax.inject.Inject;
import javax.inject.Singleton;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.google.common.eventbus.EventBus;

import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MenuCamera;
import de.sesu8642.feudaltactics.frontend.dagger.qualifierannotations.MenuViewport;
import de.sesu8642.feudaltactics.frontend.events.ScreenTransitionTriggerEvent;
import de.sesu8642.feudaltactics.frontend.events.ScreenTransitionTriggerEvent.ScreenTransitionTarget;
import de.sesu8642.feudaltactics.frontend.ui.stages.slidestage.Slide;
import de.sesu8642.feudaltactics.frontend.ui.stages.slidestage.SlideStage;

/**
 * {@link Stage} that that displays the global preferences menu.
 */
@Singleton
public class PreferencesStage extends SlideStage {

	private EventBus eventBus;

	// a lot of things are static in this class because they are called within the
	// super constructor call
	static SelectBox<Boolean> forgottenKingdomSelectBox;
	static SelectBox<Boolean> showEnemyTurnsSelectBox;

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
		this.eventBus = eventBus;
	}

	private static Slide createPreferencesSlide(Skin skin) {
		Table preferencesTable = new Table();

		forgottenKingdomSelectBox = placeBooleanSelectWithLabel(preferencesTable, "Warn about forgotten kingdoms",
				skin);
		showEnemyTurnsSelectBox = placeBooleanSelectWithLabel(preferencesTable, "Show enemy turns", skin);

		// add a row to fill the rest of the space in order for the other options to be
		// at the top of the page
		preferencesTable.row();
		preferencesTable.add().fill().expand();

		Slide result = new Slide(skin, "Preferences");
		result.getTable().add(preferencesTable).fill().expand();
		return result;
	}

	private static SelectBox<Boolean> placeBooleanSelectWithLabel(Table preferencesTable, String labelText, Skin skin) {
		Label newLabel = Slide.newNiceLabel(labelText, skin);
		newLabel.setWrap(true);
		preferencesTable.add(newLabel).left().fill().expandX().prefWidth(200);
		SelectBox<Boolean> newSelectBox = new SelectBox<>(skin);
		newSelectBox.setItems(true, false);
		newSelectBox.addListener(new ChangeListener() {
			@Override
			public void changed(ChangeEvent event, Actor actor) {
				onPreferencesChanged();
			}
		});
		preferencesTable.add(newSelectBox).center().fillX().expandX();
		preferencesTable.row();
		preferencesTable.add().height(20);
		preferencesTable.row();
		return newSelectBox;
	}

	private static void onPreferencesChanged() {
		System.out.println("CHANGED");
	}
}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.campaign.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.Value;
import com.badlogic.gdx.utils.Align;
import com.google.common.collect.ImmutableList;
import de.sesu8642.feudaltactics.lib.gamestate.GameState;
import de.sesu8642.feudaltactics.lib.gamestate.GameStateHelper;
import de.sesu8642.feudaltactics.lib.gamestate.Player;
import de.sesu8642.feudaltactics.menu.common.ui.MapPreviewFactory;
import de.sesu8642.feudaltactics.menu.common.ui.MapPreviewWidget;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;


// TODO: check this
// this is not just a slide created by a factory because it needs the additional accessors for text
// it is not created by the LevelSelectionStageForMenu because that could only use static methods as the slide needs to
// be passed to the super constructor

/**
 * UI for level selection.
 */
@Singleton
public class LevelSelectionSlide extends Slide {

    final Label descriptionLabel;
    final TextArea textArea;

    /**
     * Constructor.
     */
    @Inject
    public LevelSelectionSlide(Skin skin, MapPreviewFactory mapPreviewFactory) {
        super(skin, "Level Selection");

        final GameState dummyGameState = new GameState();
        final List<Player> players = new ArrayList<>(ImmutableList.of(new Player(0, Player.Type.LOCAL_BOT),
            new Player(1,
                Player.Type.LOCAL_BOT)));
        GameStateHelper.initializeMap(dummyGameState, players, 50, 0, 0.2F, 12343L);
        final MapPreviewWidget mapPreviewWidget = mapPreviewFactory.createPreviewWidget(dummyGameState);
        mapPreviewWidget.setPosition(0, 0);

        descriptionLabel = new Label("Lorem Ipsum", skin);
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.topLeft);

        textArea = new TextArea("", skin);
        textArea.setDisabled(true);

        getTable().add(descriptionLabel).fill().expandX();
        getTable().row();
        getTable().add(mapPreviewWidget).width(Value.percentWidth(0.5F, getTable())).height(Value.percentHeight(0.5F,
            getTable())).fill().expand();
    }

}

// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.information.ui;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import de.sesu8642.feudaltactics.LocalizationManager;
import de.sesu8642.feudaltactics.menu.common.ui.Slide;
import de.sesu8642.feudaltactics.menu.information.dagger.DependencyLicenses;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// there's no factory for this cause its just a single slide
// this is not created by the DependencyLicensesStage because that could only use static methods as the slide needs
// to be passed to the super constructor

/**
 * UI for the dependency list.
 */
@Singleton
public class DependencyListSlide extends Slide {

    final Label descriptionLabel;
    final List<Label> dependencyNameLabels = new ArrayList<>();

    /**
     * Constructor.
     */
    @Inject
    public DependencyListSlide(Skin skin, @DependencyLicenses Map<String, Map<String, String>> dependencyLicenses,
                               LocalizationManager localizationManager) {
        super(skin, localizationManager.localizeText("dependencies"));

        descriptionLabel = new Label(localizationManager.localizeText("dependency-click-description"), skin);
        descriptionLabel.setWrap(true);
        descriptionLabel.setAlignment(Align.topLeft);
        getTable().add(descriptionLabel).fill().expandX();
        getTable().row();
        final List<String> dependencyNames = new ArrayList<>(dependencyLicenses.keySet());
        dependencyNames.sort(Comparable::compareTo);
        for (String dependencyName : dependencyNames) {
            final Table labelTable = new Table();
            final Label dashLabel = new Label(dependencyName, skin);
            dashLabel.setText("- ");
            labelTable.add(dashLabel);
            final Label dependencyNameLabel = new Label(dependencyName, skin);
            dependencyNameLabel.setWrap(true);
            dependencyNameLabels.add(dependencyNameLabel);
            labelTable.add(dependencyNameLabel).expandX().fillX();
            getTable().add(labelTable).fillX();
            getTable().row();
        }

    }

}

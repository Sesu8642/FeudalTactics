// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import de.sesu8642.feudaltactics.platformspecific.PlatformInsetsProvider;

/**
 * Like a {@link InsetsRespectingSelectBox<Boolean>} but it supports displaying custom display names instead of just true and false.
 */
public class BooleanSelectBox extends InsetsRespectingSelectBox<Boolean> {

    private final String trueDisplayName;
    private final String falseDisplayName;

    public BooleanSelectBox(Skin skin, String trueDisplayName, String falseDisplayName,
                            PlatformInsetsProvider platformInsetsProvider) {
        super(skin, platformInsetsProvider);
        this.trueDisplayName = trueDisplayName;
        this.falseDisplayName = falseDisplayName;
    }

    public BooleanSelectBox(Skin skin, String styleName, String trueDisplayName, String falseDisplayName,
                            PlatformInsetsProvider platformInsetsProvider) {
        super(skin, styleName, platformInsetsProvider);
        this.trueDisplayName = trueDisplayName;
        this.falseDisplayName = falseDisplayName;
    }

    public BooleanSelectBox(SelectBox.SelectBoxStyle style, String trueDisplayName, String falseDisplayName,
                            PlatformInsetsProvider platformInsetsProvider) {
        super(style, platformInsetsProvider);
        this.trueDisplayName = trueDisplayName;
        this.falseDisplayName = falseDisplayName;
    }

    @Override
    protected String toString(Boolean item) {
        return item ? trueDisplayName : falseDisplayName;
    }


}

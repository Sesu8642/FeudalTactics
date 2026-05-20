// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.common.ui;

import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

/**
 * Like a {@link SelectBox<Boolean>} but it supports displaying custom display names instead of just true and false.
 */
public class BooleanSelectBox extends SelectBox<Boolean> {

    private final String trueDisplayName;
    private final String falseDisplayName;

    public BooleanSelectBox(Skin skin, String trueDisplayName, String falseDisplayName) {
        super(skin);
        this.trueDisplayName = trueDisplayName;
        this.falseDisplayName = falseDisplayName;
    }

    public BooleanSelectBox(Skin skin, String styleName, String trueDisplayName, String falseDisplayName) {
        super(skin, styleName);
        this.trueDisplayName = trueDisplayName;
        this.falseDisplayName = falseDisplayName;
    }

    public BooleanSelectBox(SelectBoxStyle style, String trueDisplayName, String falseDisplayName) {
        super(style);
        this.trueDisplayName = trueDisplayName;
        this.falseDisplayName = falseDisplayName;
    }

    @Override
    protected String toString(Boolean item) {
        return item ? trueDisplayName : falseDisplayName;
    }


}

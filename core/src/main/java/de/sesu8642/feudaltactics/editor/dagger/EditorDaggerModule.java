// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.editor.dagger;

import dagger.Module;

/**
 * Dagger module for the level editor.
 */
@Module
public class EditorDaggerModule {

    private EditorDaggerModule() {
        // prevent instantiation
        throw new AssertionError();
    }

}

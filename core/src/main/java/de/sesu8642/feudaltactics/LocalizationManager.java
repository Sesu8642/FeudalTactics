// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import de.sesu8642.feudaltactics.menu.preferences.SupportedLanguages;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.MissingResourceException;

public class LocalizationManager {
    private static final FileHandle LOCALISATION_FILE = Gdx.files.internal("i18n/strings");

    I18NBundle i18NBundle;

    public LocalizationManager(String language){
        Locale locale = new Locale(SupportedLanguages.getEncoding(language));
        i18NBundle = I18NBundle.createBundle(LOCALISATION_FILE, locale, StandardCharsets.UTF_8.name());
    }

    public String localizeText(String text, Object... args){
        if (i18NBundle.keys().contains(text)){
            return i18NBundle.format(text, args);
        }
        throw new MissingResourceException("Can't find resource for bundle " + LOCALISATION_FILE.path() + ", key " + text,
            i18NBundle.getClass().getName(), text);
    }
}

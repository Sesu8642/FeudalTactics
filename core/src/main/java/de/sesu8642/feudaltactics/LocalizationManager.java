// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import de.sesu8642.feudaltactics.menu.preferences.SupportedLanguages;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.stream.Collectors;

@Singleton
public class LocalizationManager {
    private static final FileHandle LOCALISATION_FILE = Gdx.files.internal("i18n/strings");

    I18NBundle i18NBundle;

    @Inject
    public LocalizationManager(String language){
        Locale locale = new Locale(SupportedLanguages.getEncoding(language));
        i18NBundle = I18NBundle.createBundle(LOCALISATION_FILE, locale, StandardCharsets.UTF_8.name());
    }

    /**
     * Get localized text in a specific locale without changing the current locale.
     */
    public String localizeTextInLocale(String locale, String text, Object... args){
        I18NBundle tempBundle = I18NBundle.createBundle(LOCALISATION_FILE,
            new Locale(SupportedLanguages.getEncoding(locale)), StandardCharsets.UTF_8.name());
        try {
            return tempBundle.format(text, args);
        } catch (java.util.MissingResourceException e) {
            throw new MissingResourceException("Can't find resource for bundle " + LOCALISATION_FILE.path() + ", key " + text,
                tempBundle.getClass().getName(), text);
        }
    }

    public String localizeText(String text, Object... args){
        try {
            return i18NBundle.format(text, args);
        } catch (java.util.MissingResourceException e) {
            throw new MissingResourceException("Can't find resource for bundle " + LOCALISATION_FILE.path() + ", key " + text,
                i18NBundle.getClass().getName(), text);
        }
    }

    public List<String> localizeTextBatch(List<String> keys) {
        return keys.stream().map(this::localizeText).collect(Collectors.toList());
    }
}

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
    public static final String DEFAULT_LANGUAGE_CODE = "en";
    public static final String DEFAULT_LANGUAGE = "English";
    private static final FileHandle BASE_LOCALISATION_FILE = Gdx.files.internal("i18n/strings");

    I18NBundle i18NBundle;

    @Inject
    public LocalizationManager(String language) {
        Locale locale = new Locale(SupportedLanguages.getCode(language));
        i18NBundle = I18NBundle.createBundle(BASE_LOCALISATION_FILE, locale, StandardCharsets.UTF_8.name());
    }

    /**
     * Get localized text in a specific locale without changing the current locale.
     */
    public String localizeTextInLocale(String locale, String text, Object... args) {
        I18NBundle tempBundle = I18NBundle.createBundle(BASE_LOCALISATION_FILE,
            new Locale(SupportedLanguages.getCode(locale)), StandardCharsets.UTF_8.name());
        return tempBundle.format(text, args);
    }

    public String localizeText(String text, Object... args) {
        try {
            return i18NBundle.format(text, args);
        } catch (MissingResourceException e) {
            return localizeTextInLocale(DEFAULT_LANGUAGE_CODE, text, args);
        }
    }

    public List<String> localizeTextBatch(List<String> keys) {
        return keys.stream().map(this::localizeText).collect(Collectors.toList());
    }
}

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

/**
 * Offers functionality to get translated texts according to the current language setting.
 */
@Singleton
public class LocalizationManager {
    private static final FileHandle BASE_LOCALISATION_FILE = Gdx.files.internal("i18n/strings");

    private final I18NBundle i18NBundle;

    @Inject
    public LocalizationManager(SupportedLanguages language) {
        final Locale locale = language.getLocale();
        i18NBundle = I18NBundle.createBundle(BASE_LOCALISATION_FILE, locale, StandardCharsets.UTF_8.name());
    }

    /**
     * Returns a localized text in a given locale without changing the current locale.
     *
     * @param key  the key for the desired string
     * @param args the arguments to be replaced in the string associated to the given key.
     */
    public String localizeTextInLanguage(SupportedLanguages language, String key, Object... args) {
        final I18NBundle tempBundle = I18NBundle.createBundle(BASE_LOCALISATION_FILE, language.getLocale(),
            StandardCharsets.UTF_8.name());
        return tempBundle.format(key, args);
    }

    /**
     * Returns a localized text in the current locale.
     *
     * @param key  the key for the desired string
     * @param args the arguments to be replaced in the string associated to the given key.
     */
    public String localizeText(String key, Object... args) {
        try {
            return i18NBundle.format(key, args);
        } catch (MissingResourceException e) {
            try {
                return localizeTextInLanguage(SupportedLanguages.getFallback(), key, args);
            } catch (MissingResourceException e2) {
                return String.format("[missing: '%s']", key);
            }
        }
    }

    /**
     * Returns multiple localized texts in the current locale.
     *
     * @param keys the keys for the desired strings
     */
    public List<String> localizeTextBatch(List<String> keys) {
        return keys.stream().map(this::localizeText).collect(Collectors.toList());
    }
}

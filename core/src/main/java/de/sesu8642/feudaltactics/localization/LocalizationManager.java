// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.localization;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.I18NBundle;
import de.sesu8642.feudaltactics.ResourceNameReader;
import de.sesu8642.feudaltactics.menu.preferences.MainPreferencesDao;
import lombok.Getter;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.MissingResourceException;
import java.util.stream.Collectors;

/**
 * Offers functionality to get translated texts according to the current language setting.
 */
@Singleton
public class LocalizationManager {
    public static final String BASE_LOCALISATION_DIR = "i18n/strings";
    private static final FileHandle BASE_LOCALIZATION_DIR_FILE_HANDLE = Gdx.files.internal(BASE_LOCALISATION_DIR);
    @Getter
    private final List<SupportedLanguage> supportedLanguages;
    private final I18NBundle currentLanguageI18NBundle;

    @Inject
    public LocalizationManager(MainPreferencesDao preferencesDao, ResourceNameReader resourceNameReader) {
        final SupportedLanguage savedLanguage = preferencesDao.getMainPreferences().getLanguage();
        currentLanguageI18NBundle = I18NBundle.createBundle(BASE_LOCALIZATION_DIR_FILE_HANDLE,
            savedLanguage.getLocale(),
            StandardCharsets.UTF_8.name());
        supportedLanguages = getSupportedLanguagesFromFiles(resourceNameReader);
        supportedLanguages.add(0, SupportedLanguage.AUTO);
    }

    private List<SupportedLanguage> getSupportedLanguagesFromFiles(ResourceNameReader resourceNameReader) {
        final List<String> languageFiles = resourceNameReader.getAssetFilePaths(BASE_LOCALISATION_DIR);
        final List<SupportedLanguage> result = new ArrayList<>();
        for (String languageFilePath : languageFiles) {
            if (languageFilePath.endsWith("strings.properties")) {
                // ignore fallback file
                continue;
            }
            final String fileName = new File(languageFilePath).getName();
            final String languageTag = fileName.substring("strings".length() + 1, fileName.length() -
                ".properties".length());
            result.add(SupportedLanguage.fromLanguageTag(languageTag));
        }
        return result;
    }

    /**
     * Returns a localized text in a given locale without changing the current locale.
     *
     * @param key  the key for the desired string
     * @param args the arguments to be replaced in the string associated to the given key.
     */
    public String localizeTextInLanguage(SupportedLanguage language, String key, Object... args) {
        final I18NBundle tempBundle = I18NBundle.createBundle(BASE_LOCALIZATION_DIR_FILE_HANDLE, language.getLocale(),
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
            return currentLanguageI18NBundle.format(key, args);
        } catch (MissingResourceException e) {
            try {
                return localizeTextInLanguage(SupportedLanguage.FALLBACK, key, args);
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

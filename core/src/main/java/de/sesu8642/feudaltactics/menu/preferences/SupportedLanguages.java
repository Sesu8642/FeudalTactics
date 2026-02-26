// SPDX-License-Identifier: GPL-3.0-or-later

package de.sesu8642.feudaltactics.menu.preferences;

import com.google.common.collect.ImmutableMap;

import java.util.Map;

public class SupportedLanguages {
    public static final Map<String, String> LANGUAGE_TO_CODE = ImmutableMap.of(
        "English", "en",
        "Deutsch", "de"
    );

    private static final String[] SUPPORTED_LANGUAGES_ARRAY = LANGUAGE_TO_CODE.keySet().toArray(new String[0]);

    public static String getCode(String language) {
        return LANGUAGE_TO_CODE.get(language);
    }

    public static String[] getSupportedLanguages() {
        return SUPPORTED_LANGUAGES_ARRAY;
    }
}
